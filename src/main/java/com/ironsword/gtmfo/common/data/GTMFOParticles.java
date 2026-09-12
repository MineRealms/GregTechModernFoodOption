package com.ironsword.gtmfo.common.data;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.common.particle.GTFOSprinkleOptions;
import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class GTMFOParticles {

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister
            .create(Registries.PARTICLE_TYPE, GregTechModernFoodOption.MODID);

    public static final RegistryObject<ParticleType<GTFOSprinkleOptions>> SPRINKLE = PARTICLE_TYPES
            .register("sprinkle", () -> new ParticleType<>(false, GTFOSprinkleOptions.DESERIALIZER) {

                @Override
                public Codec<GTFOSprinkleOptions> codec() {
                    return GTFOSprinkleOptions.CODEC;
                }
            });

    public static void init(IEventBus bus) {
        PARTICLE_TYPES.register(bus);
    }
}
