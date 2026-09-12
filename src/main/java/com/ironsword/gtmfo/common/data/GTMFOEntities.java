package com.ironsword.gtmfo.common.data;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.common.entity.ItalianBuffaloEntity;
import com.ironsword.gtmfo.common.entity.StrongSnowballEntity;
import com.ironsword.gtmfo.common.entity.StrongSnowmanEntity;
import com.ironsword.gtmfo.data.CNLangProvider;
import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = GregTechModernFoodOption.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GTMFOEntities {

    private static final Map<String, Pair<String, String>> EntityLangMap = new LinkedHashMap<>();

    static {
        EntityLangMap.put("entity.gtmfo.italian_buffalo", Pair.of("Italian Buffalo", "意大利水牛"));
        EntityLangMap.put("entity.gtmfo.strong_snowman", Pair.of("Strong Snowman", "强力雪人"));
        EntityLangMap.put("entity.gtmfo.strong_snowball", Pair.of("Strong Snowball", "强力雪球"));
    }

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, GregTechModernFoodOption.MODID);

    public static final RegistryObject<EntityType<ItalianBuffaloEntity>> ITALIAN_BUFFALO = ENTITIES.register("italian_buffalo",
            () -> EntityType.Builder.<ItalianBuffaloEntity>of(ItalianBuffaloEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 1.4F).clientTrackingRange(10).build("italian_buffalo"));

    public static final RegistryObject<EntityType<StrongSnowmanEntity>> STRONG_SNOWMAN = ENTITIES.register("strong_snowman",
            () -> EntityType.Builder.<StrongSnowmanEntity>of(StrongSnowmanEntity::new, MobCategory.MISC)
                    .sized(0.7F, 1.9F).clientTrackingRange(8).build("strong_snowman"));

    public static final RegistryObject<EntityType<StrongSnowballEntity>> STRONG_SNOWBALL = ENTITIES.register("strong_snowball",
            () -> EntityType.Builder.<StrongSnowballEntity>of(StrongSnowballEntity::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).build("strong_snowball"));

    public static void init(IEventBus bus) {
        ENTITIES.register(bus);
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ITALIAN_BUFFALO.get(), Cow.createAttributes().build());
        event.put(STRONG_SNOWMAN.get(), SnowGolem.createAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .build());
    }

    public static void initENLang(RegistrateLangProvider provider) {
        EntityLangMap.forEach((key, value) -> provider.add(key, value.getFirst()));
    }

    public static void initCNLang(CNLangProvider provider) {
        EntityLangMap.forEach((key, value) -> provider.add(key, value.getSecond()));
    }
}
