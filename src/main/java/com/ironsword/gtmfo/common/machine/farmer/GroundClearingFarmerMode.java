package com.ironsword.gtmfo.common.machine.farmer;

import com.ironsword.gtmfo.common.machine.FarmerMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Original {@code GroundClearingFarmerMode}: removes grass/flowers/etc. and never places anything.
 */
public class GroundClearingFarmerMode implements FarmerMode {

    protected final Block crop;

    public GroundClearingFarmerMode(Block crop) {
        this.crop = crop;
    }

    @Override
    public boolean canOperate(BlockState state, FarmerMachine farmer, BlockPos pos, Level level) {
        return state.getBlock() == crop;
    }

    @Override
    public boolean canPlaceItem(ItemStack stack) {
        return false;
    }
}
