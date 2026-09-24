package com.ironsword.gtmfo.integration.kubejs;

import com.ironsword.gtmfo.common.nutrient.NutrientDefinitionRegistry;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;

import java.util.Map;

/** KubeJS bridge for batch per-item nutrient definitions. */
public class GTMFOKubeJSPlugin extends KubeJSPlugin {
    private static final NutrientBindings BINDINGS = new NutrientBindings();

    @Override
    public void registerBindings(BindingsEvent event) {
        if (event.getType() == ScriptType.SERVER) {
            event.add("GTMFO", BINDINGS);
        }
    }

    @Override
    public void onServerReload() {
        NutrientDefinitionRegistry.beginReload();
    }

    public static final class NutrientBindings {
        public final NutrientApi nutrients = new NutrientApi();
    }

    public static final class NutrientApi {
        public boolean add(String itemId, Map<?, ?> values) {
            return NutrientDefinitionRegistry.add(itemId, values);
        }

        public boolean addMany(Map<?, ?> definitions) {
            return NutrientDefinitionRegistry.addMany(definitions);
        }

        public boolean addAll(Map<?, ?> definitions) {
            return addMany(definitions);
        }
    }
}
