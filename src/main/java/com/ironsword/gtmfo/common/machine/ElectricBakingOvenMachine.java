package com.ironsword.gtmfo.common.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;

import com.lowdragmc.lowdraglib.utils.BlockInfo;

import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Electric Baking Oven, faithful port of {@code MetaTileEntityElectricBakingOven}.
 * <p>
 * Temperature mechanics:
 * <ul>
 * <li>starts at 300K; the target temperature is set by the recipe when in adaptable mode
 * (or could be adjusted by hand in the original GUI);</li>
 * <li>steps by 5K every 20 ticks while not running a recipe;</li>
 * <li>maintaining a temperature above 300K costs {@code temperatureEnergyCost(temp, size)} EU/t;</li>
 * <li>a recipe only runs when {@code recipeTemp == temp} and {@code temp > 300};</li>
 * <li>recipes are parallelised by the multiblock length (parallels = size), without overclock;</li>
 * <li>the recipe EU/t is {@code temperatureEnergyCost(temp, size)}.</li>
 * </ul>
 */
public class ElectricBakingOvenMachine extends WorkableElectricMultiblockMachine {

    public static final int BASE_TEMP = 300;
    public static final String TEMPERATURE_KEY = "temperature";

    private int temp = BASE_TEMP;
    private int targetTemp = BASE_TEMP;
    private boolean canAchieveTargetTemp = true;
    private boolean hasEnoughEnergy = true;
    private int size = 1;
    private boolean adaptable = true;
    private boolean canRunRecipe = false;

    public ElectricBakingOvenMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public int getTemp() {
        return temp;
    }

    public int getTargetTemp() {
        return targetTemp;
    }

    public int getSize() {
        return size;
    }

    public boolean isAdaptable() {
        return adaptable;
    }

    public void setAdaptable(boolean adaptable) {
        this.adaptable = adaptable;
    }

    public void incrementTargetTemp(boolean shift) {
        targetTemp += shift ? 25 : 5;
    }

    public void decrementTargetTemp(boolean shift) {
        targetTemp -= shift ? 25 : 5;
    }

    /** Original {@code temperatureEnergyCost}: exponential in temperature and multiblock size. */
    public static int temperatureEnergyCost(int temp, int multiSize) {
        return temp <= BASE_TEMP ? 0 : (int) Math.exp(((double) temp - 100 + (multiSize * 5)) / 100);
    }

    /** Original {@code temperatureForEnergy}. */
    public static int temperatureForEnergy(int eut) {
        if (eut <= 8) return BASE_TEMP;
        return (int) (Math.log(eut) * 100) + 100;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        var context = getMultiblockState().getMatchContext();
        Integer length = context.get("bakingOvenLength");
        this.size = Math.max(1, (length == null ? 2 : length) - 1);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        setTemp(BASE_TEMP);
    }

    private void setTemp(int temp) {
        this.temp = temp;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        subscribeServerTick(this::tick);
    }

    private void tick() {
        if (getLevel() == null || getLevel().isClientSide) return;

        if (temp > BASE_TEMP) {
            hasEnoughEnergy = drainEnergy();
            if (adaptable && !canRunRecipe) targetTemp = BASE_TEMP;
        } else {
            hasEnoughEnergy = true;
        }

        if (getOffsetTimer() % 20 == 0 && !getRecipeLogic().isActive()) {
            stepTowardsTargetTemp();
        } else if (targetTemp == temp) {
            canAchieveTargetTemp = true;
        }

        if (!getRecipeLogic().isActive()) canRunRecipe = false;
    }

    private void stepTowardsTargetTemp() {
        canAchieveTargetTemp = true;
        if (temp > BASE_TEMP && (!getRecipeLogic().isWorking() || targetTemp < temp)) {
            setTemp(temp - 5);
            return;
        }
        if (temp == targetTemp) return;
        long maxInput = getEnergyContainer().getInputVoltage() * getEnergyContainer().getInputAmperage();
        if (temperatureEnergyCost(temp + 5, size) <= maxInput && hasEnoughEnergy) {
            setTemp(temp + 5);
        } else {
            canAchieveTargetTemp = false;
        }
    }

    private boolean drainEnergy() {
        long cost = temperatureEnergyCost(temp, size);
        if (getEnergyContainer().getEnergyStored() >= cost) {
            getEnergyContainer().removeEnergy(cost);
            return true;
        }
        setTemp(temp - 5);
        return false;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (!isFormed()) return;
        textList.add(Component.translatable("gtmfo.multiblock.electric_baking_oven.tooltip.4",
                temperatureEnergyCost(temp, size)));
        textList.add(Component.translatable("gtmfo.multiblock.electric_baking_oven.tooltip.5", targetTemp));
        if (!canAchieveTargetTemp && hasEnoughEnergy) {
            textList.add(Component.translatable("gtmfo.multiblock.electric_baking_oven.tooltip.2")
                    .withStyle(net.minecraft.ChatFormatting.RED));
        }
        if (!hasEnoughEnergy) {
            textList.add(Component.translatable("gtceu.multiblock.not_enough_energy")
                    .withStyle(net.minecraft.ChatFormatting.RED));
        }
    }

    /**
     * Recipe modifier: only runs recipes whose {@code temperature} data matches the current temperature,
     * parallelises by size and sets the EU/t to the temperature energy cost (no overclock).
     */
    public static ModifierFunction bakingOvenModifier(com.gregtechceu.gtceu.api.machine.MetaMachine machine,
                                                      GTRecipe recipe) {
        if (!(machine instanceof ElectricBakingOvenMachine oven)) return ModifierFunction.NULL;
        int recipeTemp = recipe.data.contains(TEMPERATURE_KEY) ? recipe.data.getInt(TEMPERATURE_KEY) : BASE_TEMP;
        if (recipeTemp <= BASE_TEMP) return ModifierFunction.NULL;
        if (oven.adaptable) {
            oven.targetTemp = recipeTemp;
            oven.canRunRecipe = true;
        }
        if (recipeTemp != oven.temp || oven.temp <= BASE_TEMP) return ModifierFunction.NULL;

        int parallels = Math.max(1, ParallelLogic.getParallelAmount(machine, recipe, oven.size));
        double euMultiplier = (double) temperatureEnergyCost(oven.temp, oven.size) /
                Math.max(1, temperatureEnergyCost(oven.temp, 1));
        return ModifierFunction.builder()
                .parallels(parallels)
                .inputModifier(ContentModifier.multiplier(parallels))
                .outputModifier(ContentModifier.multiplier(parallels))
                .eutMultiplier(euMultiplier)
                .build();
    }

    /** Counts the repeatable aisle blocks to determine the multiblock size (original {@code bakingOvenLength}). */
    public static TraceabilityPredicate lengthIndicator() {
        return new TraceabilityPredicate(state -> {
            if (state.getBlockState().isAir()) {
                Integer current = state.getMatchContext().get("bakingOvenLength");
                state.getMatchContext().set("bakingOvenLength", (current == null ? 0 : current) + 1);
                return true;
            }
            return false;
        }, () -> new BlockInfo[0]);
    }

    @Override
    public boolean isActive() {
        return super.isActive() && temp > BASE_TEMP;
    }
}
