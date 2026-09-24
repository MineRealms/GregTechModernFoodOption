package com.ironsword.gtmfo.api.capability;

import com.ironsword.gtmfo.GTMFOConfigHolder;

import java.util.List;

public class Nutrients {
    public static final List<String> LIST = List.of("dairy", "fruit", "grain", "protein", "vegetable");

    /** Whether the nutrient system is active (new config group, plus the legacy devConfigs flag). */
    public static boolean isEnabled() {
        GTMFOConfigHolder cfg = GTMFOConfigHolder.INSTANCE;
        if (cfg == null) return false;
        return cfg.gtfoNutrientConfig.enabled || cfg.devConfigs.nutrientMode;
    }

    /** Per-category value cap. */
    public static float cap() {
        GTMFOConfigHolder cfg = GTMFOConfigHolder.INSTANCE;
        if (cfg == null) return 30.0F;
        return (float) Math.max(0.0, cfg.gtfoNutrientConfig.cap);
    }

    /** Translation key of one category (e.g. {@code gtmfo.nutrient.dairy}). */
    public static String langKey(String name) {
        return "gtmfo.nutrient." + name;
    }

    /** Item tag that grants a flat nutrient value to any food (pack-friendly, see config.tagValue). */
    public static String tagName(String name) {
        return "nutrient/" + name;
    }
}
