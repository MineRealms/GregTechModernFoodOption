package com.ironsword.gtmfo.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.player.Player;

/**
 * Step Assist: increases step height (sneaking keeps a smaller step so you don't fall off).
 */
public class StepAssistEffect extends MobEffect {

    private static final float DEFAULT_STEP = 0.6F;

    public StepAssistEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xdb5800);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!com.ironsword.gtmfo.GTMFOConfigHolder.INSTANCE.gtfoPotionConfig.stepAssist) return;
        if (entity instanceof Player player) {
            if (player.isShiftKeyDown()) {
                player.setMaxUpStep(0.9F);
            } else {
                player.setMaxUpStep(amplifier + (1F / 16F));
            }
        }
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        super.removeAttributeModifiers(entity, attributes, amplifier);
        if (entity instanceof Player player) {
            player.setMaxUpStep(DEFAULT_STEP);
        }
    }
}
