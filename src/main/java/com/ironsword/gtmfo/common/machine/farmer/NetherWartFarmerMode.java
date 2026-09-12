package com.ironsword.gtmfo.common.machine.farmer;

import com.ironsword.gtmfo.common.machine.FarmerMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Original {@code NetherWartFarmerMode}: harvest nether wart at age 3.
 */
public class NetherWartFarmerMode implements FarmerMode {

    public static final int NETHER_WART_MAX_AGE = 3;

    @Override
    public boolean canOperate(BlockState state, FarmerMachine farmer, BlockPos pos, Level level) {
        return state.getBlock() instanceof NetherWartBlock &&
                state.getValue(NetherWartBlock.AGE) == NETHER_WART_MAX_AGE;
    }

    @Override
    public boolean canPlaceItem(ItemStack stack) {
        return stack.is(Items.NETHER_WART);
    }
}
