package com.ironsword.gtmfo.common.effect;

import com.ironsword.gtmfo.common.data.GTMFOSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

/**
 * Venting: random chorus-fruit-like teleportation (plays the smogus vent sound).
 */
public class VentingEffect extends MobEffect {

    public VentingEffect() {
        super(MobEffectCategory.NEUTRAL, 0xFF052B);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (Math.random() >= 0.025 * (amplifier + 1)) return;
        if (entity.level().isClientSide) return;

        for (int i = 0; i < 16; i++) {
            double newX = entity.getX() + (entity.getRandom().nextDouble() - 0.5) * 16.0;
            double newY = Mth.clamp(entity.getY() + (entity.getRandom().nextInt(16) - 8),
                    0.0, entity.level().getHeight() - 1);
            double newZ = entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * 16.0;
            if (entity.isPassenger()) {
                entity.stopRiding();
            }
            if (entity.randomTeleport(newX, newY, newZ, true)) {
                entity.level().playSound(null, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()),
                        GTMFOSounds.AMOGUS_VENT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                break;
            }
        }
    }
}
