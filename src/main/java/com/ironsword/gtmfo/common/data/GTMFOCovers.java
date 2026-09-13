package com.ironsword.gtmfo.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.client.renderer.cover.SimpleCoverRenderer;
import com.gregtechceu.gtceu.common.data.GTCovers;
import com.gregtechceu.gtceu.common.item.CoverPlaceBehavior;
import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.common.cover.CoverSprinkler;
import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import static com.ironsword.gtmfo.common.registry.GTMFORegistries.REGISTRATE;

/**
 * GTFO cover registration.
 */
public class GTMFOCovers {

    public static final CoverDefinition SPRINKLER = GTCovers.register(
            GregTechModernFoodOption.id("sprinkler"),
            (definition, coverable, side) -> new CoverSprinkler(definition, coverable, side, 1),
            () -> () -> new SimpleCoverRenderer(GTCEu.id("block/cover/overlay_sprinkler")));

    public static final ItemEntry<ComponentItem> SPRINKLER_COVER = REGISTRATE
            .item("sprinkler_cover", ComponentItem::create)
            .lang("Sprinkler")
            .onRegister(attach(new CoverPlaceBehavior(SPRINKLER)))
            // model is hand-written in src/main/resources (uses the cover overlay texture)
            .model(com.tterrag.registrate.util.nullness.NonNullBiConsumer.noop())
            .register();

    private static <T extends ComponentItem> NonNullConsumer<T> attach(IItemComponent components) {
        return item -> item.attachComponents(components);
    }

    public static void init() {
    }
}
