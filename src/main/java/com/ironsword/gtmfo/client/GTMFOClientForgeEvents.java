package com.ironsword.gtmfo.client;

import com.ironsword.gtmfo.GregTechModernFoodOption;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Client-side Forge (game) bus events.
 */
@Mod.EventBusSubscriber(modid = GregTechModernFoodOption.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class GTMFOClientForgeEvents {

    /**
     * Original {@code GTFOEventHandler.handlePlayerRender}: while the local player has Anti-Schizo,
     * other players are not rendered.
     */
    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        var localPlayer = Minecraft.getInstance().player;
        if (localPlayer == null || event.getEntity() == localPlayer) return;
        if (com.ironsword.gtmfo.common.data.GTMFOEffects.ANTISCHIZO.isPresent()
                && localPlayer.hasEffect(com.ironsword.gtmfo.common.data.GTMFOEffects.ANTISCHIZO.get())) {
            event.setCanceled(true);
        }
    }

    /** Drop the nutrient snapshot when leaving a world so the HUD does not show stale values. */
    @SubscribeEvent
    public static void onLoggingOut(net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
        com.ironsword.gtmfo.client.nutrient.ClientNutrientCache.clear();
    }
}
