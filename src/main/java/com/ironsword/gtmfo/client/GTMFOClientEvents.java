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

    @SubscribeEvent
    public static void onRegisterParticleProviders(net.minecraftforge.client.event.RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(com.ironsword.gtmfo.common.data.GTMFOParticles.SPRINKLE.get(),
                com.ironsword.gtmfo.client.particle.GTFOSprinkleParticle.Provider::new);
    }

    /** Nutrient HUD (top-left panel, see NutrientHudOverlay). */
    @SubscribeEvent
    public static void onRegisterGuiOverlays(net.minecraftforge.client.event.RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("gtmfo_nutrients",
                new com.ironsword.gtmfo.client.nutrient.NutrientHudOverlay());
    }

    @SubscribeEvent
    public static void onRegisterBlockColors(net.minecraftforge.client.event.RegisterColorHandlersEvent.Block event) {
        for (int i = 0; i < com.ironsword.gtmfo.common.data.GTMFOTrees.LEAVES.size(); i++) {
            var tree = com.ironsword.gtmfo.common.data.GTMFOTrees.TREES.get(i);
            var leaves = com.ironsword.gtmfo.common.data.GTMFOTrees.LEAVES.get(i).get();
            if (i == com.ironsword.gtmfo.common.data.GTMFOTrees.RAINBOWWOOD_INDEX) {
                event.register((state, level, pos, tintIndex) -> {
                    if (pos == null) return com.ironsword.gtmfo.common.data.GTMFOTrees.RAINBOWWOOD_ITEM_COLOR;
                    int[] rainbow = com.ironsword.gtmfo.common.data.GTMFOTrees.RAINBOW_ARRAY;
                    return rainbow[(Math.abs(pos.getX()) + Math.abs(pos.getY()) + Math.abs(pos.getZ()))
                            % rainbow.length];
                }, leaves);
            } else {
                event.register((state, level, pos, tintIndex) -> tree.leafColor(), leaves);
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterItemColors(net.minecraftforge.client.event.RegisterColorHandlersEvent.Item event) {
        for (int i = 0; i < com.ironsword.gtmfo.common.data.GTMFOTrees.LEAVES.size(); i++) {
            var tree = com.ironsword.gtmfo.common.data.GTMFOTrees.TREES.get(i);
            event.register((stack, tintIndex) -> tree.leafColor(), com.ironsword.gtmfo.common.data.GTMFOTrees.LEAVES.get(i).get());
        }
    }
}
