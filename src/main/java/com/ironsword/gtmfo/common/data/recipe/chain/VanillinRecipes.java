package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.material.GTMFOMaterials;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class VanillinRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        // creosote distillation now yields guaiacol (the gtceu:distill_creosote recipe is removed in GTMFORecipes#remove)
        GTRecipeTypes.DISTILLATION_RECIPES.recipeBuilder(id("distill_creosote"))
                .inputFluids(GTMaterials.Creosote.getFluid(24))
                .outputFluids(GTMaterials.Lubricant.getFluid(12), GTMFOFluids.Guaiacol.getFluid(1))
                .EUt(96).duration(16).save(provider);

        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("acetaldehyde"))
                .notConsumable(TagPrefix.dust, GTMaterials.Palladium)
                .inputFluids(GTMaterials.Ethylene.getFluid(1000), GTMaterials.Oxygen.getFluid(1000))
                .outputFluids(GTMFOFluids.Acetaldehyde.getFluid(1000))
                .EUt(30).duration(120).save(provider);
        GTRecipeTypes.LARGE_CHEMICAL_RECIPES.recipeBuilder(id("glyoxal"))
                .inputFluids(GTMFOFluids.Acetaldehyde.getFluid(1000), GTMaterials.NitricAcid.getFluid(4000))
                .outputFluids(GTMFOFluids.Glyoxal.getFluid(1000), GTMaterials.NitrogenDioxide.getFluid(4000),
                        GTMaterials.Water.getFluid(4000))
                .EUt(60).duration(120).save(provider);
        GTRecipeTypes.LARGE_CHEMICAL_RECIPES.recipeBuilder(id("glyoxylic_acid"))
                .inputFluids(GTMFOFluids.Glyoxal.getFluid(2000), GTMaterials.NitricAcid.getFluid(2000))
                .outputFluids(GTMFOFluids.GlyoxylicAcid.getFluid(2000), GTMaterials.Water.getFluid(1000),
                        GTMaterials.NitrogenDioxide.getFluid(1000), GTMaterials.NitrousOxide.getFluid(1000))
                .EUt(240).duration(30).save(provider);

        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("vanillylmandelic_acid"))
                .notConsumable(TagPrefix.dust, GTMaterials.SodiumHydroxide)
                .inputFluids(GTMFOFluids.Guaiacol.getFluid(1000), GTMFOFluids.GlyoxylicAcid.getFluid(1000))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.VanillylmandelicAcid, 24))
                .EUt(120).duration(160).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("vanilglycolic_acid"))
                .notConsumable(TagPrefix.dust, GTMaterials.SodiumHydroxide)
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.VanillylmandelicAcid, 24))
                .inputFluids(GTMaterials.Oxygen.getFluid(1000))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.VanilglycolicAcid, 22))
                .outputFluids(GTMaterials.Water.getFluid(1000))
                .EUt(120).duration(160).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("vanillin"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.VanilglycolicAcid, 22))
                .inputFluids(GTMaterials.HydrochloricAcid.getFluid(1000))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Vanillin, 19))
                .outputFluids(GTMaterials.DilutedHydrochloricAcid.getFluid(1000),
                        GTMaterials.CarbonDioxide.getFluid(1000))
                .EUt(120).duration(240).save(provider);
    }
}
