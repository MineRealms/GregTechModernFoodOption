package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.material.GTMFOMaterials;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

/**
 * Banana -> potassium extraction chain (originally gated behind GCYS absence;
 * the GTCEu recipe-replacement hacks are omitted here).
 */
public class BananaRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("sodium_chlorate"))
                .inputItems(TagPrefix.dust, GTMaterials.Salt, 2)
                .circuitMeta(1)
                .inputFluids(GTMaterials.Water.getFluid(3000))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.SodiumChlorate, 5))
                .outputFluids(GTMaterials.Hydrogen.getFluid(6000))
                .EUt(120).duration(400).save(provider);

        GTRecipeTypes.ELECTROLYZER_RECIPES.recipeBuilder(id("sodium_chlorate_electrolysis"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.SodiumChlorate, 5))
                .circuitMeta(1)
                .outputItems(TagPrefix.dust, GTMaterials.Sodium)
                .outputFluids(GTMaterials.Chlorine.getFluid(1000), GTMaterials.Oxygen.getFluid(3000))
                .EUt(60).duration(100).save(provider);
        GTRecipeTypes.ELECTROLYZER_RECIPES.recipeBuilder(id("sodium_perchlorate"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.SodiumChlorate, 5))
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .circuitMeta(2)
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.SodiumPerchlorate, 6))
                .outputFluids(GTMaterials.Hydrogen.getFluid(2000))
                .EUt(120).duration(100).save(provider);

        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("perchloric_acid"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.SodiumPerchlorate, 6))
                .inputFluids(GTMaterials.HydrochloricAcid.getFluid(1000))
                .outputItems(TagPrefix.dust, GTMaterials.Salt, 2)
                .outputFluids(GTMFOFluids.PerchloricAcid.getFluid(1000))
                .EUt(120).duration(100).save(provider);
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("ammonium_perchlorate"))
                .inputFluids(GTMFOFluids.PerchloricAcid.getFluid(1000), GTMaterials.Ammonia.getFluid(1000))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.AmmoniumPerchlorate, 10))
                .EUt(64).duration(300).save(provider);

        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("banana_peeled"))
                .inputItems(GTMFOItems.BANANA.asStack())
                .outputItems(GTMFOItems.BANANA_PEEL.asStack(), GTMFOItems.BANANA_PEELED.asStack())
                .EUt(32).duration(60).save(provider);
        // note: the burnt peel item is not ported yet; the peel goes straight to the alkaline extract
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("alkaline_extract"))
                .inputItems(GTMFOItems.BANANA_PEEL.asStack())
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .outputFluids(GTMFOFluids.AlkalineExtract.getFluid(1000))
                .EUt(24).duration(200).save(provider);

        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("potassium_perchlorate"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.AmmoniumPerchlorate, 10))
                .inputFluids(GTMFOFluids.AlkalineExtract.getFluid(1000))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.PotassiumPerchlorate, 6))
                .outputFluids(GTMFOFluids.Sludge.getFluid(1000))
                .EUt(120).duration(200).save(provider);
        GTRecipeTypes.BLAST_RECIPES.recipeBuilder(id("potassium_from_perchlorate"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.PotassiumPerchlorate, 6))
                .outputItems(TagPrefix.dust, GTMaterials.RockSalt, 2)
                .outputFluids(GTMaterials.Oxygen.getFluid(4000))
                .blastFurnaceTemp(1400)
                .EUt(120).duration(319).save(provider);
    }
}
