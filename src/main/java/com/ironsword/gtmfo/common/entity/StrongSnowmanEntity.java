package com.ironsword.gtmfo.common.entity;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.level.Level;

public class StrongSnowmanEntity extends SnowGolem {

    public StrongSnowmanEntity(EntityType<? extends SnowGolem> type, Level level) {
        super(type, level);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        StrongSnowballEntity snowball = new StrongSnowballEntity(this.level(), this);
        double d0 = target.getEyeY() - 1.100000023841858;
        double d1 = target.getX() - this.getX();
        double d2 = d0 - snowball.getY();
        double d3 = target.getZ() - this.getZ();
        double d4 = Math.sqrt(d1 * d1 + d3 * d3) * 0.2F;
        snowball.shoot(d1, d2 + d4, d3, 1.6F, 4.0F);
        this.playSound(SoundEvents.SNOW_GOLEM_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().addFreshEntity(snowball);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.tickCount > 10000) {
            this.discard();
        }
    }
}
