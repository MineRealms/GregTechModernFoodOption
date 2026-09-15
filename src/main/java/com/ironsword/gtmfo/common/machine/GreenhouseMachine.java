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

    /**
     * The original {@code GreenhouseWorkable}: without sun the progress advances by
     * {@code (long) (Math.random() * 2)} per tick, i.e. a 50% chance, making recipes take
     * twice as long (see the original tooltip).
     */
    @Override
    protected com.gregtechceu.gtceu.api.machine.trait.RecipeLogic createRecipeLogic(Object... args) {
        return new GreenhouseRecipeLogic(this);
    }

    public static class GreenhouseRecipeLogic extends com.gregtechceu.gtceu.api.machine.trait.RecipeLogic {

        private boolean hasSun = true;

        public GreenhouseRecipeLogic(com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine machine) {
            super(machine);
        }

        @Override
        public void setupRecipe(com.gregtechceu.gtceu.api.recipe.GTRecipe recipe) {
            super.setupRecipe(recipe);
            if (getMachine().self() instanceof GreenhouseMachine greenhouse) {
                this.hasSun = greenhouse.checkNaturalLighting();
                // Balance option: stretch the cycle (see GTFOMiscConfig#greenhouseDurationMultiplier).
                double multiplier = GTMFOConfigHolder.INSTANCE.gtfoMiscConfig.greenhouseDurationMultiplier;
                if (multiplier > 1.0D && this.duration > 0) {
                    this.duration = Math.max(1, (int) Math.round(this.duration * multiplier));
                }
            }
        }

        @Override
        public void handleRecipeWorking() {
            int before = this.progress;
            super.handleRecipeWorking();
            if (!hasSun && this.progress > before && getMachine().getLevel() != null &&
                    getMachine().getLevel().random.nextBoolean()) {
                this.progress = before;
            }
        }
    }

    /**
     * Soil predicate accepting dirt/grass plus the configured blocks.
     * <p>
     * The candidates are needed for the JEI pattern preview: without them the preview cannot
     * place the soil layer and logs "Pattern formed checking failed".
     */
    public static TraceabilityPredicate soilPredicate() {
        return new TraceabilityPredicate(state -> {
            BlockState blockState = state.getBlockState();
            if (blockState.is(Blocks.DIRT) || blockState.is(Blocks.GRASS_BLOCK)) return true;
            for (String config : GTMFOConfigHolder.INSTANCE.gtfoMiscConfig.greenhouseDirts) {
                BlockState parsed = parseConfig(config);
                if (parsed != null && parsed.equals(blockState)) return true;
            }
            return false;
        }, GreenhouseMachine::soilCandidates);
    }

    private static com.lowdragmc.lowdraglib.utils.BlockInfo[] soilCandidates() {
        java.util.List<com.lowdragmc.lowdraglib.utils.BlockInfo> candidates = new java.util.ArrayList<>();
        candidates.add(com.lowdragmc.lowdraglib.utils.BlockInfo.fromBlockState(Blocks.DIRT.defaultBlockState()));
        candidates.add(com.lowdragmc.lowdraglib.utils.BlockInfo.fromBlockState(Blocks.GRASS_BLOCK.defaultBlockState()));
        for (String config : GTMFOConfigHolder.INSTANCE.gtfoMiscConfig.greenhouseDirts) {
            BlockState parsed = parseConfig(config);
            if (parsed != null) {
                candidates.add(com.lowdragmc.lowdraglib.utils.BlockInfo.fromBlockState(parsed));
            }
        }
        return candidates.toArray(new com.lowdragmc.lowdraglib.utils.BlockInfo[0]);
    }

    /** Parses {@code namespace:block} or {@code namespace:block[prop=value,...]} into a block state. */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static BlockState parseConfig(String config) {
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
        if (id == null || !BuiltInRegistries.BLOCK.containsKey(id)) return null;
        BlockState state = BuiltInRegistries.BLOCK.get(id).defaultBlockState();
        if (propsPart == null || propsPart.isEmpty()) return state;
        for (String pair : propsPart.split(",")) {
            String[] kv = pair.split("=");
            if (kv.length != 2) return null;
            Property property = state.getBlock().getStateDefinition().getProperty(kv[0].trim());
            if (property == null) return null;
            var value = property.getValue(kv[1].trim());
            if (value.isEmpty()) return null;
            state = state.setValue(property, (Comparable) value.get());
        }
        return state;
    }
}
