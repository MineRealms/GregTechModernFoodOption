package com.ironsword.gtmfo.common.data;

import com.ironsword.gtmfo.common.block.GTFOBerryBushBlock;
import com.ironsword.gtmfo.common.block.GTFOCropBlock;
import com.ironsword.gtmfo.common.block.GTFORootCropBlock;
import com.ironsword.gtmfo.common.block.GTFOWaterCropBlock;
import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.ironsword.gtmfo.common.registry.GTMFORegistries.REGISTRATE;

/**
 * Crop + berry bush block registration. Seed -> crop resolution is lazy
 * (populated with suppliers, resolved after registration).
 *
 * <p>Blockstates/models are provided as hand-converted resources (assets/gtmfo/blockstates + models).</p>
 */
public class GTMFOCrops {

    /** (seed item, crop block) pairs, resolved lazily */
    private static final List<Pair<Supplier<? extends Item>, Supplier<? extends Block>>> CROP_PAIRS = new ArrayList<>();

    public static Block getCropFor(Item seed) {
        for (Pair<Supplier<? extends Item>, Supplier<? extends Block>> pair : CROP_PAIRS) {
            if (pair.getFirst().get() == seed) {
                return pair.getSecond().get();
            }
        }
        return null;
    }

    private static void pair(Supplier<? extends Item> seed, Supplier<? extends Block> block) {
        CROP_PAIRS.add(Pair.of(seed, block));
    }

    private static BlockEntry<GTFOCropBlock> crop(String name, Supplier<? extends ItemLike> seed,
                                                  Supplier<? extends ItemLike> crop) {
        return REGISTRATE.block("crop_" + name, p -> new GTFOCropBlock(p, seed, crop))
                .initialProperties(() -> Blocks.WHEAT)
                .lang("Crop: " + name)
                .blockstate(NonNullBiConsumer.noop())
                .register();
    }

    private static BlockEntry<GTFORootCropBlock> rootCrop(String name, Supplier<? extends ItemLike> seed,
                                                          Supplier<? extends ItemLike> crop) {
        return REGISTRATE.block("crop_" + name, p -> new GTFORootCropBlock(p, seed, crop))
                .initialProperties(() -> Blocks.WHEAT)
                .lang("Root Crop: " + name)
                .blockstate(NonNullBiConsumer.noop())
                .register();
    }

    private static BlockEntry<GTFOWaterCropBlock> waterCrop(String name, Supplier<? extends ItemLike> seed,
                                                            Supplier<? extends ItemLike> crop) {
        return REGISTRATE.block("crop_" + name, p -> new GTFOWaterCropBlock(p, seed, crop))
                .initialProperties(() -> Blocks.WHEAT)
                .lang("Water Crop: " + name)
                .blockstate(NonNullBiConsumer.noop())
                .register();
    }

    private static BlockEntry<GTFOBerryBushBlock> berryBush(String name, Supplier<? extends ItemLike> berry,
                                                            boolean thorny) {
        return REGISTRATE.block("crop_" + name, p -> new GTFOBerryBushBlock(p, berry, thorny))
                .initialProperties(() -> Blocks.SWEET_BERRY_BUSH)
                .properties(p -> p.randomTicks().strength(1.0F))
                .lang("Berry Bush: " + name)
                .blockstate(NonNullBiConsumer.noop())
                .register();
    }

    public static void init() {
        // === crops ===
        pair(() -> GTMFOItems.SEED_COFFEE.get(), crop("coffee", () -> GTMFOItems.SEED_COFFEE.get(), () -> GTMFOItems.COFFEE_CHERRY.get()));
        pair(() -> GTMFOItems.CUCUMBER.get(), crop("cucumber", () -> GTMFOItems.CUCUMBER.get(), () -> GTMFOItems.CUCUMBER.get()));
        pair(() -> GTMFOItems.SEED_ONION.get(), rootCrop("onion", () -> GTMFOItems.SEED_ONION.get(), () -> GTMFOItems.ONION.get()));
        pair(() -> GTMFOItems.SEED_SOY.get(), crop("soy", () -> GTMFOItems.SEED_SOY.get(), () -> GTMFOItems.SOYBEAN.get()));
        pair(() -> GTMFOItems.SEED_TOMATO.get(), crop("tomato", () -> GTMFOItems.SEED_TOMATO.get(), () -> GTMFOItems.TOMATO.get()));
        pair(() -> GTMFOItems.SEED_GRAPE.get(), crop("grape", () -> GTMFOItems.SEED_GRAPE.get(), () -> GTMFOItems.GRAPES.get()));
        pair(() -> GTMFOItems.SEED_PEA.get(), crop("pea", () -> GTMFOItems.SEED_PEA.get(), () -> GTMFOItems.PEA_POD.get()));
        pair(() -> GTMFOItems.SEED_BEAN.get(), crop("bean", () -> GTMFOItems.SEED_BEAN.get(), () -> GTMFOItems.SEED_BEAN.get()));
        pair(() -> GTMFOItems.SEED_OREGANO.get(), crop("oregano", () -> GTMFOItems.SEED_OREGANO.get(), () -> GTMFOItems.OREGANO.get()));
        pair(() -> GTMFOItems.SEED_BASIL.get(), crop("basil", () -> GTMFOItems.SEED_BASIL.get(), () -> GTMFOItems.BASIL.get()));
        pair(() -> GTMFOItems.SEED_EGGPLANT.get(), crop("aubergine", () -> GTMFOItems.SEED_EGGPLANT.get(), () -> GTMFOItems.EGGPLANT.get()));
        pair(() -> GTMFOItems.SEED_HORSERADISH.get(), rootCrop("horseradish", () -> GTMFOItems.SEED_HORSERADISH.get(), () -> GTMFOItems.HORSERADISH.get()));
        BlockEntry<GTFOCropBlock> garlic = crop("garlic", () -> GTMFOItems.SEED_GARLIC_PURPLE.get(), () -> GTMFOItems.GARLIC_PURPLE.get());
        pair(() -> GTMFOItems.SEED_GARLIC_PURPLE.get(), garlic);
        pair(() -> GTMFOItems.SEED_GARLIC_WHITE.get(), garlic);
        pair(() -> GTMFOItems.SEED_ARTICHOKE.get(), crop("artichoke", () -> GTMFOItems.SEED_ARTICHOKE.get(), () -> GTMFOItems.ARTICHOKE.get()));
        pair(() -> GTMFOItems.BLACK_PEPPER.get(), crop("black_pepper", () -> GTMFOItems.BLACK_PEPPER.get(), () -> GTMFOItems.BLACK_PEPPER.get()));
        pair(() -> GTMFOItems.RICE.get(), waterCrop("rice", () -> GTMFOItems.RICE.get(), () -> GTMFOItems.RICE.get()));
        pair(() -> GTMFOItems.SEED_WHITE_GRAPE.get(), crop("white_grape", () -> GTMFOItems.SEED_WHITE_GRAPE.get(), () -> GTMFOItems.WHITE_GRAPES.get()));
        pair(() -> GTMFOItems.SEED_COTTON.get(), crop("cotton", () -> GTMFOItems.SEED_COTTON.get(), () -> GTMFOItems.COTTON.get()));
        pair(() -> GTMFOItems.CORN_KERNEL.get(), crop("corn", () -> GTMFOItems.CORN_KERNEL.get(), () -> GTMFOItems.CORN_EAR.get()));

        // === berry bushes ===
        pair(() -> GTMFOItems.BLUEBERRY.get(), berryBush("blueberry", () -> GTMFOItems.BLUEBERRY.get(), false));
        pair(() -> GTMFOItems.BLACKBERRY.get(), berryBush("blackberry", () -> GTMFOItems.BLACKBERRY.get(), true));
        pair(() -> GTMFOItems.RASPBERRY.get(), berryBush("raspberry", () -> GTMFOItems.RASPBERRY.get(), true));
        pair(() -> GTMFOItems.STRAWBERRY.get(), berryBush("strawberry", () -> GTMFOItems.STRAWBERRY.get(), false));
        pair(() -> GTMFOItems.RED_CURRANT.get(), berryBush("red_currant", () -> GTMFOItems.RED_CURRANT.get(), false));
        pair(() -> GTMFOItems.BLACK_CURRANT.get(), berryBush("black_currant", () -> GTMFOItems.BLACK_CURRANT.get(), false));
        pair(() -> GTMFOItems.WHITE_CURRANT.get(), berryBush("white_currant", () -> GTMFOItems.WHITE_CURRANT.get(), false));
        pair(() -> GTMFOItems.LINGONBERRY.get(), berryBush("lingonberry", () -> GTMFOItems.LINGONBERRY.get(), false));
        pair(() -> GTMFOItems.ELDERBERRY.get(), berryBush("elderberry", () -> GTMFOItems.ELDERBERRY.get(), false));
        pair(() -> GTMFOItems.CRANBERRY.get(), berryBush("cranberry", () -> GTMFOItems.CRANBERRY.get(), false));
    }
}
