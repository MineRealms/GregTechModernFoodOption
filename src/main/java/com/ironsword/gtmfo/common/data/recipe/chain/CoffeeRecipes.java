package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

/**
 * Coffee chain, ported from the original {@code CoffeeChain}: cups, coffee brewing and the 5-stage bean processing
 * (raw -> fermented -> dried -> hulled -> roasted).
 */
public class CoffeeRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        cups(provider);
        coffee(provider);
        beans(provider);
    }

    private static void cups(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.FORMING_PRESS_RECIPES.recipeBuilder(id("unfired_cup"))
                .inputItems(Items.CLAY_BALL, 5)
                .notConsumable(GTMFOItems.COFFEE_FILTER.asStack())
                .outputItems(GTMFOItems.CUP_UNFIRED.asStack(2))
                .EUt(30).duration(200).save(provider);
        GTRecipeTypes.BLAST_RECIPES.recipeBuilder(id("empty_cup"))
                .inputItems(GTMFOItems.CUP_UNFIRED.asStack())
                .outputItems(GTMFOItems.CUP_EMPTY.asStack())
                .blastFurnaceTemp(1033)
                .EUt(120).duration(20).save(provider);
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(id("coffee_filter"))
                .inputItems(Items.PAPER)
                .circuitMeta(1)
                .outputItems(GTMFOItems.COFFEE_FILTER.asStack())
                .EUt(30).duration(14).save(provider);
    }

    private static void coffee(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("coffee_cup"))
                .inputFluids(GTMFOFluids.Coffee.getFluid(100))
                .inputItems(GTMFOItems.CUP_EMPTY.asStack())
                .outputItems(GTMFOItems.COFFEE.asStack())
                .EUt(40).duration(20).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("coffee_energized_cup"))
                .inputFluids(GTMFOFluids.EnergizedCoffee.getFluid(100))
                .inputItems(GTMFOItems.CUP_EMPTY.asStack())
                .outputItems(GTMFOItems.COFFEE_ENERGIZED.asStack())
                .EUt(40).duration(20).save(provider);
        GTRecipeTypes.BREWING_RECIPES.recipeBuilder(id("energized_coffee"))
                .inputFluids(GTMFOFluids.Coffee.getFluid(10))
                .inputItems(Items.SUGAR)
                .outputFluids(GTMFOFluids.EnergizedCoffee.getFluid(10))
                .EUt(120).duration(100).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("coffee_from_grounds"))
                .inputItems(GTMFOItems.COFFEE_BEANS_ROASTED_LARGE.asStack())
                .inputItems(GTMFOItems.COFFEE_FILTER.asStack())
                .inputFluids(GTMaterials.Steam.getFluid(1000))
                .outputItems(com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(
                        com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust, GTMaterials.Paper))
                .outputFluids(GTMFOFluids.Coffee.getFluid(15))
                .EUt(120).duration(30).save(provider);
    }

    private static void beans(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.CUTTER_RECIPES.recipeBuilder(id("coffee_seed_from_cherry"))
                .inputItems(GTMFOItems.COFFEE_CHERRY.asStack())
                .outputItems(GTMFOItems.COFFEE_BEANS_RAW_LARGE.asStack(9))
                .chancedOutput(GTMFOItems.COFFEE_BEANS_RAW_LARGE.asStack(), 5000, 500)
                .EUt(60).duration(20).save(provider);

        GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("coffee_beans_sorted"))
                .inputItems(GTMFOItems.COFFEE_BEANS_RAW_LARGE.asStack(30))
                .outputItems(GTMFOItems.COFFEE_BEANS_RAW_LARGE.asStack(9))
                .chancedOutput(GTMFOItems.COFFEE_BEANS_RAW_LARGE.asStack(), 5000, 200)
                .outputItems(GTMFOItems.COFFEE_BEANS_RAW_SMALL.asStack(19))
                .chancedOutput(GTMFOItems.COFFEE_BEANS_RAW_SMALL.asStack(), 5000, 200)
                .EUt(20).duration(600).save(provider);

        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("coffee_beans_wet_large"))
                .inputItems(GTMFOItems.COFFEE_BEANS_RAW_LARGE.asStack(32))
                .inputFluids(GTMaterials.Water.getFluid(8000))
                .outputItems(GTMFOItems.COFFEE_BEANS_FERMENTED_LARGE.asStack(32))
                .EUt(60).duration(3600).save(provider);
        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("coffee_beans_wet_small"))
                .inputItems(GTMFOItems.COFFEE_BEANS_RAW_SMALL.asStack(64))
                .inputFluids(GTMaterials.Water.getFluid(8000))
                .outputItems(GTMFOItems.COFFEE_BEANS_FERMENTED_SMALL.asStack(64))
                .EUt(60).duration(3600).save(provider);

        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("coffee_beans_dried_large"))
                .inputItems(GTMFOItems.COFFEE_BEANS_FERMENTED_LARGE.asStack(32))
                .outputItems(GTMFOItems.COFFEE_BEANS_DRIED_LARGE.asStack(32))
                .outputFluids(GTMaterials.Water.getFluid(8000))
                .EUt(30).duration(3600).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("coffee_beans_dried_small"))
                .inputItems(GTMFOItems.COFFEE_BEANS_FERMENTED_SMALL.asStack(64))
                .outputItems(GTMFOItems.COFFEE_BEANS_DRIED_SMALL.asStack(64))
                .outputFluids(GTMaterials.Water.getFluid(8000))
                .EUt(30).duration(1800).save(provider);

        GTRecipeTypes.LATHE_RECIPES.recipeBuilder(id("coffee_beans_hulled_large"))
                .inputItems(GTMFOItems.COFFEE_BEANS_DRIED_LARGE.asStack())
                .outputItems(GTMFOItems.COFFEE_BEANS_HULLED_LARGE.asStack())
                .EUt(30).duration(10).save(provider);
        GTRecipeTypes.LATHE_RECIPES.recipeBuilder(id("coffee_beans_hulled_small"))
                .inputItems(GTMFOItems.COFFEE_BEANS_DRIED_SMALL.asStack())
                .outputItems(GTMFOItems.COFFEE_BEANS_HULLED_SMALL.asStack())
                .EUt(30).duration(10).save(provider);

        GTRecipeTypes.PYROLYSE_RECIPES.recipeBuilder(id("coffee_beans_roasted_large"))
                .inputItems(GTMFOItems.COFFEE_BEANS_HULLED_LARGE.asStack())
                .circuitMeta(1)
                .outputItems(GTMFOItems.COFFEE_BEANS_ROASTED_LARGE.asStack())
                .outputFluids(GTMaterials.Water.getFluid(200))
                .EUt(120).duration(80).save(provider);
        GTRecipeTypes.PYROLYSE_RECIPES.recipeBuilder(id("coffee_beans_roasted_small"))
                .inputItems(GTMFOItems.COFFEE_BEANS_HULLED_SMALL.asStack())
                .circuitMeta(1)
                .outputItems(GTMFOItems.COFFEE_BEANS_ROASTED_SMALL.asStack())
                .outputFluids(GTMaterials.Water.getFluid(100))
                .EUt(120).duration(40).save(provider);
    }
}
