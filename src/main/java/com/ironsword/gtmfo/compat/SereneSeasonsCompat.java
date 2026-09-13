package com.ironsword.gtmfo.compat;

import com.ironsword.gtmfo.GregTechModernFoodOption;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * SereneSeasons 软兼容（无硬依赖，仅当 SereneSeasons 加载时生效）。
 *
 * <p>移植自原版 {@code GTFOSSTooltipHandler}：为 GTMFO 作物种子/产物显示"适宜季节"提示。
 * 季节归属由本模组自带的数据包标签决定：
 * {@code data/sereneseasons/tags/{items,blocks}/{spring,summer,autumn,winter}_crops.json}。</p>
 */
@Mod.EventBusSubscriber(modid = GregTechModernFoodOption.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class SereneSeasonsCompat {

    private static final String SS_MODID = "sereneseasons";

    private static final TagKey<Item> SPRING = seasonTag("spring_crops");
    private static final TagKey<Item> SUMMER = seasonTag("summer_crops");
    private static final TagKey<Item> AUTUMN = seasonTag("autumn_crops");
    private static final TagKey<Item> WINTER = seasonTag("winter_crops");

    private static Boolean loaded;

    private SereneSeasonsCompat() {}

    private static TagKey<Item> seasonTag(String name) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(SS_MODID, name));
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (loaded == null) {
            loaded = ModList.get().isLoaded(SS_MODID);
        }
        if (!loaded) {
            return;
        }
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) {
            return;
        }
        // 只处理本模组的物品，避免影响其它 mod 注册进季节标签的物品
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        if (id == null || !GregTechModernFoodOption.MODID.equals(id.getNamespace())) {
            return;
        }
        boolean spring = stack.is(SPRING);
        boolean summer = stack.is(SUMMER);
        boolean autumn = stack.is(AUTUMN);
        boolean winter = stack.is(WINTER);
        if (!spring && !summer && !autumn && !winter) {
            return;
        }
        List<Component> tooltip = event.getToolTip();
        tooltip.add(Component.translatable("gtmfo.tooltip.fertile_seasons"));
        if (spring && summer && autumn && winter) {
            tooltip.add(Component.translatable("gtmfo.tooltip.fertile_seasons.year_round")
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
            return;
        }
        if (spring) {
            tooltip.add(Component.translatable("gtmfo.tooltip.fertile_seasons.spring").withStyle(ChatFormatting.GREEN));
        }
        if (summer) {
            tooltip.add(Component.translatable("gtmfo.tooltip.fertile_seasons.summer").withStyle(ChatFormatting.YELLOW));
        }
        if (autumn) {
            tooltip.add(Component.translatable("gtmfo.tooltip.fertile_seasons.autumn").withStyle(ChatFormatting.GOLD));
        }
        if (winter) {
            tooltip.add(Component.translatable("gtmfo.tooltip.fertile_seasons.winter").withStyle(ChatFormatting.AQUA));
        }
    }
}
