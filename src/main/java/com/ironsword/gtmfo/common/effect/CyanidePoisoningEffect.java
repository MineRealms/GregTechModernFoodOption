package com.ironsword.gtmfo.common.effect;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Cyanide Poisoning: phased poisoning that ends in lethal damage.
 */
public class CyanidePoisoningEffect extends MobEffect {

    public CyanidePoisoningEffect() {
        super(MobEffectCategory.HARMFUL, 0xffffff);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        MobEffectInstance instance = entity.getEffect(this);
        if (instance == null) return;
        int phase = instance.getDuration();

        if (phase == 1200) {
            sendMessage(entity, "gtmfo.cyanide.1");
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 1000, 9));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1000, 3));
        } else if (phase == 300) {
            sendMessage(entity, "gtmfo.cyanide.2");
        } else if (phase == 160) {
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 1000, 0));
        } else if (phase < 200 && phase % 5 == 0) {
            entity.hurt(entity.damageSources().magic(), (float) Math.pow((double) (200 - phase) / 80, 2));
            entity.invulnerableTime = 0;
        } else if (phase < 100) {
            sendMessage(entity, "gtmfo.cyanide.3");
        }
    }

    private static void sendMessage(LivingEntity entity, String key) {
        if (entity.level().isClientSide && entity instanceof Player player && player.isAlive()) {
            player.displayClientMessage(Component.translatable(key), false);
        }
    }
}
