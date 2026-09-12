package com.ironsword.gtmfo.common.particle;

import com.ironsword.gtmfo.common.data.GTMFOEffects;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Locale;

/**
 * Sprinkler particle: flies from the sprinkler towards a target block position, with a fluid tint.
 */
public record GTFOSprinkleOptions(double targetX, double targetY, double targetZ, int color)
        implements ParticleOptions {

    public static final Codec<GTFOSprinkleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.fieldOf("target_x").forGetter(GTFOSprinkleOptions::targetX),
            Codec.DOUBLE.fieldOf("target_y").forGetter(GTFOSprinkleOptions::targetY),
            Codec.DOUBLE.fieldOf("target_z").forGetter(GTFOSprinkleOptions::targetZ),
            Codec.INT.fieldOf("color").forGetter(GTFOSprinkleOptions::color))
            .apply(instance, GTFOSprinkleOptions::new));

    @SuppressWarnings("deprecation")
    public static final Deserializer<GTFOSprinkleOptions> DESERIALIZER = new Deserializer<>() {

        @Override
        public GTFOSprinkleOptions fromCommand(ParticleType<GTFOSprinkleOptions> type, StringReader reader)
                throws CommandSyntaxException {
            double x = reader.readDouble();
            reader.expect(' ');
            double y = reader.readDouble();
            reader.expect(' ');
            double z = reader.readDouble();
            reader.expect(' ');
            int color = reader.readInt();
            return new GTFOSprinkleOptions(x, y, z, color);
        }

        @Override
        public GTFOSprinkleOptions fromNetwork(ParticleType<GTFOSprinkleOptions> type, FriendlyByteBuf buffer) {
            return new GTFOSprinkleOptions(buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                    buffer.readVarInt());
        }
    };

    @Override
    public ParticleType<?> getType() {
        return com.ironsword.gtmfo.common.data.GTMFOParticles.SPRINKLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeDouble(targetX);
        buffer.writeDouble(targetY);
        buffer.writeDouble(targetZ);
        buffer.writeVarInt(color);
    }

    @Override
    public String writeToString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f %d",
                BuiltInRegistries.PARTICLE_TYPE.getKey(getType()), targetX, targetY, targetZ, color);
    }
}
