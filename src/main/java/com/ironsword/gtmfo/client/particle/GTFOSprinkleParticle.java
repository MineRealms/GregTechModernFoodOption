package com.ironsword.gtmfo.client.particle;

import com.ironsword.gtmfo.common.particle.GTFOSprinkleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.FastColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Colored water drop flying from the sprinkler to its operation position (original {@code GTFOSprinkle}).
 */
@OnlyIn(Dist.CLIENT)
public class GTFOSprinkleParticle extends TextureSheetParticle {

    private final double targetX;
    private final double targetY;
    private final double targetZ;

    protected GTFOSprinkleParticle(ClientLevel level, double x, double y, double z,
                                   GTFOSprinkleOptions options, SpriteSet sprites) {
        super(level, x, y, z);
        this.targetX = options.targetX();
        this.targetY = options.targetY();
        this.targetZ = options.targetZ();
        this.gravity = 0.06F;
        this.lifetime = 10;
        this.hasPhysics = false;
        this.rCol = FastColor.ARGB32.red(options.color()) / 255f;
        this.gCol = FastColor.ARGB32.green(options.color()) / 255f;
        this.bCol = FastColor.ARGB32.blue(options.color()) / 255f;
        this.quadSize = 0.05F;
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        int remaining = Math.max(1, this.lifetime - this.age);
        this.move((targetX - this.x) / remaining, (targetY - this.y) / remaining, (targetZ - this.z) / remaining);

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<GTFOSprinkleOptions> {

        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(GTFOSprinkleOptions options, ClientLevel level, double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new GTFOSprinkleParticle(level, x, y, z, options, this.sprites);
        }
    }
}
