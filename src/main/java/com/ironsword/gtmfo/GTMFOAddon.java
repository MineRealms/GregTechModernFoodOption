package com.ironsword.gtmfo;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.addon.events.MaterialCasingCollectionEvent;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.ironsword.gtmfo.common.data.GTMFOBlocks;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipes;
import com.ironsword.gtmfo.common.registry.GTMFORegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

@GTAddon
public class GTMFOAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return GTMFORegistries.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
        // Initialised here (not in the mod constructor) because GTToolType needs GTSoundEntries,
        // which GTCEu only sets up during its own construction.
        com.ironsword.gtmfo.common.data.GTMFOTools.init();
    }

    @Override
    public void registerCovers() {
        // GTCEu calls this inside GTCovers.init(), right before the cover registry is frozen.
        com.ironsword.gtmfo.common.data.GTMFOCovers.init();
    }

    @Override
    public void collectMaterialCasings(MaterialCasingCollectionEvent event) {
        GTMFOBlocks.collectMaterialCasings(event);
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        GTMFORecipes.init(provider);
    }

    @Override
    public void removeRecipes(Consumer<ResourceLocation> consumer) {
        GTMFORecipes.remove(consumer);
    }

    @Override
    public String addonModId() {
        return GregTechModernFoodOption.MODID;
    }
}
