package com.ironsword.gtmfo.common.data.recipe;

import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.ironsword.gtmfo.GTMFOConfigHolder;
import com.gregtechceu.gtceu.data.recipe.builder.CampfireRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class RecipeUtils {
    /**
     * Original {@code GTFOUtils.addBakingOvenRecipes} -> {@code bakingOvenReplacement} config: furnace/smoker/campfire
     * alternatives are only registered when the config option is enabled.
     */
    public static void addFoodSmeltingRecipe(Consumer<FinishedRecipe> provider, String regName, ItemStack input,
                                             ItemStack output, float experience){
        if (!GTMFOConfigHolder.INSTANCE.gtfoMiscConfig.bakingOvenReplacement) return;
        VanillaRecipeHelper.addSmeltingRecipe(provider,id(regName),input,output,experience);
        VanillaRecipeHelper.addSmokingRecipe(provider,id(regName),input,output,experience);
        new CampfireRecipeBuilder(id(regName)).input(input).output(output).cookingTime(600).experience(experience).save(provider);
    }
}
