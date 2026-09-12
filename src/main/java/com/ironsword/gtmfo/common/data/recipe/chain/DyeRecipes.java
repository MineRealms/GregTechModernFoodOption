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

public class DyeRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.ARC_FURNACE_RECIPES.recipeBuilder(id("arsenic_trioxide"))
                .inputItems(TagPrefix.dust, GTMaterials.Arsenic, 2)
                .inputFluids(GTMaterials.Oxygen.getFluid(3000))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMaterials.ArsenicTrioxide, 5))
                .EUt(30).duration(100).save(provider);

        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("sodium_arsenite_solution"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMaterials.ArsenicTrioxide, 5))
                .inputFluids(GTMFOFluids.SodiumCarbonateSolution.getFluid(1000))
                .outputFluids(GTMFOFluids.SodiumArseniteSolution.getFluid(2000),
                        GTMaterials.CarbonDioxide.getFluid(1000))
                .EUt(30).duration(160).save(provider);

        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("cupric_hydrogen_arsenite"))
                .inputItems(TagPrefix.dust, GTMaterials.SodiumHydroxide, 3)
                .inputFluids(GTMFOFluids.SodiumArseniteSolution.getFluid(1000),
                        GTMFOFluids.BlueVitriol.getFluid(1000))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.CupricHydrogenArsenite, 6))
                .outputItems(TagPrefix.dust, GTMFOMaterials.SodiumSulfate, 7)
                .outputFluids(GTMaterials.Water.getFluid(500))
                .EUt(24).duration(80).save(provider);

        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("blue_vitriol"))
                .inputItems(TagPrefix.dust, GTMaterials.Copper)
                .inputFluids(GTMaterials.SulfuricAcid.getFluid(1000))
                .outputFluids(GTMFOFluids.BlueVitriol.getFluid(1000), GTMaterials.Hydrogen.getFluid(2000))
                .EUt(16).duration(160).save(provider);

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(id("greenhouse_glass"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.CupricHydrogenArsenite))
                .inputItems(com.gregtechceu.gtceu.common.data.GTBlocks.CASING_TEMPERED_GLASS.asStack())
                .outputItems(com.ironsword.gtmfo.common.data.GTMFOBlocks.GREENHOUSE_GLASS.asStack())
                .EUt(24).duration(60).save(provider);
    }
}
