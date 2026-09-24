package com.ironsword.gtmfo.client.nutrient;

import com.ironsword.gtmfo.api.capability.Nutrients;

/**
 * Client side mirror of the local player's nutrient values, filled by
 * {@code NutrientSyncPacket} and read by the HUD.
 */
public final class ClientNutrientCache {

    private static final float[] VALUES = new float[Nutrients.LIST.size()];
    private static boolean received = false;

    private ClientNutrientCache() {}

    public static void set(float[] values) {
        System.arraycopy(values, 0, VALUES, 0, Math.min(values.length, VALUES.length));
        received = true;
    }

    public static boolean hasData() {
        return received;
    }

    public static float get(String name) {
        int index = Nutrients.LIST.indexOf(name);
        return index < 0 ? 0.0F : VALUES[index];
    }

    public static float get(int index) {
        return index >= 0 && index < VALUES.length ? VALUES[index] : 0.0F;
    }

    public static void clear() {
        java.util.Arrays.fill(VALUES, 0.0F);
        received = false;
    }
}
