package com.ironsword.gtmfo.common.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.ironsword.gtmfo.GTMFOConfigHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Locale;

/**
 * Greenhouse: grows trees/crops from saplings. Works only with sunlight access (visual check).
 * <p>
 * The soil inside may be dirt/grass or any block listed in {@code gtfoMiscConfig.greenhouseDirts}
 * (original {@code MetaTileEntityGreenhouse.addGrasses}, including {@code namespace:block[prop=value]}).
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

    /** Soil predicate accepting dirt/grass plus the configured blocks. */
    public static TraceabilityPredicate soilPredicate() {
        return new TraceabilityPredicate(state -> {
            BlockState blockState = state.getBlockState();
            if (blockState.is(Blocks.DIRT) || blockState.is(Blocks.GRASS_BLOCK)) return true;
            for (String config : GTMFOConfigHolder.INSTANCE.gtfoMiscConfig.greenhouseDirts) {
                if (matchesConfig(blockState, config)) return true;
            }
            return false;
        }, () -> new com.lowdragmc.lowdraglib.utils.BlockInfo[0]);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static boolean matchesConfig(BlockState state, String config) {
        String idPart = config;
        String propsPart = null;
        int bracket = config.indexOf('[');
        if (bracket >= 0) {
            idPart = config.substring(0, bracket);
            if (config.endsWith("]")) {
                propsPart = config.substring(bracket + 1, config.length() - 1);
            }
        }
        ResourceLocation id = ResourceLocation.tryParse(idPart.trim());
        if (id == null || !BuiltInRegistries.BLOCK.getKey(state.getBlock()).equals(id)) return false;
        if (propsPart == null || propsPart.isEmpty()) return true;
        for (String pair : propsPart.split(",")) {
            String[] kv = pair.split("=");
            if (kv.length != 2) return false;
            Property property = state.getBlock().getStateDefinition().getProperty(kv[0].trim());
            if (property == null) return false;
            var value = property.getValue(kv[1].trim());
            if (value.isEmpty() || !state.getValue(property).equals(value.get())) return false;
        }
        return true;
    }
}
