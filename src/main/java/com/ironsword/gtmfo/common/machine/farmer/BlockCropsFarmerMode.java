package com.ironsword.gtmfo.common.machine.farmer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Vanilla {@code BlockCrops} (wheat, potatoes, carrots, beetroots): harvest at max age,
 * replant with the matching seed item.
 */
public class BlockCropsFarmerMode extends CustomCropFarmerMode {

    public BlockCropsFarmerMode(Block crop, net.minecraft.world.item.Item seed) {
        super(crop, seed);
        if (!(crop instanceof CropBlock)) {
            throw new IllegalArgumentException("Crop is not a CropBlock!");
        }
    }

    @Override
    public boolean canOperate(BlockState state, com.ironsword.gtmfo.common.machine.FarmerMachine farmer, BlockPos pos,
                              Level level) {
        return state.getBlock() == crop && ((CropBlock) crop).isMaxAge(state);
    }
}
