package com.ironsword.gtmfo.common.data.recipe.chain;

import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class MicrowaveRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        GTMFORecipeTypes.MICROWAVE_RECIPES.recipeBuilder(id("hot_mushroom_stew"))
                .inputItems(Items.MUSHROOM_STEW)
                .outputItems(GTMFOItems.HOT_MUSHROOM_STEW.asStack())
                .EUt(16).duration(200).save(provider);
        GTMFORecipeTypes.MICROWAVE_RECIPES.recipeBuilder(id("hot_rabbit_stew"))
                .inputItems(Items.RABBIT_STEW)
                .outputItems(GTMFOItems.HOT_RABBIT_STEW.asStack())
                .EUt(16).duration(200).save(provider);
        GTMFORecipeTypes.MICROWAVE_RECIPES.recipeBuilder(id("hot_beetroot_soup"))
                .inputItems(Items.BEETROOT_SOUP)
                .outputItems(GTMFOItems.HOT_BEETROOT_SOUP.asStack())
                .EUt(16).duration(200).save(provider);
    }
}
