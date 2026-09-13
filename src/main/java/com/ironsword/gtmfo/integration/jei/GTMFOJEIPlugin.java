package com.ironsword.gtmfo.integration.jei;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.gregtechceu.gtceu.common.data.GTMachines;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

/**
 * JEI plugin: GTFO food info pages, eating recipes and lacing recipes
 * (original {@code JEIGTFOPlugin}).
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
        registration.addRecipeCategories(new LacingCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new EatingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(FoodInfoCategory.TYPE, FoodInfoCategory.FoodInfo.collect());
        registration.addRecipes(LacingCategory.TYPE, LacingCategory.LacingInfo.collect());
        registration.addRecipes(EatingRecipeCategory.TYPE, EatingRecipeCategory.EatingInfo.collect());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // original: all canner tiers are catalysts for the lacing recipes
        for (var canner : GTMachines.CANNER) {
            if (canner != null) {
                registration.addRecipeCatalyst(canner.asStack(), LacingCategory.TYPE);
            }
        }
    }
}
