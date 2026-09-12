package com.ironsword.gtmfo.common.machine.farmer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

/**
 * Original {@code StemFarmerMode}: pumpkin/melon stems can only be placed on a checkerboard pattern
 * (offset % 2 == 0) and not in the far corners of the 9x9 area.
 */
public class StemFarmerMode extends CustomCropFarmerMode {

    public StemFarmerMode(Block crop, net.minecraft.world.item.Item seed) {
        super(crop, seed);
    }

    @Override
    public boolean canPlaceAt(BlockPos operationPos, BlockPos farmerPos, Direction facing, Level level) {
        BlockPos center = farmerPos.relative(facing, com.ironsword.gtmfo.common.machine.FarmerMachine.LENGTH / 2 + 1);
        return (((operationPos.getX() - farmerPos.getX()) + (operationPos.getZ() - farmerPos.getZ())) % 2 == 0) &&
                Math.abs(operationPos.getX() - center.getX()) != 4 &&
                Math.abs(operationPos.getZ() - center.getZ()) != 4;
    }
}
