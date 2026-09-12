package com.ironsword.gtmfo.common.machine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.ironsword.gtmfo.common.data.GTMFOSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

/**
 * Microwave: explodes when fed ingots / flammable / explosive / furnace-fuel items,
 * plays a "ding" when a recipe finishes.
 */
public class MicrowaveMachine extends SimpleTieredMachine {

    private RecipeLogic.Status lastStatus = RecipeLogic.Status.IDLE;

    public MicrowaveMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier, GTMachineUtils.defaultTankSizeFunction);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            subscribeServerTick(this::tickMicrowave);
        }
    }

    private void tickMicrowave() {
        if (energyContainer.getEnergyStored() > GTValues.V[tier]) {
            ItemStack stack = importItems.getStackInSlot(0);
            if (!stack.isEmpty() && isDangerous(stack)) {
                doExplosion(tier * 4f);
                return;
            }
        }
        RecipeLogic.Status status = recipeLogic.getStatus();
        if (lastStatus == RecipeLogic.Status.WORKING && status == RecipeLogic.Status.IDLE) {
            getLevel().playSound(null, getPos(), GTMFOSounds.MICROWAVE_FINISH.get(),
                    SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        lastStatus = status;
    }

    private boolean isDangerous(ItemStack stack) {
        var materialStack = ChemicalHelper.getMaterialStack(stack);
        if (!materialStack.isEmpty()) {
            var material = materialStack.material();
            if (material.hasProperty(PropertyKey.INGOT) ||
                    material.hasFlag(MaterialFlags.FLAMMABLE) ||
                    material.hasFlag(MaterialFlags.EXPLOSIVE)) {
                return true;
            }
        }
        return GTUtil.getItemBurnTime(stack.getItem()) > 0;
    }
}
