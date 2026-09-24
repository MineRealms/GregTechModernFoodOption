package com.ironsword.gtmfo.network;

import com.ironsword.gtmfo.common.nutrient.NutrientDefinitionRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/** Server-to-client snapshot of KubeJS per-item nutrient definitions. */
public final class NutrientDefinitionSyncPacket {
    private final Map<ResourceLocation, NutrientDefinitionRegistry.Definition> definitions;

    public NutrientDefinitionSyncPacket(Map<ResourceLocation, NutrientDefinitionRegistry.Definition> definitions) {
        this.definitions = new LinkedHashMap<>(definitions);
    }

    public NutrientDefinitionSyncPacket(FriendlyByteBuf buffer) {
        int count = buffer.readVarInt();
        Map<ResourceLocation, NutrientDefinitionRegistry.Definition> decoded = new LinkedHashMap<>();
        for (int i = 0; i < count; i++) {
            ResourceLocation id = buffer.readResourceLocation();
            int mask = buffer.readVarInt();
            float[] values = new float[com.ironsword.gtmfo.api.capability.Nutrients.LIST.size()];
            for (int j = 0; j < values.length; j++) values[j] = buffer.readFloat();
            decoded.put(id, new NutrientDefinitionRegistry.Definition(values, mask));
        }
        this.definitions = decoded;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(definitions.size());
        definitions.forEach((id, definition) -> {
            buffer.writeResourceLocation(id);
            buffer.writeVarInt(definition.mask());
            for (float value : definition.values()) buffer.writeFloat(value);
        });
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                () -> () -> NutrientDefinitionRegistry.replaceClient(definitions));
        context.get().setPacketHandled(true);
    }
}
