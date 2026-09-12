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

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class IceCreamRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        base(provider);
        flavours(provider);
    }

    private static void base(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.FLUID_HEATER_RECIPES.recipeBuilder(id("pasteurized_milk_slow"))
                .inputFluids(GTMaterials.Milk.getFluid(4000))
                .circuitMeta(1)
                .outputFluids(GTMFOFluids.PasteurizedMilk.getFluid(4000))
                .EUt(16).duration(800).save(provider);
        GTRecipeTypes.FLUID_HEATER_RECIPES.recipeBuilder(id("pasteurized_milk_fast"))
                .inputFluids(GTMaterials.Milk.getFluid(4000))
                .circuitMeta(2)
                .outputFluids(GTMFOFluids.PasteurizedMilk.getFluid(4000))
                .EUt(256).duration(40).save(provider);
        GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("skimmed_milk_cream"))
                .inputFluids(GTMFOFluids.PasteurizedMilk.getFluid(1000))
                .outputFluids(GTMFOFluids.SkimmedMilk.getFluid(800), GTMFOFluids.Cream.getFluid(200))
                .EUt(24).duration(100).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("milk_colloid"))
                .inputFluids(GTMaterials.Milk.getFluid(500), GTMFOFluids.SoyLecithin.getFluid(10))
                .inputItems(TagPrefix.dust, GTMaterials.Sugar)
                .outputFluids(GTMFOFluids.MilkColloid.getFluid(500))
                .EUt(24).duration(160).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("ice_cream_mixture"))
                .inputFluids(GTMFOFluids.MilkColloid.getFluid(500), GTMFOFluids.Cream.getFluid(500))
                .outputFluids(GTMFOFluids.IceCreamMixture.getFluid(1000))
                .EUt(24).duration(200).save(provider);
        GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES.recipeBuilder(id("ice_cream"))
                .inputFluids(GTMFOFluids.IceCreamMixture.getFluid(144))
                .notConsumable(GTItems.SHAPE_MOLD_BALL.asStack())
                .outputItems(GTMFOItems.ICE_CREAM.asStack())
                .EUt(7).duration(70).save(provider);
    }

    private static void flavours(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("ice_cream_chum"))
                .inputItems(GTMFOItems.ICE_CREAM.asStack(4), GTMFOItems.CHUM.asStack())
                .outputItems(GTMFOItems.ICE_CREAM_CHUM.asStack(4))
                .EUt(24).duration(120).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("ice_cream_chocolate"))
                .inputItems(GTMFOItems.ICE_CREAM.asStack(4))
                .inputFluids(GTMFOFluids.MoltenMilkChocolate.getFluid(72))
                .outputItems(GTMFOItems.ICE_CREAM_CHOCOLATE.asStack(4))
                .EUt(24).duration(120).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("ice_cream_vanilla"))
                .inputItems(GTMFOItems.ICE_CREAM.asStack(4),
                        ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Vanillin))
                .outputItems(GTMFOItems.ICE_CREAM_VANILLA.asStack(4))
                .EUt(24).duration(120).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("ice_cream_banana"))
                .inputItems(GTMFOItems.ICE_CREAM.asStack(4), GTMFOItems.BANANA_PEELED.asStack())
                .outputItems(GTMFOItems.ICE_CREAM_BANANA.asStack(4))
                .EUt(24).duration(120).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("ice_cream_bacon"))
                .inputItems(GTMFOItems.ICE_CREAM.asStack(4), GTMFOItems.BACON.asStack())
                .outputItems(GTMFOItems.ICE_CREAM_BACON.asStack(4))
                .EUt(24).duration(120).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("ice_cream_bear"))
                .inputItems(GTMFOItems.ICE_CREAM.asStack(4), GTMFOItems.MEAT_INGOT.asStack())
                .outputItems(GTMFOItems.ICE_CREAM_BEAR.asStack(4))
                .EUt(24).duration(120).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("ice_cream_melon"))
                .inputItems(GTMFOItems.ICE_CREAM.asStack(4))
                .inputFluids(GTMFOFluids.MELON_EXTRACT.getFluid(50))
                .outputItems(GTMFOItems.ICE_CREAM_MELON.asStack(4))
                .EUt(24).duration(120).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("ice_cream_lemon"))
                .inputItems(GTMFOItems.ICE_CREAM.asStack(4))
                .inputFluids(GTMFOFluids.LemonExtract.getFluid(25))
                .outputItems(GTMFOItems.ICE_CREAM_LEMON.asStack(4))
                .EUt(24).duration(120).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("ice_cream_chip"))
                .inputItems(GTMFOItems.ICE_CREAM.asStack(4), GTMFOItems.POTATO_SLICE_FRIED.asStack(3))
                .outputItems(GTMFOItems.ICE_CREAM_CHIP.asStack(4))
                .EUt(24).duration(120).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("ice_cream_rainbow"))
                .inputItems(GTMFOItems.ICE_CREAM.asStack(4))
                .inputFluids(GTMFOFluids.RainbowSap.getFluid(25))
                .outputItems(GTMFOItems.ICE_CREAM_RAINBOW.asStack(4))
                .EUt(24).duration(120).save(provider);
    }
}
