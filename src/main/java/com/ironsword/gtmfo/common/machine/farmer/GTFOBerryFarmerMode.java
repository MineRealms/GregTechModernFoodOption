package com.ironsword.gtmfo.common.machine.farmer;

import com.ironsword.gtmfo.common.block.GTFOCropBlock;
import com.ironsword.gtmfo.common.machine.FarmerMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Original {@code GTFOBerryFarmerMode}: harvests berries by resetting the bush to {@code maxAge - 1}
 * (right-click harvest behaviour), never plants anything.
 */
public class GTFOBerryFarmerMode implements FarmerMode {

    @Override
    public boolean canOperate(BlockState state, FarmerMachine farmer, BlockPos pos, Level level) {
        return state.getBlock() instanceof com.ironsword.gtmfo.common.block.GTFOBerryBushBlock bush &&
                bush.isMaxAge(state);
    }

    @Override
    public boolean canPlaceItem(ItemStack stack) {
        return false;
    }

    @Override
    public void harvest(BlockState state, ServerLevel level, BlockPos pos, FarmerMachine farmer) {
        GTFOCropBlock crop = (GTFOCropBlock) state.getBlock();
        level.setBlock(pos, state.setValue(crop.ageProperty(), crop.getMaxAge() - 1), 3);
    }
}
