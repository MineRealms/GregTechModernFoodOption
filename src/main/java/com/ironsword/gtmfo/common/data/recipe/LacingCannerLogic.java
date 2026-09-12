package com.ironsword.gtmfo.common.data.recipe;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.IRecipeCapabilityHolder;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import com.ironsword.gtmfo.api.item.ExComponentItem;
import com.ironsword.gtmfo.common.data.GTMFOLacing;

import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

/**
 * Faithful port of the original {@code RecipeMapFluidCannerMixin}: any GTFO food can be laced with a
 * lacing item in the fluid canner, producing the food with the lacing NBT tag.
 * <p>
 * In 1.12 this was a mixin on the canner's {@code findRecipe}; in GTCEu Modern the equivalent hook is
 * {@link GTRecipeType.ICustomRecipeLogic}, which is called when no registered recipe matches.
 */
public enum LacingCannerLogic implements GTRecipeType.ICustomRecipeLogic {

    INSTANCE;

    @Override
    public @Nullable GTRecipe createCustomRecipe(IRecipeCapabilityHolder holder) {
        ItemStack food = ItemStack.EMPTY;
        ItemStack lacing = ItemStack.EMPTY;

        for (var handlerList : holder.getCapabilitiesForIO(IO.IN)) {
            for (var handler : handlerList.getCapability(ItemRecipeCapability.CAP)) {
                for (var content : handler.getContents()) {
                    if (!(content instanceof ItemStack stack) || stack.isEmpty()) continue;
                    if (food.isEmpty() && ExComponentItem.getFoodStats(stack) != null) {
                        food = stack.copyWithCount(1);
                        continue;
                    }
                    if (lacing.isEmpty() && GTMFOLacing.byItem(stack) != null) {
                        lacing = stack.copyWithCount(1);
                    }
                }
            }
        }

        if (food.isEmpty() || lacing.isEmpty()) return null;
        int index = GTMFOLacing.indexOf(lacing);
        if (index < 0) return null;

        ItemStack output = food.copyWithCount(1);
        output.getOrCreateTag().putInt(GTMFOLacing.NBT_KEY, index);

        return GTRecipeTypes.CANNER_RECIPES.recipeBuilder("lace_food")
                .inputItems(food)
                .inputItems(lacing)
                .outputItems(output)
                .duration(16).EUt(4)
                .buildRawRecipe();
    }
}
