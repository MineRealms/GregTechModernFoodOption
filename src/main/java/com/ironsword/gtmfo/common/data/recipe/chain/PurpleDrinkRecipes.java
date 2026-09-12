package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.material.GTMFOMaterials;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

/**
 * Purple drink chain (hard candy part lives in {@link AppleRecipes}, carbonated water in {@link CoreChain}).
 */
public class PurpleDrinkRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        etirps(provider);
        carbonatedWater(provider);
        pharma(provider);
    }

    private static void etirps(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("lemon_lime_solution"))
                .inputFluids(GTMFOFluids.LemonExtract.getFluid(500), GTMFOFluids.LIME_EXTRACT.getFluid(500))
                .outputFluids(GTMFOFluids.LemonLimeSolution.getFluid(1000))
                .EUt(30).duration(100).save(provider);
        GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("lemon_lime_sludge"))
                .inputFluids(GTMFOFluids.LemonLimeSolution.getFluid(1500))
                .outputFluids(GTMFOFluids.LemonLimeSludge.getFluid(1000))
                .EUt(30).duration(140).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("lemon_lime_soda_syrup"))
                .inputFluids(GTMFOFluids.LemonLimeSludge.getFluid(500))
                .inputItems(Items.SUGAR, 9)
                .outputFluids(GTMFOFluids.LemonLimeSodaSyrup.getFluid(500))
                .EUt(60).duration(40).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("etirps"))
                .inputFluids(GTMFOFluids.CarbonatedWater.getFluid(1000),
                        GTMFOFluids.LemonLimeSodaSyrup.getFluid(1000))
                .outputFluids(GTMFOFluids.Etirps.getFluid(2000))
                .EUt(60).duration(20).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("etirps_bottle"))
                .inputFluids(GTMFOFluids.Etirps.getFluid(500))
                .inputItems(GTMFOItems.PLASTIC_BOTTLE.asStack())
                .outputItems(GTMFOItems.ETIRPS.asStack())
                .EUt(30).duration(20).save(provider);
    }

    private static void carbonatedWater(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("sparkling_water"))
                .inputFluids(GTMFOFluids.CarbonatedWater.getFluid(500))
                .inputItems(GTMFOItems.PLASTIC_BOTTLE.asStack())
                .outputItems(GTMFOItems.SPARKLING_WATER.asStack())
                .EUt(30).duration(100).save(provider);
        GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES.recipeBuilder(id("plastic_bottle"))
                .inputFluids(GTMaterials.Polyethylene.getFluid(288))
                .notConsumable(GTItems.SHAPE_MOLD_BOTTLE.asStack())
                .outputItems(GTMFOItems.PLASTIC_BOTTLE.asStack())
                .EUt(60).duration(160).save(provider);
    }

    private static void pharma(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("cough_syrup"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Promethazine, 40),
                        ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Codeine, 42))
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .outputFluids(GTMFOFluids.CoughSyrup.getFluid(1000))
                .EUt(60).duration(60).save(provider);
        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("codeine"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.CrushedPoppy, 30))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Codeine, 42))
                .EUt(1920).duration(600).save(provider);
        GTRecipeTypes.MACERATOR_RECIPES.recipeBuilder(id("crushed_poppy"))
                .inputItems(Items.POPPY)
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.CrushedPoppy))
                .EUt(4).duration(20).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("promethazine"))
                .inputFluids(GTMaterials.Dimethylamine.getFluid(1000),
                        GTMFOFluids.XPhenothiazineIiPropylChloride.getFluid(1000))
                .notConsumable(TagPrefix.dust, GTMaterials.Copper)
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Promethazine, 40))
                .outputFluids(GTMaterials.HydrochloricAcid.getFluid(1000))
                .EUt(8000).duration(120).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("x_phenothiazine_ii_propyl_chloride"))
                .inputFluids(GTMFOFluids.IsopropylChloride.getFluid(1000))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Phenothiazine, 23))
                .outputFluids(GTMFOFluids.XPhenothiazineIiPropylChloride.getFluid(1000))
                .EUt(240).duration(120).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("phenothiazine"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Diphenylamine, 24))
                .inputItems(TagPrefix.dust, GTMaterials.Sulfur, 2)
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Phenothiazine, 23))
                .outputFluids(GTMaterials.HydrogenSulfide.getFluid(1000))
                .EUt(120).duration(120).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("diphenylamine"))
                .inputFluids(GTMFOFluids.Aniline.getFluid(2000), GTMaterials.HydrochloricAcid.getFluid(1000))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Diphenylamine, 24))
                .outputItems(TagPrefix.dust, GTMaterials.AmmoniumChloride, 2)
                .EUt(480).duration(120).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("aniline"))
                .inputFluids(GTMaterials.Hydrogen.getFluid(6000), GTMaterials.Nitrobenzene.getFluid(1000))
                .notConsumable(TagPrefix.dust, GTMaterials.Zinc)
                .outputFluids(GTMFOFluids.Aniline.getFluid(1000))
                .outputFluids(GTMaterials.Water.getFluid(2000))
                .EUt(30).duration(100).save(provider);
    }
}
