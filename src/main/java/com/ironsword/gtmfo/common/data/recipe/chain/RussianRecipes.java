package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class RussianRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        VanillaRecipeHelper.addShapelessRecipe(provider, id("pelmeni_hand"),
                GTMFOItems.PELMENI_UNCOOKED.asStack(),
                GTMFOItems.DOUGH.asStack(),
                com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(TagPrefix.dust, GTMaterials.Meat));
        VanillaRecipeHelper.addShapelessRecipe(provider, id("pelmeni_hand_3"),
                GTMFOItems.PELMENI_UNCOOKED.asStack(3),
                GTMFOItems.DOUGH.asStack(3),
                com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper.get(TagPrefix.dust, GTMaterials.Meat),
                new net.minecraft.world.item.ItemStack(Items.BROWN_MUSHROOM));

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("pelmeni_uncooked"))
                .inputItems(GTMFOItems.DOUGH.asStack())
                .inputItems(TagPrefix.dust, GTMaterials.Meat)
                .circuitMeta(1)
                .outputItems(GTMFOItems.PELMENI_UNCOOKED.asStack())
                .EUt(24).duration(100).save(provider);
        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("pelmeni_uncooked_3"))
                .inputItems(GTMFOItems.DOUGH.asStack(3))
                .inputItems(TagPrefix.dust, GTMaterials.Meat)
                .inputItems(Items.BROWN_MUSHROOM)
                .circuitMeta(2)
                .outputItems(GTMFOItems.PELMENI_UNCOOKED.asStack(3))
                .EUt(24).duration(100).save(provider);

        GTRecipeTypes.CENTRIFUGE_RECIPES.recipeBuilder(id("lactic_acid_bacteria"))
                .inputFluids(GTMaterials.Milk.getFluid(8000))
                .circuitMeta(2)
                .outputFluids(GTMFOFluids.LacticAcidBacteria.getFluid(2))
                .EUt(16).duration(400).save(provider);
        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("sour_cream"))
                .inputFluids(GTMFOFluids.PasteurizedMilk.getFluid(8000))
                .notConsumableFluid(GTMFOFluids.LacticAcidBacteria.getFluid(8))
                .outputFluids(GTMFOFluids.SourCream.getFluid(8000))
                .EUt(4).duration(3000).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("pelmeni_seasoned_uncooked"))
                .inputItems(GTMFOItems.PELMENI_UNCOOKED.asStack(),
                        GTMFOItems.HORSERADISH.asStack(),
                        GTMFOItems.BLACK_PEPPER.asStack())
                .inputFluids(GTMFOFluids.SourCream.getFluid(100))
                .outputItems(GTMFOItems.PELMENI_SEASONED_UNCOOKED.asStack())
                .EUt(24).duration(100).save(provider);

        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("pelmeni"))
                .inputItems(GTMFOItems.PELMENI_UNCOOKED.asStack())
                .inputFluids(GTMaterials.Water.getFluid(500))
                .circuitMeta(1)
                .outputItems(GTMFOItems.PELMENI.asStack())
                .EUt(24).duration(2000).save(provider);
        GTMFORecipeTypes.MULTICOOKER_RECIPES.recipeBuilder(id("pelmeni_seasoned"))
                .inputItems(GTMFOItems.PELMENI_SEASONED_UNCOOKED.asStack())
                .inputFluids(GTMaterials.Water.getFluid(500))
                .circuitMeta(2)
                .outputItems(GTMFOItems.PELMENI_SEASONED.asStack())
                .EUt(24).duration(2000).save(provider);
    }
}
