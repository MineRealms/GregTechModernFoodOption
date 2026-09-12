package com.ironsword.gtmfo.common.data;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GTMFOSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, GregTechModernFoodOption.MODID);

    public static final RegistryObject<SoundEvent> MICROWAVE_FINISH = register("microwave.finish");
    public static final RegistryObject<SoundEvent> FARMER_LASER = register("farmer.laser");
    public static final RegistryObject<SoundEvent> AMOGUS_VENT = register("amogus.vent");

    private static RegistryObject<SoundEvent> register(String name) {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(GregTechModernFoodOption.id(name)));
    }

    public static void init(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
