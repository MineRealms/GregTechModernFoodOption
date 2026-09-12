package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

/**
 * Fat / stearin chain, ported from the original {@code FatChain}: meat maceration, animal fat, stearin,
 * sodium stearate (a kitchen cleaner) and sludge processing.
 */
public class FatRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        meatProcessing(provider);
        stearin(provider);
        sludge(provider);
    }

    private static void meatProcessing(Consumer<FinishedRecipe> provider){
        Item[] meats = { Items.BEEF, Items.CHICKEN, Items.MUTTON, Items.PORKCHOP, Items.RABBIT };
        String[] names = { "beef", "chicken", "mutton", "porkchop", "rabbit" };
        for (int i = 0; i < meats.length; i++) {
            GTRecipeTypes.MACERATOR_RECIPES.recipeBuilder(id("animal_fat_" + names[i]))
                    .inputItems(new ItemStack(meats[i], 8))
                    .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Meat, 13))
                    .outputItems(ChemicalHelper.get(TagPrefix.dustSmall, GTMaterials.Bone, 8))
                    .outputItems(GTMFOItems.ANIMAL_FAT.asStack(8))
                    .chancedOutput(GTMFOItems.ANIMAL_FAT.asStack(4), 5000, 2000)
                    .EUt(20).duration(400).save(provider);

            GTRecipeTypes.LARGE_CHEMICAL_RECIPES.recipeBuilder(id("meat_extract_" + names[i]))
                    .inputItems(new ItemStack(meats[i], 32))
                    .inputFluids(GTMaterials.Methanol.getFluid(4000), GTMaterials.Chloroform.getFluid(4000))
                    .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Meat, 40))
                    .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Bone, 16))
                    .outputFluids(GTMFOFluids.Stearin.getFluid(3200), GTMFOFluids.Sludge.getFluid(12000),
                            GTMaterials.Chlorine.getFluid(12000))
                    .EUt(256).duration(1000).save(provider);
        }

        GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("meat_dust_centrifuge"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Meat, 3))
                .outputFluids(GTMaterials.Biomass.getFluid(200), GTMFOFluids.Stearin.getFluid(10))
                .EUt(30).duration(300).save(provider);

        GTRecipeTypes.FERMENTING_RECIPES.recipeBuilder(id("scrap_meat_ferment"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Meat))
                .inputFluids(GTMaterials.Chloroform.getFluid(100))
                .outputFluids(GTMFOFluids.Stearin.getFluid(40))
                .EUt(32).duration(1200).save(provider);
    }

    private static void stearin(Consumer<FinishedRecipe> provider){
        // plant oils -> stearin (magic!)
        var oils = new com.gregtechceu.gtceu.api.data.chemical.material.Material[] {
                GTMaterials.FishOil, GTMaterials.SeedOil, GTMFOFluids.OliveOil, GTMFOFluids.FryingOil
        };
        for (int i = 0; i < oils.length; i++) {
            GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("stearin_from_oil_" + i))
                    .inputItems(ChemicalHelper.get(TagPrefix.dustTiny, GTMaterials.SodaAsh))
                    .inputFluids(oils[i].getFluid(1000), GTMaterials.Hydrogen.getFluid(1000))
                    .outputFluids(GTMFOFluids.Stearin.getFluid(100))
                    .circuitMeta(1)
                    .EUt(30).duration(300).save(provider);
        }

        GTRecipeTypes.EXTRACTOR_RECIPES.recipeBuilder(id("stearin_from_fat"))
                .inputItems(GTMFOItems.ANIMAL_FAT.asStack())
                .outputFluids(GTMFOFluids.Stearin.getFluid(100))
                .EUt(16).duration(10).save(provider);

        GTRecipeTypes.FLUID_SOLIDFICATION_RECIPES.recipeBuilder(id("animal_fat"))
                .inputFluids(GTMFOFluids.Stearin.getFluid(100))
                .notConsumable(GTItems.SHAPE_MOLD_INGOT.asStack())
                .outputItems(GTMFOItems.ANIMAL_FAT.asStack())
                .EUt(16).duration(60).save(provider);

        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("sodium_stearate"))
                .inputFluids(GTMFOFluids.Stearin.getFluid(1000), GTMaterials.Water.getFluid(2000))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMaterials.SodiumHydroxide, 3))
                .outputFluids(GTMFOFluids.SodiumStearate.getFluid(3000), GTMaterials.Glycerol.getFluid(1000))
                .EUt(120).duration(40).save(provider);

        // stearin + alcohol -> biodiesel + glycerol
        var alcohols = new com.gregtechceu.gtceu.api.data.chemical.material.Material[] {
                GTMaterials.Methanol, GTMaterials.Ethanol
        };
        for (int i = 0; i < alcohols.length; i++) {
            GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("biodiesel_from_stearin_" + i))
                    .inputItems(ChemicalHelper.get(TagPrefix.dustTiny, GTMaterials.SodiumHydroxide))
                    .inputFluids(GTMFOFluids.Stearin.getFluid(3000), alcohols[i].getFluid(1000))
                    .outputFluids(GTMaterials.Glycerol.getFluid(1000), GTMaterials.BioDiesel.getFluid(6000))
                    .EUt(30).duration(600).save(provider);
            GTRecipeTypes.LARGE_CHEMICAL_RECIPES.recipeBuilder(id("biodiesel_from_stearin_large_" + i))
                    .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMaterials.SodiumHydroxide))
                    .inputFluids(GTMFOFluids.Stearin.getFluid(27000), alcohols[i].getFluid(9000))
                    .outputFluids(GTMaterials.Glycerol.getFluid(9000), GTMaterials.BioDiesel.getFluid(54000))
                    .EUt(30).duration(5400).save(provider);
        }
    }

    private static void sludge(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.LARGE_CHEMICAL_RECIPES.recipeBuilder(id("sludge_to_biomass"))
                .inputFluids(GTMFOFluids.Sludge.getFluid(24000), GTMaterials.Bacteria.getFluid(1000))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMaterials.Calcite, 3))
                .notConsumable(GTItems.CARBON_MESH.asStack())
                .notConsumable(com.gregtechceu.gtceu.api.recipe.ingredient.IntCircuitIngredient.of(2))
                .outputFluids(GTMaterials.Biomass.getFluid(10000), GTMaterials.Water.getFluid(14000),
                        GTMaterials.SulfurDioxide.getFluid(8000), GTMaterials.Methane.getFluid(8000))
                .EUt(30).duration(800).save(provider);
    }
}
