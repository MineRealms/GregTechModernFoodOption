package com.ironsword.gtmfo.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

/**
 * Lung Cancer: slowly reduces max health (not curable).
 */
public class LungCancerEffect extends MobEffect {

    public LungCancerEffect() {
        super(MobEffectCategory.HARMFUL, 0x663650);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        MobEffectInstance instance = entity.getEffect(this);
        if (instance == null) return;
        if (instance.getDuration() % 600 != 0) return;

        AttributeInstance attr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (attr == null) return;
        attr.setBaseValue(attr.getBaseValue() - 1);
        if (attr.getBaseValue() <= 0) {
            entity.hurt(entity.damageSources().magic(), Float.MAX_VALUE);
        } else {
            entity.setHealth(Math.min((float) attr.getBaseValue(), entity.getHealth()));
        }
    }
}
