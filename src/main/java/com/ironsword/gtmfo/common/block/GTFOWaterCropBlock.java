package com.ironsword.gtmfo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

/**
 * Water crop (rice): must be planted on water with dirt/grass below.
 */
public class GTFOWaterCropBlock extends GTFOCropBlock {

    private static final VoxelShape SHAPE = box(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

    public GTFOWaterCropBlock(Properties properties, Supplier<? extends ItemLike> seedItem,
                              Supplier<? extends ItemLike> cropItem) {
        super(properties, seedItem, cropItem);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        FluidState fluid = level.getFluidState(pos.below());
        if (!fluid.is(FluidTags.WATER)) {
            return false;
        }
        BlockState below = level.getBlockState(pos.below(2));
        return below.is(Blocks.DIRT) || below.is(Blocks.GRASS_BLOCK);
    }
}
