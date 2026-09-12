package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class ChorusRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("chorus_juice"))
                .inputItems(Items.CHORUS_FRUIT)
                .outputFluids(GTMFOFluids.ChorusJuice.getFluid(50))
                .EUt(16).duration(200).save(provider);
        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("chorus_juice_popped"))
                .inputItems(Items.POPPED_CHORUS_FRUIT)
                .outputFluids(GTMFOFluids.ChorusJuice.getFluid(100))
                .EUt(16).duration(100).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("ender_pearl_solution"))
                .inputItems(TagPrefix.dust, GTMaterials.EnderPearl)
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .outputFluids(GTMFOFluids.EnderPearlSolution.getFluid(1000))
                .EUt(7).duration(200).save(provider);
        GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("ender_sugar_solution"))
                .inputFluids(GTMFOFluids.EnderPearlSolution.getFluid(1000))
                .outputFluids(GTMFOFluids.EnderSugarSolution.getFluid(1000))
                .chancedOutput(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Potassium, 3), 3000, 1000)
                .chancedOutput(FluidIngredient.of(GTMaterials.Nitrogen.getFluid(3000)), 1000, 1500)
                .EUt(30).duration(500).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("vibrant_extract"))
                .inputFluids(GTMFOFluids.EnderSugarSolution.getFluid(1000),
                        GTMFOFluids.FermentedChorusJuice.getFluid(25))
                .outputFluids(GTMFOFluids.VibrantExtract.getFluid(1000))
                .EUt(GTValues.VA[GTValues.HV]).duration(400).save(provider);

        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("fermented_chorus"))
                .notConsumableFluid(GTMFOFluids.LacticAcidBacteria.getFluid(1))
                .inputItems(Items.CHORUS_FRUIT)
                .outputItems(GTMFOItems.FERMENTED_CHORUS.asStack())
                .EUt(2).duration(1000).save(provider);
        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("fermented_chorus_popped"))
                .notConsumableFluid(GTMFOFluids.LacticAcidBacteria.getFluid(1))
                .inputItems(Items.POPPED_CHORUS_FRUIT)
                .outputItems(GTMFOItems.FERMENTED_CHORUS.asStack())
                .EUt(2).duration(800).save(provider);

        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("fermented_chorus_juice"))
                .inputItems(GTMFOItems.FERMENTED_CHORUS.asStack())
                .outputFluids(GTMFOFluids.FermentedChorusJuice.getFluid(50))
                .EUt(16).duration(200).save(provider);
        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("fermented_chorus_pie"))
                .inputItems(GTMFOItems.PIE_CRUST.asStack())
                .inputFluids(GTMFOFluids.FermentedChorusJuice.getFluid(25))
                .outputItems(GTMFOItems.FERMENTED_CHORUS_PIE.asStack())
                .EUt(16).duration(300).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("antaf"))
                .inputFluids(GTMFOFluids.FermentedChorusJuice.getFluid(50))
                .inputItems(Items.SUGAR, 9)
                .inputFluids(GTMFOFluids.CarbonatedWater.getFluid(1000))
                .outputFluids(GTMFOFluids.Antaf.getFluid(2000))
                .EUt(60).duration(20).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("antaf_bottle"))
                .inputFluids(GTMFOFluids.Antaf.getFluid(500))
                .inputItems(GTMFOItems.PLASTIC_BOTTLE.asStack())
                .outputItems(GTMFOItems.ANTAF.asStack())
                .EUt(30).duration(20).save(provider);
    }
}
