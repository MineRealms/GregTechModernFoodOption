package com.ironsword.gtmfo.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.item.IGTTool;
import com.gregtechceu.gtceu.api.item.tool.GTSwordItem;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.ToolActions;

import java.util.List;

import static com.ironsword.gtmfo.common.registry.GTMFORegistries.REGISTRATE;

/**
 * GTFO tools: rolling pin (4 materials) and the HV electric butchery knife.
 */
public class GTMFOTools {

    public static final GTToolType ROLLING_PIN = GTToolType.builder("rolling_pin")
            .toolStats(b -> b.crafting())
            .toolClassNames("rolling_pin")
            .build();

    public static final GTToolType BUTCHERY_KNIFE = GTToolType.builder("butchery_knife")
            .toolStats(b -> b.attacking().attackDamage(3.0F).attackSpeed(-2.4F)
                    .defaultEnchantment(Enchantments.MOB_LOOTING, 5)
                    .brokenStack(ToolHelper.SUPPLY_POWER_UNIT_HV))
            .constructor(GTSwordItem::new)
            .toolClassNames("butchery_knife")
            .defaultActions(ToolActions.DEFAULT_SWORD_ACTIONS)
            .sound(GTSoundEntries.CUT)
            .electric(GTValues.HV)
            .build();

    /** material -> rolling pin item */
    public static final java.util.Map<Material, com.tterrag.registrate.util.entry.ItemEntry<net.minecraft.world.item.Item>> ROLLING_PINS =
            new java.util.HashMap<>();

    public static com.tterrag.registrate.util.entry.ItemEntry<net.minecraft.world.item.Item> BUTCHERY_KNIFE_ITEM;

    public static void init() {
        for (Material material : List.of(
                GTMaterials.Wood,
                GTMaterials.Rubber,
                GTMaterials.Polyethylene,
                GTMaterials.Polytetrafluoroethylene)) {
            ROLLING_PINS.put(material, registerTool(ROLLING_PIN, material));
        }
        BUTCHERY_KNIFE_ITEM = registerTool(BUTCHERY_KNIFE, GTMaterials.StainlessSteel);
    }

    @SuppressWarnings("unchecked")
    private static com.tterrag.registrate.util.entry.ItemEntry<net.minecraft.world.item.Item> registerTool(
            GTToolType toolType, Material material) {
        var tier = material.getToolTier();
        return (com.tterrag.registrate.util.entry.ItemEntry<net.minecraft.world.item.Item>)
                (com.tterrag.registrate.util.entry.ItemEntry<?>) REGISTRATE.item(
                        toolType.idFormat.formatted(material.getName()),
                        p -> toolType.constructor.apply(toolType, tier, material, toolType.toolDefinition, p).asItem())
                        .properties(p -> p.craftRemainder(Items.AIR))
                        .setData(ProviderType.LANG, NonNullBiConsumer.noop())
                        .model(NonNullBiConsumer.noop())
                        .color(() -> IGTTool::tintColor)
                        .register();
    }
}
