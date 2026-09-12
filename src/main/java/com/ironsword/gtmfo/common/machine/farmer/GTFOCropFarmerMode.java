package com.ironsword.gtmfo.common.machine.farmer;

import com.ironsword.gtmfo.common.block.GTFOCropBlock;
import com.ironsword.gtmfo.common.data.GTMFOCrops;
import com.ironsword.gtmfo.common.machine.FarmerMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Original {@code GTFOCropFarmerMode}: harvests any mature GTFO crop, plants GTFO seeds.
 */
public class GTFOCropFarmerMode implements FarmerMode {

    @Override
    public boolean canOperate(BlockState state, FarmerMachine farmer, BlockPos pos, Level level) {
        return state.getBlock() instanceof GTFOCropBlock crop && crop.isMaxAge(state);
    }

    @Override
    public boolean canPlaceItem(ItemStack stack) {
        return GTMFOCrops.getCropFor(stack.getItem()) != null;
    }
}
