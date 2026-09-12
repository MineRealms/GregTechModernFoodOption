package com.ironsword.gtmfo.common;

import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.data.loot.ChestGenHooks;
import com.ironsword.gtmfo.GTMFOConfigHolder;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.GTMFOLacing;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

/**
 * Dungeon loot injection, ported from the original {@code GTFODungeonLootLoader}.
 * Uses GTCEu's {@link ChestGenHooks}; laced foods carry the cyanide lacing tag.
 */
public class GTFODungeonLoot {

    private static final ResourceLocation[] COMMON_TABLES = {
            BuiltInLootTables.ABANDONED_MINESHAFT,
            BuiltInLootTables.JUNGLE_TEMPLE,
            BuiltInLootTables.DESERT_PYRAMID,
            BuiltInLootTables.SIMPLE_DUNGEON,
            BuiltInLootTables.STRONGHOLD_CORRIDOR,
            BuiltInLootTables.STRONGHOLD_CROSSING,
            BuiltInLootTables.WOODLAND_MANSION
    };

    public static void init() {
        if (!GTMFOConfigHolder.INSTANCE.gtfoMiscConfig.addDungeonFoods) return;

        // GTCEu only registers its loot hook when its own loot options are on
        if (!ConfigHolder.INSTANCE.worldgen.addLoot && !ConfigHolder.INSTANCE.worldgen.increaseDungeonLoot) {
            ChestGenHooks.init();
        }

        addItem(GTMFOItems.SANDWICH_STEAK.asStack(), 2, 8, 20);
        addItem(GTMFOItems.SANDWICH_CHEESE.asStack(), 2, 8, 20);
        addItem(GTMFOItems.SANDWICH_BACON.asStack(), 2, 8, 20);
        addItem(com.ironsword.gtmfo.common.data.GTMFOBlocks.PIZZA_CHEESE.asStack(), 2, 4, 20);
        addItem(com.ironsword.gtmfo.common.data.GTMFOBlocks.PIZZA_VEGGIE.asStack(), 2, 4, 20);
        addItem(com.ironsword.gtmfo.common.data.GTMFOBlocks.PIZZA_MEAT.asStack(), 2, 4, 20);
        addItem(GTMFOItems.FRENCH_FRIES.asStack(), 2, 10, 30);
        addItem(GTMFOItems.CHIPS_SYALS.asStack(), 2, 4, 10);
        addItem(GTMFOItems.CHIPS_BAG.asStack(), 2, 10, 40);
        addItem(GTMFOItems.FISH_AND_CHIPS.asStack(), 2, 10, 20);
        addItem(GTMFOItems.CHIPS_KETTLE.asStack(), 2, 10, 30);
        addItem(GTMFOItems.CHIPS_REDUCED_FAT.asStack(), 2, 10, 10);
        addItem(GTMFOItems.BURGER_CHEESE.asStack(), 2, 10, 30);
        addItem(GTMFOItems.POTATO_STICK.asStack(), 8, 16, 40);
        addItem(GTMFOItems.CHUM_STICK.asStack(), 8, 16, 10);
        addItem(GTMFOItems.ETIRPS.asStack(), 8, 16, 40);
        addItem(GTMFOItems.BEANS_ON_TOAST.asStack(), 5, 10, 30);
        addItem(GTMFOItems.SANDWICH_TOAST.asStack(), 5, 10, 10);
        addItem(GTMFOItems.MUSHY_PEAS.asStack(), 5, 10, 20);
        addItem(GTMFOItems.BAKED_BEANS.asStack(), 5, 10, 20);
        addItem(GTMFOItems.MOZZARELLA_BALL.asStack(), 8, 32, 20);
        addItem(GTMFOItems.HOT_MUSHROOM_STEW.asStack(), 1, 1, 16);
        addItem(GTMFOItems.HOT_RABBIT_STEW.asStack(), 1, 1, 16);
        addItem(GTMFOItems.HOT_BEETROOT_SOUP.asStack(), 1, 1, 16);
        addItem(GTMFOItems.CAPLET_GEL.asStack(), 1, 1, 4);
        addItem(GTMFOItems.KEBAB_SOLTANI.asStack(), 2, 8, 16);
        addItem(GTMFOItems.KEBAB_BARG.asStack(), 2, 8, 16);
        addItem(GTMFOItems.KEBAB_CARROT.asStack(), 2, 8, 16);
        addItem(GTMFOItems.KEBAB_KUBIDEH.asStack(), 2, 8, 16);
        addItem(GTMFOItems.KEBAB_FAT.asStack(), 2, 8, 16);
        addItem(GTMFOItems.KEBAB_CHUM.asStack(), 2, 8, 8);
        addItem(GTMFOItems.NILK.asStack(), 2, 8, 10);
        addItem(GTMFOItems.VODKA.asStack(), 2, 8, 10);
        addItem(GTMFOItems.ICE_CREAM_CHOCOLATE.asStack(), 10, 32, 10);
        addItem(GTMFOItems.ICE_CREAM_CHIP.asStack(), 10, 32, 5);
        addItem(GTMFOItems.ICE_CREAM_BACON.asStack(), 10, 32, 5);
        addItem(GTMFOItems.ICE_CREAM_BANANA.asStack(), 10, 32, 5);
        addItem(GTMFOItems.ICE_CREAM_MELON.asStack(), 10, 32, 5);
        addItem(GTMFOItems.ICE_CREAM.asStack(), 10, 32, 15);
        addItem(GTMFOItems.ICE_CREAM_VANILLA.asStack(), 10, 32, 15);
        addItem(GTMFOItems.ICE_CREAM_BEAR.asStack(), 10, 32, 5);
        addItem(GTMFOItems.ICE_CREAM_LEMON.asStack(), 10, 32, 5);

        addItemRare(GTMFOItems.MINERAL_WATER.asStack(), 1, 1, 1);
        addItemRare(GTMFOItems.SMOGUS_HEART.asStack(), 1, 1, 1);
    }

    private static void addItem(ItemStack food, int min, int max, int weight) {
        for (ResourceLocation table : COMMON_TABLES) {
            ChestGenHooks.addItem(table, food.copy(), min, max, weight);
        }
        if (GTMFOConfigHolder.INSTANCE.gtfoMiscConfig.addLacedDungeonFoods) {
            ItemStack laced = food.copy();
            CompoundTag tag = laced.getOrCreateTag();
            tag.putInt(GTMFOLacing.NBT_KEY, 0); // cyanide

            ChestGenHooks.addItem(BuiltInLootTables.ABANDONED_MINESHAFT, laced.copy(), min, max, Math.max(1, weight / 3));
            ChestGenHooks.addItem(BuiltInLootTables.JUNGLE_TEMPLE, laced.copy(), min, max, weight);
            ChestGenHooks.addItem(BuiltInLootTables.DESERT_PYRAMID, laced.copy(), min, max, Math.max(1, weight / 2));
            ChestGenHooks.addItem(BuiltInLootTables.SIMPLE_DUNGEON, laced.copy(), min, max, Math.max(1, weight / 2));
            ChestGenHooks.addItem(BuiltInLootTables.STRONGHOLD_CORRIDOR, laced.copy(), min, max, Math.max(1, weight / 3));
            ChestGenHooks.addItem(BuiltInLootTables.STRONGHOLD_CROSSING, laced.copy(), min, max, Math.max(1, weight / 2));
            ChestGenHooks.addItem(BuiltInLootTables.WOODLAND_MANSION, laced.copy(), min, max, weight);
        }
    }

    private static void addItemRare(ItemStack food, int min, int max, int weight) {
        ChestGenHooks.addItem(BuiltInLootTables.JUNGLE_TEMPLE, food.copy(), min, max, weight);
        ChestGenHooks.addItem(BuiltInLootTables.WOODLAND_MANSION, food.copy(), min, max, weight);

        if (GTMFOConfigHolder.INSTANCE.gtfoMiscConfig.addLacedDungeonFoods) {
            ItemStack laced = food.copy();
            CompoundTag tag = laced.getOrCreateTag();
            tag.putInt(GTMFOLacing.NBT_KEY, 0); // cyanide

            ChestGenHooks.addItem(BuiltInLootTables.JUNGLE_TEMPLE, laced.copy(), min, max, weight);
            ChestGenHooks.addItem(BuiltInLootTables.WOODLAND_MANSION, laced.copy(), min, max, weight);
            ChestGenHooks.addItem(BuiltInLootTables.SIMPLE_DUNGEON, laced.copy(), min, max, weight * 4);
        }
    }
}
