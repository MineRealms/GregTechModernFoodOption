package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtmfo.common.data.material.GTMFOMaterials;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class LithiumRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("lithium_oxide"))
                .inputItems(TagPrefix.dust, GTMaterials.Lithium, 2)
                .inputFluids(GTMaterials.Oxygen.getFluid(1000))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.LithiumOxide, 3))
                .EUt(60).duration(60).save(provider);

        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("lithium_carbonate"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.LithiumOxide, 3))
                .inputFluids(GTMaterials.CarbonDioxide.getFluid(1000))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.LithiumCarbonate, 6))
                .EUt(16).duration(140).save(provider);
    }
}
