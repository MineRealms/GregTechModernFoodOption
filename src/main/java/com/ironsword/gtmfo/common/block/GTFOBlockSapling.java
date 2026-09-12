package com.ironsword.gtmfo.common.block;

import com.ironsword.gtmfo.common.data.GTMFOTrees;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * GTFO sapling: grows into the tree matching its registered index.
 */
public class GTFOBlockSapling extends BushBlock implements BonemealableBlock {

    private final int treeIndex;

    public GTFOBlockSapling(Properties properties, int treeIndex) {
        super(properties);
        this.treeIndex = treeIndex;
    }

    public int getTreeIndex() {
        return this.treeIndex;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.PODZOL) || state.is(Blocks.FARMLAND) || state.is(Blocks.MOSS_BLOCK);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getMaxLocalRawBrightness(pos.above()) >= 9 && random.nextInt(7) == 0) {
            GTMFOTrees.growTree(level, pos, this.treeIndex, random);
        }
    }

    // === Bonemeal ===

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextFloat() < 0.45F;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        GTMFOTrees.growTree(level, pos, this.treeIndex, random);
    }
}
