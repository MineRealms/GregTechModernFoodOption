package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.ironsword.gtmfo.common.data.GTMFOTrees;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

/**
 * Tree wood recipes: log -> planks (crafting + cutter), planks -> sticks.
 */
public class TreeRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        for (int i = 0; i < GTMFOTrees.TREES.size(); i++) {
            String name = GTMFOTrees.TREES.get(i).name();
            ItemStack log = GTMFOTrees.LOGS.get(i).asStack();
            ItemStack planks = GTMFOTrees.PLANKS.get(i).asStack();

            VanillaRecipeHelper.addShapelessRecipe(provider, id(name + "_planks_from_log"),
                    new ItemStack(planks.getItem(), 4), log.copy());
            GTRecipeTypes.CUTTER_RECIPES.recipeBuilder(id(name + "_planks_from_log_cutter"))
                    .inputItems(log.copy())
                    .outputItems(new ItemStack(planks.getItem(), 4))
                    .EUt(30).duration(50).save(provider);

            VanillaRecipeHelper.addShapedRecipe(provider, id(name + "_sticks_from_planks"),
                    new ItemStack(Items.STICK, 4),
                    "P", "P",
                    'P', planks.copy());
        }
    }
}
