package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.ironsword.gtmfo.GTMFOConfigHolder;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

/**
 * Vanilla override chain, ported from the original {@code VanillaOverrideChain}.
 * Gated behind {@code gtfoVanillaOverridesConfig.vanillaOverrideChain}.
 */
public class VanillaOverrideRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        baking(provider);
        soups(provider);
        if (GTMFOConfigHolder.INSTANCE.gtfoVanillaOverridesConfig.useRollingPinForPaper) {
            rollingPin(provider);
        }
        goldenFoods(provider);
    }

    private static void baking(Consumer<FinishedRecipe> provider){
        // potato is always overridden
        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("baked_potato"))
                .inputItems(Items.POTATO)
                .outputItems(Items.BAKED_POTATO)
                .EUt(60).duration(450).save(provider);

        if (GTMFOConfigHolder.INSTANCE.gtfoVanillaOverridesConfig.useBakingOvenForMeats) {
            bakedMeat(provider, "beef", Items.BEEF, Items.COOKED_BEEF, 1000);
            bakedMeat(provider, "porkchop", Items.PORKCHOP, Items.COOKED_PORKCHOP, 900);
            bakedMeat(provider, "mutton", Items.MUTTON, Items.COOKED_MUTTON, 800);
            bakedMeat(provider, "chicken", Items.CHICKEN, Items.COOKED_CHICKEN, 1500);
            bakedMeat(provider, "rabbit", Items.RABBIT, Items.COOKED_RABBIT, 1000);
            bakedMeat(provider, "cod", Items.COD, Items.COOKED_COD, 400);
            bakedMeat(provider, "salmon", Items.SALMON, Items.COOKED_SALMON, 400);
        }
    }

    private static void bakedMeat(Consumer<FinishedRecipe> provider, String name, net.minecraft.world.item.Item raw,
                                  net.minecraft.world.item.Item cooked, int duration){
        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("cooked_" + name))
                .inputItems(raw)
                .outputItems(cooked)
                .EUt(60).duration(duration).save(provider);
    }

    private static void soups(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("mushroom_soup"))
                .inputItems(Items.BROWN_MUSHROOM)
                .inputItems(Items.RED_MUSHROOM)
                .inputItems(TagPrefix.dust, GTMaterials.Wheat)
                .inputFluids(GTMaterials.Milk.getFluid(200))
                .outputFluids(GTMFOFluids.MushroomSoup.getFluid(200))
                .EUt(8).duration(100).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("mushroom_stew"))
                .inputFluids(GTMFOFluids.MushroomSoup.getFluid(50))
                .inputItems(Items.BOWL)
                .outputItems(Items.MUSHROOM_STEW)
                .EUt(8).duration(10).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("beetroot_soup"))
                .inputItems(Items.BEETROOT, 2)
                .inputItems(TagPrefix.dust, GTMaterials.Wheat)
                .inputFluids(GTMaterials.Water.getFluid(200))
                .outputFluids(GTMFOFluids.BeetrootSoup.getFluid(200))
                .EUt(8).duration(100).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("beetroot_soup_bowl"))
                .inputFluids(GTMFOFluids.BeetrootSoup.getFluid(50))
                .inputItems(Items.BOWL)
                .outputItems(Items.BEETROOT_SOUP)
                .EUt(8).duration(10).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("rabbit_stew"))
                .inputItems(Items.BROWN_MUSHROOM)
                .inputItems(Items.CARROT)
                .inputItems(Items.COOKED_RABBIT)
                .inputItems(GTMFOItems.POTATO_MASHED)
                .inputItems(TagPrefix.dust, GTMaterials.Wheat)
                .inputFluids(GTMaterials.Water.getFluid(50))
                .outputFluids(GTMFOFluids.RabbitStew.getFluid(500))
                .EUt(8).duration(100).save(provider);
        GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("rabbit_stew_bowl"))
                .inputFluids(GTMFOFluids.RabbitStew.getFluid(50))
                .inputItems(Items.BOWL)
                .outputItems(Items.RABBIT_STEW)
                .EUt(8).duration(10).save(provider);
    }

    private static void rollingPin(Consumer<FinishedRecipe> provider){
        VanillaRecipeHelper.addShapedRecipe(provider, id("paper_rolling_pin"),
                new ItemStack(Items.PAPER, 2),
                " R ",
                "CCC",
                'R', com.ironsword.gtmfo.common.data.GTMFOTools.ROLLING_PINS.get(GTMaterials.Wood).asStack(),
                'C', ChemicalHelper.get(TagPrefix.dust, GTMaterials.Paper));
        VanillaRecipeHelper.addShapedRecipe(provider, id("sticky_piston_resin"),
                new ItemStack(Items.STICKY_PISTON),
                "R",
                "S",
                "P",
                'R', com.ironsword.gtmfo.common.data.GTMFOTools.ROLLING_PINS.get(GTMaterials.Wood).asStack(),
                'S', com.gregtechceu.gtceu.common.data.GTItems.STICKY_RESIN.asStack(),
                'P', Items.PISTON);
        VanillaRecipeHelper.addShapedRecipe(provider, id("sticky_piston_slime"),
                new ItemStack(Items.STICKY_PISTON),
                "R",
                "S",
                "P",
                'R', com.ironsword.gtmfo.common.data.GTMFOTools.ROLLING_PINS.get(GTMaterials.Wood).asStack(),
                'S', Items.SLIME_BALL,
                'P', Items.PISTON);
    }

    private static void goldenFoods(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("carrot_structural_mesh"))
                .inputItems(Items.CARROT)
                .outputItems(GTMFOItems.CARROT_STRUCTURAL_MESH)
                .EUt(1920).duration(200).save(provider);
        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("apple_structural_mesh"))
                .inputItems(Items.APPLE)
                .outputItems(GTMFOItems.APPLE_STRUCTURAL_MESH)
                .EUt(1920).duration(200).save(provider);

        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("golden_carrot"))
                .inputItems(GTMFOItems.CARROT_STRUCTURAL_MESH)
                .inputFluids(GTMFOFluids.ChloroauricAcid.getFluid(3000))
                .outputItems(Items.GOLDEN_CARROT)
                .EUt(1920).duration(1500).save(provider);
        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("golden_apple"))
                .inputItems(GTMFOItems.APPLE_STRUCTURAL_MESH)
                .inputFluids(GTMFOFluids.ChloroauricAcid.getFluid(6000))
                .outputItems(Items.GOLDEN_APPLE)
                .EUt(100000).duration(8000).save(provider);
        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("enchanted_golden_apple"))
                .inputItems(GTMFOItems.APPLE_STRUCTURAL_MESH)
                .inputFluids(GTMFOFluids.ChloroauricAcid.getFluid(54000))
                .outputItems(Items.ENCHANTED_GOLDEN_APPLE)
                .EUt(100000000).duration(100000).save(provider);
        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("tungstensteel_apple"))
                .inputItems(GTMFOItems.APPLE_STRUCTURAL_MESH)
                .inputFluids(GTMaterials.TungstenSteel.getFluid(12000))
                .outputItems(GTMFOItems.APPLE_TUNGSTENSTEEL)
                .EUt(1000000).duration(10000).save(provider);
    }
}
