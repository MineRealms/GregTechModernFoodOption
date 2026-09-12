package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.material.GTMFOMaterials;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class BritishRecipes {

    private static final List<ItemStack> FISH = List.of(
            new ItemStack(Items.COD),
            new ItemStack(Items.SALMON),
            new ItemStack(Items.TROPICAL_FISH),
            new ItemStack(Items.PUFFERFISH));

    public static void init(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.CHEMICAL_RECIPES.recipeBuilder(id("sodium_bicarbonate"))
                .inputItems(TagPrefix.dust, GTMaterials.SodaAsh, 6)
                .inputFluids(GTMaterials.CarbonDioxide.getFluid(1000), GTMaterials.Water.getFluid(1000))
                .outputItems(TagPrefix.dust, GTMaterials.SodiumBicarbonate, 12)
                .EUt(30).duration(300).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("baking_soda_solution"))
                .inputItems(TagPrefix.dust, GTMaterials.SodiumBicarbonate)
                .inputFluids(GTMaterials.Water.getFluid(1000))
                .outputFluids(GTMFOFluids.BakingSodaSolution.getFluid(1000))
                .EUt(8).duration(60).save(provider);

        GTRecipeTypes.BREWING_RECIPES.recipeBuilder(id("mushy_peas"))
                .inputFluids(GTMFOFluids.BakingSodaSolution.getFluid(500))
                .inputItems(GTMFOItems.SEED_PEA.asStack())
                .outputItems(GTMFOItems.MUSHY_PEAS.asStack())
                .EUt(4).duration(1280).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("beer_batter"))
                .inputItems(TagPrefix.dust, GTMaterials.Wheat, 6)
                .inputItems(TagPrefix.dustTiny, GTMaterials.Salt)
                .inputFluids(GTMFOFluids.PoorQualityBeer.getFluid(200))
                .outputFluids(GTMFOFluids.BeerBatter.getFluid(200))
                .EUt(8).duration(200).save(provider);
        for (int i = 0; i < FISH.size(); i++) {
            GTRecipeTypes.BREWING_RECIPES.recipeBuilder(id("fried_fish_" + i))
                    .inputFluids(GTMFOFluids.BeerBatter.getFluid(40))
                    .inputItems(FISH.get(i).copy())
                    .outputItems(GTMFOItems.FRIED_FISH.asStack())
                    .EUt(16).duration(400).save(provider);
        }

        VanillaRecipeHelper.addShapelessRecipe(provider, id("fish_and_chips_hand"),
                GTMFOItems.FISH_AND_CHIPS.asStack(),
                GTMFOItems.FRIED_FISH.asStack(), GTMFOItems.FRENCH_FRIES.asStack());
        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("fish_and_chips"))
                .inputItems(GTMFOItems.FRIED_FISH.asStack(), GTMFOItems.FRENCH_FRIES.asStack())
                .outputItems(GTMFOItems.FISH_AND_CHIPS.asStack())
                .EUt(8).duration(10).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("full_breakfast"))
                .inputItems(GTMFOItems.BACON.asStack(), GTMFOItems.BAKED_BEANS.asStack(),
                        GTMFOItems.MUSHROOM_SLICE.asStack(8), GTMFOItems.TOAST.asStack(),
                        GTMFOItems.TOMATO_SLICE.asStack(2))
                .outputItems(GTMFOItems.FULL_BREAKFAST.asStack())
                .EUt(64).duration(200).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("beans_with_sauce"))
                .inputItems(GTMFOItems.SEED_BEAN.asStack())
                .inputFluids(GTMFOFluids.TomatoSauce.getFluid(100))
                .outputItems(GTMFOItems.BEANS_WITH_SAUCE.asStack())
                .EUt(8).duration(80).save(provider);
        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("baked_beans"))
                .inputItems(GTMFOItems.BEANS_WITH_SAUCE.asStack())
                .outputItems(GTMFOItems.BAKED_BEANS.asStack())
                .EUt(30).duration(1000).save(provider);
        VanillaRecipeHelper.addShapelessRecipe(provider, id("beans_on_toast_hand"),
                GTMFOItems.BEANS_ON_TOAST.asStack(),
                GTMFOItems.BAKED_BEANS.asStack(), GTMFOItems.TOAST.asStack());

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("shepherds_pie"))
                .inputItems(GTMFOItems.POTATO_MASHED.asStack(),
                        GTMFOItems.MINCE_MEAT_COOKED.asStack(), GTMFOItems.SEED_PEA.asStack())
                .outputItems(GTMFOItems.SHEPHERDS_PIE.asStack())
                .EUt(64).duration(200).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("laminated_dough"))
                .inputItems(GTMFOItems.DOUGH.asStack())
                .inputFluids(GTMFOFluids.Butter.getFluid(100))
                .outputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.LaminatedDough))
                .EUt(24).duration(400).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("sausage_roll_raw"))
                .inputItems(ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.LaminatedDough),
                        GTMFOItems.SAUSAGE_RAW.asStack())
                .outputItems(GTMFOItems.SAUSAGE_ROLL_RAW.asStack())
                .EUt(8).duration(100).save(provider);
        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("sausage_roll"))
                .inputItems(GTMFOItems.SAUSAGE_ROLL_RAW.asStack())
                .outputItems(GTMFOItems.SAUSAGE_ROLL.asStack())
                .EUt(30).duration(250).save(provider);

        GTRecipeTypes.EXTRUDER_RECIPES.recipeBuilder(id("sausage_raw"))
                .inputItems(GTMFOItems.MEAT_INGOT.asStack())
                .notConsumable(GTItems.SHAPE_EXTRUDER_ROD)
                .outputItems(GTMFOItems.SAUSAGE_RAW.asStack())
                .EUt(16).duration(100).save(provider);
    }
}
