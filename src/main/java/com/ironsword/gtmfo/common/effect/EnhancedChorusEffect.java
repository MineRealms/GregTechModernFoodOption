package com.ironsword.gtmfo.common.effect;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

/**
 * Enhanced Chorus: sneak to teleport in the direction you are looking.
 */
public class EnhancedChorusEffect extends MobEffect {

    public EnhancedChorusEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFF052B);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) return;
        if (entity.tickCount % 20 != 0 || !player.isShiftKeyDown()) return;
        if (entity.level().isClientSide) return;

        for (int i = 0; i < 16; i++) {
            Vec3 look = entity.getLookAngle();
            double newX = entity.getX() + look.x * 8 + Math.random() * 2;
            double newY = Mth.clamp(entity.getY() + 8 * look.y + Math.random(),
                    0.0, entity.level().getHeight() - 1);
            double newZ = entity.getZ() + look.z * 8 + Math.random() * 2;
            if (entity.isPassenger()) {
                entity.stopRiding();
            }
            if (entity.randomTeleport(newX, newY, newZ, true)) {
                entity.level().playSound(null, entity.blockPosition(), SoundEvents.CHORUS_FRUIT_TELEPORT,
                        SoundSource.PLAYERS, 1.0F, 1.0F);
                player.fallDistance = 0;
                break;
            }
        }
    }
}
