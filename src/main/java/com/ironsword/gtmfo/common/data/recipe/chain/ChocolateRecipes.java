package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

/**
 * Cocoa / chocolate sub-chain of the original SmogusChain
 * (the s'more + marshmallow + graham parts already live in {@link SmoreRecipes}).
 */
public class ChocolateRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.PYROLYSE_RECIPES.recipeBuilder(id("cocoa_beans_roasted"))
                .inputItems(Items.COCOA_BEANS, 8)
                .circuitMeta(1)
                .outputItems(GTMFOItems.COCOA_BEANS_ROASTED.asStack(8))
                .outputFluids(GTMaterials.Water.getFluid(1000))
                .EUt(120).duration(30).save(provider);

        GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("cocoa_hull_and_nib"))
                .inputItems(GTMFOItems.COCOA_BEANS_ROASTED.asStack(6))
                .inputFluids(GTMaterials.Steam.getFluid(10000))
                .outputItems(GTMFOItems.COCOA_SHELL.asStack(6), GTMFOItems.COCOA_BEANS_HULLED.asStack(6))
                .EUt(360).duration(90).save(provider);

        GTRecipeTypes.MACERATOR_RECIPES.recipeBuilder(id("cocoa_nibs"))
                .inputItems(GTMFOItems.COCOA_BEANS_HULLED.asStack())
                .outputItems(GTMFOItems.COCOA_NIBS.asStack())
                .EUt(30).duration(40).save(provider);

        GTRecipeTypes.MACERATOR_RECIPES.recipeBuilder(id("cocoa_shell_wood_dust"))
                .inputItems(GTMFOItems.COCOA_SHELL.asStack())
                .outputItems(TagPrefix.dustSmall, GTMaterials.Wood, 2)
                .EUt(8).duration(15).save(provider);

        // nibs -> molten chocolate + cocoa butter (combines the original press + extract steps)
        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("molten_unsweetened_chocolate"))
                .inputItems(GTMFOItems.COCOA_NIBS.asStack(2))
                .outputFluids(GTMFOFluids.MoltenUnsweetenedChocolate.getFluid(288),
                        GTMFOFluids.CocoaButter.getFluid(72))
                .EUt(270).duration(40).save(provider);

        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("molten_unsweetened_chocolate_dust"))
                .inputItems(TagPrefix.dust, GTMaterials.Cocoa)
                .outputFluids(GTMFOFluids.MoltenUnsweetenedChocolate.getFluid(144))
                .EUt(30).duration(20).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("molten_dark_chocolate"))
                .inputItems(TagPrefix.dust, GTMaterials.Sugar)
                .inputFluids(GTMFOFluids.CocoaButter.getFluid(144))
                .inputFluids(GTMFOFluids.MoltenUnsweetenedChocolate.getFluid(1008))
                .outputFluids(GTMFOFluids.MoltenDarkChocolate.getFluid(1152))
                .EUt(120).duration(160).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("molten_milk_chocolate"))
                .inputFluids(GTMFOFluids.MoltenDarkChocolate.getFluid(864))
                .inputFluids(GTMaterials.Milk.getFluid(288))
                .outputFluids(GTMFOFluids.MoltenMilkChocolate.getFluid(1152))
                .EUt(120).duration(280).save(provider);

        GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES.recipeBuilder(id("milk_chocolate"))
                .inputFluids(GTMFOFluids.MoltenMilkChocolate.getFluid(144))
                .notConsumable(GTItems.SHAPE_MOLD_INGOT.asStack())
                .outputItems(GTMFOItems.MILK_CHOCOLATE.asStack())
                .EUt(32).duration(200).save(provider);

        // gelatin from fish oil (the meat-based gelatin recipes are in SmoreRecipes)
        GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES.recipeBuilder(id("gelatin_from_fish_oil"))
                .inputFluids(GTMaterials.FishOil.getFluid(500))
                .notConsumable(GTItems.SHAPE_MOLD_PLATE.asStack())
                .outputItems(GTMFOItems.GELATIN.asStack())
                .EUt(60).duration(100).save(provider);
    }
}
