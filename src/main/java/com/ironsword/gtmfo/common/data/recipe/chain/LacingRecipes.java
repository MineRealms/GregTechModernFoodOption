package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.GTMFOLacing;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

/**
 * Lacing recipes: canner + lacing item + food -> food with the lacing NBT tag.
 */
public class LacingRecipes {

    private static final List<ItemStack> LACEABLE_FOODS = List.of(
            GTMFOItems.BREAD_SLICE.asStack(),
            GTMFOItems.TOAST.asStack(),
            GTMFOItems.BUN.asStack(),
            GTMFOItems.PIZZA_CHEESE_SLICE.asStack(),
            GTMFOItems.BURGER_STEAK.asStack(),
            GTMFOItems.PORCHETTA_SLICE.asStack());

    public static void init(Consumer<FinishedRecipe> provider){
        for (int i = 0; i < GTMFOLacing.ENTRIES.size(); i++) {
            GTMFOLacing.LacingEntry entry = GTMFOLacing.ENTRIES.get(i);
            for (ItemStack food : LACEABLE_FOODS) {
                ItemStack output = food.copyWithCount(1);
                CompoundTag tag = output.getOrCreateTag();
                tag.putInt(GTMFOLacing.NBT_KEY, i);
                GTRecipeTypes.CANNER_RECIPES.recipeBuilder(id("lace_%d_%s".formatted(i,
                                food.getItem().builtInRegistryHolder().key().location().getPath())))
                        .inputItems(food.copyWithCount(1))
                        .inputItems(entry.lacingItem().copy())
                        .outputItems(output)
                        .EUt(4).duration(16).save(provider);
            }
        }
    }
}
