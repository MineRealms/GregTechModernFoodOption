package com.ironsword.gtmfo.common.data;

import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.MetaMachineItem;
import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.api.item.component.GTMFOFoodStats;
import com.ironsword.gtmfo.common.registry.GTMFORegistries;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Set;
import java.util.function.Supplier;

import static com.ironsword.gtmfo.common.registry.GTMFORegistries.REGISTRATE;

/**
 * Creative tabs split, mirroring the original's 8 tabs (main / food / crops / tools / blocks /
 * drugs-alcohol / drinks / fruits-vegetables).
 * <p>
 * All items register into the main tab by default; {@link #assignTabs()} then moves them into their category,
 * matching the original's {@code getSubItems} filter rules.
 */
public class GTMFOCreativeModeTabs {

    public static RegistryEntry<CreativeModeTab> MAIN_TAB = GTMFORegistries.REGISTRATE.defaultCreativeTab(
            GregTechModernFoodOption.MODID,
            builder -> builder
                    .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(GregTechModernFoodOption.MODID, GTMFORegistries.REGISTRATE))
                    .icon(GTMFOItems.CHIPS_BAG::asStack)
                    .title(Component.literal("GregTech Modern Food Option"))
                    .build()
    ).register();

    private static final java.util.Map<String, String> CN_LANG = new java.util.LinkedHashMap<>();

    public static RegistryEntry<CreativeModeTab> FOOD_TAB = tab("food", "Food", "食物", GTMFOItems.BANANA::asStack);
    public static RegistryEntry<CreativeModeTab> CROPS_TAB = tab("crops", "Crops", "作物", GTMFOItems.SEED_ONION::asStack);
    public static RegistryEntry<CreativeModeTab> TOOLS_TAB = tab("tools", "Tools", "工具",
            () -> GTMFOTools.ROLLING_PINS.get(GTMaterials.Wood).asStack());
    public static RegistryEntry<CreativeModeTab> BLOCKS_TAB = tab("blocks", "Blocks", "方块", GTMFOItems.BRICK_ADOBE::asStack);
    public static RegistryEntry<CreativeModeTab> DRUGS_ALCOHOL_TAB = tab("funnystuff", "Drugs & Alcohol", "药品与酒精",
            GTMFOItems.BEER::asStack);
    public static RegistryEntry<CreativeModeTab> DRINKS_TAB = tab("drinks", "Drinks", "饮品", GTMFOItems.JUICE_ORANGE::asStack);
    public static RegistryEntry<CreativeModeTab> FRUITS_VEGETABLES_TAB = tab("fruitsvegetables", "Fruits & Vegetables",
            "水果与蔬菜", GTMFOItems.MANGO::asStack);

    private static RegistryEntry<CreativeModeTab> tab(String name, String title, String cnTitle, Supplier<ItemStack> icon) {
        String fullName = GregTechModernFoodOption.MODID + "_" + name;
        CN_LANG.put("itemGroup." + GregTechModernFoodOption.MODID + "." + fullName,
                "GregTech Modern Food Option：" + cnTitle);
        return REGISTRATE.defaultCreativeTab(fullName,
                builder -> builder
                        .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(fullName, REGISTRATE))
                        .icon(icon)
                        .title(REGISTRATE.addLang("itemGroup", GregTechModernFoodOption.id(fullName),
                                "GregTech Modern Food Option: " + title))
                        .build())
                .register();
    }

    public static void initCNLang(com.ironsword.gtmfo.data.CNLangProvider provider) {
        CN_LANG.forEach(provider::add);
    }

    public static void init() {
        // restore the main tab as default after creating the category tabs
        REGISTRATE.creativeModeTab(() -> MAIN_TAB);
    }

    // ===== original GTFOMetaItem categorization =====

    private static final Set<String> DRUGS_ALCOHOL = Set.of("vodka", "leninade", "beer", "white_wine", "red_wine");

    private static final Set<String> DRINKS = Set.of("mineral_water", "sparkling_water", "juice_apple",
            "juice_orange", "etirps", "etirps_cranberry", "coffee", "coffee_energized");

    private static final Set<String> FRUITS_VEGETABLES = Set.of("banana", "orange", "grapes", "white_grapes", "mango",
            "apricot", "lemon", "lime", "blackberry", "blueberry", "raspberry", "strawberry", "red_currant",
            "black_currant", "white_currant", "lingonberry", "elderberry", "cranberry", "olive", "tomato", "onion",
            "cucumber", "eggplant", "coffee_cherry");

    private static boolean isSeed(String path) {
        return path.startsWith("seed_");
    }

    private static boolean isDrugOrAlcohol(String path) {
        return DRUGS_ALCOHOL.contains(path) || path.startsWith("caplet_");
    }

    private static boolean isNonAlcoholDrink(String path) {
        return DRINKS.contains(path);
    }

    private static boolean isFruitOrVegetable(String path) {
        return FRUITS_VEGETABLES.contains(path);
    }

    private static boolean isTool(String path) {
        return path.startsWith("rolling_pin") || path.startsWith("butchery_knife");
    }

    private static boolean isFood(Item item) {
        if (item instanceof IComponentItem componentItem) {
            for (var component : componentItem.getComponents()) {
                if (component instanceof GTMFOFoodStats) return true;
            }
        }
        return item.isEdible();
    }

    /**
     * Reassigns every item registered in the main tab into its original category tab.
     * Called after all items/blocks/machines have been registered.
     */
    public static void assignTabs() {
        for (RegistryEntry<Item> entry : REGISTRATE.getAll(Registries.ITEM)) {
            if (!REGISTRATE.isInCreativeTab(entry, MAIN_TAB)) continue;
            Item item = entry.get();
            String path = entry.getId().getPath();
            RegistryEntry<CreativeModeTab> target = categorize(item, path);
            if (target != null && target != MAIN_TAB) {
                REGISTRATE.setCreativeTab(entry, target);
            }
        }
    }

    private static RegistryEntry<CreativeModeTab> categorize(Item item, String path) {
        if (isTool(path)) return TOOLS_TAB;
        if (isSeed(path)) return CROPS_TAB;
        if (isDrugOrAlcohol(path)) return DRUGS_ALCOHOL_TAB;
        if (isNonAlcoholDrink(path)) return DRINKS_TAB;
        if (isFruitOrVegetable(path)) return FRUITS_VEGETABLES_TAB;
        if (item instanceof BlockItem && !(item instanceof MetaMachineItem)) return BLOCKS_TAB;
        if (isFood(item)) return FOOD_TAB;
        return MAIN_TAB;
    }
}
