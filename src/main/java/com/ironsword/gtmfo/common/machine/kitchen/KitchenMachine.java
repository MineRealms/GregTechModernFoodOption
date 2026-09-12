package com.ironsword.gtmfo.common.machine.kitchen;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.PhantomSlotWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.SimpleGeneratorMachine;
import com.gregtechceu.gtceu.api.machine.WorkableTieredMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.transfer.item.CustomItemStackHandler;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import com.ironsword.gtmfo.common.data.material.CleanerProperty;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Kitchen: a multiblock that automates a chain of food machines placed inside it.
 * <p>
 * A target item is set in a phantom slot on the controller. The kitchen scans every recipe of the machines inside,
 * builds a crafting tree for the target, feeds ingredients from its input buses into the machines and collects the
 * results, repeating until the ordered amount is produced.
 */
public class KitchenMachine extends WorkableElectricMultiblockMachine {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(KitchenMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private static final int[] ORDER_SIZES = { 1, 2, 4, 8, 16, 32, 64 };

    @Persisted
    protected final CustomItemStackHandler targetSlot = new CustomItemStackHandler(1);
    @Persisted
    @DescSynced
    protected int orderSize = 1;
    @Persisted
    @DescSynced
    protected int dirtiness;
    @Persisted
    @DescSynced
    protected int stateOrdinal = KitchenState.NO_RECIPE.ordinal();

    private final List<WorkableTieredMachine> machines = new ArrayList<>();
    private final List<IItemHandlerModifiable> inputBuses = new ArrayList<>();
    private final List<IItemHandlerModifiable> outputBuses = new ArrayList<>();
    private final List<IFluidHandler> inputHatches = new ArrayList<>();
    private final List<IFluidHandler> outputHatches = new ArrayList<>();
    private final List<KitchenCraftNode> nodes = new ArrayList<>();
    private ItemStack lastTarget = ItemStack.EMPTY;
    private boolean treeDirty = true;
    @Nullable
    private TickableSubscription tickSubs;

    public KitchenMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    //////////////////////////////////////
    // ******* Multiblock Lifecycle ******//
    //////////////////////////////////////

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        scanStructure();
        treeDirty = true;
        unsubscribe(tickSubs);
        tickSubs = subscribeServerTick(this::kitchenTick);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        unsubscribe(tickSubs);
        tickSubs = null;
        machines.clear();
        inputBuses.clear();
        outputBuses.clear();
        inputHatches.clear();
        outputHatches.clear();
        nodes.clear();
    }

    private void scanStructure() {
        machines.clear();
        inputBuses.clear();
        outputBuses.clear();
        inputHatches.clear();
        outputHatches.clear();

        for (IMultiPart part : getParts()) {
            var machine = part.self();
            if (machine instanceof ItemBusPartMachine bus) {
                IO io = bus.getInventory().getHandlerIO();
                if (io == IO.IN) {
                    inputBuses.add(bus.getInventory());
                } else if (io == IO.OUT) {
                    outputBuses.add(bus.getInventory());
                }
            } else if (machine instanceof FluidHatchPartMachine hatch) {
                IO io = hatch.tank.getHandlerIO();
                if (io == IO.IN) {
                    inputHatches.add(hatch.tank);
                } else if (io == IO.OUT) {
                    outputHatches.add(hatch.tank);
                }
            }
        }

        if (getLevel() == null) return;
        Direction back = getFrontFacing().getOpposite();
        Direction side = getFrontFacing().getClockWise();
        BlockPos origin = getPos();
        for (int depth = 1; depth <= 4; depth++) {
            for (int offset = -1; offset <= 1; offset++) {
                BlockPos pos = origin.relative(back, depth).relative(side, offset).above();
                if (!(getLevel().getBlockEntity(pos) instanceof IMachineBlockEntity machineBlockEntity)) continue;
                if (machineBlockEntity.getMetaMachine() instanceof WorkableTieredMachine workable &&
                        !(workable instanceof SimpleGeneratorMachine)) {
                    machines.add(workable);
                }
            }
        }
    }

    //////////////////////////////////////
    // *********** Kitchen Logic *********//
    //////////////////////////////////////

    private void kitchenTick() {
        if (getLevel() == null || getLevel().isClientSide || !isFormed()) return;

        cleanSelf();
        for (WorkableTieredMachine machine : machines) {
            slurpItems(machine.exportItems);
            slurpFluids(machine.exportFluids);
        }

        // keep running machines topped up from the kitchen's energy buffer
        if (getEnergyContainer() != null) {
            for (WorkableTieredMachine machine : machines) {
                if (!machine.getRecipeLogic().isWorking()) continue;
                long needed = machine.energyContainer.getEnergyCapacity()
                        - machine.energyContainer.getEnergyStored();
                if (needed > 0) {
                    long moved = getEnergyContainer().removeEnergy(needed);
                    machine.energyContainer.addEnergy(moved);
                }
            }
        }

        if (getOffsetTimer() % operationInterval() != 0) return;

        if (getEnergyContainer() == null || getEnergyContainer().getEnergyStored() <= 0) {
            setState(KitchenState.MACHINES_NOT_WORKING);
            return;
        }
        long upkeep = Math.max(1L, GTValues.VA[Math.min(Math.max(0, getTier()), GTValues.VA.length - 1)] / 2);
        getEnergyContainer().removeEnergy(upkeep);

        ItemStack target = targetSlot.getStackInSlot(0);
        if (target.isEmpty()) {
            nodes.clear();
            setState(KitchenState.NO_RECIPE);
            return;
        }
        if (treeDirty || !ItemStack.isSameItemSameTags(target, lastTarget)) {
            rebuildTree(target);
            lastTarget = target.copyWithCount(1);
            treeDirty = false;
        }

        if (checkOrderComplete(target)) {
            setState(KitchenState.ORDER_COMPLETE);
            return;
        }
        if (nodes.isEmpty()) {
            setState(KitchenState.NO_RECIPE);
            return;
        }

        for (KitchenCraftNode node : nodes) {
            if (startNode(node)) {
                setState(KitchenState.PROBABLY_FINE);
                return;
            }
        }
        setState(KitchenState.NO_INGREDIENTS);
    }

    private int operationInterval() {
        return Math.max(4, 20 / (getTier() + 1) + (int) Math.ceil(Math.log(Math.max(1, dirtiness))));
    }

    private boolean dirtinessChance() {
        return Math.random() * Math.max(1, dirtiness) < 10;
    }

    private void cleanSelf() {
        if (dirtiness <= 0) return;
        for (IFluidHandler hatch : inputHatches) {
            for (int i = 0; i < hatch.getTanks(); i++) {
                FluidStack fluid = hatch.getFluidInTank(i);
                if (fluid.isEmpty()) continue;
                var material = ChemicalHelper.getMaterial(fluid.getFluid());
                if (material != null && material.hasProperty(CleanerProperty.CLEANER)) {
                    int power = material.getProperty(CleanerProperty.CLEANER).getCleaningPower();
                    FluidStack drained = hatch.drain(copyWithAmount(fluid, Math.min(1, fluid.getAmount())),
                            FluidAction.EXECUTE);
                    if (!drained.isEmpty()) {
                        dirtiness -= power;
                    }
                }
            }
        }
        dirtiness = Math.max(0, dirtiness);
    }

    private void rebuildTree(ItemStack target) {
        nodes.clear();
        Map<ItemStack, Integer> depths = new HashMap<>();
        Set<ItemStack> seen = new HashSet<>();
        Deque<ItemStack> queue = new ArrayDeque<>();
        ItemStack root = target.copyWithCount(1);
        queue.add(root);
        depths.put(root, 0);

        while (!queue.isEmpty()) {
            ItemStack need = queue.poll();
            if (!seen.add(need)) continue;
            GTRecipe recipe = findRecipeFor(need);
            if (recipe == null) continue;
            int depth = depths.getOrDefault(need, 0);
            nodes.add(new KitchenCraftNode(recipe, depth,
                    RecipeHelper.getInputItems(recipe),
                    RecipeHelper.getInputFluids(recipe),
                    RecipeHelper.getOutputItems(recipe),
                    RecipeHelper.getOutputFluids(recipe)));
            for (ItemStack input : RecipeHelper.getInputItems(recipe)) {
                if (input.isEmpty()) continue;
                ItemStack key = input.copyWithCount(1);
                if (!depths.containsKey(key) || depths.get(key) < depth + 1) {
                    depths.put(key, depth + 1);
                }
                queue.add(key);
            }
        }
        nodes.sort(Comparator.comparingInt((KitchenCraftNode n) -> n.depth).reversed());
    }

    @Nullable
    private GTRecipe findRecipeFor(ItemStack target) {
        if (getLevel() == null) return null;
        for (GTRecipeType type : getAvailableRecipeTypes()) {
            for (GTRecipe recipe : getLevel().getRecipeManager().getAllRecipesFor(type)) {
                for (ItemStack output : RecipeHelper.getOutputItems(recipe)) {
                    if (ItemStack.isSameItemSameTags(output, target)) {
                        return recipe;
                    }
                }
            }
        }
        return null;
    }

    private Set<GTRecipeType> getAvailableRecipeTypes() {
        Set<GTRecipeType> types = new LinkedHashSet<>();
        for (WorkableTieredMachine machine : machines) {
            types.addAll(Arrays.asList(machine.getRecipeTypes()));
        }
        return types;
    }

    private boolean startNode(KitchenCraftNode node) {
        WorkableTieredMachine machine = findIdleMachine(node);
        if (machine == null) return false;
        if (!hasItemIngredients(node.itemInputs)) return false;
        if (!hasFluidIngredients(node.fluidInputs)) return false;

        long totalEu = Math.max(1L, RecipeHelper.getRealEUt(node.recipe).getTotalEU() * node.recipe.duration);
        if (getEnergyContainer() == null || getEnergyContainer().getEnergyStored() < totalEu) {
            setState(KitchenState.MACHINES_NOT_WORKING);
            return false;
        }
        if (!dirtinessChance()) return false;

        if (!moveItemsTo(node.itemInputs, machine.importItems)) return false;
        if (!moveFluidsTo(node.fluidInputs, machine.importFluids)) return false;

        getEnergyContainer().removeEnergy(totalEu);
        machine.energyContainer.addEnergy(totalEu);

        RecipeLogic logic = machine.getRecipeLogic();
        logic.setupRecipe(node.recipe);
        if (!logic.isWorking()) {
            setState(KitchenState.MACHINES_NOT_WORKING);
            return false;
        }
        dirtiness += 1;
        return true;
    }

    @Nullable
    private WorkableTieredMachine findIdleMachine(KitchenCraftNode node) {
        for (WorkableTieredMachine machine : machines) {
            if (machine.getRecipeLogic().isWorking()) continue;
            for (GTRecipeType type : machine.getRecipeTypes()) {
                if (type == node.recipe.recipeType) {
                    return machine;
                }
            }
        }
        return null;
    }

    private boolean hasItemIngredients(List<ItemStack> required) {
        for (ItemStack req : required) {
            if (req.isEmpty()) continue;
            int remaining = req.getCount();
            for (IItemHandler bus : inputBuses) {
                for (int slot = 0; slot < bus.getSlots(); slot++) {
                    ItemStack stack = bus.getStackInSlot(slot);
                    if (!stack.isEmpty() && ItemStack.isSameItemSameTags(stack, req)) {
                        remaining -= stack.getCount();
                    }
                }
            }
            if (remaining > 0) return false;
        }
        return true;
    }

    private boolean hasFluidIngredients(List<FluidStack> required) {
        for (FluidStack req : required) {
            if (req.isEmpty()) continue;
            int remaining = req.getAmount();
            for (IFluidHandler hatch : inputHatches) {
                for (int tank = 0; tank < hatch.getTanks(); tank++) {
                    FluidStack fluid = hatch.getFluidInTank(tank);
                    if (!fluid.isEmpty() && fluid.isFluidEqual(req)) {
                        remaining -= fluid.getAmount();
                    }
                }
            }
            if (remaining > 0) return false;
        }
        return true;
    }

    private boolean moveItemsTo(List<ItemStack> required, IItemHandlerModifiable destination) {
        for (ItemStack req : required) {
            if (req.isEmpty()) continue;
            int remaining = req.getCount();
            for (IItemHandlerModifiable bus : inputBuses) {
                for (int slot = 0; slot < bus.getSlots() && remaining > 0; slot++) {
                    ItemStack stack = bus.getStackInSlot(slot);
                    if (stack.isEmpty() || !ItemStack.isSameItemSameTags(stack, req)) continue;
                    int toMove = Math.min(remaining, stack.getCount());
                    ItemStack extracted = bus.extractItem(slot, toMove, false);
                    if (extracted.isEmpty()) continue;
                    ItemStack leftover = ItemHandlerHelper.insertItem(destination, extracted, false);
                    if (!leftover.isEmpty()) {
                        bus.insertItem(slot, leftover, false);
                        return false;
                    }
                    remaining -= extracted.getCount();
                }
            }
            if (remaining > 0) return false;
        }
        return true;
    }

    private boolean moveFluidsTo(List<FluidStack> required, IFluidHandler destination) {
        for (FluidStack req : required) {
            if (req.isEmpty()) continue;
            int remaining = req.getAmount();
            for (IFluidHandler hatch : inputHatches) {
                for (int tank = 0; tank < hatch.getTanks() && remaining > 0; tank++) {
                    FluidStack fluid = hatch.getFluidInTank(tank);
                    if (fluid.isEmpty() || !fluid.isFluidEqual(req)) continue;
                    FluidStack drained = hatch.drain(copyWithAmount(fluid, Math.min(remaining, fluid.getAmount())),
                            FluidAction.EXECUTE);
                    if (drained.isEmpty()) continue;
                    int filled = destination.fill(drained, FluidAction.EXECUTE);
                    remaining -= filled;
                    if (filled < drained.getAmount()) {
                        hatch.fill(copyWithAmount(drained, drained.getAmount() - filled), FluidAction.EXECUTE);
                        return false;
                    }
                }
            }
            if (remaining > 0) return false;
        }
        return true;
    }

    private void slurpItems(IItemHandler source) {
        for (int slot = 0; slot < source.getSlots(); slot++) {
            ItemStack stack = source.getStackInSlot(slot);
            if (stack.isEmpty()) continue;
            boolean intermediate = isIntermediate(stack) && !isTarget(stack);
            List<IItemHandlerModifiable> dest = intermediate ? inputBuses : outputBuses;
            ItemStack remaining = stack.copy();
            for (IItemHandlerModifiable handler : dest) {
                remaining = ItemHandlerHelper.insertItem(handler, remaining, false);
                if (remaining.isEmpty()) break;
            }
            if (!remaining.isEmpty() && intermediate) {
                for (IItemHandlerModifiable handler : outputBuses) {
                    remaining = ItemHandlerHelper.insertItem(handler, remaining, false);
                    if (remaining.isEmpty()) break;
                }
            }
            if (remaining.getCount() != stack.getCount()) {
                source.extractItem(slot, stack.getCount() - remaining.getCount(), false);
            }
        }
    }

    private void slurpFluids(com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank source) {
        for (int tank = 0; tank < source.getTanks(); tank++) {
            FluidStack fluid = source.getFluidInTank(tank);
            if (fluid.isEmpty()) continue;
            boolean intermediate = isIntermediateFluid(fluid) && !isTargetFluid(fluid);
            List<IFluidHandler> dest = intermediate ? inputHatches : outputHatches;
            FluidStack remaining = fluid.copy();
            for (IFluidHandler handler : dest) {
                int filled = handler.fill(remaining, FluidAction.EXECUTE);
                if (filled > 0) {
                    remaining.shrink(filled);
                }
                if (remaining.isEmpty()) break;
            }
            int moved = fluid.getAmount() - remaining.getAmount();
            if (moved > 0) {
                source.drain(copyWithAmount(fluid, moved), FluidAction.EXECUTE);
            }
        }
    }


    private static FluidStack copyWithAmount(FluidStack stack, int amount) {
        FluidStack copy = stack.copy();
        copy.setAmount(amount);
        return copy;
    }
    private boolean isIntermediate(ItemStack stack) {
        for (KitchenCraftNode node : nodes) {
            for (ItemStack output : node.itemOutputs) {
                if (ItemStack.isSameItemSameTags(output, stack)) return true;
            }
        }
        return false;
    }

    private boolean isIntermediateFluid(FluidStack stack) {
        for (KitchenCraftNode node : nodes) {
            for (FluidStack output : node.fluidOutputs) {
                if (output.isFluidEqual(stack)) return true;
            }
        }
        return false;
    }

    private boolean isTarget(ItemStack stack) {
        return ItemStack.isSameItemSameTags(targetSlot.getStackInSlot(0), stack);
    }

    private boolean isTargetFluid(FluidStack stack) {
        return false;
    }

    private boolean checkOrderComplete(ItemStack target) {
        int count = 0;
        for (IItemHandler bus : outputBuses) {
            for (int slot = 0; slot < bus.getSlots(); slot++) {
                ItemStack stack = bus.getStackInSlot(slot);
                if (!stack.isEmpty() && ItemStack.isSameItemSameTags(stack, target)) {
                    count += stack.getCount();
                }
            }
        }
        return count >= orderSize;
    }

    public void setState(KitchenState state) {
        this.stateOrdinal = state.ordinal();
    }

    public KitchenState getKitchenState() {
        KitchenState[] values = KitchenState.values();
        return values[Math.floorMod(stateOrdinal, values.length)];
    }

    public int getOrderSize() {
        return orderSize;
    }

    //////////////////////////////////////
    // *************** GUI **************//
    //////////////////////////////////////

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.translatable("gtmfo.machine.kitchen.target",
                targetSlot.getStackInSlot(0).isEmpty() ? Component.translatable("gtmfo.machine.kitchen.none") :
                        targetSlot.getStackInSlot(0).getHoverName()));
        textList.add(Component.translatable("gtmfo.machine.kitchen.order", orderSize));
        textList.add(Component.translatable("gtmfo.machine.kitchen.status."
                + getKitchenState().name().toLowerCase(Locale.ROOT)));
        textList.add(Component.translatable("gtmfo.machine.kitchen.machines", machines.size()));
        if (dirtiness > 0) {
            textList.add(Component.translatable("gtmfo.machine.kitchen.dirtiness", dirtiness));
        }
    }

    @Override
    public Widget createUIWidget() {
        Widget base = super.createUIWidget();
        if (base instanceof WidgetGroup group) {
            group.addWidget(new PhantomSlotWidget(targetSlot, 0, 190, 4)
                    .setClearSlotOnRightClick(true)
                    .setBackgroundTexture(new GuiTextureGroup(GuiTextures.SLOT, GuiTextures.IN_SLOT_OVERLAY)));
            group.addWidget(new LabelWidget(190, 24, () -> "x" + orderSize));
            group.addWidget(new ButtonWidget(190, 36, 16, 16,
                    new GuiTextureGroup(ResourceBorderTexture.BUTTON_COMMON),
                    cd -> {
                        if (!cd.isRemote) {
                            int index = 0;
                            for (int i = 0; i < ORDER_SIZES.length; i++) {
                                if (ORDER_SIZES[i] == orderSize) {
                                    index = i;
                                    break;
                                }
                            }
                            orderSize = ORDER_SIZES[(index + 1) % ORDER_SIZES.length];
                        }
                    }).setHoverTooltips(Component.translatable("gtmfo.machine.kitchen.order_button")));
        }
        return base;
    }
}
