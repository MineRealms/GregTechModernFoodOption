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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class PlateRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        boneChinaClay(provider);
        ceramics(provider);
        porcelainTile(provider);
    }

    private static void boneChinaClay(Consumer<FinishedRecipe> provider){
        record Feldspar(String name, ItemStack stack) {}
        record Calcium(String name, ItemStack stack) {}

        List<Feldspar> feldspars = List.of(
                new Feldspar("stone", ChemicalHelper.get(TagPrefix.dust, GTMaterials.Stone, 8)),
                new Feldspar("granite", ChemicalHelper.get(TagPrefix.dust, GTMaterials.Granite, 3)),
                new Feldspar("granite_red", ChemicalHelper.get(TagPrefix.dust, GTMaterials.GraniteRed, 3)),
                new Feldspar("potassium_feldspar", ChemicalHelper.get(TagPrefix.dust, GTMaterials.PotassiumFeldspar)));
        List<Calcium> calciums = List.of(
                new Calcium("apatite", ChemicalHelper.get(TagPrefix.dust, GTMaterials.Apatite, 6)),
                new Calcium("tricalcium_phosphate", ChemicalHelper.get(TagPrefix.dust, GTMaterials.TricalciumPhosphate, 4)),
                new Calcium("bone_meal", new ItemStack(Items.BONE_MEAL, 3)),
                new Calcium("bone_ash", ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.BoneAsh)));

        for (Feldspar feldspar : feldspars) {
            for (Calcium calcium : calciums) {
                GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("bone_china_clay_" + feldspar.name() + "_" + calcium.name()))
                        .inputItems(Items.CLAY_BALL)
                        .inputItems(feldspar.stack().copy())
                        .inputItems(calcium.stack().copy())
                        .circuitMeta(11)
                        .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.BoneChinaClay))
                        .EUt(8).duration(100).save(provider);
            }
        }
    }

    private static void ceramics(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.COMPRESSOR_RECIPES.recipeBuilder(id("unfired_ceramic_plate"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.BoneChinaClay, 4))
                .outputItems(GTMFOItems.CERAMIC_PLATE_UNFIRED.asStack())
                .EUt(8).duration(80).save(provider);
        GTRecipeTypes.LATHE_RECIPES.recipeBuilder(id("unfired_ceramic_bowl"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.BoneChinaClay, 6))
                .outputItems(GTMFOItems.CERAMIC_BOWL_UNFIRED.asStack())
                .EUt(8).duration(80).save(provider);
        GTRecipeTypes.ALLOY_SMELTER_RECIPES.recipeBuilder(id("ceramic_plate"))
                .inputItems(TagPrefix.dust, GTMaterials.Glass)
                .inputItems(GTMFOItems.CERAMIC_PLATE_UNFIRED.asStack())
                .outputItems(GTMFOItems.CERAMIC_PLATE.asStack())
                .EUt(16).duration(800).save(provider);
        GTRecipeTypes.ALLOY_SMELTER_RECIPES.recipeBuilder(id("ceramic_bowl"))
                .inputItems(TagPrefix.dust, GTMaterials.Glass)
                .inputItems(GTMFOItems.CERAMIC_BOWL_UNFIRED.asStack())
                .outputItems(GTMFOItems.CERAMIC_BOWL.asStack())
                .EUt(16).duration(800).save(provider);

        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("clean_ceramic_plate_water"))
                .inputItems(GTMFOItems.CERAMIC_PLATE_DIRTY.asStack())
                .inputFluids(GTMaterials.Water.getFluid(2000))
                .outputItems(GTMFOItems.CERAMIC_PLATE.asStack())
                .EUt(8).duration(800).save(provider);
        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("clean_ceramic_bowl_water"))
                .inputItems(GTMFOItems.CERAMIC_BOWL_DIRTY.asStack())
                .inputFluids(GTMaterials.Water.getFluid(2000))
                .outputItems(GTMFOItems.CERAMIC_BOWL.asStack())
                .EUt(8).duration(800).save(provider);
        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("clean_ceramic_plate_cleaner"))
                .inputItems(GTMFOItems.CERAMIC_PLATE_DIRTY.asStack())
                .inputFluids(GTMFOFluids.SodiumStearate.getFluid(100))
                .outputItems(GTMFOItems.CERAMIC_PLATE.asStack())
                .EUt(16).duration(100).save(provider);
        GTRecipeTypes.CHEMICAL_BATH_RECIPES.recipeBuilder(id("clean_ceramic_bowl_cleaner"))
                .inputItems(GTMFOItems.CERAMIC_BOWL_DIRTY.asStack())
                .inputFluids(GTMFOFluids.SodiumStearate.getFluid(100))
                .outputItems(GTMFOItems.CERAMIC_BOWL.asStack())
                .EUt(16).duration(100).save(provider);
    }

    private static void porcelainTile(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.FORMING_PRESS_RECIPES.recipeBuilder(id("unfired_porcelain_tile"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.BoneChinaClay, 2))
                .notConsumable(GTItems.SHAPE_MOLD_PLATE.asStack())
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.UnfiredPorcelainTile))
                .EUt(28).duration(160).save(provider);
        GTRecipeTypes.BLAST_RECIPES.recipeBuilder(id("biscuit_porcelain_tile"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.UnfiredPorcelainTile))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.BiscuitPorcelainTile))
                .blastFurnaceTemp(1600)
                .EUt(120).duration(800).save(provider);
        GTRecipeTypes.BLAST_RECIPES.recipeBuilder(id("glazed_porcelain_tile"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.BiscuitPorcelainTile))
                .inputItems(TagPrefix.dust, GTMaterials.Glass)
                .circuitMeta(1)
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.GlazedPorcelainTile))
                .blastFurnaceTemp(1600)
                .EUt(120).duration(500).save(provider);
        GTRecipeTypes.BLAST_RECIPES.recipeBuilder(id("black_glazed_porcelain_tile"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.BiscuitPorcelainTile))
                .inputItems(TagPrefix.dust, GTMaterials.Glass)
                .inputFluids(GTMaterials.DyeBlack.getFluid(100))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.BlackGlazedPorcelainTile))
                .blastFurnaceTemp(1600)
                .EUt(120).duration(500).save(provider);
    }
}
