package com.ironsword.gtmfo.api.capability;

import com.ironsword.gtmfo.GTMFOConfigHolder;
import it.unimi.dsi.fastutil.objects.Object2FloatArrayMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import lombok.Getter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Per-player nutrient tracker (dairy / fruit / grain / protein / vegetable).
 *
 * <p>Values accumulate by eating (see {@code GTMFOFoodStats#finishUsingItem}), are clamped to
 * {@code gtfoNutrientConfig.cap} and decay once per in-game day while the player is online.</p>
 *
 * <p>NBT layout is unchanged from earlier versions for saves compatibility:
 * {@code {nutrients:{dairy:1.0,...}, lastDecayDay:123}}.</p>
 */
public class NutrientsTracker implements INBTSerializable<CompoundTag> {

    public static final String NBT_ROOT = "nutrients";
    public static final String NBT_LAST_DECAY = "lastDecayDay";

    @Getter
    private final Object2FloatMap<String> nutrients = new Object2FloatArrayMap<>();

    private final Player player;

    /** In-game day of the last decay settlement (server side only, -1 = not initialised yet). */
    private long lastDecayDay = -1;

    /** Set whenever the values change, consumed by the sync layer. */
    private boolean dirty = false;

    public NutrientsTracker(Player player) {
        this.player = player;
    }

    // =========================================================
    // ******* read / write *********************************** //
    // =========================================================

    /** Adds the given amounts to each category (accumulating, unlike the old overwrite behaviour). */
    public void gain(float dairy, float fruit, float grain, float protein, float vegetable) {
        if (dairy > 0) gain("dairy", dairy);
        if (fruit > 0) gain("fruit", fruit);
        if (grain > 0) gain("grain", grain);
        if (protein > 0) gain("protein", protein);
        if (vegetable > 0) gain("vegetable", vegetable);
    }

    /** Adds {@code amount} to one category, clamped to {@code [0, cap]}. */
    public void gain(String name, float amount) {
        if (!Nutrients.LIST.contains(name) || amount == 0) return;
        set(name, get(name) + amount);
    }

    /** Sets one category, clamped to {@code [0, cap]}. Values below/at zero remove the entry. */
    public void set(String name, float value) {
        if (!Nutrients.LIST.contains(name)) return;
        float cap = Nutrients.cap();
        float clamped = Math.max(0.0F, Math.min(value, cap));
        float previous = get(name);
        if (clamped <= 0.0F) {
            nutrients.removeFloat(name);
        } else {
            nutrients.put(name, clamped);
        }
        if (clamped != previous) dirty = true;
    }

    public float get(String name) {
        return nutrients.getFloat(name);
    }

    /** Removes one category entirely (admin/debug). */
    public void remove(String name) {
        if (get(name) > 0.0F) {
            nutrients.removeFloat(name);
            dirty = true;
        }
    }

    /** Removes every category. */
    public void clear() {
        if (!nutrients.isEmpty()) {
            nutrients.clear();
            dirty = true;
        }
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }

    // =========================================================
    // ******* decay ****************************************** //
    // =========================================================

    /**
     * Called roughly once per second on the server. Settles decay when the in-game day changes.
     *
     * <p>A single decay step is applied per observed day change, so time spent offline does not
     * multiply the decay.</p>
     */
    public void tick() {
        if (player.level().isClientSide) return;
        double decay = GTMFOConfigHolder.INSTANCE.gtfoNutrientConfig.decayPerDay;
        long day = player.level().getGameTime() / 24000L;

        if (lastDecayDay < 0) { // first tick after (re)load/join - just anchor the day
            lastDecayDay = day;
            return;
        }
        if (day <= lastDecayDay) return;

        lastDecayDay = day;
        if (decay <= 0) return;

        boolean changed = false;
        for (String name : Nutrients.LIST) {
            float current = get(name);
            if (current <= 0) continue;
            float next = (float) Math.max(0.0, current - decay);
            if (next <= 0.0F) nutrients.removeFloat(name);
            else nutrients.put(name, next);
            changed = true;
        }
        if (changed) dirty = true;
    }

    // =========================================================
    // ******* NBT ******************************************** //
    // =========================================================

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        CompoundTag values = new CompoundTag();

        nutrients.forEach((key, value) -> {
            if (value > 0) {
                values.putFloat(key, value);
            }
        });

        tag.put(NBT_ROOT, values);
        if (lastDecayDay >= 0) {
            tag.putLong(NBT_LAST_DECAY, lastDecayDay);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains(NBT_ROOT)) {
            CompoundTag values = nbt.getCompound(NBT_ROOT);
            nutrients.clear();
            for (String key : values.getAllKeys()) {
                float value = values.getFloat(key);
                if (Nutrients.LIST.contains(key) && value > 0) {
                    nutrients.put(key, Math.min(value, Nutrients.cap()));
                }
            }
        }
        lastDecayDay = nbt.contains(NBT_LAST_DECAY) ? nbt.getLong(NBT_LAST_DECAY) : -1L;
    }

    /** Copies another tracker's values (used when the player entity is cloned, e.g. End return). */
    public void copyFrom(NutrientsTracker other) {
        if (other == null) return;
        nutrients.clear();
        nutrients.putAll(other.nutrients);
        lastDecayDay = other.lastDecayDay;
        dirty = true;
    }
}
