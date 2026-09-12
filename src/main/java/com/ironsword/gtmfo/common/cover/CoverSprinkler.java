package com.ironsword.gtmfo.common.cover;

import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Map;

/**
 * Sprinkler cover: drains fluid from the attached machine and waters a 9x9 area below.
 * Some fluids accelerate crop growth (fertilizer) and suppress fire.
 */
public class CoverSprinkler extends CoverBehavior {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(CoverSprinkler.class,
            CoverBehavior.MANAGED_FIELD_HOLDER);

    private static final int LENGTH = 9;
    private static final int FIRE_SUPPRESSION_HEIGHT = 9;

    /** fluid -> growth boost percentage (original FertilizerProperty values) */
    public static final Map<net.minecraft.world.level.material.Fluid, Integer> FERTILIZER_BOOST = Map.of(
            net.minecraft.world.level.material.Fluids.WATER, 5,
            com.ironsword.gtmfo.common.data.material.GTMFOFluids.FertilizerSolution.getFluid(), 15,
            com.ironsword.gtmfo.common.data.material.GTMFOFluids.Blood.getFluid(), 30);

    private final int tier;

    @Persisted
    @DescSynced
    private BlockPos operationPosition;

    private int fireSuppressTimer = 0;
    private TickableSubscription subscription;

    public CoverSprinkler(CoverDefinition definition, ICoverable coverHolder, Direction attachedSide, int tier) {
        super(definition, coverHolder, attachedSide);
        this.tier = tier;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public boolean canAttach() {
        return super.canAttach() && attachedSide == Direction.DOWN;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        subscription = coverHolder.subscribeServerTick(subscription, this::update);
    }

    @Override
    public void onRemoved() {
        super.onRemoved();
        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }
    }

    private void update() {
        if (!(coverHolder.getLevel() instanceof ServerLevel level)) return;

        // drain 1 mB from the attached fluid handler
        IFluidHandler fluidHandler = FluidUtil
                .getFluidHandler(level, coverHolder.getPos(), attachedSide).orElse(null);
        if (fluidHandler == null) return;
        FluidStack fluid = fluidHandler.drain(1, IFluidHandler.FluidAction.EXECUTE);
        if (fluid.isEmpty()) return;

        int percentageChance = FERTILIZER_BOOST.getOrDefault(fluid.getFluid(), 0);
        boolean isWater = fluid.getFluid().isSame(net.minecraft.world.level.material.Fluids.WATER);

        // cycle through the 9x9 area below the machine
        BlockPos basePos = coverHolder.getPos().below(3);
        if (operationPosition == null || !isInsideArea(basePos, operationPosition)) {
            operationPosition = basePos.offset(-LENGTH / 2, 0, -LENGTH / 2);
        }
        BlockPos opPos = operationPosition;
        updateOperationPosition(basePos);

        // visual feedback: colored drop flying from the sprinkler to the target
        if (level.random.nextFloat() < 0.5F) {
            level.sendParticles(
                    new com.ironsword.gtmfo.common.particle.GTFOSprinkleOptions(
                            opPos.getX() + 0.5, opPos.getY() + 1, opPos.getZ() + 0.5, fluidColor(fluid)),
                    coverHolder.getPos().getX() + 0.5, coverHolder.getPos().getY() - 0.1,
                    coverHolder.getPos().getZ() + 0.5, 1, 0, 0, 0, 0);
        }

        // accelerate crop growth
        if (level.random.nextInt(100) < percentageChance) {
            BlockState cropState = level.getBlockState(opPos);
            if (cropState.getBlock() instanceof net.minecraft.world.level.block.BonemealableBlock bonemeal
                    && bonemeal.isValidBonemealTarget(level, opPos, cropState, false)) {
                bonemeal.performBonemeal(level, level.random, opPos, cropState);
            } else if (cropState.isRandomlyTicking()) {
                cropState.randomTick(level, opPos, level.random);
            }
        }

        // moisten farmland
        BlockState below = level.getBlockState(opPos.below());
        if (below.getBlock() instanceof FarmBlock) {
            int moisture = below.getValue(FarmBlock.MOISTURE);
            if (moisture < 7) {
                level.setBlock(opPos.below(), below.setValue(FarmBlock.MOISTURE, Math.min(7, moisture + 2)), 2);
            }
        }

        // fire suppression every 20 ticks
        if (isWater && ++fireSuppressTimer >= 20) {
            fireSuppressTimer = 0;
            suppressFire(level, basePos);
        }
    }

    private boolean isInsideArea(BlockPos basePos, BlockPos pos) {
        return Math.abs(pos.getX() - basePos.getX()) <= LENGTH / 2
                && Math.abs(pos.getZ() - basePos.getZ()) <= LENGTH / 2
                && pos.getY() == basePos.getY();
    }

    /** Particle tint per supported fluid (client fluid extensions are not available server-side). */
    private static int fluidColor(FluidStack fluid) {
        if (fluid.getFluid().isSame(net.minecraft.world.level.material.Fluids.WATER)) {
            return 0x3F76E4;
        }
        if (fluid.getFluid().isSame(com.ironsword.gtmfo.common.data.material.GTMFOFluids.FertilizerSolution.getFluid())) {
            return 0x947760;
        }
        if (fluid.getFluid().isSame(com.ironsword.gtmfo.common.data.material.GTMFOFluids.Blood.getFluid())) {
            return 0x7A0C0C;
        }
        return 0xFFFFFF;
    }

    private void updateOperationPosition(BlockPos basePos) {
        operationPosition = operationPosition.east();
        if (!isInsideArea(basePos, operationPosition)) {
            operationPosition = operationPosition.west(LENGTH).south();
            if (!isInsideArea(basePos, operationPosition)) {
                operationPosition = basePos.offset(-LENGTH / 2, 0, -LENGTH / 2);
            }
        }
    }

    private void suppressFire(ServerLevel level, BlockPos basePos) {
        int minY = basePos.getY() - FIRE_SUPPRESSION_HEIGHT + 1;
        int maxY = basePos.getY();
        for (int y = minY; y <= maxY; y++) {
            for (int x = basePos.getX() - 4; x <= basePos.getX() + 4; x++) {
                for (int z = basePos.getZ() - 4; z <= basePos.getZ() + 4; z++) {
                    BlockPos candidate = new BlockPos(x, y, z);
                    if (level.getBlockState(candidate).is(Blocks.FIRE)) {
                        level.removeBlock(candidate, false);
                    }
                }
            }
        }
    }
}
