package com.ironsword.gtmfo.common.nutrient;

import com.ironsword.gtmfo.api.capability.Nutrients;
import com.ironsword.gtmfo.api.capability.NutrientsTracker;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Per-item nutrient overrides supplied by server-side KubeJS scripts.
 *
 * <p>The server map is rebuilt at the beginning of every KubeJS reload and committed on the
 * following server tick. The client map is populated by the definition sync packet and is used
 * only for client-side tooltips.</p>
 */
public final class NutrientDefinitionRegistry {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Object LOCK = new Object();

    private static volatile Map<ResourceLocation, Definition> active = Map.of();
    private static final Map<ResourceLocation, MutableDefinition> staging = new LinkedHashMap<>();
    private static volatile Map<ResourceLocation, Definition> client = Map.of();
    private static boolean reloadPending;
    private static volatile boolean commitBroadcastPending;

    private NutrientDefinitionRegistry() {}

    /** A sparse definition; the mask distinguishes an explicit zero from an omitted category. */
    public static final class Definition {
        private final float[] values;
        private final int mask;

        public Definition(float[] values, int mask) {
            this.values = values.clone();
            this.mask = mask;
        }

        public boolean overrides(int index) {
            return (mask & (1 << index)) != 0;
        }

        public float value(int index) {
            return values[index];
        }

        public int mask() {
            return mask;
        }

        public float[] values() {
            return values.clone();
        }
    }

    private static final class MutableDefinition {
        private final float[] values = new float[Nutrients.LIST.size()];
        private int mask;

        private MutableDefinition copy() {
            MutableDefinition copy = new MutableDefinition();
            System.arraycopy(values, 0, copy.values, 0, values.length);
            copy.mask = mask;
            return copy;
        }

        private Definition freeze() {
            return new Definition(values, mask);
        }
    }

    /** Starts a fresh script generation. Called by the KubeJS plugin before server scripts load. */
    public static void beginReload() {
        synchronized (LOCK) {
            staging.clear();
            reloadPending = true;
        }
    }

    /** Commits the generation after scripts have run; returns true when a snapshot was published. */
    public static boolean commitPending(MinecraftServer server) {
        Map<ResourceLocation, Definition> next;
        synchronized (LOCK) {
            if (!reloadPending) return false;
            next = new LinkedHashMap<>();
            staging.forEach((id, definition) -> next.put(id, definition.freeze()));
            active = Collections.unmodifiableMap(next);
            reloadPending = false;
            commitBroadcastPending = true;
        }
        return true;
    }

    public static boolean consumeCommitBroadcast() {
        if (!commitBroadcastPending) return false;
        commitBroadcastPending = false;
        return true;
    }

    /** Adds one sparse item definition. Invalid input is rejected without changing the registry. */
    public static boolean add(String itemId, Map<?, ?> values) {
        ResourceLocation id = parseItemId(itemId);
        if (id == null) return false;
        MutableDefinition parsed = parseValues(values, id.toString());
        if (parsed == null) return false;
        synchronized (LOCK) {
            // Repeated calls merge categories; the latest value wins for duplicate categories.
            MutableDefinition target = staging.computeIfAbsent(id, ignored -> new MutableDefinition());
            for (int i = 0; i < Nutrients.LIST.size(); i++) {
                if (parsed.mask != 0 && (parsed.mask & (1 << i)) != 0) {
                    target.values[i] = parsed.values[i];
                    target.mask |= 1 << i;
                }
            }
            reloadPending = true;
        }
        return true;
    }

    /** Adds a complete batch atomically; each item's categories merge with earlier registrations. */
    public static boolean addMany(Map<?, ?> definitions) {
        if (definitions == null || definitions.isEmpty()) {
            LOGGER.warn("Ignoring empty GTMFO nutrient batch");
            return false;
        }
        Map<ResourceLocation, MutableDefinition> parsed = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : definitions.entrySet()) {
            if (!(entry.getKey() instanceof String itemId)) {
                LOGGER.warn("Ignoring GTMFO nutrient entry with non-string item id: {}", entry.getKey());
                return false;
            }
            ResourceLocation id = parseItemId(itemId);
            if (id == null) return false;
            MutableDefinition values = parseValues(entry.getValue(), id.toString());
            if (values == null) return false;
            parsed.merge(id, values, (left, right) -> {
                for (int i = 0; i < Nutrients.LIST.size(); i++) {
                    if ((right.mask & (1 << i)) != 0) {
                        left.values[i] = right.values[i];
                        left.mask |= 1 << i;
                    }
                }
                return left;
            });
        }
        synchronized (LOCK) {
            parsed.forEach((id, values) -> {
                MutableDefinition target = staging.computeIfAbsent(id, ignored -> new MutableDefinition());
                for (int i = 0; i < Nutrients.LIST.size(); i++) {
                    if ((values.mask & (1 << i)) != 0) {
                        target.values[i] = values.values[i];
                        target.mask |= 1 << i;
                    }
                }
            });
            reloadPending = true;
        }
        return true;
    }

    private static ResourceLocation parseItemId(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            LOGGER.warn("Ignoring GTMFO nutrient entry with blank item id");
            return null;
        }
        ResourceLocation id = ResourceLocation.tryParse(itemId.trim());
        if (id == null || !BuiltInRegistries.ITEM.containsKey(id)) {
            LOGGER.warn("Ignoring GTMFO nutrient entry for unknown item id: {}", itemId);
            return null;
        }
        return id;
    }

    private static MutableDefinition parseValues(Object raw, String itemId) {
        if (!(raw instanceof Map<?, ?> values) || values.isEmpty()) {
            LOGGER.warn("Ignoring GTMFO nutrient entry for {}: expected a non-empty object", itemId);
            return null;
        }
        MutableDefinition parsed = new MutableDefinition();
        for (Map.Entry<?, ?> entry : values.entrySet()) {
            if (!(entry.getKey() instanceof String name)) {
                LOGGER.warn("Ignoring GTMFO nutrient entry for {}: nutrient name must be a string", itemId);
                return null;
            }
            int index = Nutrients.LIST.indexOf(name.toLowerCase(java.util.Locale.ROOT));
            if (index < 0) {
                LOGGER.warn("Ignoring GTMFO nutrient entry for {}: unknown nutrient '{}'", itemId, name);
                return null;
            }
            if (!(entry.getValue() instanceof Number number)) {
                LOGGER.warn("Ignoring GTMFO nutrient entry for {}.{}: value must be numeric", itemId, name);
                return null;
            }
            double value = number.doubleValue();
            if (!Double.isFinite(value) || value < 0.0 || value > Float.MAX_VALUE) {
                LOGGER.warn("Ignoring GTMFO nutrient entry for {}.{}: value must be finite and non-negative", itemId, name);
                return null;
            }
            parsed.values[index] = (float) value;
            parsed.mask |= 1 << index;
        }
        return parsed;
    }

    /** Returns the active server snapshot for packet encoding. */
    public static Map<ResourceLocation, Definition> snapshot() {
        return active;
    }

    public static Definition serverDefinition(ItemStack stack) {
        if (stack.isEmpty()) return null;
        return active.get(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    /** Returns true when the server has a definition for this item/category. */
    public static boolean serverOverrides(ItemStack stack, String name) {
        Definition definition = serverDefinition(stack);
        int index = Nutrients.LIST.indexOf(name);
        return definition != null && index >= 0 && definition.overrides(index);
    }

    public static Definition clientDefinition(ItemStack stack) {
        if (stack.isEmpty()) return null;
        return client.get(BuiltInRegistries.ITEM.getKey(stack.getItem()));
    }

    /** Applies built-ins and tag fallback, with explicit scripted categories replacing both. */
    public static void apply(ItemStack stack, NutrientsTracker tracker, Map<String, Float> builtIns) {
        if (stack.isEmpty() || tracker == null) return;
        Definition definition = serverDefinition(stack);
        float[] tags = NutrientTags.tagValues(stack);
        for (int i = 0; i < Nutrients.LIST.size(); i++) {
            String name = Nutrients.LIST.get(i);
            if (definition != null && definition.overrides(i)) {
                if (definition.value(i) > 0) tracker.gain(name, definition.value(i));
                continue;
            }
            if (builtIns != null) {
                Float builtIn = builtIns.get(name);
                if (builtIn != null && builtIn > 0) tracker.gain(name, builtIn);
            }
            if (tags[i] > 0) tracker.gain(name, tags[i]);
        }
    }

    /** Returns display values using the client snapshot and current item tags. */
    public static float[] displayValues(ItemStack stack, Map<String, Float> builtIns) {
        float[] result = new float[Nutrients.LIST.size()];
        if (stack.isEmpty()) return result;
        Definition definition = clientDefinition(stack);
        float[] tags = NutrientTags.tagValues(stack);
        for (int i = 0; i < result.length; i++) {
            String name = Nutrients.LIST.get(i);
            if (definition != null && definition.overrides(i)) {
                result[i] = definition.value(i);
                continue;
            }
            if (builtIns != null) result[i] += Math.max(0.0F, builtIns.getOrDefault(name, 0.0F));
            result[i] += tags[i];
        }
        return result;
    }

    /** Replaces client-side definitions from a network snapshot. */
    public static void replaceClient(Map<ResourceLocation, Definition> definitions) {
        client = Collections.unmodifiableMap(new LinkedHashMap<>(definitions));
    }

    public static void clearClient() {
        client = Map.of();
    }
}
