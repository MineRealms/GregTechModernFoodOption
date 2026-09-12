package com.ironsword.gtmfo.common.data;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.common.effect.AntiSchizoEffect;
import com.ironsword.gtmfo.common.effect.CreativeFlyEffect;
import com.ironsword.gtmfo.common.effect.CyanidePoisoningEffect;
import com.ironsword.gtmfo.common.effect.EnhancedChorusEffect;
import com.ironsword.gtmfo.common.effect.LungCancerEffect;
import com.ironsword.gtmfo.common.effect.PotionAmplifierEffect;
import com.ironsword.gtmfo.common.effect.PotionLengthenerEffect;
import com.ironsword.gtmfo.common.effect.SnowGolemSpawnerEffect;
import com.ironsword.gtmfo.common.effect.StepAssistEffect;
import com.ironsword.gtmfo.common.effect.VentingEffect;
import com.ironsword.gtmfo.data.CNLangProvider;
import com.ironsword.gtmfo.data.GTMFODataGen;
import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GTMFOEffects {
    public static final Map<String, Pair<String,String>> EffectLangMap = new HashMap<>();

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, GregTechModernFoodOption.MODID);

    public static final RegistryObject<MobEffect> FLY        = register("fly"        , CreativeFlyEffect::new     , "Creativity"       , "创造之力");
    public static final RegistryObject<MobEffect> STEP_ASSIST= register("step_assist", StepAssistEffect::new      , "Step Assist"      , "台阶辅助");
    public static final RegistryObject<MobEffect> SNOW       = register("snow"       , SnowGolemSpawnerEffect::new, "Snow-Protected"   , "霜之守护");
    public static final RegistryObject<MobEffect> CYANIDE    = register("cyanide"    , CyanidePoisoningEffect::new, "Cyanide Poisoning","氰化物中毒");
    public static final RegistryObject<MobEffect> VENTING    = register("venting"    , VentingEffect::new         , "Venting"          , "风口传送");
    public static final RegistryObject<MobEffect> AMPLIFIER  = register("amplifier"  , PotionAmplifierEffect::new , "Potion Amplifier" , "药水增幅");
    public static final RegistryObject<MobEffect> LENGTHENER = register("lengthener" , PotionLengthenerEffect::new, "Potion Lengthener", "药水延长");
    public static final RegistryObject<MobEffect> ANTISCHIZO = register("antischizo" , AntiSchizoEffect::new      , "Anti-Schizo"      , "抗精神分裂");
    public static final RegistryObject<MobEffect> LUNG_CANCER= register("lung_cancer", LungCancerEffect::new      , "Lung Cancer"      , "肺癌");
    public static final RegistryObject<MobEffect> CHORUS     = register("chorus"     , EnhancedChorusEffect::new  , "Enhanced Chorus"  , "强化紫颂果");

    private static RegistryObject<MobEffect> register(String name, Supplier<MobEffect> factory, String enLang, String cnLang){
        EffectLangMap.put(GregTechModernFoodOption.langKey("effect",name),Pair.of(enLang,cnLang));
        return EFFECTS.register(name,factory);
    }

    public static void init(IEventBus bus){
        EFFECTS.register(bus);
        GTMFODataGen.initEffectLang();
    }

    public static void initENLang(RegistrateLangProvider provider){
        EffectLangMap.forEach((key,value)->provider.add(key,value.getFirst()));
    }

    public static void initCNLang(CNLangProvider provider){
        EffectLangMap.forEach((key,value)->provider.add(key,value.getSecond()));
    }
}
