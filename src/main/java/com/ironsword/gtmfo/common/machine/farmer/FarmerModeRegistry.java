package com.ironsword.gtmfo.common.machine.farmer;

import com.ironsword.gtmfo.common.machine.FarmerMachine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Faithful port of the original {@code FarmerModeRegistry.registerDefaultModes}.
 */
public class FarmerModeRegistry {

    private static final List<FarmerMode> FARMER_MODES = new ArrayList<>();

    public static FarmerMode findSuitableFarmerMode(BlockState state, FarmerMachine farmer, BlockPos pos, Level level) {
        for (FarmerMode mode : FARMER_MODES) {
            if (mode.canOperate(state, farmer, pos, level)) return mode;
        }
        return null;
    }

    public static FarmerMode findSuitableFarmerMode(ItemStack stack, BlockPos operationPos, BlockPos farmerPos,
                                                    Direction facing, Level level) {
        for (FarmerMode mode : FARMER_MODES) {
            if (mode.canPlaceItem(stack) && mode.canPlaceAt(operationPos, farmerPos, facing, level)) return mode;
        }
        return null;
    }

    public static FarmerMode findSuitableFarmerMode(ItemStack stack) {
        for (FarmerMode mode : FARMER_MODES) {
            if (mode.canPlaceItem(stack)) return mode;
        }
        return null;
    }

    public static FarmerMode getAnyMode() {
        return FARMER_MODES.isEmpty() ? null : FARMER_MODES.get(0);
    }

    public static void registerFarmerMode(FarmerMode mode) {
        FARMER_MODES.add(mode);
    }

    public static void registerDefaultModes() {
        registerFarmerMode(new BlockCropsFarmerMode(Blocks.WHEAT, Items.WHEAT_SEEDS));
        registerFarmerMode(new BlockCropsFarmerMode(Blocks.POTATOES, Items.POTATO));
        registerFarmerMode(new BlockCropsFarmerMode(Blocks.CARROTS, Items.CARROT));
        registerFarmerMode(new BlockCropsFarmerMode(Blocks.BEETROOTS, Items.BEETROOT_SEEDS));
        registerFarmerMode(new CocoaFarmerMode());
        registerFarmerMode(new NetherWartFarmerMode());
        registerFarmerMode(new StemFarmerMode(Blocks.MELON, Items.MELON_SEEDS));
        registerFarmerMode(new StemFarmerMode(Blocks.PUMPKIN, Items.PUMPKIN_SEEDS));
        registerFarmerMode(new HeightCropFarmerMode(Blocks.SUGAR_CANE, Items.SUGAR_CANE));
        registerFarmerMode(new HeightCropFarmerMode(Blocks.CACTUS, Items.CACTUS));
        registerFarmerMode(new GroundClearingFarmerMode(Blocks.DEAD_BUSH));
        registerFarmerMode(new GroundClearingFarmerMode(Blocks.GRASS));
        registerFarmerMode(new GroundClearingFarmerMode(Blocks.TALL_GRASS));
        registerFarmerMode(new GroundClearingFarmerMode(Blocks.FERN));
        registerFarmerMode(new GroundClearingFarmerMode(Blocks.LARGE_FERN));
        registerFarmerMode(new GroundClearingFarmerMode(Blocks.DANDELION));
        registerFarmerMode(new GroundClearingFarmerMode(Blocks.POPPY));
        registerFarmerMode(new GroundClearingFarmerMode(Blocks.RED_MUSHROOM));
        registerFarmerMode(new GroundClearingFarmerMode(Blocks.BROWN_MUSHROOM));
        registerFarmerMode(new GroundClearingFarmerMode(Blocks.SNOW));
        registerFarmerMode(new GroundClearingFarmerMode(Blocks.SUNFLOWER));
        registerFarmerMode(new GroundClearingFarmerMode(Blocks.LILAC));
        registerFarmerMode(new GroundClearingFarmerMode(Blocks.ROSE_BUSH));
        registerFarmerMode(new GroundClearingFarmerMode(Blocks.PEONY));
        registerFarmerMode(new ChorusFarmerMode());
        registerFarmerMode(new GTFORootCropFarmerMode());
        registerFarmerMode(new GTFOBerryFarmerMode());
        registerFarmerMode(new GTFOCropFarmerMode());
    }

    private FarmerModeRegistry() {}
}
