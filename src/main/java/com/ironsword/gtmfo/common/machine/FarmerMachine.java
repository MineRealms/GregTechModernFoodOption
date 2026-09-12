package com.ironsword.gtmfo.common.machine;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.TieredEnergyMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.List;

/**
 * Farmer: harvests and replants crops in a 9x9 area in front of the machine using a fake player.
 * Simplified port of the original multi-mode farmer (vanilla crops + GTFO crops/berries).
 */
public class FarmerMachine extends TieredEnergyMachine implements IFancyUIMachine, IMachineLife {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            FarmerMachine.class, TieredEnergyMachine.MANAGED_FIELD_HOLDER);

    private static final int BASE_EU_CONSUMPTION = 16;
    private static final int LENGTH = 9;

    private final int ticksPerAction;

    @Persisted
    public final NotifiableItemStackHandler importItems;
    @Persisted
    public final NotifiableItemStackHandler exportItems;
    @Persisted
    public final CustomItemStackHandler chargerInventory;

    @Persisted
    @DescSynced
    @RequireRerender
    private BlockPos operationPosition;

    @Persisted
    @DescSynced
    @RequireRerender
    private boolean isWorking;

    private TickableSubscription tickSubs;
    private int seedSlot = 0;

    public FarmerMachine(IMachineBlockEntity holder, int tier, int ticksPerAction) {
        super(holder, tier);
        this.ticksPerAction = ticksPerAction;
        this.importItems = new NotifiableItemStackHandler(this, 9, IO.IN, IO.BOTH);
        attachTraits(this.importItems);
        this.exportItems = new NotifiableItemStackHandler(this, 9, IO.OUT);
        attachTraits(this.exportItems);
        this.chargerInventory = new CustomItemStackHandler(1);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            tickSubs = subscribeServerTick(tickSubs, this::tick);
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (tickSubs != null) {
            tickSubs.unsubscribe();
            tickSubs = null;
        }
    }

    private void tick() {
        boolean workingNow = energyContainer.getEnergyStored() >= getEnergyConsumedPerTick();
        if (workingNow != isWorking) {
            isWorking = workingNow;
            markDirty();
        }
        if (!workingNow || getOffsetTimer() % ticksPerAction != 0) return;

        energyContainer.removeEnergy(getEnergyConsumedPerTick());
        if (operationPosition == null || !isInsideWorkingArea(operationPosition)) {
            setupWorkingArea();
        }

        if (getLevel() instanceof ServerLevel serverLevel) {
            operate(serverLevel, operationPosition);
        }
        updateOperationPosition();
    }

    /** Harvests or plants at the given position. */
    private void operate(ServerLevel level, BlockPos pos) {
        var fakePlayer = FakePlayerFactory.getMinecraft(level);
        BlockState state = level.getBlockState(pos);

        // 1. harvest mature crops (right-click harvest for GTFO crops/berries, break otherwise)
        if (state.getBlock() instanceof CropBlock crop && crop.isMaxAge(state)) {
            // try right-click harvest first (GTFO crops support this)
            InteractionResult result = state.use(level, fakePlayer, InteractionHand.MAIN_HAND,
                    new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
            if (!result.consumesAction()) {
                // vanilla-style: break and collect drops
                List<ItemStack> drops = state.getBlock().getDrops(state,
                        new net.minecraft.world.level.storage.loot.LootParams.Builder(level)
                                .withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.ORIGIN,
                                        Vec3.atCenterOf(pos))
                                .withParameter(net.minecraft.world.level.storage.loot.parameters.LootContextParams.TOOL,
                                        fakePlayer.getMainHandItem())
                                .withOptionalParameter(
                                        net.minecraft.world.level.storage.loot.parameters.LootContextParams.THIS_ENTITY,
                                        fakePlayer));
                level.removeBlock(pos, false);
                for (ItemStack drop : drops) {
                    if (!exportItems.insertItem(0, drop, true).isEmpty()) {
                        net.minecraft.world.level.block.Block.popResource(level, pos, drop);
                    } else {
                        exportItems.insertItem(0, drop, false);
                    }
                }
            }
            return;
        }

        // 2. plant a seed on empty farmland
        if (state.isAir()) {
            BlockPos below = pos.below();
            if (level.getBlockState(below).is(Blocks.FARMLAND)) {
                plantSeed(level, pos, fakePlayer);
            }
        }
    }

    private void plantSeed(ServerLevel level, BlockPos pos, net.minecraftforge.common.util.FakePlayer fakePlayer) {
        for (int i = 0; i < importItems.getSlots(); i++) {
            ItemStack stack = importItems.getStackInSlot(i);
            if (stack.isEmpty()) continue;

            // GTFO seeds place their crop block directly (same rules as the planting event)
            net.minecraft.world.level.block.Block crop = com.ironsword.gtmfo.common.data.GTMFOCrops
                    .getCropFor(stack.getItem());
            if (crop != null) {
                if (!level.getBlockState(pos).isAir()) continue;
                BlockPos below = pos.below();
                boolean onFarmland = level.getBlockState(below).is(Blocks.FARMLAND);
                boolean onWater = level.getBlockState(below).getFluidState()
                        .is(net.minecraft.tags.FluidTags.WATER);
                if (onFarmland || onWater) {
                    level.setBlock(pos, crop.defaultBlockState(), 3);
                    importItems.extractItem(i, 1, false);
                    return;
                }
                continue;
            }

            // vanilla seeds
            ItemStack toPlace = stack.copyWithCount(1);
            fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, toPlace);
            InteractionResult result = toPlace.useOn(new net.minecraft.world.item.context.UseOnContext(
                    fakePlayer, InteractionHand.MAIN_HAND,
                    new BlockHitResult(Vec3.atCenterOf(pos.below()), Direction.UP, pos.below(), false)));
            if (result.consumesAction()) {
                importItems.extractItem(i, 1, false);
                return;
            }
        }
    }

    protected long getEnergyConsumedPerTick() {
        return (long) BASE_EU_CONSUMPTION * (1L << ((getTier() - 1) * 2));
    }

    public boolean isWorking() {
        return isWorking;
    }

    // === working area ===

    private void setupWorkingArea() {
        operationPosition = getPos().relative(getFrontFacing())
                .relative(getFrontFacing().getClockWise(), LENGTH / 2);
    }

    private boolean isInsideWorkingArea(BlockPos pos) {
        BlockPos front = getPos().relative(getFrontFacing());
        Direction left = getFrontFacing().getClockWise();
        int dx = pos.getX() - front.getX();
        int dz = pos.getZ() - front.getZ();
        int forward = dx * getFrontFacing().getStepX() + dz * getFrontFacing().getStepZ();
        int sideways = dx * left.getStepX() + dz * left.getStepZ();
        return forward >= 0 && forward < LENGTH && sideways >= -LENGTH / 2 && sideways <= LENGTH / 2
                && pos.getY() == getPos().getY();
    }

    private void updateOperationPosition() {
        Direction left = getFrontFacing().getClockWise();
        BlockPos next = operationPosition.relative(left);
        if (!isInsideWorkingArea(next)) {
            next = operationPosition.relative(left.getOpposite(), LENGTH).relative(getFrontFacing());
            if (!isInsideWorkingArea(next)) {
                setupWorkingArea();
                return;
            }
        }
        operationPosition = next;
    }

    // === UI ===

    @Override
    public WidgetGroup createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 176, 166);
        group.addWidget(new LabelWidget(10, 5, getBlockState().getBlock().getDescriptionId()));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int index = i * 3 + j;
                group.addWidget(new SlotWidget(importItems.storage, index, 28 + j * 18, 18 + i * 18, true, true));
                group.addWidget(new SlotWidget(exportItems.storage, index, 94 + j * 18, 18 + i * 18, true, false));
            }
        }
        group.addWidget(new SlotWidget(chargerInventory, 0, 79, 80, true, true));
        return group;
    }

    @Override
    public void onMachineRemoved() {
        clearInventory(importItems.storage);
        clearInventory(exportItems.storage);
        clearInventory(chargerInventory);
    }
}
