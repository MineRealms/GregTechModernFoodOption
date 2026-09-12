package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.material.GTMFOMaterials;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class CapletRecipes {
    public static void init(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.FLUID_HEATER_RECIPES.recipeBuilder(id("gelatin_solution"))
                .inputItems(GTMFOItems.GELATIN,8)
                .inputFluids(GTMaterials.Water,1000)
                .outputFluids(GTMFOFluids.GelatinSolution.getFluid(1000))
                .EUt(480).duration(200).save(provider);

        GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES.recipeBuilder(id("caplet_body"))
                .inputFluids(GTMFOFluids.GelatinSolution,108)
                .notConsumable(GTItems.SHAPE_MOLD_BALL)
                .outputItems(GTMFOItems.CAPLET_BODY,16)
                .EUt(120).duration(100).save(provider);
        GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES.recipeBuilder(id("caplet_cap"))
                .inputFluids(GTMFOFluids.GelatinSolution,72)
                .notConsumable(GTItems.SHAPE_MOLD_BOTTLE)
                .outputItems(GTMFOItems.CAPLET_CAP)
                .EUt(120).duration(100).save(provider);
        GTRecipeTypes.FORMING_PRESS_RECIPES.recipeBuilder(id("caplet_gel"))
                .inputItems(GTMFOItems.CAPLET_BODY)
                .inputItems(GTMFOItems.CAPLET_CAP)
                .circuitMeta(1)
                .outputItems(GTMFOItems.CAPLET_GEL)
                .EUt(30).duration(100).save(provider);

        GTRecipeTypes.FLUID_HEATER_RECIPES.recipeBuilder(id("heated_water"))
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .circuitMeta(2)
                .outputFluids(GTMFOFluids.HeatedWater.getFluid(1000))
                .EUt(120).duration(540).save(provider);

        // paracetamol caplet
        GTRecipeTypes.FORMING_PRESS_RECIPES.recipeBuilder(id("caplet_paracetamol"))
                .inputItems(GTMFOItems.CAPLET_BODY.asStack())
                .inputItems(com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(
                        com.gregtechceu.gtceu.api.data.tag.TagPrefix.dustSmall, GTMFOMaterials.Paracetamol))
                .inputItems(GTMFOItems.CAPLET_CAP.asStack())
                .circuitMeta(3)
                .outputItems(GTMFOItems.CAPLET_PARACETAMOL.asStack())
                .EUt(30).duration(20).save(provider);

        // nitrophenols -> 4-/2-nitrophenol
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("nitrophenols"))
                .inputFluids(GTMaterials.NitrationMixture.getFluid(2000), GTMaterials.Phenol.getFluid(5000))
                .outputFluids(GTMFOFluids.Nitrophenols.getFluid(6000), GTMaterials.DilutedSulfuricAcid.getFluid(1000))
                .EUt(480).duration(500).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("nitrophenol_separation"))
                .inputFluids(GTMFOFluids.Nitrophenols.getFluid(1000))
                .outputItems(com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(
                        com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust, GTMFOMaterials.IVNitrophenol, 15))
                .outputItems(com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(
                        com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust, GTMFOMaterials.IINitrophenol, 15))
                .EUt(480).duration(10).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("aminophenol"))
                .inputItems(com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(
                        com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust, GTMFOMaterials.IVNitrophenol, 15))
                .inputFluids(GTMaterials.Hydrogen.getFluid(6000))
                .notConsumable(com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(
                        com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust, GTMaterials.Nickel))
                .outputItems(com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(
                        com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust, GTMFOMaterials.Aminophenol, 15))
                .outputFluids(GTMaterials.Water.getFluid(2000))
                .EUt(480).duration(80).save(provider);

        // paracetamol
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("acetic_anhydride"))
                .inputFluids(GTMaterials.MethylAcetate.getFluid(1000), GTMaterials.CarbonMonoxide.getFluid(1000))
                .circuitMeta(16)
                .outputFluids(GTMaterials.AceticAnhydride.getFluid(2000))
                .EUt(480).duration(500).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("paracetamol"))
                .inputFluids(GTMaterials.AceticAnhydride.getFluid(1000))
                .inputItems(com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(
                        com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust, GTMFOMaterials.Aminophenol, 15))
                .circuitMeta(16)
                .outputItems(com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(
                        com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust, GTMFOMaterials.Paracetamol, 20))
                .outputFluids(GTMaterials.Water.getFluid(1000))
                .EUt(1800).duration(400).save(provider);

        // plutonium 241 caplet
        GTRecipeTypes.FORMING_PRESS_RECIPES.recipeBuilder(id("caplet_plutonium_241"))
                .inputItems(GTMFOItems.CAPLET_BODY.asStack())
                .inputItems(com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(
                        com.gregtechceu.gtceu.api.data.tag.TagPrefix.dustSmall, GTMaterials.Plutonium241))
                .inputItems(GTMFOItems.CAPLET_CAP.asStack())
                .circuitMeta(2)
                .outputItems(GTMFOItems.CAPLET_PLUTONIUM_241.asStack())
                .EUt(30).duration(20).save(provider);

        // chorus / vibrant caplets
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("caplet_chorus"))
                .inputItems(GTMFOItems.CAPLET_BODY.asStack())
                .inputFluids(GTMFOFluids.FermentedChorusJuice.getFluid(100))
                .inputItems(GTMFOItems.CAPLET_CAP.asStack())
                .outputItems(GTMFOItems.CAPLET_CHORUS.asStack())
                .EUt(30).duration(100).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("caplet_vibrant"))
                .inputItems(GTMFOItems.CAPLET_BODY.asStack())
                .inputFluids(GTMFOFluids.VibrantExtract.getFluid(100))
                .inputItems(GTMFOItems.CAPLET_CAP.asStack())
                .outputItems(GTMFOItems.CAPLET_VIBRANT.asStack())
                .EUt(30).duration(200).save(provider);
    }
}
