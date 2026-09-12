package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class SorbetRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("sorbet"))
                .inputItems(TagPrefix.dust, GTMaterials.Ice, 4)
                .inputItems(TagPrefix.dustTiny, GTMaterials.Sugar)
                .circuitMeta(1)
                .outputItems(GTMFOItems.SORBET.asStack(4))
                .EUt(30).duration(200).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("sorbet_apple"))
                .inputItems(TagPrefix.dust, GTMaterials.Ice, 4)
                .inputItems(TagPrefix.dustTiny, GTMaterials.Sugar)
                .inputFluids(GTMFOFluids.AppleExtract.getFluid(50))
                .outputItems(GTMFOItems.SORBET_APPLE.asStack(4))
                .EUt(30).duration(200).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("sorbet_apricot"))
                .inputItems(TagPrefix.dust, GTMaterials.Ice, 4)
                .inputItems(TagPrefix.dustTiny, GTMaterials.Sugar)
                .inputFluids(GTMFOFluids.ApricotExtract.getFluid(40), GTMFOFluids.LemonExtract.getFluid(10))
                .outputItems(GTMFOItems.SORBET_APRICOT.asStack(4))
                .EUt(30).duration(200).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("sorbet_grape"))
                .inputItems(TagPrefix.dust, GTMaterials.Ice, 4)
                .inputItems(TagPrefix.dustTiny, GTMaterials.Sugar)
                .inputFluids(GTMFOFluids.GRAPE_EXTRACT.getFluid(50))
                .outputItems(GTMFOItems.SORBET_GRAPE.asStack(4))
                .EUt(30).duration(200).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("sorbet_lime"))
                .inputItems(TagPrefix.dust, GTMaterials.Ice, 4)
                .inputItems(TagPrefix.dustTiny, GTMaterials.Sugar)
                .inputFluids(GTMFOFluids.LIME_EXTRACT.getFluid(50))
                .outputItems(GTMFOItems.SORBET_LIME.asStack(4))
                .EUt(30).duration(200).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("sorbet_chorus"))
                .inputItems(TagPrefix.dust, GTMaterials.Ice, 4)
                .inputItems(TagPrefix.dustTiny, GTMaterials.Sugar)
                .inputFluids(GTMFOFluids.ChorusJuice.getFluid(40), GTMFOFluids.LemonExtract.getFluid(10))
                .outputItems(GTMFOItems.SORBET_CHORUS.asStack(4))
                .EUt(30).duration(200).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("sorbet_vibrant"))
                .inputItems(TagPrefix.dust, GTMaterials.Ice, 4)
                .inputItems(TagPrefix.dustTiny, GTMaterials.Sugar)
                .inputFluids(GTMFOFluids.VibrantExtract.getFluid(40), GTMFOFluids.LemonExtract.getFluid(10))
                .outputItems(GTMFOItems.SORBET_VIBRANT.asStack(4))
                .EUt(30).duration(200).save(provider);
    }
}
