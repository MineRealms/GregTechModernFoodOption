package com.ironsword.gtmfo.common.worldgen;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Faithful port of the original {@code GTFOFeatureGen.getAmountInChunk}:
 * <ul>
 * <li>finds the first condition satisfied by the chunk's biome;</li>
 * <li>computes simplex noise at {@code (chunkX * perlinScale, chunkZ * perlinScale)}
 * (original {@code GTFOFeature.getRandomStrength});</li>
 * <li>if the noise is above the condition's perlin cutoff, places
 * {@code ceil(maxAmount - cutoff * maxAmount)} features in the chunk.</li>
 * </ul>
 * Conditions are either explicit biome sets or the original temperature/rainfall habitation formula.
 */
public class GTFOFeaturePlacement extends PlacementModifier {

    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIERS =
            DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, GregTechModernFoodOption.MODID);

    public static final RegistryObject<PlacementModifierType<GTFOFeaturePlacement>> TYPE =
            PLACEMENT_MODIFIERS.register("gtfo_feature", () -> () -> GTFOFeaturePlacement.CODEC);

    public static final Codec<GTFOFeaturePlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("feature_seed").forGetter(GTFOFeaturePlacement::featureSeed),
            Condition.CODEC.listOf().fieldOf("conditions").forGetter(GTFOFeaturePlacement::conditions),
            Codec.DOUBLE.optionalFieldOf("perlin_scale", 0.04).forGetter(GTFOFeaturePlacement::perlinScale))
            .apply(instance, GTFOFeaturePlacement::new));

    private final int featureSeed;
    private final List<Condition> conditions;
    private final double perlinScale;

    public GTFOFeaturePlacement(int featureSeed, List<Condition> conditions, double perlinScale) {
        this.featureSeed = featureSeed;
        this.conditions = conditions;
        this.perlinScale = perlinScale;
    }

    public int featureSeed() {
        return featureSeed;
    }

    public List<Condition> conditions() {
        return conditions;
    }

    public double perlinScale() {
        return perlinScale;
    }

    /** A single feature condition: explicit biomes (with cutoff) or the temp/rainfall formula. */
    public record Condition(Optional<HolderSet<Biome>> biomes, Optional<TempRain> tempRain,
                            int maxAmount, double cutoff) {

        public static final Codec<Condition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biomes")
                        .forGetter(Condition::biomes),
                TempRain.CODEC.optionalFieldOf("temp_rain").forGetter(Condition::tempRain),
                Codec.INT.fieldOf("max_amount").forGetter(Condition::maxAmount),
                Codec.DOUBLE.optionalFieldOf("cutoff", 0.0D).forGetter(Condition::cutoff))
                .apply(instance, Condition::new));
    }

    /** Original {@code TemperatureRainfallCondition}. */
    public record TempRain(double optimalTemp, double optimalRain, double range, double commonality) {

        public static final Codec<TempRain> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.DOUBLE.fieldOf("optimal_temp").forGetter(TempRain::optimalTemp),
                Codec.DOUBLE.fieldOf("optimal_rain").forGetter(TempRain::optimalRain),
                Codec.DOUBLE.fieldOf("range").forGetter(TempRain::range),
                Codec.DOUBLE.fieldOf("commonality").forGetter(TempRain::commonality))
                .apply(instance, TempRain::new));

        private double habitation(Biome biome) {
            float downfall = ((com.ironsword.gtmfo.mixin.BiomeAccessor) (Object) biome)
                    .getClimateSettings().downfall();
            return range - Math.sqrt(Math.pow(biome.getBaseTemperature() - optimalTemp, 2) +
                    Math.pow(downfall - optimalRain, 2));
        }

        private double perlinCutoff(Biome biome) {
            return 1 - habitation(biome) * commonality;
        }

        private boolean isSatisfied(Biome biome) {
            return habitation(biome) > 0;
        }
    }

    private static final Map<Long, GTFOSimplexNoise> NOISE_CACHE = new HashMap<>();

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        var level = context.getLevel();
        var biomeHolder = level.getBiome(pos);
        Biome biome = biomeHolder.value();
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;

        for (Condition condition : conditions) {
            boolean satisfied;
            double cutoff = condition.cutoff();
            if (condition.tempRain().isPresent()) {
                TempRain tempRain = condition.tempRain().get();
                satisfied = tempRain.isSatisfied(biome);
                cutoff = tempRain.perlinCutoff(biome);
            } else if (condition.biomes().isPresent()) {
                satisfied = condition.biomes().get().contains(biomeHolder);
            } else {
                satisfied = false;
            }
            if (!satisfied) continue;

            long seed = level.getLevel().getSeed() + featureSeed;
            GTFOSimplexNoise noise = NOISE_CACHE.computeIfAbsent(seed,
                    s -> new GTFOSimplexNoise(new Random(s)));
            double strength = noise.getValue(chunkX * perlinScale, chunkZ * perlinScale);
            if (cutoff < strength) {
                int count = (int) Math.ceil(condition.maxAmount() - cutoff * condition.maxAmount());
                if (count <= 0) return Stream.empty();
                return IntStream.range(0, count).mapToObj(i -> {
                    int x = chunkX * 16 + random.nextInt(16) + 8;
                    int z = chunkZ * 16 + random.nextInt(16) + 8;
                    int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                    return new BlockPos(x, y, z);
                });
            }
            return Stream.empty();
        }
        return Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        return TYPE.get();
    }

    public static void init(IEventBus bus) {
        PLACEMENT_MODIFIERS.register(bus);
    }
}
