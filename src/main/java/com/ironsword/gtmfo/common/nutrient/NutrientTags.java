package com.ironsword.gtmfo.common.nutrient;

import com.ironsword.gtmfo.GTMFOConfigHolder;
import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.api.capability.Nutrients;
import com.ironsword.gtmfo.api.capability.NutrientsTracker;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Tag driven nutrient values.
 *
 * <p>GTMFO foods carry their own values (see {@code Foods}); foods from other mods can be covered
 * by adding them to {@code gtmfo:nutrient/dairy}, {@code gtmfo:nutrient/fruit},
 * {@code gtmfo:nutrient/grain}, {@code gtmfo:nutrient/protein} or {@code gtmfo:nutrient/vegetable}
 * (item tags), each granting {@code gtfoNutrientConfig.tagValue}.</p>
 */
public final class NutrientTags {

    private static final List<TagKey<Item>> TAGS = new ArrayList<>();

    static {
        for (String name : Nutrients.LIST) {
            TAGS.add(TagKey.create(Registries.ITEM, GregTechModernFoodOption.id(Nutrients.tagName(name))));
        }
    }

    private NutrientTags() {}

    /** The item tag of one nutrient category (same order as {@link Nutrients#LIST}). */
    public static TagKey<Item> tag(String name) {
        int index = Nutrients.LIST.indexOf(name);
        return index < 0 ? null : TAGS.get(index);
    }

    /** Adds the configured flat tag value for every nutrient tag the stack carries. */
    public static void applyTagValues(ItemStack stack, NutrientsTracker tracker) {
        if (stack.isEmpty() || tracker == null) return;
        float value = tagValue();
        if (value <= 0) return;
        for (int i = 0; i < Nutrients.LIST.size(); i++) {
            if (stack.is(TAGS.get(i))) {
                tracker.gain(Nutrients.LIST.get(i), value);
            }
        }
    }

    /** Values granted purely by tags, in {@link Nutrients#LIST} order (used by tooltips). */
    public static float[] tagValues(ItemStack stack) {
        float[] values = new float[Nutrients.LIST.size()];
        if (stack.isEmpty()) return values;
        float value = tagValue();
        if (value <= 0) return values;
        for (int i = 0; i < values.length; i++) {
            if (stack.is(TAGS.get(i))) values[i] = value;
        }
        return values;
    }

    private static float tagValue() {
        GTMFOConfigHolder cfg = GTMFOConfigHolder.INSTANCE;
        if (cfg == null) return 0.0F;
        return (float) Math.max(0.0, cfg.gtfoNutrientConfig.tagValue);
    }
}
