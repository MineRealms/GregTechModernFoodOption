package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.material.GTMFOMaterials;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class ItalianRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        pastaRecipes(provider);
        italianRecipes(provider);
    }

    private static void pastaRecipes(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("pasta_dough_premixed"))
                .inputItems(TagPrefix.dust, GTMaterials.Wheat)
                .inputFluids(GTMaterials.Water.getFluid(16))
                .circuitMeta(4)
                .outputItems(GTMFOItems.PASTA_DOUGH_PREMIXED.asStack())
                .EUt(120).duration(30).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("pasta_dough_egg"))
                .inputItems(GTMFOItems.PASTA_DOUGH_PREMIXED.asStack())
                .inputFluids(GTMFOFluids.Egg.getFluid(400))
                .inputFluids(GTMaterials.Air.getFluid(600))
                .circuitMeta(1)
                .outputItems(GTMFOItems.PASTA_DOUGH_EGG.asStack(2))
                .EUt(120).duration(600).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("pasta_dough"))
                .inputItems(GTMFOItems.PASTA_DOUGH_PREMIXED.asStack())
                .inputFluids(GTMaterials.Air.getFluid(600))
                .circuitMeta(2)
                .outputItems(GTMFOItems.PASTA_DOUGH.asStack())
                .EUt(30).duration(600).save(provider);

        if (!com.ironsword.gtmfo.GTMFOConfigHolder.INSTANCE.gtfoChainsConfig.makeChainsHarder) {
            GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("pasta_dough_egg_easy"))
                    .inputItems(TagPrefix.dust, GTMaterials.Wheat, 2)
                    .inputFluids(GTMFOFluids.Egg.getFluid(400), GTMaterials.Air.getFluid(600))
                    .circuitMeta(2)
                    .outputItems(GTMFOItems.PASTA_DOUGH_EGG.asStack())
                    .EUt(30).duration(1200).save(provider);
        }
        GTRecipeTypes.BENDER_RECIPES.recipeBuilder(id("shape_pasta_blank"))
                .inputItems(TagPrefix.plate, GTMaterials.Bronze, 4)
                .circuitMeta(4)
                .outputItems(GTMFOItems.SHAPE_PASTA_BLANK.asStack())
                .EUt(24).duration(200).save(provider);

        VanillaRecipeHelper.addShapedRecipe(provider,id("shape_pasta_tagliatelle"),
                GTMFOItems.SHAPE_PASTA_TAGLIATELLE.asStack(),
                "f  ",
                " S ",
                "   ",
                'S', GTMFOItems.SHAPE_PASTA_BLANK.asStack());
        VanillaRecipeHelper.addShapedRecipe(provider,id("shape_pasta_spaghetti"),
                GTMFOItems.SHAPE_PASTA_SPAGHETTI.asStack(),
                " f ",
                " S ",
                "   ",
                'S', GTMFOItems.SHAPE_PASTA_BLANK.asStack());
        VanillaRecipeHelper.addShapedRecipe(provider,id("shape_pasta_lasagna"),
                GTMFOItems.SHAPE_PASTA_LASAGNA.asStack(),
                "  f",
                " S ",
                "   ",
                'S', GTMFOItems.SHAPE_PASTA_BLANK.asStack());
        VanillaRecipeHelper.addShapedRecipe(provider,id("shape_pasta_rigatoni"),
                GTMFOItems.SHAPE_PASTA_RIGATONI.asStack(),
                "   ",
                "fS ",
                "   ",
                'S', GTMFOItems.SHAPE_PASTA_BLANK.asStack());
        VanillaRecipeHelper.addShapedRecipe(provider,id("shape_pasta_ditalini"),
                GTMFOItems.SHAPE_PASTA_DITALINI.asStack(),
                "   ",
                " Sf",
                "   ",
                'S', GTMFOItems.SHAPE_PASTA_BLANK.asStack());

        GTRecipeTypes.EXTRUDER_RECIPES.recipeBuilder(id("tagliatelle_raw"))
                .inputItems(GTMFOItems.PASTA_DOUGH_EGG.asStack())
                .notConsumable(GTMFOItems.SHAPE_PASTA_TAGLIATELLE.asStack())
                .outputItems(GTMFOItems.TAGLIATELLE_RAW.asStack())
                .EUt(16).duration(300).save(provider);
        GTRecipeTypes.EXTRUDER_RECIPES.recipeBuilder(id("spaghetti_raw"))
                .inputItems(GTMFOItems.PASTA_DOUGH.asStack())
                .notConsumable(GTMFOItems.SHAPE_PASTA_SPAGHETTI.asStack())
                .outputItems(GTMFOItems.SPAGHETTI_RAW.asStack())
                .EUt(16).duration(300).save(provider);
        GTRecipeTypes.EXTRUDER_RECIPES.recipeBuilder(id("lasagna_raw"))
                .inputItems(GTMFOItems.PASTA_DOUGH.asStack())
                .notConsumable(GTMFOItems.SHAPE_PASTA_LASAGNA.asStack())
                .outputItems(GTMFOItems.LASAGNA_RAW.asStack())
                .EUt(16).duration(300).save(provider);
        GTRecipeTypes.EXTRUDER_RECIPES.recipeBuilder(id("rigatoni_raw"))
                .inputItems(GTMFOItems.PASTA_DOUGH.asStack())
                .notConsumable(GTMFOItems.SHAPE_PASTA_RIGATONI.asStack())
                .outputItems(GTMFOItems.RIGATONI_RAW.asStack())
                .EUt(16).duration(300).save(provider);
        GTRecipeTypes.EXTRUDER_RECIPES.recipeBuilder(id("ditalini_raw"))
                .inputItems(GTMFOItems.PASTA_DOUGH.asStack())
                .notConsumable(GTMFOItems.SHAPE_PASTA_DITALINI.asStack())
                .outputItems(GTMFOItems.DITALINI_RAW.asStack())
                .EUt(16).duration(300).save(provider);

        VanillaRecipeHelper.addSmeltingRecipe(provider,id("tagliatelle_dried"),
                GTMFOItems.TAGLIATELLE_RAW.asStack(),
                GTMFOItems.TAGLIATELLE_DRIED.asStack(),
                0.1f);
        VanillaRecipeHelper.addSmeltingRecipe(provider,id("spaghetti_dried"),
                GTMFOItems.SPAGHETTI_RAW.asStack(),
                GTMFOItems.SPAGHETTI_DRIED.asStack(),
                0.1f);
        VanillaRecipeHelper.addSmeltingRecipe(provider,id("lasagna_dried"),
                GTMFOItems.LASAGNA_RAW.asStack(),
                GTMFOItems.LASAGNA_DRIED.asStack(),
                0.1f);
        VanillaRecipeHelper.addSmeltingRecipe(provider,id("rigatoni_dried"),
                GTMFOItems.RIGATONI_RAW.asStack(),
                GTMFOItems.RIGATONI_DRIED.asStack(),
                0.1f);
        VanillaRecipeHelper.addSmeltingRecipe(provider,id("ditalini_dried"),
                GTMFOItems.DITALINI_RAW.asStack(),
                GTMFOItems.DITALINI_DRIED.asStack(),
                0.1f);

        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("tagliatelle"))
                .inputItems(GTMFOItems.TAGLIATELLE_DRIED.asStack())
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .outputItems(GTMFOItems.TAGLIATELLE.asStack())
                .EUt(16).duration(600).save(provider);
        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("spaghetti"))
                .inputItems(GTMFOItems.SPAGHETTI_DRIED.asStack())
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .outputItems(GTMFOItems.SPAGHETTI.asStack())
                .EUt(16).duration(600).save(provider);
        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("rigatoni"))
                .inputItems(GTMFOItems.RIGATONI_DRIED.asStack())
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .outputItems(GTMFOItems.RIGATONI.asStack())
                .EUt(16).duration(600).save(provider);
        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("ditalini"))
                .inputItems(GTMFOItems.DITALINI_DRIED.asStack())
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .outputItems(GTMFOItems.DITALINI.asStack())
                .EUt(16).duration(600).save(provider);

        if (!com.ironsword.gtmfo.GTMFOConfigHolder.INSTANCE.gtfoChainsConfig.makeChainsHarder) {
            GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("tagliatelle_easy"))
                    .inputItems(GTMFOItems.TAGLIATELLE_RAW.asStack())
                    .inputFluids(GTMFOFluids.HeatedWater.getFluid(1000))
                    .outputItems(GTMFOItems.TAGLIATELLE.asStack())
                    .EUt(24).duration(1500).save(provider);
            GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("spaghetti_easy"))
                    .inputItems(GTMFOItems.SPAGHETTI_RAW.asStack())
                    .inputFluids(GTMFOFluids.HeatedWater.getFluid(1000))
                    .outputItems(GTMFOItems.SPAGHETTI.asStack())
                    .EUt(24).duration(1500).save(provider);
            GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("rigatoni_easy"))
                    .inputItems(GTMFOItems.RIGATONI_RAW.asStack())
                    .inputFluids(GTMFOFluids.HeatedWater.getFluid(1000))
                    .outputItems(GTMFOItems.RIGATONI.asStack())
                    .EUt(24).duration(1500).save(provider);
            GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("ditalini_easy"))
                    .inputItems(GTMFOItems.DITALINI_RAW.asStack())
                    .inputFluids(GTMFOFluids.HeatedWater.getFluid(1000))
                    .outputItems(GTMFOItems.DITALINI.asStack())
                    .EUt(24).duration(1500).save(provider);
        }
    }

    private static void italianRecipes(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("pesto"))
                .inputItems(
                        ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.ShreddedParmesan),
                        GTMFOItems.BASIL.asStack(3),
                        GTMFOItems.SEED_GARLIC_PURPLE.asStack())
                .inputItems(TagPrefix.dustTiny, GTMaterials.Salt)
                .inputFluids(GTMFOFluids.OliveOil.getFluid(500))
                .outputFluids(GTMFOFluids.Pesto.getFluid(500))
                .EUt(24).duration(100).save(provider);
        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("bechamel_sauce"))
                .inputItems(GTMFOItems.NUTMEG.asStack())
                .inputItems(TagPrefix.dust, GTMaterials.Wheat)
                .inputFluids(GTMFOFluids.Butter.getFluid(50), GTMaterials.Milk.getFluid(450))
                .outputFluids(GTMFOFluids.BechamelSauce.getFluid(500))
                .EUt(24).duration(100).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("agrodolce"))
                .inputItems(TagPrefix.dust, GTMaterials.Sugar)
                .inputFluids(GTMaterials.AceticAcid.getFluid(1000))
                .outputFluids(GTMFOFluids.Agrodolce.getFluid(1000))
                .EUt(8).duration(300).save(provider);

        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("polenta"))
                .inputItems(GTMFOItems.CORN_KERNEL.asStack(10))
                .inputFluids(GTMFOFluids.HeatedWater.getFluid(250))
                .outputFluids(GTMFOFluids.Polenta.getFluid(250))
                .EUt(16).duration(400).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("polenta_bowl"))
                .inputItems(GTMFOItems.CERAMIC_BOWL.asStack())
                .inputFluids(GTMFOFluids.Polenta.getFluid(250))
                .outputItems(GTMFOItems.POLENTA.asStack())
                .EUt(16).duration(40).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("caponata"))
                .circuitMeta(1)
                .inputItems(GTMFOItems.CERAMIC_BOWL.asStack(), GTMFOItems.CARROT_SLICE.asStack(3),
                        GTMFOItems.OLIVE_SLICE.asStack(3), GTMFOItems.EGGPLANT_SLICE.asStack(3))
                .inputFluids(GTMFOFluids.Agrodolce.getFluid(250), GTMFOFluids.OliveOil.getFluid(250))
                .outputItems(GTMFOItems.CAPONATA.asStack())
                .EUt(24).duration(300).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("bruschetta"))
                .circuitMeta(1)
                .inputItems(GTMFOItems.TOMATO_SLICE.asStack(3), GTMFOItems.BUN.asStack(1),
                        GTMFOItems.SEED_GARLIC_PURPLE.asStack(1))
                .inputItems(TagPrefix.dustSmall, GTMaterials.Salt)
                .inputFluids(GTMFOFluids.OliveOil.getFluid(250))
                .outputItems(GTMFOItems.BRUSCHETTA.asStack())
                .EUt(24).duration(300).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("seasoned_pork"))
                .inputItems(Items.PORKCHOP)
                .inputItems(TagPrefix.dustSmall, GTMaterials.Salt)
                .inputItems(GTMFOItems.SEED_GARLIC_PURPLE.asStack())
                .outputItems(GTMFOItems.SEASONED_PORK.asStack())
                .EUt(24).duration(40).save(provider);

        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("porchetta"))
                .inputItems(GTMFOItems.SEASONED_PORK.asStack())
                .outputItems(GTMFOItems.PORCHETTA.asStack())
                .EUt(60).duration(2000).save(provider);
        GTMFORecipeTypes.SLICER_RECIPES.recipeBuilder(id("porchetta_slice"))
                .inputItems(GTMFOItems.PORCHETTA.asStack())
                .notConsumable(GTMFOItems.SLICER_BLADE_FLAT.asStack())
                .outputItems(GTMFOItems.PORCHETTA_SLICE.asStack(8))
                .EUt(18).duration(30).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("spaghetti_all_assassina"))
                .inputItems(GTMFOItems.SPAGHETTI_DRIED.asStack(), GTMFOItems.CERAMIC_PLATE.asStack())
                .inputFluids(GTMFOFluids.TomatoSauce.getFluid(500))
                .outputItems(GTMFOItems.SPAGHETTI_ALLASSASSINA.asStack())
                .EUt(64).duration(400).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("pasta_all_amogus"))
                .inputItems(GTMFOItems.RIGATONI.asStack(), GTMFOItems.CERAMIC_PLATE.asStack())
                .inputItems(TagPrefix.dust, GTMFOMaterials.ShreddedParmesan, 2)
                .inputFluids(GTMFOFluids.TomatoSauce.getFluid(500))
                .outputItems(GTMFOItems.PASTA_ALL_AMOGUS.asStack())
                .EUt(64).duration(400).save(provider);
        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("pasta_alla_norma"))
                .inputItems(GTMFOItems.RIGATONI.asStack(), GTMFOItems.CERAMIC_PLATE.asStack(),
                        GTMFOItems.EGGPLANT_SLICE.asStack(3), GTMFOItems.BASIL.asStack())
                .inputFluids(GTMFOFluids.TomatoSauce.getFluid(200))
                .outputItems(GTMFOItems.PASTA_ALLA_NORMA.asStack())
                .EUt(16).duration(400).save(provider);

        GTRecipeTypes.FORMING_PRESS_RECIPES.recipeBuilder(id("baking_tray"))
                .inputItems(TagPrefix.ingot, GTMaterials.Steel, 4)
                .notConsumable(com.gregtechceu.gtceu.common.data.GTItems.SHAPE_MOLD_BLOCK.asStack())
                .outputItems(GTMFOItems.BAKING_TRAY.asStack())
                .EUt(16).duration(800).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("lasagna_napoletana_raw"))
                .inputItems(GTMFOItems.LASAGNA_RAW.asStack(4), GTMFOItems.BAKING_TRAY.asStack(),
                        ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.ShreddedParmesan, 3))
                .inputItems(TagPrefix.dust, GTMaterials.Meat, 2)
                .inputFluids(GTMFOFluids.TomatoSauce.getFluid(500))
                .outputItems(GTMFOItems.LASAGNA_NAPOLETANA_RAW.asStack())
                .EUt(16).duration(400).save(provider);
        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("lasagna_pesto_raw"))
                .inputItems(GTMFOItems.LASAGNA_RAW.asStack(4), GTMFOItems.BAKING_TRAY.asStack(),
                        GTMFOItems.MOZZARELLA_BALL.asStack(1))
                .inputItems(TagPrefix.dust, GTMaterials.Meat, 2)
                .inputFluids(GTMFOFluids.Pesto.getFluid(250), GTMFOFluids.BechamelSauce.getFluid(250))
                .outputItems(GTMFOItems.LASAGNA_PESTO_RAW.asStack())
                .EUt(16).duration(400).save(provider);
        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("lasagna_chum_raw"))
                .inputItems(GTMFOItems.LASAGNA_RAW.asStack(4), GTMFOItems.BAKING_TRAY.asStack(),
                        GTMFOItems.CHUM.asStack(2), GTMFOItems.CHEDDAR_SLICE.asStack(1))
                .inputFluids(GTMFOFluids.TomatoSauce.getFluid(200))
                .outputItems(GTMFOItems.LASAGNA_CHUM_RAW.asStack())
                .EUt(16).duration(400).save(provider);

        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("lasagna_napoletana_cooked"))
                .inputItems(GTMFOItems.LASAGNA_NAPOLETANA_RAW.asStack())
                .outputItems(GTMFOItems.LASAGNA_NAPOLETANA_COOKED.asStack())
                .EUt(60).duration(1500).save(provider);
        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("lasagna_pesto_cooked"))
                .inputItems(GTMFOItems.LASAGNA_PESTO_RAW.asStack())
                .outputItems(GTMFOItems.LASAGNA_PESTO_COOKED.asStack())
                .EUt(60).duration(1750).save(provider);
        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("lasagna_chum_cooked"))
                .inputItems(GTMFOItems.LASAGNA_CHUM_RAW.asStack())
                .outputItems(GTMFOItems.LASAGNA_CHUM_COOKED.asStack())
                .EUt(60).duration(2000).save(provider);

        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("lasagna_napoletana"))
                .inputItems(GTMFOItems.CERAMIC_PLATE.asStack(4), GTMFOItems.LASAGNA_NAPOLETANA_COOKED.asStack())
                .outputItems(GTMFOItems.LASAGNA_NAPOLETANA.asStack(4), GTMFOItems.BAKING_TRAY.asStack())
                .EUt(8).duration(80).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("lasagna_pesto"))
                .inputItems(GTMFOItems.CERAMIC_PLATE.asStack(4), GTMFOItems.LASAGNA_PESTO_COOKED.asStack())
                .outputItems(GTMFOItems.LASAGNA_PESTO.asStack(4), GTMFOItems.BAKING_TRAY.asStack())
                .EUt(8).duration(80).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("lasagna_chum"))
                .inputItems(GTMFOItems.CERAMIC_PLATE.asStack(4), GTMFOItems.LASAGNA_CHUM_COOKED.asStack())
                .outputItems(GTMFOItems.LASAGNA_CHUM.asStack(4), GTMFOItems.BAKING_TRAY.asStack())
                .EUt(8).duration(80).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("tortellini"))
                .inputItems(GTMFOItems.LASAGNA_RAW.asStack(),
                        ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.ShreddedParmesan),
                        GTMFOItems.NUTMEG.asStack())
                .inputItems(Items.PORKCHOP)
                .outputItems(GTMFOItems.TORTELLINI.asStack(8))
                .EUt(24).duration(100).save(provider);
        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("tortellini_in_brodo"))
                .inputItems(GTMFOItems.TORTELLINI.asStack(6), GTMFOItems.CERAMIC_BOWL.asStack())
                .inputFluids(GTMFOFluids.ChickenBroth.getFluid(1000))
                .outputItems(GTMFOItems.TORTELLINI_IN_BRODO.asStack())
                .EUt(16).duration(1000).save(provider);

        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("vitello_tonnato_flavorant"))
                .inputItems(Items.COD)
                .inputItems(GTMFOItems.SEED_GARLIC_PURPLE.asStack(), GTMFOItems.ONION_SLICE.asStack())
                .inputFluids(GTMFOFluids.WhiteWine.getFluid(20))
                .outputFluids(GTMFOFluids.VitelloTonnatoFlavorant.getFluid(200))
                .EUt(24).duration(2000).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("vitello_tonnato_sauce"))
                .inputFluids(GTMFOFluids.VitelloTonnatoFlavorant.getFluid(200),
                        GTMFOFluids.OliveOil.getFluid(200))
                .outputFluids(GTMFOFluids.VitelloTonnatoSauce.getFluid(400))
                .EUt(16).duration(100).save(provider);
        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("vitello_tonnato"))
                .inputFluids(GTMFOFluids.VitelloTonnatoSauce.getFluid(100),
                        GTMFOFluids.Yolk.getFluid(100))
                .inputItems(Items.COOKED_BEEF)
                .inputItems(GTMFOItems.CERAMIC_PLATE.asStack())
                .outputItems(GTMFOItems.VITELLO_TONNATO.asStack())
                .EUt(16).duration(100).save(provider);

        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("rafanata_mixture"))
                .inputItems(GTMFOItems.POTATO_MASHED.asStack(4),
                        GTMFOItems.HORSERADISH.asStack())
                .inputFluids(GTMFOFluids.Egg.getFluid(600), GTMFOFluids.OliveOil.getFluid(200))
                .outputFluids(GTMFOFluids.RafanataMixture.getFluid(800))
                .EUt(24).duration(2000).save(provider);
        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("rafanata"))
                .inputItems(GTMFOItems.CERAMIC_PLATE.asStack())
                .inputFluids(GTMFOFluids.RafanataMixture.getFluid(200))
                .outputItems(GTMFOItems.RAFANATA.asStack())
                .EUt(64).duration(500).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("fettuccine_alfredo"))
                .inputItems(GTMFOItems.TAGLIATELLE.asStack(),
                        ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.ShreddedParmesan),
                        GTMFOItems.CERAMIC_PLATE.asStack())
                .inputFluids(GTMFOFluids.Butter.getFluid(200))
                .outputItems(GTMFOItems.FETTUCCINE_ALFREDO.asStack())
                .EUt(24).duration(300).save(provider);

        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("risotto"))
                .inputItems(GTMFOItems.RICE.asStack(3),
                        ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.ShreddedParmesan),
                        GTMFOItems.ONION_SLICE.asStack(), GTMFOItems.CERAMIC_BOWL.asStack())
                .inputFluids(GTMFOFluids.ChickenBroth.getFluid(700), GTMFOFluids.Butter.getFluid(200),
                        GTMFOFluids.WhiteWine.getFluid(5))
                .outputItems(GTMFOItems.RISOTTO.asStack())
                .EUt(24).duration(1000).save(provider);

        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("carbonara_sauce"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.ShreddedParmesan))
                .inputFluids(GTMFOFluids.Egg.getFluid(500))
                .outputFluids(GTMFOFluids.CarbonaraSauce.getFluid(500))
                .EUt(24).duration(2000).save(provider);
        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("carbonara"))
                .inputItems(GTMFOItems.CERAMIC_PLATE.asStack(), GTMFOItems.SPAGHETTI.asStack(),
                        GTMFOItems.BACON.asStack())
                .inputFluids(GTMFOFluids.CarbonaraSauce.getFluid(200))
                .outputItems(GTMFOItems.CARBONARA.asStack())
                .EUt(16).duration(100).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("pasta_al_pomodoro"))
                .inputItems(GTMFOItems.CERAMIC_PLATE.asStack(), GTMFOItems.TOMATO_SLICE.asStack(2),
                        GTMFOItems.SPAGHETTI.asStack(), GTMFOItems.BASIL.asStack())
                .inputFluids(GTMFOFluids.TomatoSauce.getFluid(300), GTMFOFluids.OliveOil.getFluid(50))
                .outputItems(GTMFOItems.PASTA_AL_POMODORO.asStack())
                .EUt(16).duration(200).save(provider);

        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("pasta_e_fagioli_base"))
                .inputItems(GTMFOItems.CARROT_SLICE.asStack(4), GTMFOItems.SEED_GARLIC_PURPLE.asStack(2),
                        GTMFOItems.ONION_SLICE.asStack(3))
                .inputFluids(GTMFOFluids.ChickenBroth.getFluid(500), GTMFOFluids.OliveOil.getFluid(100),
                        GTMaterials.Water.getFluid(1400))
                .outputFluids(GTMFOFluids.PastaEFagioliBase.getFluid(2000))
                .EUt(24).duration(1000).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("mixed_pasta_e_fagioli_raw"))
                .inputItems(GTMFOItems.DITALINI_RAW.asStack(), GTMFOItems.SEED_BEAN.asStack())
                .inputFluids(GTMFOFluids.PastaEFagioliBase.getFluid(500))
                .outputFluids(GTMFOFluids.MixedPastaEFagioli.getFluid(500))
                .EUt(16).duration(100).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("mixed_pasta_e_fagioli_dried"))
                .inputItems(GTMFOItems.DITALINI_DRIED.asStack(), GTMFOItems.SEED_BEAN.asStack())
                .inputFluids(GTMFOFluids.PastaEFagioliBase.getFluid(500))
                .outputFluids(GTMFOFluids.MixedPastaEFagioli.getFluid(500))
                .EUt(16).duration(100).save(provider);
        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("pasta_e_fagioli"))
                .inputItems(GTMFOItems.CERAMIC_BOWL.asStack())
                .inputFluids(GTMFOFluids.MixedPastaEFagioli.getFluid(250))
                .outputItems(GTMFOItems.PASTA_E_FAGIOLI.asStack())
                .EUt(24).duration(300).save(provider);

        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("bolognese_sauce"))
                .inputItems(TagPrefix.dust, GTMaterials.Meat, 2)
                .inputItems(GTMFOItems.CARROT_SLICE.asStack(4), GTMFOItems.ONION_SLICE.asStack(2))
                .inputFluids(GTMFOFluids.ChickenBroth.getFluid(800), GTMFOFluids.OliveOil.getFluid(200))
                .outputFluids(GTMFOFluids.BologneseSauce.getFluid(1000))
                .EUt(16).duration(1500).save(provider);
        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("tomato_bolognese_sauce"))
                .inputFluids(GTMFOFluids.BologneseSauce.getFluid(100), GTMFOFluids.RedWine.getFluid(10),
                        GTMFOFluids.TomatoSauce.getFluid(100))
                .outputFluids(GTMFOFluids.TomatoBologneseSauce.getFluid(200))
                .EUt(24).duration(500).save(provider);
        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("tagliatelle_al_ragu"))
                .inputItems(GTMFOItems.TAGLIATELLE.asStack(), GTMFOItems.CERAMIC_PLATE.asStack())
                .inputFluids(GTMFOFluids.TomatoBologneseSauce.getFluid(350), GTMaterials.Milk.getFluid(50))
                .outputItems(GTMFOItems.TAGLIATELLE_AL_RAGU.asStack())
                .EUt(24).duration(500).save(provider);
        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("parmigiana"))
                .inputItems(GTMFOItems.EGGPLANT_SLICE.asStack(8),
                        ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.ShreddedParmesan, 2),
                        GTMFOItems.MOZZARELLA_SLICE.asStack(2), GTMFOItems.CERAMIC_PLATE.asStack())
                .inputFluids(GTMFOFluids.TomatoSauce.getFluid(200))
                .outputItems(GTMFOItems.PARMIGIANA.asStack())
                .EUt(64).duration(1000).save(provider);

        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("carciofi_alla_romana"))
                .inputItems(GTMFOItems.ARTICHOKE.asStack(), GTMFOItems.SEED_GARLIC_PURPLE.asStack(),
                        GTMFOItems.CERAMIC_PLATE.asStack())
                .outputItems(GTMFOItems.CARCIOFI_ALLA_ROMANA.asStack())
                .EUt(64).duration(3000).save(provider);
    }
}
