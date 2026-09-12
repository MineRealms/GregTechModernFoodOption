package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.material.GTMFOMaterials;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class CheeseRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        cheddar(provider);
        mozzarella(provider);
        ricotta(provider);
        gorgonzola(provider);
        parmigiano(provider);
    }

    private static void cheddar(Consumer<FinishedRecipe> provider){
        GTMFORecipeTypes.SLICER_RECIPES.recipeBuilder(id("beef_slice"))
                .inputItems(Items.BEEF)
                .outputItems(GTMFOItems.BEEF_SLICE.asStack(9))
                .notConsumable(GTMFOItems.SLICER_BLADE_STRIPES.asStack())
                .EUt(24).duration(80).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("crude_rennet_solution"))
                .inputItems(GTMFOItems.BEEF_SLICE.asStack(4))
                .inputFluids(GTMaterials.SaltWater.getFluid(1000), GTMaterials.AceticAcid.getFluid(100))
                .outputFluids(GTMFOFluids.CrudeRennetSolution.getFluid(500))
                .EUt(8).duration(300).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("coagulated_milk_curd"))
                .inputFluids(GTMFOFluids.CrudeRennetSolution.getFluid(1), GTMaterials.Milk.getFluid(3000))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.CoagulatedMilkCurd))
                .outputFluids(GTMFOFluids.Whey.getFluid(600))
                .EUt(30).duration(200).save(provider);
        GTMFORecipeTypes.SLICER_RECIPES.recipeBuilder(id("cut_curd"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.CoagulatedMilkCurd))
                .notConsumable(GTMFOItems.SLICER_BLADE_STRIPES.asStack())
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.CutCurd, 64))
                .EUt(16).duration(40).save(provider);
        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("cooked_curd"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.CutCurd, 64))
                .inputFluids(GTMaterials.Steam.getFluid(1000))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.CookedCurd, 64))
                .EUt(6).duration(2400).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("salted_curd"))
                .inputItems(TagPrefix.dust, GTMaterials.Salt)
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.CookedCurd, 64))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.SaltedCurd, 64))
                .EUt(20).duration(100).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("cheddar_curd_mold"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.SaltedCurd, 32),
                        GTItems.SHAPE_MOLD_BLOCK.asStack())
                .outputItems(GTMFOItems.CHEDDAR_CURD_MOLD.asStack())
                .EUt(4).duration(200).save(provider);
        GTRecipeTypes.COMPRESSOR_RECIPES.recipeBuilder(id("aged_cheddar_mold"))
                .inputItems(GTMFOItems.CHEDDAR_CURD_MOLD.asStack())
                .outputItems(GTMFOItems.CHEDDAR_AGED_MOLD.asStack())
                .EUt(16).duration(6000).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("cheddar_block"))
                .inputItems(GTMFOItems.CHEDDAR_AGED_MOLD.asStack())
                .outputItems(GTMFOItems.CHEDDAR_BLOCK.asStack(), GTItems.SHAPE_MOLD_BLOCK.asStack())
                .EUt(8).duration(40).save(provider);
        GTMFORecipeTypes.SLICER_RECIPES.recipeBuilder(id("cheddar_slice"))
                .inputItems(GTMFOItems.CHEDDAR_BLOCK.asStack())
                .notConsumable(GTMFOItems.SLICER_BLADE_FLAT.asStack())
                .outputItems(GTMFOItems.CHEDDAR_SLICE.asStack(9))
                .EUt(16).duration(80).save(provider);
    }

    private static void mozzarella(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("activated_buffalo_milk"))
                .inputFluids(GTMFOFluids.ItalianBuffaloMilk.getFluid(730), GTMFOFluids.Whey.getFluid(270))
                .outputFluids(GTMFOFluids.ActivatedBuffaloMilk.getFluid(1000))
                .EUt(8).duration(40).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("large_mozzarella_curd"))
                .inputFluids(GTMFOFluids.ActivatedBuffaloMilk.getFluid(3000),
                        GTMFOFluids.CrudeRennetSolution.getFluid(1))
                .circuitMeta(1)
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.LargeMozzarellaCurd))
                .EUt(8).duration(120).save(provider);
        GTMFORecipeTypes.SLICER_RECIPES.recipeBuilder(id("small_mozzarella_curd"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.LargeMozzarellaCurd))
                .notConsumable(GTMFOItems.SLICER_BLADE_STRIPES.asStack())
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.SmallMozzarellaCurd, 9))
                .EUt(12).duration(1000).save(provider);
        GTRecipeTypes.THERMAL_CENTRIFUGE_RECIPES.recipeBuilder(id("dried_mozzarella_curd"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.SmallMozzarellaCurd))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.DriedMozzarellaCurd))
                .EUt(30).duration(1000).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("solidified_mozzarella_curd"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.DriedMozzarellaCurd))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.SolidifiedMozzarellaCurd))
                .outputFluids(GTMFOFluids.Whey.getFluid(30))
                .EUt(16).duration(200).save(provider);
        GTRecipeTypes.BENDER_RECIPES.recipeBuilder(id("mozzarella_ball"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.SolidifiedMozzarellaCurd, 5))
                .circuitMeta(1)
                .outputItems(GTMFOItems.MOZZARELLA_BALL.asStack())
                .EUt(16).duration(400).save(provider);
        GTRecipeTypes.EXTRUDER_RECIPES.recipeBuilder(id("mozzarella_slice"))
                .inputItems(GTMFOItems.MOZZARELLA_BALL.asStack())
                .notConsumable(GTItems.SHAPE_EXTRUDER_PLATE.asStack())
                .outputItems(GTMFOItems.MOZZARELLA_SLICE.asStack(9))
                .EUt(16).duration(400).save(provider);
    }

    private static void ricotta(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("whey_salt_water_mix"))
                .inputFluids(GTMFOFluids.Whey.getFluid(1000), GTMaterials.SaltWater.getFluid(50))
                .outputFluids(GTMFOFluids.WheySaltWaterMix.getFluid(1050))
                .EUt(12).duration(40).save(provider);
        GTRecipeTypes.FLUID_HEATER_RECIPES.recipeBuilder(id("heated_ricotta_starter"))
                .inputFluids(GTMFOFluids.WheySaltWaterMix.getFluid(1000))
                .circuitMeta(1)
                .outputFluids(GTMFOFluids.HeatedRicottaStarter.getFluid(1000))
                .EUt(30).duration(100).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("acidic_milk_solution_lemon"))
                .inputFluids(GTMaterials.Milk.getFluid(144), GTMFOFluids.LemonExtract.getFluid(10))
                .outputFluids(GTMFOFluids.AcidicMilkSolution.getFluid(144))
                .EUt(12).duration(40).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("acidic_milk_solution_citric"))
                .inputFluids(GTMaterials.Milk.getFluid(144), GTMFOFluids.CitricAcid.getFluid(1))
                .outputFluids(GTMFOFluids.AcidicMilkSolution.getFluid(144))
                .EUt(12).duration(40).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("coagulating_ricotta_solution"))
                .inputFluids(GTMFOFluids.HeatedRicottaStarter.getFluid(1000),
                        GTMFOFluids.AcidicMilkSolution.getFluid(144))
                .outputFluids(GTMFOFluids.CoagulatingRicottaSolution.getFluid(1144))
                .EUt(20).duration(60).save(provider);
        GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("ricotta_piece"))
                .inputFluids(GTMFOFluids.CoagulatingRicottaSolution.getFluid(1144))
                .outputItems(GTMFOItems.RICOTTA_PIECE.asStack(2))
                .outputFluids(GTMFOFluids.Whey.getFluid(856))
                .EUt(30).duration(160).save(provider);
    }

    private static void gorgonzola(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("penicillium_roqueforti"))
                .inputFluids(GTMFOFluids.ColdMoistAir.getFluid(8000))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.PenicilliumRoqueforti))
                .EUt(500).duration(1000).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("fungal_rennet_solution"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.PenicilliumRoqueforti))
                .inputFluids(GTMFOFluids.LacticAcidBacteria.getFluid(1))
                .inputFluids(GTMFOFluids.CrudeRennetSolution.getFluid(250))
                .outputFluids(GTMFOFluids.FungalRennetSolution.getFluid(250))
                .EUt(110).duration(120).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("gorgonzola_curd"))
                .inputFluids(GTMFOFluids.ActivatedBuffaloMilk.getFluid(3000),
                        GTMFOFluids.FungalRennetSolution.getFluid(3))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.GorgonzolaCurd, 12))
                .EUt(16).duration(120).save(provider);
        GTRecipeTypes.FORMING_PRESS_RECIPES.recipeBuilder(id("gorgonzola_wheel"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.GorgonzolaCurd, 20))
                .notConsumable(GTItems.SHAPE_MOLD_CYLINDER.asStack())
                .outputItems(GTMFOItems.GORGONZOLA_WHEEL.asStack())
                .EUt(20).duration(75).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("gorgonzola_wheel_salted"))
                .inputItems(GTMFOItems.GORGONZOLA_WHEEL.asStack())
                .inputItems(TagPrefix.dust, GTMaterials.Salt)
                .outputItems(GTMFOItems.GORGONZOLA_WHEEL_SALTED.asStack())
                .EUt(18).duration(65).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("gorgonzola_wheel_slightly_aged"))
                .inputItems(GTMFOItems.GORGONZOLA_WHEEL_SALTED.asStack())
                .outputItems(GTMFOItems.GORGONZOLA_WHEEL_SLIGHTLY_AGED.asStack())
                .outputFluids(GTMFOFluids.Whey.getFluid(35))
                .EUt(24).duration(460).save(provider);
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(id("gorgonzola_wheel_punctured"))
                .inputItems(GTMFOItems.GORGONZOLA_WHEEL_SLIGHTLY_AGED.asStack())
                .notConsumable(TagPrefix.rod, GTMaterials.StainlessSteel)
                .outputItems(GTMFOItems.GORGONZOLA_WHEEL_PUNCTURED.asStack())
                .EUt(32).duration(90).save(provider);
        GTRecipeTypes.AUTOCLAVE_RECIPES.recipeBuilder(id("gorgonzola_wheel_fully_cured"))
                .inputItems(GTMFOItems.GORGONZOLA_WHEEL_PUNCTURED.asStack())
                .inputFluids(GTMFOFluids.ColdMoistAir.getFluid(500))
                .outputItems(GTMFOItems.GORGONZOLA_WHEEL_FULLY_CURED.asStack())
                .EUt(24).duration(3200).save(provider);
        GTMFORecipeTypes.SLICER_RECIPES.recipeBuilder(id("gorgonzola_triangular_slice"))
                .inputItems(GTMFOItems.GORGONZOLA_WHEEL_FULLY_CURED.asStack())
                .notConsumable(GTMFOItems.SLICER_BLADE_OCTAGONAL.asStack())
                .outputItems(GTMFOItems.GORGONZOLA_TRIANGULAR_SLICE.asStack(16))
                .EUt(28).duration(160).save(provider);
    }

    private static void parmigiano(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.LATHE_RECIPES.recipeBuilder(id("parmigiano_cheese_form"))
                .inputItems(TagPrefix.block, GTMaterials.StainlessSteel)
                .outputItems(GTMFOItems.PARMIGIANO_CHEESE_FORM.asStack())
                .EUt(72).duration(360).save(provider);
        GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("unpasteurized_skimmed_milk"))
                .inputFluids(GTMaterials.Milk.getFluid(1000))
                .circuitMeta(1)
                .outputFluids(GTMFOFluids.UnpasteurizedSkimmedMilk.getFluid(800))
                .EUt(24).duration(100).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("parmigiano_reggiano_starter"))
                .inputItems(TagPrefix.dustTiny, GTMaterials.Copper)
                .inputFluids(GTMFOFluids.UnpasteurizedSkimmedMilk.getFluid(500), GTMFOFluids.Whey.getFluid(500))
                .outputFluids(GTMFOFluids.ParmigianoReggianoStarter.getFluid(1000))
                .EUt(16).duration(40).save(provider);
        GTRecipeTypes.FLUID_HEATER_RECIPES.recipeBuilder(id("curdling_parmigiano_reggiano"))
                .inputFluids(GTMFOFluids.ParmigianoReggianoStarter.getFluid(1000))
                .circuitMeta(1)
                .outputFluids(GTMFOFluids.CurdlingParmigianoReggiano.getFluid(2000))
                .EUt(48).duration(300).save(provider);
        GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES.recipeBuilder(id("parmigiano_curdling"))
                .inputFluids(GTMFOFluids.CurdlingParmigianoReggiano.getFluid(1000))
                .inputItems(GTMFOItems.PARMIGIANO_CHEESE_FORM.asStack())
                .outputItems(GTMFOItems.PARMIGIANO_CURDLING.asStack())
                .EUt(16).duration(150).save(provider);
        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("parmigiano_brined"))
                .inputItems(GTMFOItems.PARMIGIANO_CURDLING.asStack())
                .inputFluids(GTMaterials.SaltWater.getFluid(100))
                .outputItems(GTMFOItems.PARMIGIANO_BRINED.asStack())
                .EUt(8).duration(800).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("parmigiano_brined_roll"))
                .inputItems(GTMFOItems.PARMIGIANO_BRINED.asStack())
                .outputItems(GTMFOItems.PARMIGIANO_BRINED_ROLL.asStack(),
                        GTMFOItems.PARMIGIANO_CHEESE_FORM.asStack())
                .EUt(8).duration(40).save(provider);
        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("parmigiano_aged_roll"))
                .inputItems(GTMFOItems.PARMIGIANO_BRINED_ROLL.asStack(64))
                .inputFluids(GTMaterials.Air.getFluid(10000))
                .outputItems(GTMFOItems.PARMIGIANO_AGED_ROLL.asStack(64))
                .outputFluids(GTMaterials.Air.getFluid(9000))
                .EUt(2).duration(36000).save(provider);
        GTRecipeTypes.MACERATOR_RECIPES.recipeBuilder(id("shredded_parmesan"))
                .inputItems(GTMFOItems.PARMIGIANO_AGED_ROLL.asStack())
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.ShreddedParmesan, 8))
                .EUt(8).duration(120).save(provider);
    }
}
