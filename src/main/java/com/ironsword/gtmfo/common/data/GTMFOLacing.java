package com.ironsword.gtmfo.common.data;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.ironsword.gtmfo.common.data.material.GTMFOMaterials;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Lacing system: food items carrying the {@link #NBT_KEY} tag apply an extra mob effect when eaten.
 */
public class GTMFOLacing {

    public static final String NBT_KEY = "gtmfo_lacing";

    public record LacingEntry(ItemStack lacingItem, MobEffect effect, int duration, int amplifier) {}

    public static final List<LacingEntry> ENTRIES = new ArrayList<>();

    public static void init() {
        ENTRIES.add(new LacingEntry(
                ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.SodiumCyanide),
                GTMFOEffects.CYANIDE.get(), 1300, 0));
        ENTRIES.add(new LacingEntry(
                ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.LithiumCarbonate),
                GTMFOEffects.ANTISCHIZO.get(), 1000, 0));
        ENTRIES.add(new LacingEntry(
                ChemicalHelper.get(TagPrefix.dust, GTMaterials.Asbestos),
                GTMFOEffects.LUNG_CANCER.get(), 99999999, 0));
    }

    public static LacingEntry byItem(ItemStack stack) {
        for (LacingEntry entry : ENTRIES) {
            if (ItemStack.isSameItem(entry.lacingItem(), stack)) {
                return entry;
            }
        }
        return null;
    }

    public static int indexOf(ItemStack stack) {
        for (int i = 0; i < ENTRIES.size(); i++) {
            if (ItemStack.isSameItem(ENTRIES.get(i).lacingItem(), stack)) {
                return i;
            }
        }
        return -1;
    }
}
