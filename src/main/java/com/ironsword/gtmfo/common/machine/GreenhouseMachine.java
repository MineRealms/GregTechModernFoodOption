package com.ironsword.gtmfo.common.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;

import net.minecraft.core.BlockPos;

/**
 * Greenhouse: grows trees/crops from saplings. Works only with sunlight access (visual check).
 */
public class GreenhouseMachine extends WorkableElectricMultiblockMachine {

    public GreenhouseMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    /** Returns true when the sky is visible above the structure and it is daytime. */
    public boolean checkNaturalLighting() {
        if (getLevel() == null || !getLevel().isDay()) return false;
        BlockPos top = getPos().above(8);
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                if (!getLevel().canSeeSky(top.offset(x, 0, z))) return false;
            }
        }
        return true;
    }
}
