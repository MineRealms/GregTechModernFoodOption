package com.ironsword.gtmfo.common.effect;

import com.ironsword.gtmfo.common.data.GTMFOEntities;
import com.ironsword.gtmfo.common.entity.StrongSnowmanEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Snow Golem Spawner: occasionally spawns a strong snowman near the entity.
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
        if (!com.ironsword.gtmfo.GTMFOConfigHolder.INSTANCE.gtfoPotionConfig.snowGolemSpawner) return;
        if (entity.level().isClientSide) return;
        if (entity.getRandom().nextInt(100 / (amplifier + 1)) != 0) return;

        float angle = (float) (entity.getRandom().nextFloat() * Math.PI);
        Vec3 start = entity.getEyePosition();
        Vec3 direction = entity.getLookAngle().yRot(angle);
        Vec3 end = start.add(direction.scale(2.0D));

        BlockHitResult hit = entity.level().clip(new ClipContext(start, end,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));
        if (hit.getType() == HitResult.Type.MISS) return;

        BlockPos pos = hit.getBlockPos().above();
        if (!entity.level().isEmptyBlock(pos)) return;

        StrongSnowmanEntity snowman = GTMFOEntities.STRONG_SNOWMAN.get().create(entity.level());
        if (snowman == null) return;
        snowman.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, entity.getYRot(), entity.getXRot());
        snowman.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1000, 4));
        entity.level().addFreshEntity(snowman);
    }
}
