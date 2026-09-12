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

public class AlcoholRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        vodka(provider);
        wine(provider);
    }

    private static void vodka(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("potato_juice"))
                .inputItems(Items.POTATO)
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .outputFluids(GTMFOFluids.PotatoJuice.getFluid(1000))
                .EUt(8).duration(1000).save(provider);
        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("wheaty_juice"))
                .inputItems(Items.WHEAT)
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .outputFluids(GTMFOFluids.WheatyJuice.getFluid(1000))
                .EUt(8).duration(1000).save(provider);

        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("vodka"))
                .inputFluids(GTMFOFluids.PotatoJuice.getFluid(2000))
                .outputFluids(GTMFOFluids.Vodka.getFluid(2000))
                .EUt(8).duration(3000).save(provider);
        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("poor_quality_beer"))
                .inputFluids(GTMFOFluids.WheatyJuice.getFluid(2000))
                .outputFluids(GTMFOFluids.PoorQualityBeer.getFluid(2000))
                .EUt(8).duration(3000).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("leninade"))
                .inputFluids(GTMFOFluids.Vodka.getFluid(1000), GTMFOFluids.LemonExtract.getFluid(100))
                .outputFluids(GTMFOFluids.Leninade.getFluid(1100))
                .EUt(12).duration(30).save(provider);

        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("vodka_bottle"))
                .inputItems(Items.GLASS_BOTTLE)
                .inputFluids(GTMFOFluids.Vodka.getFluid(100))
                .outputItems(GTMFOItems.VODKA.asStack())
                .EUt(12).duration(30).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("leninade_bottle"))
                .inputItems(Items.GLASS_BOTTLE)
                .inputFluids(GTMFOFluids.Leninade.getFluid(100))
                .outputItems(GTMFOItems.LENINADE.asStack())
                .EUt(12).duration(30).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("beer_bottle"))
                .inputItems(Items.GLASS_BOTTLE)
                .inputFluids(GTMFOFluids.PoorQualityBeer.getFluid(100))
                .outputItems(GTMFOItems.BEER.asStack())
                .EUt(12).duration(30).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("nilk"))
                .inputFluids(GTMaterials.Milk.getFluid(1000))
                .inputItems(TagPrefix.dustTiny, GTMaterials.Naquadah)
                .outputFluids(GTMFOFluids.Nilk.getFluid(1000))
                .EUt(800).duration(1000).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("nilk_bottle"))
                .inputItems(Items.GLASS_BOTTLE)
                .inputFluids(GTMFOFluids.Nilk.getFluid(200))
                .outputItems(GTMFOItems.NILK.asStack())
                .EUt(12).duration(30).save(provider);
    }

    private static void wine(Consumer<FinishedRecipe> provider){
        // white wine
        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("macerated_white_grapes"))
                .inputItems(GTMFOItems.WHITE_GRAPES.asStack(10))
                .inputFluids(GTMaterials.Water.getFluid(4000))
                .outputFluids(GTMFOFluids.MaceratedWhiteGrapes.getFluid(4000))
                .EUt(4).duration(500).save(provider);
        GTRecipeTypes.COMPRESSOR_RECIPES.recipeBuilder(id("pressed_white_wort"))
                .inputFluids(GTMFOFluids.MaceratedWhiteGrapes.getFluid(1000))
                .outputFluids(GTMFOFluids.PressedWhiteWort.getFluid(1000))
                .EUt(24).duration(100).save(provider);
        GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("clarified_white_wort"))
                .inputFluids(GTMFOFluids.PressedWhiteWort.getFluid(1000))
                .outputFluids(GTMFOFluids.ClarifiedWhiteWort.getFluid(800), GTMaterials.Biomass.getFluid(200))
                .EUt(16).duration(200).save(provider);
        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("white_wine"))
                .inputFluids(GTMFOFluids.ClarifiedWhiteWort.getFluid(8000))
                .outputFluids(GTMFOFluids.WhiteWine.getFluid(8000))
                .EUt(2).duration(4000).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("white_wine_bottle"))
                .inputItems(Items.GLASS_BOTTLE)
                .inputFluids(GTMFOFluids.WhiteWine.getFluid(200))
                .outputItems(GTMFOItems.WHITE_WINE.asStack())
                .EUt(12).duration(30).save(provider);

        // red wine
        GTRecipeTypes.COMPRESSOR_RECIPES.recipeBuilder(id("red_grapes_must"))
                .inputItems(GTMFOItems.GRAPES.asStack(8))
                .outputItems(GTItems.PLANT_BALL.asStack())
                .outputFluids(GTMFOFluids.RedGrapesMust.getFluid(4000))
                .EUt(16).duration(300).save(provider);
        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("fermented_red_grapes_must"))
                .inputFluids(GTMFOFluids.RedGrapesMust.getFluid(8000))
                .outputFluids(GTMFOFluids.FermentedRedGrapesMust.getFluid(8000))
                .EUt(8).duration(8000).save(provider);
        GTRecipeTypes.COMPRESSOR_RECIPES.recipeBuilder(id("alcoholic_red_grape_juice"))
                .inputFluids(GTMFOFluids.FermentedRedGrapesMust.getFluid(1000))
                .outputItems(GTItems.BIO_CHAFF.asStack())
                .outputFluids(GTMFOFluids.AlcoholicRedGrapeJuice.getFluid(1000))
                .EUt(24).duration(500).save(provider);
        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("red_wine"))
                .inputFluids(GTMFOFluids.AlcoholicRedGrapeJuice.getFluid(8000))
                .outputFluids(GTMFOFluids.RedWine.getFluid(8000))
                .EUt(2).duration(8000).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("red_wine_bottle"))
                .inputItems(Items.GLASS_BOTTLE)
                .inputFluids(GTMFOFluids.RedWine.getFluid(200))
                .outputItems(GTMFOItems.RED_WINE.asStack())
                .EUt(12).duration(30).save(provider);
    }
}
