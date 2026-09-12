package com.ironsword.gtmfo.client;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.client.renderer.ItalianBuffaloRenderer;
import com.ironsword.gtmfo.common.data.GTMFOEntities;
import net.minecraft.client.renderer.entity.SnowGolemRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GregTechModernFoodOption.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GTMFOClientEvents {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GTMFOEntities.ITALIAN_BUFFALO.get(), ItalianBuffaloRenderer::new);
        event.registerEntityRenderer(GTMFOEntities.STRONG_SNOWMAN.get(), SnowGolemRenderer::new);
        event.registerEntityRenderer(GTMFOEntities.STRONG_SNOWBALL.get(), context -> new ThrownItemRenderer<>(context, 1.0F, true));
    }
}
