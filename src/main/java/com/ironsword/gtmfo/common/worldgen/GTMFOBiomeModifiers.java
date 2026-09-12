package com.ironsword.gtmfo.common.worldgen;

import com.ironsword.gtmfo.GTMFOConfigHolder;
import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Config-gated replacement for Forge's {@code add_features} biome modifier.
 * <p>
 * The original {@code GTFOWorldGenConfig.enableGTFOTrees/enableGTFOBerries} toggles world generation;
 * data-driven 1.20.1 worldgen needs a custom biome modifier to honour these options at world load.
 */
public class GTMFOBiomeModifiers {

    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, GregTechModernFoodOption.MODID);

    public static final Codec<ConfigGatedFeaturesModifier> CONFIG_GATED_FEATURES_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("biomes")
                            .forGetter(ConfigGatedFeaturesModifier::biomes),
                    RegistryCodecs.homogeneousList(Registries.PLACED_FEATURE).fieldOf("features")
                            .forGetter(ConfigGatedFeaturesModifier::features),
                    GenerationStep.Decoration.CODEC.fieldOf("step").forGetter(ConfigGatedFeaturesModifier::step),
                    Kind.CODEC.fieldOf("kind").forGetter(ConfigGatedFeaturesModifier::kind))
                    .apply(instance, ConfigGatedFeaturesModifier::new));

    public static final RegistryObject<Codec<? extends BiomeModifier>> CONFIG_GATED_FEATURES =
            BIOME_MODIFIER_SERIALIZERS.register("config_gated_features", () -> CONFIG_GATED_FEATURES_CODEC);

    public enum Kind implements StringRepresentable {
        TREES("trees"),
        BERRIES("berries");

        public static final Codec<Kind> CODEC = StringRepresentable.fromEnum(Kind::values);
        private final String name;

        Kind(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public record ConfigGatedFeaturesModifier(HolderSet<Biome> biomes, HolderSet<PlacedFeature> features,
                                              GenerationStep.Decoration step, Kind kind) implements BiomeModifier {

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            if (phase != Phase.ADD || !biomes.contains(biome)) return;
            boolean enabled = switch (kind) {
                case TREES -> GTMFOConfigHolder.INSTANCE.gtfoWorldGenConfig.enableGTFOTrees;
                case BERRIES -> GTMFOConfigHolder.INSTANCE.gtfoWorldGenConfig.enableGTFOBerries;
            };
            if (!enabled) return;
            var generationSettings = builder.getGenerationSettings();
            for (Holder<PlacedFeature> feature : features) {
                generationSettings.addFeature(step, feature);
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec() {
            return CONFIG_GATED_FEATURES.get();
        }
    }

    public static void init(IEventBus bus) {
        BIOME_MODIFIER_SERIALIZERS.register(bus);
    }

    private GTMFOBiomeModifiers() {}
}
