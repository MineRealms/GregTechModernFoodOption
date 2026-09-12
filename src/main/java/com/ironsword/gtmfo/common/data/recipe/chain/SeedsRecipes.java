package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
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
        seedProcessing(provider);
        seedOilExtraction(provider);
        unknownSeed(provider);
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

    /** Per-crop processing from the original SeedsChain: centrifuge (config), brewing, plant ball compression. */
    private static void seedProcessing(Consumer<FinishedRecipe> provider){
        net.minecraft.world.item.Item[] crops = {
                GTMFOItems.LEMON.get(), GTMFOItems.LIME.get(), GTMFOItems.TOMATO.get(),
                GTMFOItems.CUCUMBER.get(), GTMFOItems.OLIVE.get(), GTMFOItems.ONION.get(),
                GTMFOItems.BANANA.get(), GTMFOItems.ORANGE.get(), GTMFOItems.MANGO.get(),
                GTMFOItems.APRICOT.get(), GTMFOItems.PEA_POD.get(), GTMFOItems.SOYBEAN.get(),
                GTMFOItems.SEED_BEAN.get(), GTMFOItems.COFFEE_CHERRY.get(), GTMFOItems.CORN_COB.get(),
                GTMFOItems.RICE.get(), GTMFOItems.HORSERADISH.get(), GTMFOItems.OREGANO.get(),
                GTMFOItems.GARLIC_PURPLE.get(), GTMFOItems.BASIL.get(), GTMFOItems.EGGPLANT.get(),
                GTMFOItems.ARTICHOKE.get(), GTMFOItems.BLACK_PEPPER.get()
        };
        for (int i = 0; i < crops.length; i++) {
            if (com.ironsword.gtmfo.GTMFOConfigHolder.INSTANCE.gtfoMiscConfig.centrifugeSeeds) {
                GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("seed_methane_" + i))
                        .inputItems(crops[i])
                        .outputFluids(GTMaterials.Methane.getFluid(34))
                        .EUt(5).duration(144).save(provider);
            }
            GTRecipeTypes.BREWING_RECIPES.recipeBuilder(id("seed_biomass_" + i))
                    .inputItems(crops[i])
                    .inputFluids(GTMaterials.Water.getFluid(100))
                    .outputFluids(GTMaterials.Biomass.getFluid(100))
                    .EUt(3).duration(800).save(provider);
            GTRecipeTypes.COMPRESSOR_RECIPES.recipeBuilder(id("seed_plant_ball_" + i))
                    .inputItems(new net.minecraft.world.item.ItemStack(crops[i], 8))
                    .outputItems(GTItems.PLANT_BALL.asStack())
                    .EUt(2).duration(300).save(provider);
        }
    }

    /** Each GTFO seed extracts into a little seed oil (original: circuit 3, 8 mB). */
    private static void seedOilExtraction(Consumer<FinishedRecipe> provider){
        var seeds = java.util.List.of(
                GTMFOItems.SEED_ARTICHOKE, GTMFOItems.SEED_BASIL, GTMFOItems.SEED_BEAN,
                GTMFOItems.SEED_COTTON, GTMFOItems.SEED_CUCUMBER, GTMFOItems.SEED_EGGPLANT,
                GTMFOItems.SEED_GARLIC_PURPLE, GTMFOItems.SEED_GARLIC_WHITE, GTMFOItems.SEED_GRAPE,
                GTMFOItems.SEED_HORSERADISH, GTMFOItems.SEED_ONION, GTMFOItems.SEED_OREGANO,
                GTMFOItems.SEED_PEA, GTMFOItems.SEED_SOY, GTMFOItems.SEED_TOMATO,
                GTMFOItems.SEED_WHITE_GRAPE);
        for (var seed : seeds) {
            GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("seed_oil_" + seed.getId().getPath()))
                    .inputItems(seed.asStack())
                    .circuitMeta(3)
                    .outputFluids(GTMaterials.SeedOil.getFluid(8))
                    .EUt(2).duration(32).save(provider);
        }
    }

    /**
     * Undetermined GTFO seeds can be shaped into specific seeds: the slot the unknown seed occupies in the
     * crafting grid decides the result (original {@code *_ungenerify} recipes).
     */
    private static void unknownSeed(Consumer<FinishedRecipe> provider){
        Object[][] recipes = {
                { "seed_soy_ungenerify", GTMFOItems.SEED_SOY.asStack(), "S  ", "   ", "   " },
                { "seed_tomato_ungenerify", GTMFOItems.SEED_TOMATO.asStack(), " S ", "   ", "   " },
                { "seed_cucumber_ungenerify", GTMFOItems.SEED_CUCUMBER.asStack(), "   ", "S  ", "   " },
                { "seed_onion_ungenerify", GTMFOItems.SEED_ONION.asStack(), "  S", "   ", "   " },
                { "seed_grapes_ungenerify", GTMFOItems.SEED_GRAPE.asStack(), "   ", " S ", "   " },
                { "seed_coffee_ungenerify", GTMFOItems.COFFEE_CHERRY.asStack(), "   ", "   ", "S  " },
                { "seed_pea_ungenerify", GTMFOItems.SEED_PEA.asStack(), "   ", "  S", "   " },
                { "seed_bean_ungenerify", GTMFOItems.SEED_BEAN.asStack(), "   ", "   ", " S " },
                { "seed_oregano_ungenerify", GTMFOItems.SEED_OREGANO.asStack(), "   ", "   ", "  S" },
                { "seed_horseradish_ungenerify", GTMFOItems.SEED_HORSERADISH.asStack(2), "SS ", "   ", "   " },
                { "seed_garlic_ungenerify", GTMFOItems.SEED_GARLIC_PURPLE.asStack(2), "S S", "   ", "   " },
                { "seed_basil_ungenerify", GTMFOItems.SEED_BASIL.asStack(2), " SS", "   ", "   " },
                { "seed_eggplant_ungenerify", GTMFOItems.SEED_EGGPLANT.asStack(2), "S  ", "S  ", "   " },
                { "seed_corn_ungenerify", GTMFOItems.CORN_COB.asStack(2), " S ", "S  ", "   " },
                { "seed_artichoke_ungenerify", GTMFOItems.SEED_ARTICHOKE.asStack(2), "  S", "S  ", "   " },
                { "seed_black_pepper_ungenerify", GTMFOItems.BLACK_PEPPER.asStack(2), "S  ", " S ", "   " },
                { "seed_rice_ungenerify", GTMFOItems.RICE.asStack(2), " S ", " S ", "   " },
                { "seed_white_grapes_ungenerify", GTMFOItems.SEED_WHITE_GRAPE.asStack(2), "  S", " S ", "   " },
                { "seed_cotton_ungenerify", GTMFOItems.SEED_COTTON.asStack(2), "   ", "   ", " SS" },
        };
        for (Object[] r : recipes) {
            VanillaRecipeHelper.addShapedRecipe(provider, id((String) r[0]),
                    (net.minecraft.world.item.ItemStack) r[1],
                    (String) r[2], (String) r[3], (String) r[4],
                    'S', GTMFOItems.SEED_UNKNOWN.asStack());
        }
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
