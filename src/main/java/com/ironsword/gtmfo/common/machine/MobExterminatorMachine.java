package com.ironsword.gtmfo.common.machine;

import com.gregtechceu.gtceu.api.capability.IControllable;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.widget.TankWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.TieredEnergyMachine;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;

/**
 * Mob Exterminator: kills mobs in a 9x9 area in front. Looting level = tier - 1.
 * Nitrous oxide in the tank increases kills per cycle (up to 16).
 */
public class MobExterminatorMachine extends TieredEnergyMachine implements IFancyUIMachine, IControllable {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MobExterminatorMachine.class, TieredEnergyMachine.MANAGED_FIELD_HOLDER);

    private static final int BASE_EU_CONSUMPTION_PER_KILL = 2;
    private static final int RADIUS = 4;

    @Persisted
    @DescSynced
    @RequireRerender
    private boolean isWorking;

    @Persisted
    private boolean isWorkingEnabled = true;

    @Persisted
    public final NotifiableFluidTank fluidTank;

    private TickableSubscription tickSubs;

    public MobExterminatorMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
        this.fluidTank = new NotifiableFluidTank(this, 1, tier * 4000, IO.IN, IO.NONE);
        attachTraits(this.fluidTank);
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
                && energyContainer.getEnergyStored() >= getEnergyConsumedPerKill()
                && getLevel().hasNeighborSignal(getPos());

        if (workingNow != isWorking) {
            isWorking = workingNow;
            markDirty();
        }
        if (!workingNow || getOffsetTimer() % 20 != 0) return;

        BlockPos center = getPos().relative(getFrontFacing(), 5);
        AABB area = new AABB(center).inflate(RADIUS, 0, RADIUS);
        List<LivingEntity> mobs = getLevel().getEntitiesOfClass(LivingEntity.class, area);
        if (mobs.isEmpty()) return;

        int loopLength = Math.min(getMobsPerCycle(), mobs.size());
        for (int i = 0; i < loopLength; i++) {
            attackMob(mobs.get(i));
            if (i > 3) {
                fluidTank.drain(1, IFluidHandler.FluidAction.EXECUTE);
            }
            energyContainer.removeEnergy(getEnergyConsumedPerKill());
        }
    }

    /** Must only be called inside tick(). */
    private int getMobsPerCycle() {
        int maximumKills = (int) (energyContainer.getEnergyStored() / getEnergyConsumedPerKill());
        FluidStack currentGas = fluidTank.getFluidInTank(0);
        if (!currentGas.isEmpty() && currentGas.getFluid() == GTMaterials.NitrousOxide.getFluid()) {
            int amountToExtract = Math.min(currentGas.getAmount(), 12);
            return Math.min(amountToExtract + 4, maximumKills);
        }
        return Math.min(4, maximumKills);
    }

    private void attackMob(LivingEntity mob) {
        if (!(getLevel() instanceof ServerLevel serverLevel)) return;
        var fakePlayer = FakePlayerFactory.getMinecraft(serverLevel);
        ItemStack sword = new ItemStack(Items.DIAMOND_SWORD);
        int looting = getTier() - 1;
        if (looting > 0) {
            sword.enchant(Enchantments.MOB_LOOTING, looting);
        }
        fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, sword);
        mob.hurt(mob.damageSources().playerAttack(fakePlayer), 40.0F);
    }

    protected long getEnergyConsumedPerKill() {
        return (long) BASE_EU_CONSUMPTION_PER_KILL * (1L << ((getTier() - 1) * 2));
    }

    public boolean isWorking() {
        return isWorking;
    }

    // === UI ===

    @Override
    public WidgetGroup createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 170, 90);
        group.addWidget(new LabelWidget(6, 6, getBlockState().getBlock().getDescriptionId()));
        group.addWidget(new LabelWidget(11, 20, "gtceu.gui.fluid_amount"));
        group.addWidget(new TankWidget(fluidTank.getStorages()[0], 69, 52, 18, 18, true, true));
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
