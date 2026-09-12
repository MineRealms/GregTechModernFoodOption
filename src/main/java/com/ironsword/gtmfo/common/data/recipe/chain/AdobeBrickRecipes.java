package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.ironsword.gtmfo.common.data.GTMFOBlocks;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class AdobeBrickRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        // mud bricks (hand crafting)
        VanillaRecipeHelper.addShapedRecipe(provider, id("mud_bricks_1"),
                GTMFOItems.BRICK_MUD.asStack(5),
                "SCS", "SMS", "GCG",
                'C', Items.CLAY_BALL,
                'S', Tags.Items.SAND,
                'G', Tags.Items.GRAVEL,
                'M', GTItems.WOODEN_FORM_BRICK.asStack());
        VanillaRecipeHelper.addShapedRecipe(provider, id("mud_bricks_2"),
                GTMFOItems.BRICK_MUD.asStack(10),
                "SBS", "SMS", "GTG",
                'S', Tags.Items.SAND,
                'G', Tags.Items.GRAVEL,
                'B', ChemicalHelper.get(TagPrefix.dust, GTMaterials.Bentonite),
                'T', ChemicalHelper.get(TagPrefix.dust, GTMaterials.Talc),
                'M', GTItems.WOODEN_FORM_BRICK.asStack());
        VanillaRecipeHelper.addShapedRecipe(provider, id("mud_bricks_3"),
                GTMFOItems.BRICK_MUD.asStack(8),
                "SCS", "SMW", "GCG",
                'C', Items.CLAY_BALL,
                'S', Tags.Items.SAND,
                'G', Tags.Items.GRAVEL,
                'W', Items.WHEAT,
                'M', GTItems.WOODEN_FORM_BRICK.asStack());
        VanillaRecipeHelper.addShapedRecipe(provider, id("mud_bricks_4"),
                GTMFOItems.BRICK_MUD.asStack(16),
                "SBS", "SMW", "GTG",
                'S', Tags.Items.SAND,
                'G', Tags.Items.GRAVEL,
                'B', ChemicalHelper.get(TagPrefix.dust, GTMaterials.Bentonite),
                'T', ChemicalHelper.get(TagPrefix.dust, GTMaterials.Talc),
                'W', Items.WHEAT,
                'M', GTItems.WOODEN_FORM_BRICK.asStack());

        GTRecipeTypes.FORMING_PRESS_RECIPES.recipeBuilder(id("mud_bricks_press"))
                .inputItems(Tags.Items.SAND, 3)
                .inputItems(Tags.Items.GRAVEL, 2)
                .inputItems(TagPrefix.dust, GTMaterials.Bentonite)
                .inputItems(TagPrefix.dust, GTMaterials.Talc)
                .inputItems(Items.WHEAT)
                .notConsumable(GTItems.WOODEN_FORM_BRICK)
                .outputItems(GTMFOItems.BRICK_MUD.asStack(16))
                .EUt(30).duration(100).save(provider);

        // mud brick -> adobe brick
        VanillaRecipeHelper.addSmeltingRecipe(provider, id("adobe_brick"),
                GTMFOItems.BRICK_MUD.asStack(),
                GTMFOItems.BRICK_ADOBE.asStack(),
                0f);

        // adobe brick casings
        VanillaRecipeHelper.addShapedRecipe(provider, id("casing_adobe_bricks"),
                GTMFOBlocks.ADOBE_BRICKS.asStack(),
                "XX", "XX",
                'X', GTMFOItems.BRICK_ADOBE.asStack());
        VanillaRecipeHelper.addShapedRecipe(provider, id("casing_reinforced_adobe_bricks"),
                GTMFOBlocks.REINFORCED_ADOBE_BRICKS.asStack(),
                " h ", "ABA", " C ",
                'A', GTMFOItems.BRICK_ADOBE.asStack(),
                'B', ChemicalHelper.get(TagPrefix.plate, GTMaterials.Bronze),
                'C', GTMFOBlocks.ADOBE_BRICKS.asStack());
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(id("casing_reinforced_adobe_bricks_1"))
                .inputItems(TagPrefix.plate, GTMaterials.Bronze)
                .inputItems(GTMFOItems.BRICK_ADOBE.asStack(2))
                .inputItems(GTMFOBlocks.ADOBE_BRICKS.asStack())
                .circuitMeta(1)
                .outputItems(GTMFOBlocks.REINFORCED_ADOBE_BRICKS.asStack())
                .EUt(28).duration(20).save(provider);
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(id("casing_reinforced_adobe_bricks_3"))
                .inputItems(GTMFOBlocks.ADOBE_BRICKS.asStack(3))
                .inputItems(TagPrefix.plate, GTMaterials.Bronze, 3)
                .inputItems(GTMFOItems.BRICK_ADOBE.asStack(6))
                .circuitMeta(3)
                .outputItems(GTMFOBlocks.REINFORCED_ADOBE_BRICKS.asStack(3))
                .EUt(28).duration(80).save(provider);
    }
}
