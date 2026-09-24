package com.ironsword.gtmfo.network;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.api.capability.NutrientsTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Network channel of the mod. Currently only used to mirror nutrient values to the owning client
 * (HUD). All packets are optional: the system keeps working without a client connection.
 */
public final class NutrientsNetwork {

    private static final String VERSION = "2";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            GregTechModernFoodOption.id("main"),
            () -> VERSION,
            VERSION::equals,
            VERSION::equals);

    private NutrientsNetwork() {}

    public static void init() {
        CHANNEL.messageBuilder(NutrientSyncPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(NutrientSyncPacket::encode)
                .decoder(NutrientSyncPacket::decode)
                .consumerMainThread(NutrientSyncPacket::handle)
                .add();
        CHANNEL.messageBuilder(NutrientDefinitionSyncPacket.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(NutrientDefinitionSyncPacket::encode)
                .decoder(NutrientDefinitionSyncPacket::new)
                .consumerMainThread(NutrientDefinitionSyncPacket::handle)
                .add();
    }

    /** Sends the player's current nutrient values to that player's client. */
    public static void sendToPlayer(ServerPlayer player, NutrientsTracker tracker) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new NutrientSyncPacket(tracker));
    }

    /** Sends the current KubeJS definition snapshot to one client. */
    public static void sendDefinitions(ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new NutrientDefinitionSyncPacket(com.ironsword.gtmfo.common.nutrient.NutrientDefinitionRegistry.snapshot()));
    }
}
