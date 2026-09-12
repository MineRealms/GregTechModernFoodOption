package com.ironsword.gtmfo.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Potion Lengthener: marker effect used by the lacing system to extend other effects.
 */
public class PotionLengthenerEffect extends MobEffect {

    public PotionLengthenerEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x69FF56);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
