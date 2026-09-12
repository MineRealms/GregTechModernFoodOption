package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

/**
 * Seed extraction + soybean oil processing.
 * (The greenhouse recipe from the original is skipped until the greenhouse multiblock is ported.)
 */
public class SeedsRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        seedExtraction(provider);
        soybeanOil(provider);
    }

    private static void seedExtraction(Consumer<FinishedRecipe> provider){
        VanillaRecipeHelper.addShapelessRecipe(provider, id("seed_soy_extraction"),
                GTMFOItems.SEED_SOY.asStack(), GTMFOItems.SOYBEAN.asStack());
        VanillaRecipeHelper.addShapelessRecipe(provider, id("seed_tomato_extraction"),
                GTMFOItems.SEED_TOMATO.asStack(), GTMFOItems.TOMATO.asStack());
        VanillaRecipeHelper.addShapelessRecipe(provider, id("seed_cucumber_extraction"),
                GTMFOItems.SEED_CUCUMBER.asStack(), GTMFOItems.CUCUMBER.asStack());
        VanillaRecipeHelper.addShapelessRecipe(provider, id("seed_grapes_extraction"),
                GTMFOItems.SEED_GRAPE.asStack(), GTMFOItems.GRAPES.asStack());
        VanillaRecipeHelper.addShapelessRecipe(provider, id("seed_eggplant_extraction"),
                GTMFOItems.SEED_EGGPLANT.asStack(), GTMFOItems.EGGPLANT.asStack());
        VanillaRecipeHelper.addShapelessRecipe(provider, id("seed_artichoke_extraction"),
                GTMFOItems.SEED_ARTICHOKE.asStack(), GTMFOItems.ARTICHOKE.asStack());
        VanillaRecipeHelper.addShapelessRecipe(provider, id("seed_garlic_extraction"),
                GTMFOItems.SEED_GARLIC_PURPLE.asStack(3), GTMFOItems.GARLIC_PURPLE.asStack());
        VanillaRecipeHelper.addShapelessRecipe(provider, id("seed_pea_extraction"),
                GTMFOItems.SEED_PEA.asStack(), GTMFOItems.PEA_POD.asStack());

        GTMFORecipeTypes.SLICER_RECIPES.recipeBuilder(id("seed_garlic_from_bulb"))
                .inputItems(GTMFOItems.GARLIC_PURPLE.asStack())
                .notConsumable(GTMFOItems.SLICER_BLADE_OCTAGONAL.asStack())
                .outputItems(GTMFOItems.SEED_GARLIC_PURPLE.asStack(8))
                .EUt(8).duration(40).save(provider);

        GTRecipeTypes.CUTTER_RECIPES.recipeBuilder(id("seed_soy_from_soybean"))
                .inputItems(GTMFOItems.SOYBEAN.asStack())
                .outputItems(GTMFOItems.SEED_SOY.asStack(3))
                .chancedOutput(GTMFOItems.SEED_SOY.asStack(2), 5000, 100)
                .EUt(8).duration(100).save(provider);

        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("seed_pea_from_pod"))
                .inputItems(GTMFOItems.PEA_POD.asStack())
                .outputItems(GTMFOItems.SEED_PEA.asStack(8))
                .EUt(8).duration(20).save(provider);

        GTRecipeTypes.MACERATOR_RECIPES.recipeBuilder(id("black_pepper_ground"))
                .inputItems(GTMFOItems.BLACK_PEPPER.asStack())
                .outputItems(GTMFOItems.BLACK_PEPPER.asStack(2))
                .EUt(8).duration(80).save(provider);
    }

    private static void soybeanOil(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("raw_soybean_oil"))
                .inputItems(GTMFOItems.SEED_SOY.asStack())
                .circuitMeta(1)
                .outputFluids(GTMFOFluids.RawSoybeanOil.getFluid(25))
                .EUt(2).duration(30).save(provider);
        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("raw_soybean_oil_wood"))
                .inputItems(GTMFOItems.SEED_SOY.asStack())
                .circuitMeta(2)
                .outputItems(TagPrefix.dustSmall, GTMaterials.Wood)
                .outputFluids(GTMFOFluids.RawSoybeanOil.getFluid(50))
                .EUt(16).duration(30).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("hydrated_soybean_oil"))
                .inputFluids(GTMFOFluids.RawSoybeanOil.getFluid(1000), GTMaterials.Water.getFluid(500))
                .outputFluids(GTMFOFluids.HydratedSoybeanOil.getFluid(1000))
                .EUt(24).duration(20).save(provider);
        GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("soybean_oil"))
                .inputFluids(GTMFOFluids.HydratedSoybeanOil.getFluid(1000))
                .outputFluids(GTMFOFluids.SoyLecithin.getFluid(50), GTMFOFluids.SoybeanOil.getFluid(1000))
                .EUt(16).duration(60).save(provider);

        GTRecipeTypes.DISTILLATION_RECIPES.recipeBuilder(id("seed_oil_from_raw_soybean"))
                .inputFluids(GTMFOFluids.RawSoybeanOil.getFluid(1000))
                .outputFluids(GTMaterials.SeedOil.getFluid(600))
                .EUt(24).duration(40).save(provider);
        GTRecipeTypes.DISTILLATION_RECIPES.recipeBuilder(id("seed_oil_from_soybean"))
                .inputFluids(GTMFOFluids.SoybeanOil.getFluid(1000))
                .outputFluids(GTMaterials.SeedOil.getFluid(1000))
                .EUt(24).duration(40).save(provider);
        GTRecipeTypes.DISTILLATION_RECIPES.recipeBuilder(id("seed_oil_from_olive"))
                .inputFluids(GTMFOFluids.OliveOil.getFluid(1000))
                .outputFluids(GTMaterials.SeedOil.getFluid(500))
                .EUt(24).duration(40).save(provider);
    }
}
