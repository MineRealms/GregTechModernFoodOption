package com.ironsword.gtmfo.common.machine;

import com.gregtechceu.gtceu.api.capability.IControllable;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.ToggleButtonWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.TieredEnergyMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Mob Age Sorter: moves children (or adults when inverted) towards the machine.
 * Requires redstone signal.
 */
public class MobAgeSorterMachine extends TieredEnergyMachine implements IFancyUIMachine, IControllable {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MobAgeSorterMachine.class, TieredEnergyMachine.MANAGED_FIELD_HOLDER);

    private static final int BASE_EU_CONSUMPTION = 8;

    private final int suckingRange;

    @Persisted
    @DescSynced
    @RequireRerender
    private boolean movesAdults;

    @Persisted
    @DescSynced
    @RequireRerender
    private boolean isWorking;

    @Persisted
    private boolean isWorkingEnabled = true;

    private TickableSubscription tickSubs;

    public MobAgeSorterMachine(IMachineBlockEntity holder, int tier, int suckingRange) {
        super(holder, tier);
        this.suckingRange = suckingRange;
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
        boolean workingNow = this.isWorkingEnabled
                && energyContainer.getEnergyStored() >= getEnergyConsumedPerTick()
                && getLevel().hasNeighborSignal(getPos());

        if (workingNow) {
            energyContainer.removeEnergy(getEnergyConsumedPerTick());
            BlockPos center = getPos().relative(getFrontFacing(), suckingRange);
            AABB area = new AABB(center).inflate(suckingRange - 1, 1.0, suckingRange - 1);
            List<LivingEntity> animals = getLevel().getEntitiesOfClass(LivingEntity.class, area);
            animals.removeIf(animal -> animal.isBaby() != movesAdults);

            if (!animals.isEmpty()) {
                BlockPos target = getPos().relative(getFrontFacing().getOpposite());
                animals.get(0).teleportTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5);
            }
        }

        if (workingNow != isWorking) {
            isWorking = workingNow;
            markDirty();
        }
    }

    protected long getEnergyConsumedPerTick() {
        return (long) BASE_EU_CONSUMPTION * (1L << ((getTier() - 1) * 2));
    }

    public boolean isMovesAdults() {
        return movesAdults;
    }

    public void setMovesAdults(boolean movesAdults) {
        this.movesAdults = movesAdults;
        markDirty();
    }

    public boolean isWorking() {
        return isWorking;
    }

    public int getSuckingRange() {
        return suckingRange;
    }

    // === UI ===

    @Override
    public WidgetGroup createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 150, 50);
        group.addWidget(new LabelWidget(10, 6, getBlockState().getBlock().getDescriptionId()));
        group.addWidget(new ToggleButtonWidget(10, 20, 20, 20, GuiTextures.BUTTON_POWER,
                this::isMovesAdults, this::setMovesAdults)
                .setShouldUseBaseBackground()
                .setTooltipText("gtmfo.gui.mob_age_sorter_mode"));
        return group;
    }

    // === IControllable ===

    @Override
    public boolean isWorkingEnabled() {
        return isWorkingEnabled;
    }

    @Override
    public void setWorkingEnabled(boolean workingEnabled) {
        this.isWorkingEnabled = workingEnabled;
        markDirty();
    }
}
