package com.ironsword.gtmfo.common.machine.farmer;

import com.ironsword.gtmfo.common.machine.FarmerMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusFlowerBlock;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Original {@code ChorusFarmerMode}: harvests a whole chorus plant (only when all flowers are fully grown),
 * collecting one chorus flower per flower block.
 */
public class ChorusFarmerMode implements FarmerMode {

    @Override
    public boolean canOperate(BlockState blockState, FarmerMachine farmer, BlockPos pos, Level level) {
        Block block = blockState.getBlock();
        if (block != Blocks.CHORUS_PLANT && block != Blocks.CHORUS_FLOWER) return false;
        for (BlockPos p : collect(pos, level)) {
            BlockState state = level.getBlockState(p);
            if (state.getBlock() == Blocks.CHORUS_FLOWER &&
                    state.getValue(ChorusFlowerBlock.AGE) != ChorusFlowerBlock.DEAD_AGE) {
                return false; // Let all flowers grow
            }
        }
        return true;
    }

    private List<BlockPos> collect(BlockPos pos, Level level) {
        List<BlockPos> result = new ArrayList<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(pos);
        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();
            if (!visited.add(current)) continue;
            result.add(current);
            BlockState state = level.getBlockState(current);
            if (state.getBlock() instanceof ChorusPlantBlock) {
                for (Direction direction : Direction.values()) {
                    if (state.getValue(ChorusPlantBlock.PROPERTY_BY_DIRECTION.get(direction))) {
                        queue.add(current.relative(direction));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<ItemStack> getDrops(BlockState blockState, ServerLevel level, BlockPos pos, FarmerMachine farmer) {
        List<ItemStack> drops = new ArrayList<>();
        for (BlockPos p : collect(pos, level)) {
            BlockState state = level.getBlockState(p);
            if (state.getBlock() == Blocks.CHORUS_FLOWER) {
                drops.add(new ItemStack(Blocks.CHORUS_FLOWER));
            } else {
                drops.addAll(Block.getDrops(state, level, p, null, farmer.getFakePlayer(),
                        farmer.getFakePlayer().getMainHandItem()));
            }
        }
        return drops;
    }

    @Override
    public boolean canPlaceItem(ItemStack stack) {
        return stack.is(Items.CHORUS_FLOWER);
    }

    @Override
    public void harvest(BlockState state, ServerLevel level, BlockPos pos, FarmerMachine farmer) {
        List<BlockPos> blocks = collect(pos, level);
        blocks.sort((a, b) -> Integer.compare(b.getY(), a.getY()));
        for (BlockPos p : blocks) {
            level.levelEvent(2001, p, Block.getId(level.getBlockState(p)));
            level.removeBlock(p, false);
        }
    }
}
