package com.ironsword.gtmfo.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

/**
 * Anti-Schizo: marker effect (cures hallucination-type effects).
 */
public class AntiSchizoEffect extends MobEffect {

    public AntiSchizoEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xf5f5f5);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}
