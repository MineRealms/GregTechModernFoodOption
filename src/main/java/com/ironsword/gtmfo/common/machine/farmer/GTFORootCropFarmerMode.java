package com.ironsword.gtmfo.common.machine.farmer;

import com.ironsword.gtmfo.common.block.GTFORootCropBlock;
import com.ironsword.gtmfo.common.machine.FarmerMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Original {@code GTFORootCropFarmerMode}: without seeds in the input, harvest the seeds at full growth;
 * with seeds available, harvest the produce at the intermediate harvesting ages.
 */
public class GTFORootCropFarmerMode extends GTFOCropFarmerMode {

    @Override
    public boolean canOperate(BlockState state, FarmerMachine farmer, BlockPos pos, Level level) {
        if (!(state.getBlock() instanceof GTFORootCropBlock crop)) return false;
        if (crop.isMaxAge(state)) return true;
        return hasNoSeeds(farmer, crop) ? crop.seedHarvestable(state) : crop.cropHarvestable(state);
    }

    private boolean hasNoSeeds(FarmerMachine farmer, GTFORootCropBlock crop) {
        ItemStack seed = crop.getSeedStack();
        for (int i = 0; i < farmer.importItems.getSlots(); i++) {
            if (ItemStack.isSameItemSameTags(seed, farmer.importItems.getStackInSlot(i))) {
                return false;
            }
        }
        return true;
    }
}
