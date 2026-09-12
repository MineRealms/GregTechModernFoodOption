package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.ironsword.gtmfo.common.data.GTMFOTrees;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

/**
 * Greenhouse recipes: grow logs/leaves/fruit from saplings (per tree).
 */
public class GreenhouseRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        for (int i = 0; i < GTMFOTrees.TREES.size(); i++) {
            var tree = GTMFOTrees.TREES.get(i);
            ItemStack sapling = GTMFOTrees.SAPLINGS.get(i).asStack();
            ItemStack log = GTMFOTrees.LOGS.get(i).asStack();
            ItemStack leaves = GTMFOTrees.LEAVES.get(i).asStack();
            ItemStack fruit = tree.fruit() != null ? new ItemStack(tree.fruit().get()) : ItemStack.EMPTY;

            // circuit 1: logs + sapling + fruit
            var logs = GTMFORecipeTypes.GREENHOUSE_RECIPES.recipeBuilder(id(tree.name() + "_logs"))
                    .inputItems(sapling.copy())
                    .circuitMeta(1)
                    .inputFluids(GTMaterials.Water.getFluid(10000))
                    .outputItems(new ItemStack(log.getItem(), 6), sapling.copy())
                    .chancedOutput(sapling.copy(), 2000, 1000)
                    .EUt(60).duration(2000);
            if (!fruit.isEmpty()) {
                logs.outputItems(fruit.copy());
            }
            logs.save(provider);

            // circuit 2: logs + leaves
            GTMFORecipeTypes.GREENHOUSE_RECIPES.recipeBuilder(id(tree.name() + "_leaves"))
                    .inputItems(sapling.copy())
                    .circuitMeta(2)
                    .inputFluids(GTMaterials.Water.getFluid(10000))
                    .outputItems(new ItemStack(log.getItem(), 5))
                    .chancedOutput(sapling.copy(), 1000, 1000)
                    .outputItems(new ItemStack(leaves.getItem(), 20))
                    .EUt(60).duration(2000).save(provider);

            // circuit 3: fruit
            if (!fruit.isEmpty()) {
                GTMFORecipeTypes.GREENHOUSE_RECIPES.recipeBuilder(id(tree.name() + "_fruit"))
                        .inputItems(sapling.copy())
                        .circuitMeta(3)
                        .inputFluids(GTMaterials.Water.getFluid(20000))
                        .outputItems(new ItemStack(log.getItem(), 5))
                        .chancedOutput(sapling.copy(), 8000, 200)
                        .outputItems(new ItemStack(fruit.getItem(), 3))
                        .chancedOutput(new ItemStack(fruit.getItem(), 2), 4000, 500)
                        .EUt(60).duration(3000).save(provider);
            }
        }
    }
}
