package com.ironsword.gtmfo.common.machine.farmer;

import com.ironsword.gtmfo.common.machine.FarmerMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Original {@code HeightCropFarmerMode}: sugarcane/cactus, harvests the whole column.
 */
public class HeightCropFarmerMode extends CustomCropFarmerMode {

    public HeightCropFarmerMode(Block crop, net.minecraft.world.item.Item seed) {
        super(crop, seed);
    }

    @Override
    public boolean canOperate(BlockState state, FarmerMachine farmer, BlockPos pos, Level level) {
        return level.getBlockState(pos.above()).getBlock() == crop;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, ServerLevel level, BlockPos pos, FarmerMachine farmer) {
        List<ItemStack> drops = new ArrayList<>();
        BlockPos.MutableBlockPos cursor = pos.above().mutable();
        while (level.getBlockState(cursor).getBlock() == crop) {
            drops.addAll(Block.getDrops(level.getBlockState(cursor), level, cursor, null,
                    farmer.getFakePlayer(), farmer.getFakePlayer().getMainHandItem()));
            cursor.move(Direction.UP);
        }
        return drops;
    }

    @Override
    public void harvest(BlockState state, ServerLevel level, BlockPos pos, FarmerMachine farmer) {
        BlockPos.MutableBlockPos cursor = pos.above().mutable();
        while (level.getBlockState(cursor).getBlock() == crop) {
            cursor.move(Direction.UP);
        }
        cursor.move(Direction.DOWN);
        while (!cursor.equals(pos)) {
            level.levelEvent(2001, cursor, Block.getId(level.getBlockState(cursor)));
            level.removeBlock(cursor.immutable(), false);
            cursor.move(Direction.DOWN);
        }
    }
}
