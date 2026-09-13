package com.ironsword.gtmfo.common.worldgen;

import java.util.Random;

/**
 * Faithful port of Minecraft 1.12's {@code NoiseGeneratorSimplex} (used by the original
 * {@code GTFOFeature.getRandomStrength}). Seeded with a plain {@link Random} so the noise field
 * matches the original for the same world seed + feature seed.
 */
public class GTFOSimplexNoise {

    private static final int[][] GRAD3 = {
            { 1, 1, 0 }, { -1, 1, 0 }, { 1, -1, 0 }, { -1, -1, 0 },
            { 1, 0, 1 }, { -1, 0, 1 }, { 1, 0, -1 }, { -1, 0, -1 },
            { 0, 1, 1 }, { 0, -1, 1 }, { 0, 1, -1 }, { 0, -1, -1 }
    };

    public static final double SQRT_3 = Math.sqrt(3.0D);
    private static final double F2 = 0.5D * (SQRT_3 - 1.0D);
    private static final double G2 = (3.0D - SQRT_3) / 6.0D;

    private final int[] p = new int[512];
    private final double xo;
    private final double yo;
    private final double zo;

    public GTFOSimplexNoise(Random random) {
        this.xo = random.nextDouble() * 256.0D;
        this.yo = random.nextDouble() * 256.0D;
        this.zo = random.nextDouble() * 256.0D;
        for (int i = 0; i < 256; ++i) {
            this.p[i] = i;
        }
        for (int l = 0; l < 256; ++l) {
            int j = random.nextInt(256 - l) + l;
            int k = this.p[l];
            this.p[l] = this.p[j];
            this.p[j] = k;
        }
    }

    private static int fastFloor(double value) {
        return value > 0 ? (int) value : (int) value - 1;
    }

    private static double dot(int[] g, double x, double y) {
        return (double) g[0] * x + (double) g[1] * y;
    }

    /** 2D simplex noise, identical to 1.12's {@code NoiseGeneratorSimplex#getValue}. */
    public double getValue(double xin, double yin) {
        double d3 = (xin + yin) * F2;
        int i = fastFloor(xin + d3);
        int j = fastFloor(yin + d3);
        double d6 = (double) (i + j) * G2;
        double d7 = (double) i - d6;
        double d8 = (double) j - d6;
        double d9 = xin - d7;
        double d10 = yin - d8;
        int k;
        int l;
        if (d9 > d10) {
            k = 1;
            l = 0;
        } else {
            k = 0;
            l = 1;
        }
        double d11 = d9 - (double) k + G2;
        double d12 = d10 - (double) l + G2;
        double d13 = d9 - 1.0D + 2.0D * G2;
        double d14 = d10 - 1.0D + 2.0D * G2;
        int i1 = i & 255;
        int j1 = j & 255;
        int k1 = this.p[i1 + this.p[j1]] % 12;
        int l1 = this.p[i1 + k + this.p[j1 + l]] % 12;
        int i2 = this.p[i1 + 1 + this.p[j1 + 1]] % 12;
        double d0 = 0.0D;
        double d15 = 0.5D - d9 * d9 - d10 * d10;
        if (d15 >= 0.0D) {
            d15 *= d15;
            d0 = d15 * d15 * dot(GRAD3[k1], d9, d10);
        }
        double d1 = 0.0D;
        double d16 = 0.5D - d11 * d11 - d12 * d12;
        if (d16 >= 0.0D) {
            d16 *= d16;
            d1 = d16 * d16 * dot(GRAD3[l1], d11, d12);
        }
        double d2 = 0.0D;
        double d17 = 0.5D - d13 * d13 - d14 * d14;
        if (d17 >= 0.0D) {
            d17 *= d17;
            d2 = d17 * d17 * dot(GRAD3[i2], d13, d14);
        }
        return 70.0D * (d0 + d1 + d2);
    }

    public double getXo() {
        return xo;
    }

    public double getYo() {
        return yo;
    }

    public double getZo() {
        return zo;
    }
}
