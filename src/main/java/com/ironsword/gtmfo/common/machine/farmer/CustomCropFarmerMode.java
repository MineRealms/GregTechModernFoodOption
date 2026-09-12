package com.ironsword.gtmfo.common.machine.farmer;

import com.ironsword.gtmfo.common.machine.FarmerMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Original {@code CustomCropFarmerMode}: a crop block tied to a seed item.
 */
public class CustomCropFarmerMode implements FarmerMode {

    protected final Block crop;
    protected final ItemStack seed;

    public CustomCropFarmerMode(Block crop, ItemLike seed) {
        this(crop, new ItemStack(seed));
    }

    public CustomCropFarmerMode(Block crop, ItemStack seed) {
        this.crop = crop;
        this.seed = seed;
    }

    @Override
    public boolean canOperate(BlockState state, FarmerMachine farmer, BlockPos pos, Level level) {
        return state.getBlock() == crop;
    }

    @Override
    public boolean canPlaceItem(ItemStack stack) {
        return ItemStack.isSameItemSameTags(stack, seed);
    }
}
