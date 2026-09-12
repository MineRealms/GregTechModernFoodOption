package com.ironsword.gtmfo.integration.jei;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

/**
 * JEI plugin: GTFO food info pages (original {@code JEIGTFOPlugin}).
 */
@JeiPlugin
public class GTMFOJEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return GregTechModernFoodOption.id("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new FoodInfoCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(FoodInfoCategory.TYPE, FoodInfoCategory.FoodInfo.collect());
    }
}
