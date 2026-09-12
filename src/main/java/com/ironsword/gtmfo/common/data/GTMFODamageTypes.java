package com.ironsword.gtmfo.common.data;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.data.CNLangProvider;
import com.tterrag.registrate.providers.RegistrateLangProvider;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.Nullable;

/**
 * Original {@code GTFODamageSources}: custom damage types.
 * <p>
 * The damage type JSONs live in {@code data/gtmfo/damage_type} and all of them are in the
 * {@code minecraft:bypasses_armor} tag, matching the original's {@code setDamageBypassesArmor()}.
 */
public class GTMFODamageTypes {

    public static final ResourceKey<DamageType> EXTRACTION = key("extraction");
    public static final ResourceKey<DamageType> EXTERMINATION = key("extermination");
    public static final ResourceKey<DamageType> CYANIDE = key("cyanide");
    public static final ResourceKey<DamageType> LUNG_CANCER = key("lung_cancer");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, GregTechModernFoodOption.id(name));
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> key) {
        return source(level, key, null);
    }

    public static DamageSource source(Level level, ResourceKey<DamageType> key, @Nullable Entity entity) {
        var holder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key);
        return entity == null ? new DamageSource(holder) : new DamageSource(holder, entity);
    }

    public static void initENLang(RegistrateLangProvider provider) {
        provider.add("death.attack.extraction", "%s's internal organs were harvested");
        provider.add("death.attack.extermination", "%s was exterminated");
        provider.add("death.attack.cyanide", "%s smelled some almonds");
        provider.add("death.attack.lung_cancer", "%s died of lung cancer");
    }

    public static void initCNLang(CNLangProvider provider) {
        provider.add("death.attack.extraction", "%s的内脏被抽了出来");
        provider.add("death.attack.extermination", "%s被消灭了");
        provider.add("death.attack.cyanide", "%s闻到了一丝苦杏仁味");
        provider.add("death.attack.lung_cancer", "%s 死于肺癌");
    }

    public static void init() {}
}
