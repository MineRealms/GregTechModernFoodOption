package com.ironsword.gtmfo.common.data.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;
import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.common.data.GTMFOCreativeModeTabs;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import com.ironsword.gtmfo.common.machine.FarmerMachine;
import com.ironsword.gtmfo.common.machine.MicrowaveMachine;
import com.ironsword.gtmfo.common.machine.MobAgeSorterMachine;
import com.ironsword.gtmfo.common.machine.MobExterminatorMachine;
import com.ironsword.gtmfo.common.machine.MobExtractorMachine;
import com.ironsword.gtmfo.data.CNLangProvider;
import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.gregtechceu.gtceu.api.GTValues.VLVT;
import static com.gregtechceu.gtceu.utils.FormattingUtil.toEnglishName;
import static com.ironsword.gtmfo.common.registry.GTMFORegistries.REGISTRATE;
import static net.minecraft.ChatFormatting.*;

public class GTMFOMachines {
    public static final String[] VLVH_CN = new String[] {
            "超低压",
            "基础",
            AQUA + "进阶",
            GOLD + "进阶",
            DARK_PURPLE + "进阶",
            BLUE + "精英",
            LIGHT_PURPLE + "精英",
            RED + "精英",
            DARK_AQUA + "终极",
            DARK_RED + "史诗",
            GREEN + "史诗",
            DARK_GREEN + "史诗",
            YELLOW + "史诗",
            BLUE.toString() + BOLD + "传奇",
            RED.toString() + BOLD + "MAX" };

    public static final Map<String, Pair<String,String>> JEILangPairMap = new HashMap<>();
    public static final Map<String, String> CNLangMap = new HashMap<>();

    static {
        REGISTRATE.creativeModeTab(()-> GTMFOCreativeModeTabs.MAIN_TAB);
    }

    public static MachineDefinition[] SLICER = GTMFOMachineUtils.registerSimpleMachines(REGISTRATE,"slicer", GTMFORecipeTypes.SLICER_RECIPES);
    public static MachineDefinition[] CUISINE_ASSEMBLER = GTMFOMachineUtils.registerSimpleMachines(REGISTRATE,"cuisine_assembler",GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES);
    public static MachineDefinition[] MICROWAVE = registerMicrowaves();
    public static MachineDefinition[] MULTICOOKER = GTMFOMachineUtils.registerSimpleMachines(REGISTRATE,"multicooker",GTMFORecipeTypes.MULTICOOKER_RECIPES);

    /** sucking range per tier (LV, MV, HV, EV) */
    private static final int[] MOB_AGE_SORTER_RANGES = {1, 3, 5, 9};

    public static MachineDefinition[] MOB_AGE_SORTER = GTMachineUtils.registerTieredMachines(REGISTRATE,
            "mob_age_sorter",
            (holder, tier) -> new MobAgeSorterMachine(holder, tier, MOB_AGE_SORTER_RANGES[tier - 1]),
            (tier, builder) -> builder
                    .langValue("%s Mob Age Sorter %s".formatted(GTValues.VLVH[tier], GTValues.VLVT[tier]))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .workableTieredHullModel(GTCEu.id("block/machines/mob_age_sorter"))
                    .tooltips(
                            Component.translatable("gtmfo.machine.mob_age_sorter.range",
                                    MOB_AGE_SORTER_RANGES[tier - 1], MOB_AGE_SORTER_RANGES[tier - 1]),
                            Component.translatable("gtceu.universal.tooltip.voltage_in",
                                    GTValues.V[tier], GTValues.VNF[tier]),
                            Component.translatable("gtceu.universal.tooltip.energy_storage_capacity",
                                    GTValues.V[tier] * 64),
                            Component.translatable("gtceu.universal.tooltip.requires_redstone"),
                            Component.translatable("gtceu.universal.tooltip.uses_per_tick",
                                    8L * (1L << ((tier - 1) * 2))))
                    .register(),
            GTValues.LV, GTValues.MV, GTValues.HV, GTValues.EV);

    public static MachineDefinition[] MOB_EXTERMINATOR = GTMachineUtils.registerTieredMachines(REGISTRATE,
            "mob_exterminator",
            MobExterminatorMachine::new,
            (tier, builder) -> builder
                    .langValue("%s Mob Exterminator %s".formatted(GTValues.VLVH[tier], GTValues.VLVT[tier]))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .workableTieredHullModel(GTCEu.id("block/machines/mob_exterminator"))
                    .tooltips(
                            Component.translatable("gtmfo.machine.mob_exterminator.tooltip", tier - 1),
                            Component.translatable("gtceu.universal.tooltip.voltage_in",
                                    GTValues.V[tier], GTValues.VNF[tier]),
                            Component.translatable("gtceu.universal.tooltip.energy_storage_capacity",
                                    GTValues.V[tier] * 64),
                            Component.translatable("gtceu.universal.tooltip.requires_redstone"),
                            Component.translatable("gtmfo.machine.mob_exterminator.tooltip.consumption",
                                    2L * (1L << ((tier - 1) * 2))))
                    .register(),
            GTValues.LV, GTValues.MV, GTValues.HV, GTValues.EV);

    public static MachineDefinition[] MOB_EXTRACTOR = GTMachineUtils.registerTieredMachines(REGISTRATE,
            "mob_extractor",
            MobExtractorMachine::new,
            (tier, builder) -> builder
                    .langValue("%s Mob Extractor %s".formatted(GTValues.VLVH[tier], GTValues.VLVT[tier]))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .recipeType(GTMFORecipeTypes.MOB_EXTRACTOR_RECIPES)
                    .editableUI(GTMFOGuiUtils.withLogo(GTCEu.id("mob_extractor"),
                            GTMFORecipeTypes.MOB_EXTRACTOR_RECIPES))
                    .workableTieredHullModel(GTCEu.id("block/machines/mob_extractor"))
                    .tooltips(GTMachineUtils.workableTiered(tier, GTValues.V[tier], GTValues.V[tier] * 64,
                            GTMFORecipeTypes.MOB_EXTRACTOR_RECIPES,
                            GTMachineUtils.defaultTankSizeFunction.applyAsInt(tier), true))
                    .register(),
            GTValues.LV, GTValues.MV, GTValues.HV, GTValues.EV, GTValues.IV, GTValues.LuV, GTValues.ZPM, GTValues.UV);

    /** ticks per action per tier (LV, MV, HV, EV) */
    private static final int[] FARMER_SPEEDS = {20, 10, 5, 2};

    public static MachineDefinition[] FARMER = GTMachineUtils.registerTieredMachines(REGISTRATE,
            "farmer",
            (holder, tier) -> new FarmerMachine(holder, tier, FARMER_SPEEDS[tier - 1]),
            (tier, builder) -> builder
                    .langValue("%s Farmer %s".formatted(GTValues.VLVH[tier], GTValues.VLVT[tier]))
                    .rotationState(RotationState.NON_Y_AXIS)
                    .workableTieredHullModel(GTCEu.id("block/machines/farmer"))
                    .tooltips(
                            Component.translatable("gtmfo.machine.farmer.tooltip"),
                            Component.translatable("gtceu.universal.tooltip.voltage_in",
                                    GTValues.V[tier], GTValues.VNF[tier]),
                            Component.translatable("gtceu.universal.tooltip.energy_storage_capacity",
                                    GTValues.V[tier] * 64),
                            Component.translatable("gtmfo.machine.farmer.tooltip.speed", FARMER_SPEEDS[tier - 1]),
                            Component.translatable("gtceu.universal.tooltip.uses_per_tick",
                                    16L * (1L << ((tier - 1) * 2))))
                    .register(),
            GTValues.LV, GTValues.MV, GTValues.HV, GTValues.EV);

    private static MachineDefinition[] registerMicrowaves() {
        return GTMachineUtils.registerTieredMachines(REGISTRATE, "microwave",
                (info, tier) -> new MicrowaveMachine(info, tier),
                (tier, builder) -> {
                    java.util.List<Component> tooltips = new java.util.ArrayList<>();
                    String flavorKey = flavorTooltipKey("microwave", tier);
                    if (flavorKey != null) {
                        tooltips.add(Component.translatable(flavorKey));
                    }
                    tooltips.addAll(java.util.Arrays.asList(GTMachineUtils.workableTiered(tier, GTValues.V[tier],
                            GTValues.V[tier] * 64, GTMFORecipeTypes.MICROWAVE_RECIPES,
                            GTMachineUtils.defaultTankSizeFunction.applyAsInt(tier), true)));
                    return builder
                            .langValue("%s %s %s".formatted(GTValues.VLVH[tier], toEnglishName("microwave"),
                                    GTValues.VLVT[tier]))
                            .editableUI(GTMFOGuiUtils.withLogo(GTCEu.id("microwave"),
                                    GTMFORecipeTypes.MICROWAVE_RECIPES))
                            .rotationState(RotationState.NON_Y_AXIS)
                            .recipeType(GTMFORecipeTypes.MICROWAVE_RECIPES)
                            .workableTieredHullModel(GTCEu.id("block/machines/microwave"))
                            .tooltips(tooltips.toArray(new Component[0]))
                            .register();
                },
                GTMachineUtils.ELECTRIC_TIERS);
    }


    public static void addJEILang(String name, String enLang, String cnLang){
        JEILangPairMap.put("gtceu."+name,Pair.of(enLang,cnLang));
    }

    public static void addTieredLang(String name, String enLang, String cnLang){
        addJEILang(name,enLang,cnLang);
        for (int tier:GTMachineUtils.ELECTRIC_TIERS){
            CNLangMap.put("block."+ GregTechModernFoodOption.MODID+ "." +GTValues.VN[tier].toLowerCase(Locale.ROOT) + "_" + name,"%s%s %s".formatted(VLVH_CN[tier], cnLang, VLVT[tier]));
        }
    }

    /**
     * Per-tier flavor tooltip, ported from the original language files
     * (e.g. {@code gtmfo.machine.slicer.lv.tooltip = Slap-Chop}).
     *
     * @return the translation key, or {@code null} for machines without one
     */
    public static String flavorTooltipKey(String machine, int tier) {
        return switch (machine) {
            case "slicer", "microwave", "multicooker" -> "gtmfo.machine." + machine + ".flavor."
                    + (tier >= GTValues.UV ? 2 : 1);
            case "cuisine_assembler" -> switch (tier) {
                case GTValues.IV -> "gtmfo.machine.cuisine_assembler.flavor.2";
                case GTValues.LuV -> "gtmfo.machine.cuisine_assembler.flavor.3";
                case GTValues.ZPM -> "gtmfo.machine.cuisine_assembler.flavor.4";
                case GTValues.UV -> "gtmfo.machine.cuisine_assembler.flavor.5";
                default -> tier > GTValues.UV ? "gtmfo.machine.cuisine_assembler.flavor.6"
                        : "gtmfo.machine.cuisine_assembler.flavor.1";
            };
            default -> null;
        };
    }

    public static void init(){
        addTieredLang("slicer","Slicer","食材切片机");
        addTieredLang("cuisine_assembler","Cuisine Assembler","菜肴组装机");
        addTieredLang("microwave","Microwave","微波炉");
        addTieredLang("multicooker","Multicooker","多功能烹饪机");
        addTieredLang("mob_age_sorter","Mob Age Sorter","生物年龄分拣机");
        addTieredLang("mob_exterminator","Mob Exterminator","生物灭绝机");
        addTieredLang("mob_extractor","Mob Extractor","生物提取机");
        addTieredLang("farmer","Farmer","农场机");

        JEILangPairMap.put("gtmfo.machine.mob_age_sorter.range", Pair.of("Moves mobs in a %sx%s area",
                "移动 %sx%s 区域内的生物"));
        JEILangPairMap.put("gtmfo.gui.mob_age_sorter_mode", Pair.of("Toggle adult/child filter",
                "切换成年/幼年过滤"));
        JEILangPairMap.put("gtmfo.machine.mob_exterminator.tooltip", Pair.of("Looting level: %s",
                "掠夺等级：%s"));
        JEILangPairMap.put("gtmfo.machine.mob_exterminator.tooltip.consumption", Pair.of("Energy per kill: %s EU",
                "每次击杀耗能：%s EU"));
        JEILangPairMap.put("gtmfo.machine.farmer.tooltip", Pair.of("Harvests and replants a 9x9 area in front",
                "收获并补种前方 9x9 区域"));
        JEILangPairMap.put("gtmfo.jei.food_info", Pair.of("GTFO Food Info", "GTFO 食物信息"));
        JEILangPairMap.put("gtmfo.jei.food_info.hunger", Pair.of("Hunger: %s", "饥饿值：%s"));
        JEILangPairMap.put("gtmfo.jei.food_info.saturation", Pair.of("Saturation: %s", "饱和度：%s"));
        JEILangPairMap.put("gtmfo.jei.food_info.effects", Pair.of("Effects:", "效果："));
        JEILangPairMap.put("gtmfo.jei.lacing", Pair.of("Food Lacing", "食物掺加"));
        JEILangPairMap.put("gtmfo.jei.lacing.effect", Pair.of("Applies: %s", "施加：%s"));
        JEILangPairMap.put("gtmfo.machine.farmer.tooltip.speed", Pair.of("Action every %s ticks",
                "每 %s tick 执行一次动作"));

        JEILangPairMap.put("gtmfo.machine.slicer.flavor.1", Pair.of("Slap-Chop", "拍拍刀"));
        JEILangPairMap.put("gtmfo.machine.slicer.flavor.2", Pair.of("Slaps those nuts into pieces",
                "把这些家伙打碎！"));
        JEILangPairMap.put("gtmfo.machine.microwave.flavor.1", Pair.of("Turns Ingots into Energy instantly",
                "可以把锭瞬间转化为能量！"));
        JEILangPairMap.put("gtmfo.machine.microwave.flavor.2", Pair.of("Insert Ingot for free Doge Coin",
                "放入锭以获取免费狗狗币！"));
        JEILangPairMap.put("gtmfo.machine.multicooker.flavor.1",
                Pair.of("Now you're cooking with (other Things)", "你终于开始用[别的东西]烹饪了"));
        JEILangPairMap.put("gtmfo.machine.multicooker.flavor.2", Pair.of("Cooking Pot 9001", "烹饪锅 9001"));
        JEILangPairMap.put("gtmfo.machine.cuisine_assembler.flavor.1",
                Pair.of("Industrial Sandwich Maker", "工业三明治制造者"));
        JEILangPairMap.put("gtmfo.machine.cuisine_assembler.flavor.2", Pair.of("Chef-Bot 3000", "厨师机器人 3000"));
        JEILangPairMap.put("gtmfo.machine.cuisine_assembler.flavor.3", Pair.of("Chef-Bot 5000", "厨师机器人 5000"));
        JEILangPairMap.put("gtmfo.machine.cuisine_assembler.flavor.4", Pair.of("Chef-Bot 7000", "厨师机器人 7000"));
        JEILangPairMap.put("gtmfo.machine.cuisine_assembler.flavor.5", Pair.of("Chef-Bot 9001", "厨师机器人 9001"));
        JEILangPairMap.put("gtmfo.machine.cuisine_assembler.flavor.6", Pair.of("Aggressively Makes Pizza",
                "激进地制造披萨（小心意大利人）"));
    }

    public static void initENLang(RegistrateLangProvider provider){
        JEILangPairMap.forEach((key, value)->provider.add(key,value.getFirst()));
    }

    public static void initCNLang(CNLangProvider provider){
        JEILangPairMap.forEach((key, value)->provider.add(key,value.getSecond()));
        CNLangMap.forEach(provider::add);
    }
}
