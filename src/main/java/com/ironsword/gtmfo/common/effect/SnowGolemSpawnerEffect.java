package com.ironsword.gtmfo.common.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.block.Blocks;

/**
 * Snow Golem Spawner: occasionally spawns a snow golem nearby.
 */
public class SnowGolemSpawnerEffect extends MobEffect {

    public SnowGolemSpawnerEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xcef0e8);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return;
        if (entity.getRandom().nextInt(100 / (amplifier + 1)) != 0) return;

        var level = entity.level();
        for (int i = 0; i < 8; i++) {
            var pos = entity.blockPosition().offset(
                    entity.getRandom().nextInt(9) - 4, 0, entity.getRandom().nextInt(9) - 4);
            if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).is(Blocks.SNOW_BLOCK)) {
                SnowGolem golem = new SnowGolem(net.minecraft.world.entity.EntityType.SNOW_GOLEM, level);
                golem.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);
                level.addFreshEntity(golem);
                break;
            }
        }
    }
}
