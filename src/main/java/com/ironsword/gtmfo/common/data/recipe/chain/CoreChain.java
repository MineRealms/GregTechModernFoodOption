package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.material.GTMFOMaterials;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class CoreChain {
    public static void init(Consumer<FinishedRecipe> provider){
        zestChain(provider);
        caneSyrupChain(provider);
        generalChemicals(provider);
        sliceBlades(provider);
        liquidFoodExtracts(provider);
        slicingRecipes(provider);
        drink(provider);
        chum(provider);
        misc(provider);
    }

    private static void chum(Consumer<FinishedRecipe> provider){
        // Mixer: sludge + rotten fish/meat + red mushroom + poisonous potato + fermented spider eye -> chum
        for (int i = 0; i < 2; i++) {
            boolean fish = i == 0;
            String suffix = fish ? "fish" : "meat";
            var rotten = fish ? GTMFOItems.ROTTEN_FISH : GTMFOItems.ROTTEN_MEAT;

            GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("chum_" + suffix))
                    .inputFluids(GTMFOFluids.Sludge.getFluid(100))
                    .inputItems(rotten)
                    .inputItems(Ingredient.of(Items.RED_MUSHROOM), Ingredient.of(Items.POISONOUS_POTATO), Ingredient.of(Items.FERMENTED_SPIDER_EYE))
                    .outputItems(GTMFOItems.CHUM.asStack(3))
                    .duration(100).EUt(24).save(provider);

            GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("chum_" + suffix + "_purple"))
                    .inputFluids(GTMFOFluids.Sludge.getFluid(100), GTMFOFluids.PurpleDrink.getFluid(100))
                    .inputItems(rotten)
                    .inputItems(Ingredient.of(Items.RED_MUSHROOM), Ingredient.of(Items.POISONOUS_POTATO), Ingredient.of(Items.FERMENTED_SPIDER_EYE))
                    .outputItems(GTMFOItems.CHUM.asStack(6))
                    .duration(100).EUt(24).save(provider);
        }

        // Fermenting: fish -> rotten fish
        String[] fishNames = { "cod", "salmon", "tropical_fish" };
        Item[] fishes = { Items.COD, Items.SALMON, Items.TROPICAL_FISH };
        for (int i = 0; i < fishes.length; i++) {
            GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("rotten_fish_" + fishNames[i]))
                    .inputItems(fishes[i])
                    .inputFluids(GTMaterials.Water.getFluid(100))
                    .outputItems(GTMFOItems.ROTTEN_FISH)
                    .outputFluids(GTMaterials.Water.getFluid(100))
                    .duration(100).EUt(8).save(provider);
        }

        // Fermenting: meat (+ rotten flesh / spider eye) -> rotten meat
        String[] meatNames = { "beef", "chicken", "mutton", "porkchop", "rabbit", "rotten_flesh", "spider_eye" };
        Item[] meats = { Items.BEEF, Items.CHICKEN, Items.MUTTON, Items.PORKCHOP, Items.RABBIT, Items.ROTTEN_FLESH,
                Items.SPIDER_EYE };
        for (int i = 0; i < meats.length; i++) {
            GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("rotten_meat_" + meatNames[i]))
                    .inputItems(meats[i])
                    .inputFluids(GTMaterials.Water.getFluid(100))
                    .outputItems(GTMFOItems.ROTTEN_MEAT)
                    .outputFluids(GTMaterials.Water.getFluid(100))
                    .duration(100).EUt(8).save(provider);
        }

        // Mixer: animal products -> sludge
        Item[] animalProducts = { Items.BEEF, Items.CHICKEN, Items.MUTTON, Items.PORKCHOP, Items.RABBIT,
                Items.COD, Items.SALMON, Items.TROPICAL_FISH };
        for (int i = 0; i < animalProducts.length; i++) {
            GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("sludge_water_" + i))
                    .inputItems(animalProducts[i])
                    .inputFluids(GTMaterials.Water.getFluid(400))
                    .outputFluids(GTMFOFluids.Sludge.getFluid(100))
                    .duration(500).EUt(16).save(provider);
            GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("sludge_sulfuric_" + i))
                    .inputItems(animalProducts[i])
                    .inputFluids(GTMaterials.SulfuricAcid.getFluid(200))
                    .outputFluids(GTMFOFluids.Sludge.getFluid(200))
                    .duration(250).EUt(16).save(provider);
        }

        // Chum on a stick
        VanillaRecipeHelper.addShapelessRecipe(provider, id("chum_stick_by_hand"),
                GTMFOItems.CHUM_STICK.asStack(), GTMFOItems.CHUM.asStack(), new ItemStack(Items.STICK));
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(id("chum_stick"))
                .inputItems(GTMFOItems.CHUM)
                .inputItems(Items.STICK)
                .outputItems(GTMFOItems.CHUM_STICK)
                .EUt(4).duration(5).save(provider);
    }

    public static void zestChain(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("lemon_zest"))
                .inputItems(GTMFOItems.LEMON.asStack())
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Zest))
                .outputFluids(GTMFOFluids.LemonExtract.getFluid(100))
                .EUt(5).duration(100).save(provider);
        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("lime_zest"))
                .inputItems(GTMFOItems.LIME.asStack())
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Zest))
                .outputFluids(GTMFOFluids.LIME_EXTRACT.getFluid(100))
                .EUt(5).duration(100).save(provider);
        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("orange_zest"))
                .inputItems(GTMFOItems.ORANGE.asStack())
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Zest))
                .outputFluids(GTMFOFluids.ORANGE_EXTRACT.getFluid(100))
                .EUt(5).duration(100).save(provider);

        VanillaRecipeHelper.addShapelessRecipe(provider, id("zest_from_lemon"),
                ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Zest),
                GTMFOItems.LEMON.asStack(), GTMFOItems.LEMON.asStack(),
                GTMFOItems.LEMON.asStack(), GTMFOItems.LEMON.asStack(),
                CustomTags.CRAFTING_MORTARS);
        VanillaRecipeHelper.addShapelessRecipe(provider, id("zest_from_lime"),
                ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Zest),
                GTMFOItems.LIME.asStack(), GTMFOItems.LIME.asStack(),
                GTMFOItems.LIME.asStack(), GTMFOItems.LIME.asStack(),
                CustomTags.CRAFTING_MORTARS);
        VanillaRecipeHelper.addShapelessRecipe(provider, id("zest_from_orange"),
                ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Zest),
                GTMFOItems.ORANGE.asStack(), GTMFOItems.ORANGE.asStack(),
                GTMFOItems.ORANGE.asStack(), GTMFOItems.ORANGE.asStack(),
                CustomTags.CRAFTING_MORTARS);
    }

    public static void caneSyrupChain(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder("cane_syrup_unheated")
                .inputItems(Items.SUGAR,24)
                .inputFluids(GTMaterials.Water.getFluid(2000))
                .circuitMeta(2)
                .outputFluids(GTMFOFluids.CaneSyrupUnheated.getFluid(2000))
                .EUt(80)
                .duration(260)
                .save(provider);

        GTRecipeTypes.FLUID_HEATER_RECIPES.recipeBuilder("cane_syrup")
                .inputFluids(GTMFOFluids.CaneSyrupUnheated.getFluid(1000))
                .circuitMeta(2)
                .outputFluids(GTMFOFluids.CaneSyrup.getFluid(1000))
                .EUt(120)
                .duration(80)
                .save(provider);
    }

    public static void generalChemicals(Consumer<FinishedRecipe> provider){
//        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder("isopropyl_chloride")
//                .inputFluids(
//                        GTMaterials.HydrochloricAcid.getFluid(1000),
//                        GTMaterials.Propene.getFluid(1000))
//                .outputFluids(GTMFOFluids.ISOPROPYL_CHLORIDE.getFluid(1000))
//                .EUt(30)
//                .duration(200)
//                .save(provider);
//
//        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder("chloroauric_acid")
//                .inputItems(ChemicalHelper.get(TagPrefix.dust,GTMaterials.Gold,2))
//                .inputFluids(GTMaterials.HydrochloricAcid.getFluid(8000))
//                .outputFluids(GTMFOFluids.CHLOROAURIC_ACID.getFluid(2000),GTMaterials.Hydrogen.getFluid(3000))
//                .EUt(480)
//                .duration(300)
//                .save(provider);
    }

    public static void sliceBlades(Consumer<FinishedRecipe> provider){
        VanillaRecipeHelper.addShapedRecipe(provider, id("slicer_flat"),
                GTMFOItems.SLICER_BLADE_FLAT.asStack(),
                "hPS", " M ", "fPs",
                'P', ChemicalHelper.get(TagPrefix.plate,GTMaterials.Iron),
                'S', ChemicalHelper.get(TagPrefix.screw,GTMaterials.Iron),
                'M', GTItems.SHAPE_EXTRUDER_BLOCK);
        VanillaRecipeHelper.addShapedRecipe(provider,id("slicer_stripes"),
                GTMFOItems.SLICER_BLADE_STRIPES.asStack(),
                "hPS", "PMP", "fPs",
                'P', ChemicalHelper.get(TagPrefix.plate,GTMaterials.Iron),
                'S', ChemicalHelper.get(TagPrefix.screw,GTMaterials.Iron),
                'M', GTItems.SHAPE_EXTRUDER_BLOCK);
        VanillaRecipeHelper.addShapedRecipe(provider,id("slicer_octagonal"),
                GTMFOItems.SLICER_BLADE_OCTAGONAL.asStack(),
                "PhP", "fMS", "PsP",
                'P', ChemicalHelper.get(TagPrefix.plate,GTMaterials.Iron),
                'S', ChemicalHelper.get(TagPrefix.screw,GTMaterials.Iron),
                'M', GTItems.SHAPE_EXTRUDER_BLOCK);
    }

    public static void liquidFoodExtracts(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.FLUID_HEATER_RECIPES.recipeBuilder(id("frying_oil"))
                .inputFluids(GTMaterials.SeedOil.getFluid(16))
                .outputFluids(GTMFOFluids.FryingOil.getFluid(16))
                .circuitMeta(1)
                .EUt(12).duration(10).save(provider);
        GTRecipeTypes.FLUID_HEATER_RECIPES.recipeBuilder(id("frying_oil_hot_2"))
                .inputFluids(GTMaterials.SeedOil.getFluid(16))
                .outputFluids(GTMFOFluids.FryingOilHot.getFluid(16))
                .circuitMeta(2)
                .EUt(60).duration(25).save(provider);
        GTRecipeTypes.FLUID_HEATER_RECIPES.recipeBuilder(id("frying_oil_hot_1"))
                .inputFluids(GTMFOFluids.FryingOil.getFluid(16))
                .outputFluids(GTMFOFluids.FryingOilHot.getFluid(16))
                .circuitMeta(1)
                .EUt(18).duration(15).save(provider);

        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("tomato_sauce"))
                .inputItems(GTMFOItems.TOMATO_SLICE.asStack())
                .outputFluids(GTMFOFluids.TomatoSauce.getFluid(100))
                .EUt(2).duration(10).save(provider);
//
        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("olive_oil"))
                .inputItems(GTMFOItems.OLIVE.asStack())
                .outputFluids(GTMFOFluids.OliveOil.getFluid(100))
                .EUt(27).duration(60).save(provider);

        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("melon_extract"))
                .inputItems(Items.MELON_SLICE)
                .outputFluids(GTMFOFluids.MELON_EXTRACT.getFluid(100))
                .EUt(2).duration(10).save(provider);

        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("cranberry_extract"))
                .inputItems(GTMFOItems.CRANBERRY.asStack())
                .circuitMeta(1)
                .outputFluids(GTMFOFluids.CranberryExtract.getFluid(25))
                .EUt(2).duration(10).save(provider);

        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("grape_extract"))
                .inputItems(GTMFOItems.GRAPES.asStack())
                .circuitMeta(1)
                .outputFluids(GTMFOFluids.GRAPE_EXTRACT.getFluid(25))
                .EUt(2).duration(10).save(provider);

        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("grape_extract_white"))
                .inputItems(GTMFOItems.WHITE_GRAPES.asStack())
                .circuitMeta(1)
                .outputFluids(GTMFOFluids.GRAPE_EXTRACT.getFluid(25))
                .EUt(2).duration(10).save(provider);

        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("apricot_extract"))
                .inputItems(GTMFOItems.APRICOT.asStack())
                .circuitMeta(1)
                .outputFluids(GTMFOFluids.ApricotExtract.getFluid(100))
                .EUt(8).duration(40).save(provider);

        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("apple_extract_from_juice"))
                .inputItems(GTMFOItems.JUICE_APPLE.asStack())
                .outputItems(Items.GLASS_BOTTLE)
                .outputFluids(GTMFOFluids.AppleExtract.getFluid(100))
                .EUt(12).duration(30).save(provider);

        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("orange_extract_from_juice"))
                .inputItems(GTMFOItems.JUICE_ORANGE.asStack())
                .outputItems(Items.GLASS_BOTTLE)
                .outputFluids(GTMFOFluids.ORANGE_EXTRACT.getFluid(100))
                .EUt(12).duration(30).save(provider);

        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("apple_juice_bottling"))
                .inputItems(Items.GLASS_BOTTLE)
                .inputFluids(GTMFOFluids.AppleExtract.getFluid(100))
                .outputItems(GTMFOItems.JUICE_APPLE.asStack())
                .EUt(12).duration(30).save(provider);

        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("orange_juice_bottling"))
                .inputItems(Items.GLASS_BOTTLE)
                .inputFluids(GTMFOFluids.ORANGE_EXTRACT.getFluid(100))
                .outputItems(GTMFOItems.JUICE_ORANGE.asStack())
                .EUt(12).duration(30).save(provider);

        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("apple_cider_fermenting"))
                .inputFluids(GTMFOFluids.AppleExtract.getFluid(100))
                .outputFluids(GTMFOFluids.AppleCider.getFluid(100))
                .EUt(2).duration(150).save(provider);

        GTRecipeTypes.DISTILLATION_RECIPES.recipeBuilder(id("orange_extract_distillation"))
                .inputFluids(GTMFOFluids.ORANGE_EXTRACT.getFluid(1000))
                .outputFluids(GTMaterials.Biomass.getFluid(300), GTMFOFluids.CitricAcid.getFluid(30),
                        GTMaterials.Water.getFluid(700))
                .outputItems(GTItems.PLANT_BALL.asStack())
                .EUt(120).duration(40).save(provider);

        GTRecipeTypes.DISTILLATION_RECIPES.recipeBuilder(id("lime_extract_distillation"))
                .inputFluids(GTMFOFluids.LIME_EXTRACT.getFluid(1000))
                .outputFluids(GTMaterials.Biomass.getFluid(300), GTMFOFluids.CitricAcid.getFluid(100),
                        GTMaterials.Water.getFluid(600))
                .outputItems(GTItems.PLANT_BALL.asStack())
                .EUt(120).duration(40).save(provider);

        GTRecipeTypes.DISTILLATION_RECIPES.recipeBuilder(id("lemon_extract_distillation"))
                .inputFluids(GTMFOFluids.LemonExtract.getFluid(1000))
                .outputFluids(GTMaterials.Biomass.getFluid(300), GTMFOFluids.CitricAcid.getFluid(100),
                        GTMaterials.Water.getFluid(600))
                .outputItems(GTItems.PLANT_BALL.asStack())
                .EUt(120).duration(40).save(provider);

        GTRecipeTypes.DISTILLATION_RECIPES.recipeBuilder(id("apple_extract_distillation"))
                .inputFluids(GTMFOFluids.AppleExtract.getFluid(1000))
                .outputFluids(GTMaterials.Biomass.getFluid(200), GTMaterials.AceticAcid.getFluid(10),
                        GTMaterials.Water.getFluid(1000), GTMaterials.HydrogenCyanide.getFluid(10))
                .outputItems(TagPrefix.dust, GTMaterials.Sugar)
                .EUt(120).duration(40).save(provider);

        GTRecipeTypes.DISTILLATION_RECIPES.recipeBuilder(id("apple_cider_distillation"))
                .inputFluids(GTMFOFluids.AppleCider.getFluid(1000))
                .outputFluids(GTMaterials.AceticAcid.getFluid(80), GTMaterials.Ethanol.getFluid(210),
                        GTMaterials.Water.getFluid(400), GTMaterials.Methanol.getFluid(100),
                        GTMaterials.CarbonDioxide.getFluid(400), GTMaterials.Methane.getFluid(500))
                .outputItems(GTItems.PLANT_BALL.asStack())
                .EUt(120).duration(40).save(provider);

        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("sodium_cyanide"))
                .inputFluids(GTMaterials.HydrogenCyanide.getFluid(1000))
                .inputItems(TagPrefix.dust, GTMaterials.SodiumHydroxide, 3)
                .outputItems(TagPrefix.dust, GTMFOMaterials.SodiumCyanide, 3)
                .outputFluids(GTMaterials.Water.getFluid(1000))
                .EUt(30).duration(80).save(provider);


//
//        GTRecipeTypes.CANNER_RECIPES.recipeBuilder("orange_extract")
//                .inputItems(GTMFOItems.JUICE_ORANGE.asStack())
//                .outputItems(Items.GLASS_BOTTLE)
//                .outputFluids(GTMFOFluids.ORANGE_EXTRACT.getFluid(100))
//                .EUt(12).duration(30).save(provider);
//
//        GTRecipeTypes.CANNER_RECIPES.recipeBuilder("orange_juice")
//                .inputItems(Items.GLASS_BOTTLE)
//                .inputFluids(GTMFOFluids.ORANGE_EXTRACT.getFluid(100))
//                .outputItems(GTMFOItems.JUICE_ORANGE.asStack())
//                .EUt(12).duration(30).save(provider);

        //this is a temporary recipe
//        GTRecipeTypes.DISTILLATION_RECIPES.recipeBuilder("apple_extract")
//                .inputFluids(GTMFOFluids.APPLE_EXTRACT.getFluid(1000))
//                .outputFluids(GTMaterials.Biomass.getFluid(200))
//                .outputFluids(GTMaterials.AceticAcid.getFluid(10))
//                .outputFluids(GTMaterials.Water.getFluid(1000))
//                .outputFluids(GTMaterials.HydrogenCyanide.getFluid(10))
//                .outputItems(TagPrefix.dust,GTMaterials.Sugar)
//                .save(provider);
//
//        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder("sodium_cyanide")
//                .inputFluids(GTMaterials.HydrogenCyanide.getFluid(1000))
//                .inputItems(TagPrefix.dust,GTMaterials.SodiumHydroxide,3)
//                .outputItems(TagPrefix.dust,GTMFOMaterials.SodiumCyanide,3)
//                .outputFluids(GTMaterials.Water.getFluid(1000))
//                .EUt(30).duration(80).save(provider);
    }

    private static void slicingRecipes(Consumer<FinishedRecipe> provider){


        GTMFORecipeTypes.SLICER_RECIPES.recipeBuilder(id("olive_slice"))
                .inputItems(GTMFOItems.OLIVE)
                .notConsumable(GTMFOItems.SLICER_BLADE_FLAT)
                .outputItems(GTMFOItems.OLIVE_SLICE,8)
                .EUt(18).duration(30).save(provider);

        GTMFORecipeTypes.SLICER_RECIPES.recipeBuilder(id("mushroom_slice"))
                .inputItems(Items.BROWN_MUSHROOM)
                .notConsumable(GTMFOItems.SLICER_BLADE_FLAT)
                .outputItems(GTMFOItems.MUSHROOM_SLICE,8)
                .EUt(18).duration(30).save(provider);

        GTMFORecipeTypes.SLICER_RECIPES.recipeBuilder(id("tomato_slice"))
                .inputItems(GTMFOItems.TOMATO)
                .notConsumable(GTMFOItems.SLICER_BLADE_FLAT)
                .outputItems(GTMFOItems.TOMATO_SLICE,8)
                .EUt(18).duration(30).save(provider);

        // bacon (original CoreChain.bakingOvenRecipes)
        VanillaRecipeHelper.addShapelessRecipe(provider, id("bacon_by_hand"),
                GTMFOItems.BACON_RAW.asStack(3), Items.PORKCHOP, CustomTags.CRAFTING_KNIVES);
        GTMFORecipeTypes.SLICER_RECIPES.recipeBuilder(id("bacon_raw"))
                .inputItems(Items.PORKCHOP)
                .notConsumable(GTMFOItems.SLICER_BLADE_FLAT)
                .outputItems(GTMFOItems.BACON_RAW.asStack(6))
                .EUt(18).duration(30).save(provider);
        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("bacon"))
                .inputItems(GTMFOItems.BACON_RAW.asStack())
                .outputItems(GTMFOItems.BACON.asStack())
                .EUt(60).duration(500).save(provider);
    }

    private static void drink(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("carbonated_water"))
                .inputFluids(
                        GTMaterials.Water.getFluid(1000),
                        GTMaterials.CarbonDioxide.getFluid(100))
                .outputFluids(GTMFOFluids.CarbonatedWater.getFluid(1050))
                .EUt(120).duration(60).save(provider);
    }

    private static void misc(Consumer<FinishedRecipe> provider) {
        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("egg"))
                .inputItems(Items.EGG.getDefaultInstance())
                .outputFluids(GTMFOFluids.Egg.getFluid(200))
                .EUt(24).duration(60).save(provider);

        GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("egg_separation"))
                .inputFluids(GTMFOFluids.Egg.getFluid(200))
                .outputFluids(GTMFOFluids.Albumen.getFluid(100))
                .outputFluids(GTMFOFluids.Yolk.getFluid(100))
                .EUt(16).duration(60).save(provider);

        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("butter"))
                .inputFluids(GTMaterials.Milk.getFluid(10000))
                .outputFluids(GTMFOFluids.Butter.getFluid(9000))
                .EUt(15).duration(1200).save(provider);

        GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("egg_separation_item"))
                .inputItems(Items.EGG)
                .outputFluids(GTMFOFluids.Albumen.getFluid(100), GTMFOFluids.Yolk.getFluid(100))
                .EUt(45).duration(60).save(provider);

        GTRecipeTypes.MACERATOR_RECIPES.recipeBuilder(id("mashed_potato"))
                .inputItems(Items.POTATO)
                .outputItems(GTMFOItems.POTATO_MASHED.asStack())
                .EUt(4).duration(40).save(provider);

        net.minecraft.world.item.Item[] minceMeats = { Items.BEEF, Items.CHICKEN, Items.MUTTON, Items.PORKCHOP,
                Items.RABBIT };
        String[] minceNames = { "beef", "chicken", "mutton", "porkchop", "rabbit" };
        for (int i = 0; i < minceMeats.length; i++) {
            GTRecipeTypes.MACERATOR_RECIPES.recipeBuilder(id("mince_meat_" + minceNames[i]))
                    .inputItems(minceMeats[i])
                    .outputItems(GTMFOItems.MINCE_MEAT.asStack())
                    .EUt(8).duration(80).save(provider);
        }

        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("mince_meat_cooked"))
                .inputItems(GTMFOItems.MINCE_MEAT.asStack())
                .outputItems(GTMFOItems.MINCE_MEAT_COOKED.asStack())
                .EUt(30).duration(200).save(provider);

        GTRecipeTypes.EXTRUDER_RECIPES.recipeBuilder(id("meat_ingot"))
                .inputItems(TagPrefix.dust, GTMaterials.Meat)
                .notConsumable(GTItems.SHAPE_EXTRUDER_INGOT.asStack())
                .outputItems(GTMFOItems.MEAT_INGOT.asStack())
                .EUt(28).duration(20).save(provider);

        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("meat_ingot_cooked"))
                .inputItems(GTMFOItems.MEAT_INGOT.asStack())
                .outputItems(GTMFOItems.MEAT_INGOT_COOKED.asStack())
                .EUt(30).duration(200).save(provider);

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(id("paper_bag"))
                .inputItems(Items.PAPER, 3)
                .circuitMeta(2)
                .outputItems(GTMFOItems.PAPER_BAG.asStack())
                .EUt(80).duration(30).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("fertilizer_solution"))
                .inputItems(GTItems.FERTILIZER.asStack())
                .inputFluids(GTMaterials.Water.getFluid(10000))
                .outputFluids(GTMFOFluids.FertilizerSolution.getFluid(10000))
                .EUt(16).duration(100).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("fertilizer_solution_bone_meal"))
                .inputItems(Items.BONE_MEAL)
                .inputFluids(GTMaterials.Water.getFluid(5000))
                .outputFluids(GTMFOFluids.FertilizerSolution.getFluid(5000))
                .EUt(16).duration(100).save(provider);

        GTRecipeTypes.FORMING_PRESS_RECIPES.recipeBuilder(id("emergency_rations"))
                .inputItems(GTMFOItems.SEED_BEAN.asStack(), GTMFOItems.MEAT_INGOT.asStack())
                .inputItems(ChemicalHelper.get(TagPrefix.plate, GTMaterials.Polyethylene, 2))
                .outputItems(GTMFOItems.EMERGENCY_RATIONS.asStack())
                .EUt(64).duration(200).save(provider);

        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("purple_drink_from_bottle"))
                .inputItems(GTItems.BOTTLE_PURPLE_DRINK.asStack())
                .outputItems(Items.GLASS_BOTTLE)
                .outputFluids(GTMFOFluids.PurpleDrink.getFluid(500))
                .EUt(30).duration(20).save(provider);
    }














}
