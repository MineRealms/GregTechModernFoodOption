package com.ironsword.gtmfo.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Potion Amplifier: marker effect used by the lacing system to boost other effects.
 */
public class PotionAmplifierEffect extends MobEffect {

    public PotionAmplifierEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x69FF56);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
