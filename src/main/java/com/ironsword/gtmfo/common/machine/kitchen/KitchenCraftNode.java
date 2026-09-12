package com.ironsword.gtmfo.common.machine.kitchen;

import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

/**
 * One crafting step of a kitchen order: a recipe to run on a controlled machine, with its ingredients and products.
 */
public class KitchenCraftNode {

    public final GTRecipe recipe;
    public final int depth;
    public final List<ItemStack> itemInputs;
    public final List<FluidStack> fluidInputs;
    public final List<ItemStack> itemOutputs;
    public final List<FluidStack> fluidOutputs;

    public KitchenCraftNode(GTRecipe recipe, int depth, List<ItemStack> itemInputs, List<FluidStack> fluidInputs,
                            List<ItemStack> itemOutputs, List<FluidStack> fluidOutputs) {
        this.recipe = recipe;
        this.depth = depth;
        this.itemInputs = itemInputs;
        this.fluidInputs = fluidInputs;
        this.itemOutputs = itemOutputs;
        this.fluidOutputs = fluidOutputs;
    }
}
