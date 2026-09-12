package com.ironsword.gtmfo.common.data;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.common.loot.UnknownSeedsLootModifier;
import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GTMFOLootModifiers {

    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister
            .create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, GregTechModernFoodOption.MODID);

    public static final RegistryObject<Codec<UnknownSeedsLootModifier>> UNKNOWN_SEEDS = LOOT_MODIFIERS
            .register("unknown_seeds", () -> UnknownSeedsLootModifier.CODEC);

    public static void init(IEventBus bus) {
        LOOT_MODIFIERS.register(bus);
    }
}
