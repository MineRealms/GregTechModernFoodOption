package com.ironsword.gtmfo.common.data.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;

import static com.gregtechceu.gtceu.api.GTValues.VLVH;
import static com.gregtechceu.gtceu.api.GTValues.VLVT;
import static com.gregtechceu.gtceu.utils.FormattingUtil.toEnglishName;

/**
 * Simple machine registration with the GTFO logo in the GUI (original {@code GTFOSimpleMachineMetaTileEntity}).
 */
public class GTMFOMachineUtils {

    public static MachineDefinition[] registerSimpleMachines(GTRegistrate registrate, String name,
                                                             GTRecipeType recipeType) {
        return registerSimpleMachines(registrate, name, recipeType, GTMachineUtils.defaultTankSizeFunction);
    }

    public static MachineDefinition[] registerSimpleMachines(GTRegistrate registrate, String name,
                                                             GTRecipeType recipeType,
                                                             it.unimi.dsi.fastutil.ints.Int2IntFunction tankScalingFunction) {
        return GTMachineUtils.registerTieredMachines(registrate, name,
                (holder, tier) -> new SimpleTieredMachine(holder, tier, tankScalingFunction), (tier, builder) -> builder
                        .recipeModifier(com.gregtechceu.gtceu.common.data.GTRecipeModifiers.OC_NON_PERFECT)
                        .langValue("%s %s %s".formatted(VLVH[tier], toEnglishName(name), VLVT[tier]))
                        .editableUI(GTMFOGuiUtils.withLogo(GTCEu.id(name), recipeType))
                        .rotationState(RotationState.NON_Y_AXIS)
                        .recipeType(recipeType)
                        .workableTieredHullModel(GTCEu.id("block/machines/" + name))
                        .tooltips(GTMachineUtils.workableTiered(tier, GTValues.V[tier], GTValues.V[tier] * 64,
                                recipeType, tankScalingFunction.applyAsInt(tier), true))
                        .register(),
                GTMachineUtils.ELECTRIC_TIERS);
    }
}
