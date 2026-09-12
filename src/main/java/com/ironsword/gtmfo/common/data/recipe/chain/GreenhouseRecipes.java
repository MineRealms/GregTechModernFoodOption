package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtmfo.common.data.GTMFOTrees;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import net.minecraft.world.item.Items;
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
        vanillaTrees(provider);
        rubberTree(provider);
        sap(provider);
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

    /** Vanilla trees (original {@code registerVanillaTreeRecipes}). */
    private static void vanillaTrees(Consumer<FinishedRecipe> provider){
        record VanillaTree(String name, net.minecraft.world.item.Item sapling, net.minecraft.world.item.Item log,
                           net.minecraft.world.item.Item leaves, ItemStack crop) {}
        var trees = java.util.List.of(
                new VanillaTree("oak", Items.OAK_SAPLING, Items.OAK_LOG, Items.OAK_LEAVES,
                        new ItemStack(Items.APPLE)),
                new VanillaTree("spruce", Items.SPRUCE_SAPLING, Items.SPRUCE_LOG, Items.SPRUCE_LEAVES,
                        new ItemStack(Items.STICK)),
                new VanillaTree("birch", Items.BIRCH_SAPLING, Items.BIRCH_LOG, Items.BIRCH_LEAVES,
                        new ItemStack(Items.STICK)),
                new VanillaTree("jungle", Items.JUNGLE_SAPLING, Items.JUNGLE_LOG, Items.JUNGLE_LEAVES,
                        new ItemStack(Items.STICK)),
                new VanillaTree("acacia", Items.ACACIA_SAPLING, Items.ACACIA_LOG, Items.ACACIA_LEAVES,
                        new ItemStack(Items.STICK)),
                new VanillaTree("dark_oak", Items.DARK_OAK_SAPLING, Items.DARK_OAK_LOG, Items.DARK_OAK_LEAVES,
                        new ItemStack(Items.STICK)));
        for (var tree : trees) {
            ItemStack sapling = new ItemStack(tree.sapling());
            GTMFORecipeTypes.GREENHOUSE_RECIPES.recipeBuilder(id("vanilla_" + tree.name() + "_1"))
                    .inputItems(sapling.copy())
                    .circuitMeta(1)
                    .inputFluids(GTMaterials.Water.getFluid(10000))
                    .outputItems(new ItemStack(tree.log(), 6), tree.crop().copy(), sapling.copy())
                    .chancedOutput(sapling.copy(), 2000, 1000)
                    .EUt(60).duration(2000).save(provider);
            GTMFORecipeTypes.GREENHOUSE_RECIPES.recipeBuilder(id("vanilla_" + tree.name() + "_2"))
                    .inputItems(sapling.copy())
                    .circuitMeta(2)
                    .inputFluids(GTMaterials.Water.getFluid(10000))
                    .outputItems(new ItemStack(tree.log(), 5), tree.crop().copy())
                    .chancedOutput(sapling.copy(), 1000, 1000)
                    .outputItems(new ItemStack(tree.leaves(), 20))
                    .EUt(60).duration(2000).save(provider);
            GTMFORecipeTypes.GREENHOUSE_RECIPES.recipeBuilder(id("vanilla_" + tree.name() + "_3"))
                    .inputItems(sapling.copy())
                    .circuitMeta(3)
                    .inputFluids(GTMaterials.Water.getFluid(20000))
                    .outputItems(new ItemStack(tree.log(), 5))
                    .chancedOutput(sapling.copy(), 8000, 200)
                    .outputItems(new ItemStack(tree.crop().getItem(), 3))
                    .chancedOutput(new ItemStack(tree.crop().getItem(), 2), 4000, 500)
                    .EUt(60).duration(2000).save(provider);
        }
    }

    /** Rubber tree tapping (original {@code registerTappingRecipes}). */
    private static void rubberTree(Consumer<FinishedRecipe> provider){
        ItemStack sapling = GTBlocks.RUBBER_SAPLING.asStack();
        ItemStack log = GTBlocks.RUBBER_LOG.asStack();
        ItemStack leaves = GTBlocks.RUBBER_LEAVES.asStack();

        GTMFORecipeTypes.GREENHOUSE_RECIPES.recipeBuilder(id("rubber_tapping_1"))
                .inputItems(sapling.copy())
                .circuitMeta(1)
                .inputFluids(GTMaterials.Water.getFluid(10000))
                .outputItems(new ItemStack(log.getItem(), 6), sapling.copy())
                .chancedOutput(sapling.copy(), 2000, 1000)
                .EUt(60).duration(2000).save(provider);
        GTMFORecipeTypes.GREENHOUSE_RECIPES.recipeBuilder(id("rubber_tapping_2"))
                .inputItems(sapling.copy())
                .circuitMeta(2)
                .inputFluids(GTMaterials.Water.getFluid(10000))
                .outputItems(new ItemStack(log.getItem(), 5))
                .chancedOutput(sapling.copy(), 1000, 1000)
                .outputItems(new ItemStack(leaves.getItem(), 20))
                .EUt(60).duration(2000).save(provider);
        GTMFORecipeTypes.GREENHOUSE_RECIPES.recipeBuilder(id("rubber_tapping_3"))
                .inputItems(sapling.copy())
                .circuitMeta(3)
                .inputFluids(GTMaterials.Water.getFluid(10000))
                .outputItems(new ItemStack(log.getItem(), 5))
                .chancedOutput(sapling.copy(), 8000, 200)
                .outputFluids(GTMFOFluids.RubberSap.getFluid(4000))
                .EUt(90).duration(3000).save(provider);
        GTMFORecipeTypes.GREENHOUSE_RECIPES.recipeBuilder(id("rubber_tapping_4"))
                .inputItems(sapling.copy(), GTItems.FERTILIZER.asStack())
                .circuitMeta(4)
                .inputFluids(GTMaterials.Water.getFluid(10000))
                .outputItems(new ItemStack(log.getItem(), 8))
                .chancedOutput(sapling.copy(), 8000, 200)
                .outputFluids(GTMFOFluids.RubberSap.getFluid(16000))
                .EUt(90).duration(4000).save(provider);
    }

    /** Rubber sap -> sticky resin (original sap processing). */
    private static void sap(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES.recipeBuilder(id("sticky_resin"))
                .inputFluids(GTMFOFluids.RubberSap.getFluid(100))
                .notConsumable(GTItems.SHAPE_MOLD_BALL.asStack())
                .outputItems(GTItems.STICKY_RESIN.asStack())
                .EUt(8).duration(160).save(provider);
    }
}
