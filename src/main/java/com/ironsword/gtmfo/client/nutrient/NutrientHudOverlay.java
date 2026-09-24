package com.ironsword.gtmfo.client.nutrient;

import com.ironsword.gtmfo.GTMFOConfigHolder;
import com.ironsword.gtmfo.api.capability.Nutrients;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.Locale;

/**
 * Small nutrient HUD panel (top-left). Values come from {@link ClientNutrientCache}; the panel is
 * drawn only when the nutrient system is enabled, the {@code hud} config is on, no screen is open
 * and the server has sent at least one snapshot.
 */
public class NutrientHudOverlay implements IGuiOverlay {

    private static final int X = 4;
    private static final int Y = 4;
    private static final int LINE_HEIGHT = 10;
    private static final int PANEL_WIDTH = 84;

    /** Resolved lazily because the class is registered during client setup, before languages load. */
    private String[] labels;

    private String[] labels() {
        if (labels == null) {
            labels = new String[Nutrients.LIST.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = Component.translatable(Nutrients.langKey(Nutrients.LIST.get(i))).getString();
            }
        }
        return labels;
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight) {
        GTMFOConfigHolder cfg = GTMFOConfigHolder.INSTANCE;
        if (cfg == null || !Nutrients.isEnabled() || !cfg.gtfoNutrientConfig.hud) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui || minecraft.screen != null) return;
        if (!ClientNutrientCache.hasData()) return;

        double threshold = cfg.gtfoNutrientConfig.benefitThreshold;
        String[] labels = labels();
        int panelHeight = LINE_HEIGHT * Nutrients.LIST.size() + 4;
        graphics.fill(X - 2, Y - 2, X + PANEL_WIDTH, Y + panelHeight - 2, 0x55000000);

        for (int i = 0; i < Nutrients.LIST.size(); i++) {
            float value = ClientNutrientCache.get(i);
            int color = value <= 0.0F ? 0x808080
                    : (threshold > 0 && value >= threshold ? 0x55FF55 : 0xFFFFFF);
            graphics.drawString(minecraft.font, labels[i] + " " + format(value),
                    X, Y + i * LINE_HEIGHT, color, true);
        }
    }

    private static String format(float value) {
        if (value == Math.round(value)) {
            return Integer.toString((int) value);
        }
        return String.format(Locale.ROOT, "%.1f", value);
    }
}
