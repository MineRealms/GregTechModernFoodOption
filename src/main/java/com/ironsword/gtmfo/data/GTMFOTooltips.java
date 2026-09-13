package com.ironsword.gtmfo.data;

import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.providers.RegistrateLangProvider;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Legacy item tooltips (original {@code metaitem.*.tooltip} entries), kept in a language
 * provider so datagen keeps them; read at runtime by the {@code ItemTooltipEvent} handler.
 * <p>
 * NOTE: deliberately self-contained (no reference to machine classes) because this class is
 * loaded during mod construction, before GTCEu registries are ready.
 */
public class GTMFOTooltips {

    public static final Map<String, Pair<String, String>> LANG = new LinkedHashMap<>();

    private static void add(String key, String en, String cn) {
        LANG.put(key, Pair.of(en, cn));
    }

    public static void init() {
        add("item.gtmfo.apricot.tooltip", "The only Thing better than This is a damascus steel Apricot", "没有比这更好的了，除非是大马士革钢杏子");
        add("item.gtmfo.bacon.tooltip", "Has been Roasted", "烤过了");
        add("item.gtmfo.banana.tooltip", "From Antananarivo", "产自塔那那利佛");
        add("item.gtmfo.beans_on_toast.tooltip", "\"It's like Polenta, but worse\"", "“就像意式玉米糊，但是更糟。”");
        add("item.gtmfo.berry_medley.tooltip", "Just how Remy would have made it", "就像《料理鼠王》里小老鼠雷米做的那样");
        add("item.gtmfo.blackberry.tooltip", "Would you eat a phone?", "你打算吃手机么？");
        add("item.gtmfo.blueberry.tooltip", "You see a Caracal's Face on one of them", "你在其中一颗上看见了狞猫的脸");
        add("item.gtmfo.burger_cheese.tooltip", "Technically a Hamrger", "严格来说这只能叫汉土（Hamrger）");
        add("item.gtmfo.burger_chum.tooltip", "Chyummy", "海霸好吃！");
        add("item.gtmfo.burger_steak.tooltip", "For normal people", "大家都爱吃");
        add("item.gtmfo.burger_veggie.tooltip", "I'm still going to call this a burger.", "我还是要把它叫做汉堡");
        add("item.gtmfo.cake_bottom.tooltip", "Also known as an a-", "也被叫做p……");
        add("item.gtmfo.cake_bottom_baked.tooltip", "I didn't promise you that this would already be the cake.", "我可没跟你说过它已经可以吃了哈。");
        add("item.gtmfo.caplet_chorus.tooltip", "Did the color just change?", "刚刚颜色变了吗？");
        add("item.gtmfo.caplet_gel.tooltip", "For performing Placebo-controlled Tests on Cyanide Poisoning", "用于进行氰化物中毒的安慰剂对照试验");
        add("item.gtmfo.caplet_plutonium_241.tooltip", "Do Not Eat (pretty please)", "请勿食用（拜托）");
        add("item.gtmfo.caplet_vibrant.tooltip", "It just doesn't have that same polymoscovium pentahalide flavor", "它只是没有相同的五卤化物味道");
        add("item.gtmfo.cheddar_block.tooltip", "Alien Attack! throw the CHEESE", "外星人入侵！快丢——奶酪！");
        add("item.gtmfo.cheddar_slice.tooltip", "Does not have holes. Please. Stop getting this wrong.", "里边没有洞。求求了，别再以讹传讹了。");
        add("item.gtmfo.chips_bag.tooltip", "With a lot O' carbohydrates", "碳水超标");
        add("item.gtmfo.chips_kettle.tooltip", "Complete with metal flakes", "带点金属屑才有灵魂");
        add("item.gtmfo.chips_naquadah.tooltip", "Your scientists were so preoccupied with whether or not they could, they didn't stop to think if they should.", "你们的科学家们如此全神贯注于他们是否可以，他们没有停下来思考他们是否应该这样做。");
        add("item.gtmfo.chips_reduced_fat.tooltip", "My grandfather's favorite", "我爷爷的最爱");
        add("item.gtmfo.chips_syals.tooltip", "Full of delicious Air", "一大袋喷喷香的空气");
        add("item.gtmfo.chum.tooltip", "It's not Great...", "这不太好……");
        add("item.gtmfo.chum_bucket.tooltip", "This isn't Chum Bucket...", "这不是海霸糊桶……");
        add("item.gtmfo.chum_stick.tooltip", "Don't forget to try our Chum-balaya", "别忘了试试我们的新品：海霸什锦！");
        add("item.gtmfo.coconut.tooltip", "A cold-blooded Killer", "一位冷血杀手");
        add("item.gtmfo.cranberry.tooltip", "Tastes of holiday Cheer", "具有节日气息的味道");
        add("item.gtmfo.cucumber.tooltip", "Not a Pickle.", "不是腌黄瓜。");
        add("item.gtmfo.elderberry.tooltip", "For Confucians", "儒家专属的长者莓（Elderberry）");
        add("item.gtmfo.emergency_rations.tooltip", "The seal is way too hard to open", "封条有点难拆");
        add("item.gtmfo.etirps_cranberry.tooltip", "Wanna'n Etirps Cranberry?", "来一杯越莓味碧雪吗？");
        add("item.gtmfo.flat_dough.tooltip", "Basic Pizza (Voltage IN: 32 (LV))", "基础披萨-输入电压: 32 EU/t（LV）");
        add("item.gtmfo.french_fries.tooltip", "Now included with Frenchmen!", "炸法国人");
        add("item.gtmfo.full_breakfast.tooltip", "Actually just a half Breakfast", "其实只能算半份早餐");
        add("item.gtmfo.gorgonzola_triangular_slice.tooltip", "Taste the new fungi overlords.", "快来尝尝我们的新一代真菌霸主。");
        add("item.gtmfo.grapes.tooltip", "They did surgery on Grapes", "他们给葡萄做了个手术");
        add("item.gtmfo.hot_beetroot_soup.tooltip", "Soup time", "喝汤时间到！");
        add("item.gtmfo.hot_mushroom_stew.tooltip", "Yaaaa, that's hot", "哎呀，好烫！");
        add("item.gtmfo.hot_rabbit_stew.tooltip", "You just ate a Rabbit, feel proud of your moral Achievement", "你刚刚吃了一只兔子，为你的道德成就感到骄傲吧");
        add("item.gtmfo.ice_cream.tooltip", "Tastes like Ice, I guess", "尝起来大概像是冰");
        add("item.gtmfo.ice_cream_bear.tooltip", "Jr's Prize for getting the Baking Oven for once", "作为Jr造出烤炉的奖励");
        add("item.gtmfo.ice_cream_chip.tooltip", "https://tenor.com/view/cat-swim-in-milk-cat-kitty-milk-milk-cat-gif-23473453", "https://tenor.com/view/cat-swim-in-milk-cat-kitty-milk-milk-cat-gif-23473453");
        add("item.gtmfo.ice_cream_chocolate.tooltip", "Your standard Chocolate Gelato", "你的标准巧克力意式冰淇淋");
        add("item.gtmfo.ice_cream_melon.tooltip", "Does not use Human Blood, I swear", "敢承诺不使用人血制作");
        add("item.gtmfo.ice_cream_rainbow.tooltip", "Taste the full Rainbow", "遇上全虹，吃定全虹");
        add("item.gtmfo.ice_cream_vanilla.tooltip", "Version 1.12.2", "1.12.2版");
        add("item.gtmfo.iv_bag.tooltip", "A direct upgrade to the EV Bag", "EV袋的升级版");
        add("item.gtmfo.juice_apple.tooltip", "cause Minecraft apples are yellow", "一定是因为Minecraft苹果是黄色的");
        add("item.gtmfo.kebab_barg.tooltip", "Barg bargi Bargiun Bargi", "Barg bargi Bargiun Bargi");
        add("item.gtmfo.kebab_carrot.tooltip", "BigChungus", "兔霸哥");
        add("item.gtmfo.kebab_fat.tooltip", "Donbe's Actually Healthy!", "Donbe 其实很健康！");
        add("item.gtmfo.kebab_kubideh.tooltip", "Goes with Ayran!", "配着艾兰酸奶吃！");
        add("item.gtmfo.kebab_meat.tooltip", "Also known as Chenge", "也被称为Chenge");
        add("item.gtmfo.kebab_soltani.tooltip", "EV Kebab (Voltage In: Delicious!)", "EV烤肉串-输入电压：美味！");
        add("item.gtmfo.kubide_meat.tooltip", "needs some carbon!", "来点碳烤一烤！");
        add("item.gtmfo.lasagna_chum.tooltip", "\"chumagna\" - serenibyss, the wise", "“chumagna” —— 智者 serenibyss");
        add("item.gtmfo.lasagna_pesto.tooltip", "Italians only capitalize the first letter of the first word/proper nouns of titles", "注：意大利人只将标题或专有名词的第一个字母大写");
        add("item.gtmfo.lemon.tooltip", "Looks like nuclear Sludge", "看起来像核废料");
        add("item.gtmfo.leninade.tooltip", "Let the Communism flow through you!", "让共产主义在你的血液中流淌！");
        add("item.gtmfo.lingonberry.tooltip", "Sponsored by IKEA", "由宜家赞助");
        add("item.gtmfo.mango.tooltip", "Persongo", "男果（Mango）不是人果（Persongo）");
        add("item.gtmfo.mince_meat.tooltip", "Time to yeet it in the oven!", "是时候把它丢进烤炉了！");
        add("item.gtmfo.mineral_water.tooltip", "Checkmate, atheists.", "认输吧，无神论者。");
        add("item.gtmfo.mozzarella_ball.tooltip", "Regian can't keep holding it in much longer!", "Regian再也没法私藏它了！");
        add("item.gtmfo.mozzarella_slice.tooltip", "For putting on pizzas, not eating!", "不是直接吃的！是做披萨用的！");
        add("item.gtmfo.mushy_peas.tooltip", "I'm \"peased\" to meet You -> They don't find enough Evidence", "你是个什么“豌噎” -> 他们没有足够的证据");
        add("item.gtmfo.nilk.tooltip", "Nilk", "釢");
        add("item.gtmfo.onion.tooltip", "Nail Your Cries!", "钉住你的哭泣！");
        add("item.gtmfo.orange.tooltip", "WaterPortuguese", "喝橙汁的葡萄牙人.mp4");
        add("item.gtmfo.parmigiano_aged_roll.tooltip", "Stop! You cannot call it that unless it was made in Italy!", "停！只有在意大利生产的才能这么叫！");
        add("item.gtmfo.pasta_all_amogus.tooltip", "Is it the Impostor?", "是冒充者（Impostor）吗？");
        add("item.gtmfo.pasta_alla_norma.tooltip", "This is a virtual Norma!", "这不是真的诺玛！");
        add("item.gtmfo.pelmeni.tooltip", "Better than PelmeniCraft", "比 PelmeniCraft 好");
        add("item.gtmfo.pelmeni_seasoned.tooltip", "With a side of Sour Cream", "有一面蘸了酸奶油");
        add("item.gtmfo.pie_crust.tooltip", "For making Pies", "用来做派");
        add("item.gtmfo.pizza_cheese_raw.tooltip", "Time to yeet it in the oven!", "是时候把它丢进烤炉了！");
        add("item.gtmfo.pizza_cheese_slice.tooltip", "Daring today, aren't we?", "我们今天可真大胆，是吧？");
        add("item.gtmfo.pizza_veggie_raw.tooltip", "Time to yeet it in the oven!", "是时候把它丢进烤炉了！");
        add("item.gtmfo.pizza_veggie_slice.tooltip", "Wait, *what* does the GT5U veggie pizza have on it???", "等会，GT5U 的蔬菜披萨用的是*啥*料来着？？？");
        add("item.gtmfo.popcorn_bag.tooltip", "Tastes like delicious, butter-flavored chemicals.", "尝起来像黄油味的可口化学物质。");
        add("item.gtmfo.potato_slice_oily.tooltip", "Still tastes good, though", "其实尝起来还行");
        add("item.gtmfo.raspberry.tooltip", "Otherwise known as a Voiceless Linguolabial Trill", "也被称为无声的舌唇颤音");
        add("item.gtmfo.rice.tooltip", "Also works as a Seed", "可作为种子使用");
        add("item.gtmfo.sandwich_bacon_large.tooltip", "For Women! (and feminine Men)", "女士（和女性化的男人）们专享！");
        add("item.gtmfo.sandwich_cheese.tooltip", "Who cut the cheese?", "谁在切奶酪？/谁放屁了？");
        add("item.gtmfo.sandwich_cheese_large.tooltip", "Set your mind at cheese.", "把心思放在芝士上。");
        add("item.gtmfo.sandwich_steak.tooltip", "Steamed Meat", "在我们这叫蒸肉（心虚）");
        add("item.gtmfo.sandwich_steak_large.tooltip", "At least you didn't call it Steamed Hams", "至少你不会把它叫做蒸汉");
        add("item.gtmfo.sandwich_veggie.tooltip", "Also Boneless", "注：也没有骨头");
        add("item.gtmfo.sandwich_veggie_large.tooltip", "Just not worth it.", "不是很值当。");
        add("item.gtmfo.sandwich_vibrant.tooltip", "I guess it works decently as jelly.", "我想它像果冻一样有效。");
        add("item.gtmfo.scrap_meat.tooltip", "The Parts that People don't usually eat, although I won't judge", "人们通常不会吃的那部分肉。不过你想的话我也不会拦着。");
        add("item.gtmfo.seed_bean.tooltip", "Also works as a Seed", "可作为种子使用");
        add("item.gtmfo.seed_onion.tooltip", "For growing YoungOnions", "用于种植青年洋葱/为了成长中的YoungOnion们");
        add("item.gtmfo.seed_pea.tooltip", "Also works as a Seed", "可作为种子使用");
        add("item.gtmfo.seed_unknown.tooltip", "Found by breaking §2Grass/n§6Craft§r to get any Seed your Heart desires/n§cCrafted Seeds cannot be uprooted after planting (because Logic)", "破坏§2草§7时有概率获得/n§6合成§r以获得你心中所想要的任何种子/n§c合成所得的种子在种下后不能被连根拔起（因为逻辑）");
        add("item.gtmfo.shepherds_pie.tooltip", "Given what you put in It, probably an Industrialist's Pie", "鉴于你往里边放的东西，叫工业家派可能更合适");
        add("item.gtmfo.skewer.tooltip", "Yes, you would most diffidently Eat it!", "是的，你会很不情愿地吃下它！");
        add("item.gtmfo.sprinkler_cover.tooltip", "As a §4downwards-facing§7 Cover, Sprays Fluids onto a 9x9 Area of Crops/nLeave a §63-Block Space§7 between This and the Ground/nLeft click with a Soft Mallet to turn off Particles", "用作§4朝下§7的覆盖板时，向 9x9 范围内的作物喷洒液体/n在覆盖板与地面间预留§6 3 格空间§7/n使用软锤左键以关闭粒子效果");
        add("item.gtmfo.strawberry.tooltip", "Made of Straw", "用草做的");
        add("item.gtmfo.sugary_dough.tooltip", "Highly effective at provoking Heart Attacks", "对引发心脏病非常有效");
        add("item.gtmfo.tomato.tooltip", "No Not the Mr Tomato!!", "不！番茄先生！");
        add("item.gtmfo.vodka.tooltip", "Distilled Russia", "蒸馏浓缩的俄罗斯");
        add("item.gtmfo.white_grapes.tooltip", "They did surgery on white Grapes (shocking)", "震惊！他们在白葡萄上做了个手术！");
    }

    public static void initENLang(RegistrateLangProvider provider) {
        LANG.forEach((key, pair) -> provider.add(key, pair.getFirst()));
    }

    public static void initCNLang(CNLangProvider provider) {
        LANG.forEach((key, pair) -> provider.add(key, pair.getSecond()));
    }
}
