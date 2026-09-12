package com.ironsword.gtmfo.common.entity;

import com.ironsword.gtmfo.common.data.GTMFOEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class StrongSnowballEntity extends Snowball {

    public StrongSnowballEntity(EntityType<? extends Snowball> type, Level level) {
        super(type, level);
    }

    public StrongSnowballEntity(Level level, LivingEntity thrower) {
        this(GTMFOEntities.STRONG_SNOWBALL.get(), level);
        this.setOwner(thrower);
        this.setPos(thrower.getX(), thrower.getEyeY() - 0.1D, thrower.getZ());
    }

    @Override
    protected void onHit(HitResult result) {
        if (result.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) result).getEntity();
            if (entity instanceof SnowGolem || entity instanceof Player) {
                return;
            }
        }
        super.onHit(result);
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        int damage = 2 + this.random.nextInt(2);
        if (entity instanceof Blaze) {
            damage += 3;
        }
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), (float) damage);
    }
}
