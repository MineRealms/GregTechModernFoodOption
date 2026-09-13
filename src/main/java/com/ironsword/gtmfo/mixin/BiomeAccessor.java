package com.ironsword.gtmfo.mixin;

import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes {@link Biome#climateSettings} (private in vanilla) for the GTFO worldgen
 * temperature/rainfall conditions.
 */
@Mixin(Biome.class)
public interface BiomeAccessor {

    @Accessor("climateSettings")
    Biome.ClimateSettings getClimateSettings();
}
