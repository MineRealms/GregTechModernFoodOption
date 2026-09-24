package com.ironsword.gtmfo.network;

import com.ironsword.gtmfo.api.capability.Nutrients;
import com.ironsword.gtmfo.api.capability.NutrientsTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Server -&gt; client: the player's current nutrient values, in {@link Nutrients#LIST} order.
 * The client stores them in {@code ClientNutrientCache} for the HUD.
 */
public class NutrientSyncPacket {

    private final float[] values;

    public NutrientSyncPacket(float[] values) {
        this.values = values;
    }

    public NutrientSyncPacket(NutrientsTracker tracker) {
        this.values = new float[Nutrients.LIST.size()];
        for (int i = 0; i < this.values.length; i++) {
            this.values[i] = tracker.get(Nutrients.LIST.get(i));
        }
    }

    public void encode(FriendlyByteBuf buf) {
        for (float value : values) {
            buf.writeFloat(value);
        }
    }

    public static NutrientSyncPacket decode(FriendlyByteBuf buf) {
        float[] values = new float[Nutrients.LIST.size()];
        for (int i = 0; i < values.length; i++) {
            values[i] = buf.readFloat();
        }
        return new NutrientSyncPacket(values);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        // consumerMainThread already runs this on the client thread; keep the client class lazy
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> com.ironsword.gtmfo.client.nutrient.ClientNutrientCache.set(values));
        context.get().setPacketHandled(true);
    }
}
