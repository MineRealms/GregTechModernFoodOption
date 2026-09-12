package com.ironsword.gtmfo.common.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.common.machine.multiblock.steam.SteamParallelMultiblockMachine;

/**
 * Steam Baking Oven, faithful port of {@code MetaTileEntitySteamBakingOven.SteamBakingOvenWorkable}:
 * <ul>
 * <li>duration is multiplied by 4 (original {@code calculateOverclock} returns {@code duration * 4});</li>
 * <li>steam consumption is {@code temperature / 100} (original {@code recipeSteamT});</li>
 * <li>no parallel processing (original {@code setParallelLimit(1)}).</li>
 * </ul>
 * Only recipes with a baking temperature can run, so the fuel (x4/x8) variants are excluded.
 */
public class SteamBakingOvenMachine extends SteamParallelMultiblockMachine {

    public SteamBakingOvenMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, 1);
    }

    public static ModifierFunction steamBakingOvenModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof SteamBakingOvenMachine)) return ModifierFunction.NULL;
        int temperature = recipe.data.contains(ElectricBakingOvenMachine.TEMPERATURE_KEY) ?
                recipe.data.getInt(ElectricBakingOvenMachine.TEMPERATURE_KEY) : 0;
        if (temperature <= 0) return ModifierFunction.NULL;
        // fuel recipes have EUt 0, so addition sets the steam-equivalent EUt directly
        return ModifierFunction.builder()
                .durationMultiplier(4)
                .eutModifier(ContentModifier.addition(temperature / 100.0))
                .build();
    }
}
