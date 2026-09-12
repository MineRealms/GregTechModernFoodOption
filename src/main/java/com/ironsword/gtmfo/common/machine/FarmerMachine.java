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
    public static final int LENGTH = 9;

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

    private com.ironsword.gtmfo.common.machine.farmer.FarmerMode cachedMode;
    private final List<com.ironsword.gtmfo.common.machine.farmer.FarmerMode> unusableHarvestingModes =
            new java.util.ArrayList<>();
    private boolean seedsAreEmpty = false;
    private net.minecraftforge.common.util.FakePlayer fakePlayer;

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
            if (cachedMode == null) {
                cachedMode = com.ironsword.gtmfo.common.machine.farmer.FarmerModeRegistry.getAnyMode();
            }
            tickSubs = subscribeServerTick(tickSubs, this::tick);
        }
    }

    /** Lazily created fake player used for drops and item use. */
    public net.minecraftforge.common.util.FakePlayer getFakePlayer() {
        if (fakePlayer == null && getLevel() instanceof ServerLevel serverLevel) {
            fakePlayer = FakePlayerFactory.getMinecraft(serverLevel);
        }
        return fakePlayer;
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
            operateServer(serverLevel);
        }
        updateOperationPosition();
    }

    /**
     * Original {@code MetaTileEntityFarmer.operateServer}: first collect crops (if the output has room),
     * then place a seed from the input slots.
     */
    private void operateServer(ServerLevel level) {
        boolean didSomething = collectCrops(level);

        // If the output inventory has updated and isn't full, use all modes again
        if (!unusableHarvestingModes.isEmpty() && !isExportFull()) {
            unusableHarvestingModes.clear();
        }

        didSomething |= placeSeed(level);

        if (didSomething) {
            level.playSound(null, getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5,
                    com.ironsword.gtmfo.common.data.GTMFOSounds.FARMER_LASER.get(),
                    net.minecraft.sounds.SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    /** Original {@code collectCrops}. */
    private boolean collectCrops(ServerLevel level) {
        BlockState state = level.getBlockState(operationPosition);
        if (state.isAir()) return false;

        boolean canHarvestBlock = true;
        if (cachedMode == null || !cachedMode.canOperate(state, this, operationPosition, level)) {
            var mode = com.ironsword.gtmfo.common.machine.farmer.FarmerModeRegistry
                    .findSuitableFarmerMode(state, this, operationPosition, level);
            if (mode != null) {
                cachedMode = mode;
            } else {
                canHarvestBlock = false;
            }
        }
        if (canHarvestBlock && cachedMode != null && !unusableHarvestingModes.contains(cachedMode) &&
                !isExportFull()) {
            List<ItemStack> drops = cachedMode.getDrops(state, level, operationPosition, this);
            if (canInsertIntoExport(drops, true)) {
                canInsertIntoExport(drops, false);
                cachedMode.harvest(state, level, operationPosition, this);
                return true;
            } else {
                unusableHarvestingModes.add(cachedMode);
            }
        }
        return false;
    }

    /** Original {@code placeSeed}. */
    private boolean placeSeed(ServerLevel level) {
        if (!level.getBlockState(operationPosition).isAir()) return false;
        if (seedsAreEmpty && importItems.getStackInSlot(0).isEmpty()) return false;

        seedsAreEmpty = false;
        seedSlot = findUnemptySeedSlot(seedSlot + 1);
        if (seedSlot == -1) {
            seedsAreEmpty = true;
            return false;
        }
        ItemStack seedItem = importItems.extractItem(seedSlot, 1, true);
        boolean canPlaceSeed = true;
        if (cachedMode == null || !cachedMode.canPlaceItem(seedItem) ||
                !cachedMode.canPlaceAt(operationPosition, getPos(), getFrontFacing(), level)) {
            var mode = com.ironsword.gtmfo.common.machine.farmer.FarmerModeRegistry
                    .findSuitableFarmerMode(seedItem, operationPosition, getPos(), getFrontFacing(), level);
            if (mode != null) {
                cachedMode = mode;
            } else {
                canPlaceSeed = false;
                if (com.ironsword.gtmfo.common.machine.farmer.FarmerModeRegistry
                        .findSuitableFarmerMode(seedItem) == null) {
                    // Move this unusable stack to the output
                    ItemStack junk = importItems.extractItem(seedSlot, seedItem.getCount(), true);
                    if (canInsertIntoExport(List.of(junk), true)) {
                        canInsertIntoExport(List.of(importItems.extractItem(seedSlot, seedItem.getCount(), false)),
                                false);
                    }
                }
            }
        }
        if (canPlaceSeed && cachedMode != null) {
            if (cachedMode.place(seedItem, level, operationPosition, this)) {
                importItems.extractItem(seedSlot, 1, false);
                return true;
            }
        }
        return false;
    }

    private int findUnemptySeedSlot(int start) {
        int slots = importItems.getSlots();
        for (int i = 0; i < slots; i++) {
            int index = (start + i) % slots;
            if (!importItems.getStackInSlot(index).isEmpty()) return index;
        }
        return -1;
    }

    private boolean isExportFull() {
        for (int i = 0; i < exportItems.getSlots(); i++) {
            if (exportItems.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    private boolean canInsertIntoExport(List<ItemStack> stacks, boolean simulate) {
        int slot = 0;
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;
            ItemStack remaining = stack.copy();
            for (int i = 0; i < exportItems.getSlots() && !remaining.isEmpty(); i++) {
                remaining = exportItems.insertItem(i, remaining, simulate);
            }
            if (!remaining.isEmpty()) return false;
        }
        return true;
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
