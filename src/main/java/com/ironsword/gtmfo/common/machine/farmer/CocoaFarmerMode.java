package com.ironsword.gtmfo.common.machine.farmer;

import com.ironsword.gtmfo.common.machine.FarmerMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Original {@code CocoaFarmerMode}: harvest cocoa at max age and plant cocoa beans on jungle logs.
 */
public class CocoaFarmerMode implements FarmerMode {

    public static final int COCOA_MAX_AGE = 2;

    @Override
    public boolean canOperate(BlockState state, FarmerMachine farmer, BlockPos pos, Level level) {
        return state.getBlock() instanceof CocoaBlock && state.getValue(CocoaBlock.AGE) == COCOA_MAX_AGE;
    }

    @Override
    public boolean canPlaceItem(ItemStack stack) {
        return stack.is(Items.COCOA_BEANS);
    }

    @Override
    public boolean place(ItemStack stack, ServerLevel level, BlockPos pos, FarmerMachine farmer) {
        var placer = farmer.getFakePlayer();
        placer.setItemInHand(InteractionHand.MAIN_HAND, stack);
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            BlockPos target = pos.relative(facing);
            InteractionResult result = stack.useOn(new UseOnContext(placer, InteractionHand.MAIN_HAND,
                    new BlockHitResult(Vec3.atCenterOf(target), facing.getOpposite(), target, false)));
            if (result.consumesAction()) return true;
        }
        return false;
    }
}
