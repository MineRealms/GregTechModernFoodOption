package com.ironsword.gtmfo.common.machine.farmer;

import com.ironsword.gtmfo.common.machine.FarmerMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Faithful port of the original {@code gregtechfoodoption.machines.farmer.FarmerMode}.
 */
public interface FarmerMode {

    boolean canOperate(BlockState state, FarmerMachine farmer, BlockPos pos, Level level);

    default void harvest(BlockState state, ServerLevel level, BlockPos pos, FarmerMachine farmer) {
        level.levelEvent(2001, pos, Block.getId(state));
        level.removeBlock(pos, false);
    }

    default List<ItemStack> getDrops(BlockState state, ServerLevel level, BlockPos pos, FarmerMachine farmer) {
        return Block.getDrops(state, level, pos, null, farmer.getFakePlayer(), farmer.getFakePlayer().getMainHandItem());
    }

    /** If the farming mode can work with this ItemStack in any situation. */
    boolean canPlaceItem(ItemStack stack);

    /** If the farming mode can actually place something in this position. */
    default boolean canPlaceAt(BlockPos operationPos, BlockPos farmerPos, Direction facing, Level level) {
        return true;
    }

    default boolean place(ItemStack stack, ServerLevel level, BlockPos pos, FarmerMachine farmer) {
        var placer = farmer.getFakePlayer();
        placer.setItemInHand(InteractionHand.MAIN_HAND, stack);
        InteractionResult result = stack.useOn(new UseOnContext(placer, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(pos.below()), Direction.UP, pos.below(), false)));
        return result.consumesAction();
    }
}
