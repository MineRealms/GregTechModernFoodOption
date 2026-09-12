package com.ironsword.gtmfo.common.data.material;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.ironsword.gtmfo.GTMFOUtils;
import com.ironsword.gtmfo.data.CNLangProvider;
import com.mojang.datafixers.util.Pair;
import com.tterrag.registrate.providers.RegistrateLangProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GTMFOMaterials {

    public static final Map<String, Pair<String,String>> LANG_MAP = new HashMap<>();

    public static Material Zest = simpleDust("zest",0xd8ff4a,"Zest", "碎橙皮");
    public static Material SodiumCyanide = dust("sodium_cyanide", 0x7575ae,"Sodium Cyanide","氰化钠");
    public static Material ShreddedParmesan = simpleDust("shredded_parmesan", 0xdbd6b4, "Shredded Parmesan", "帕马森奶酪碎");
    // 奶酪链中间品（原版为 GT 矿辞物品，现代转为粉末材料）
    public static Material CoagulatedMilkCurd = simpleDust("coagulated_milk_curd", 0xede3cc, "Coagulated Milk Curd", "凝固奶凝乳");
    public static Material CutCurd = simpleDust("cut_curd", 0xede3cc, "Cut Curd", "切碎的凝乳");
    public static Material CookedCurd = simpleDust("cooked_curd", 0xffe8b3, "Cooked Curd", "煮熟的凝乳");
    public static Material SaltedCurd = simpleDust("salted_curd", 0xf7d68b, "Salted Curd", "加盐凝乳");
    public static Material LargeMozzarellaCurd = simpleDust("large_mozzarella_curd", 0xf5f5f5, "Large Mozzarella Curd", "大马苏里拉凝乳块");
    public static Material SmallMozzarellaCurd = simpleDust("small_mozzarella_curd", 0xf5f5f5, "Small Mozzarella Curd", "小马苏里拉凝乳块");
    public static Material DriedMozzarellaCurd = simpleDust("dried_mozzarella_curd", 0xf5f4e4, "Dried Mozzarella Curd", "干燥马苏里拉凝乳块");
    public static Material SolidifiedMozzarellaCurd = simpleDust("solidified_mozzarella_curd", 0xedebca, "Solidified Mozzarella Curd", "固化马苏里拉凝乳块");
    public static Material GorgonzolaCurd = simpleDust("gorgonzola_curd", 0xe5e5f5, "Gorgonzola Curd", "戈贡佐拉凝乳");
    public static Material PenicilliumRoqueforti = simpleDust("penicillium_roqueforti", 0x2a7b5a, "Penicillium Roqueforti", "罗克福尔青霉菌");
    public static Material CrushedPoppy = simpleDust("crushed_poppy", 0x940801, "Crushed Poppy", "碾碎罂粟");
    // 骨瓷/陶瓷链（原版为 GT 矿辞物品）
    public static Material BoneChinaClay = simpleDust("bone_china_clay", 0xEEC1C1, "Bone China Clay", "骨瓷土");
    public static Material UnfiredPorcelainTile = simpleDust("unfired_porcelain_tile", 0xC2C2C4, "Unfired Porcelain Tile", "未烧制瓷砖");
    public static Material BiscuitPorcelainTile = simpleDust("biscuit_porcelain_tile", 0xFEFEE8, "Biscuit Porcelain Tile", "素烧瓷砖");
    public static Material GlazedPorcelainTile = simpleDust("glazed_porcelain_tile", 0xEEEEEE, "Glazed Porcelain Tile", "釉面瓷砖");
    public static Material BlackGlazedPorcelainTile = simpleDust("black_glazed_porcelain_tile", 0x111111, "Black Glazed Porcelain Tile", "黑色釉面瓷砖");
    public static Material LaminatedDough = simpleDust("laminated_dough", 0xc6b4bb, "Laminated Dough", "起酥面团");
    public static Material SodiumSulfate = chemicalDust("sodium_sulfate", 0xf0f0f0, MaterialIconSet.DULL,"Sodium Sulfate","硫酸钠",
            GTMaterials.Sodium, 2, GTMaterials.Sulfur, 1, GTMaterials.Oxygen, 4);
    public static Material Paracetamol = chemicalDust("paracetamol", 0x0045A0, MaterialIconSet.SHINY,"Paracetamol","对乙酰氨基酚",
            GTMaterials.Carbon, 8, GTMaterials.Hydrogen, 9, GTMaterials.Nitrogen, 1, GTMaterials.Oxygen, 2);
    public static Material Promethazine = chemicalDust("promethazine", 0xf8fade, MaterialIconSet.DULL,"Promethazine","异丙嗪",
            GTMaterials.Carbon, 17, GTMaterials.Hydrogen, 20, GTMaterials.Nitrogen, 2, GTMaterials.Sulfur, 1);
    public static Material Codeine = chemicalDust("codeine", 0xfadef2, MaterialIconSet.DULL,"Codeine","可待因",
            GTMaterials.Carbon, 18, GTMaterials.Hydrogen, 21, GTMaterials.Nitrogen, 1, GTMaterials.Oxygen, 3);
    public static Material Phenothiazine = chemicalDust("phenothiazine", 0x67735c, MaterialIconSet.DULL,"Phenothiazine","吩噻嗪",
            GTMaterials.Carbon, 12, GTMaterials.Hydrogen, 9, GTMaterials.Nitrogen, 1, GTMaterials.Sulfur, 1);
    public static Material Diphenylamine = chemicalDust("diphenylamine", 0xe3932b, MaterialIconSet.DULL,"Diphenylamine","二苯胺",
            GTMaterials.Carbon, 12, GTMaterials.Hydrogen, 11, GTMaterials.Nitrogen, 1);
    public static Material AmmoniumPerchlorate = chemicalDust("ammonium_perchlorate", 0xd9d9d9, MaterialIconSet.DULL,"Ammonium Perchlorate","高氯酸铵",
            GTMaterials.Nitrogen, 1, GTMaterials.Hydrogen, 4, GTMaterials.Chlorine, 1, GTMaterials.Oxygen, 4);
    public static Material PotassiumPerchlorate = chemicalDust("potassium_perchlorate", 0xdcdcdc, MaterialIconSet.DULL,"Potassium Perchlorate","高氯酸钾",
            GTMaterials.Potassium, 1, GTMaterials.Chlorine, 1, GTMaterials.Oxygen, 4);
    public static Material SodiumPerchlorate = chemicalDust("sodium_perchlorate", 0xe6e6e6, MaterialIconSet.DULL,"Sodium Perchlorate","高氯酸钠",
            GTMaterials.Sodium, 1, GTMaterials.Chlorine, 1, GTMaterials.Oxygen, 4);
    public static Material SodiumChlorate = chemicalDust("sodium_chlorate", 0xdcdcdc, MaterialIconSet.DULL,"Sodium Chlorate","氯酸钠",
            GTMaterials.Sodium, 1, GTMaterials.Chlorine, 1, GTMaterials.Oxygen, 3);
    public static Material VanillylmandelicAcid = chemicalDust("vanillylmandelic_acid", 0xf2efbd, MaterialIconSet.DULL,"Vanillylmandelic Acid","香草扁桃酸",
            GTMaterials.Carbon, 9, GTMaterials.Hydrogen, 10, GTMaterials.Oxygen, 5);
    public static Material VanilglycolicAcid = chemicalDust("vanilglycolic_acid", 0xebe7a4, MaterialIconSet.DULL,"Vanilglycolic Acid","香草乙醇酸",
            GTMaterials.Carbon, 9, GTMaterials.Hydrogen, 8, GTMaterials.Oxygen, 5);
    public static Material Vanillin = chemicalDust("vanillin", 0xfbfbfb, MaterialIconSet.SHINY,"Vanillin","香草醛",
            GTMaterials.Carbon, 8, GTMaterials.Hydrogen, 8, GTMaterials.Oxygen, 3);
    public static Material CupricHydrogenArsenite = chemicalDust("cupric_hydrogen_arsenite", 0x0fff00, MaterialIconSet.SHINY,"Cupric Hydrogen Arsenite","亚砷酸氢铜",
            GTMaterials.Copper, 1, GTMaterials.Hydrogen, 1, GTMaterials.Arsenic, 1, GTMaterials.Oxygen, 3);
    public static Material Aminophenol = chemicalDust("aminophenol", 0xffffff, MaterialIconSet.SHINY,"Aminophenol","氨基苯酚",
            GTMaterials.Carbon, 6, GTMaterials.Hydrogen, 7, GTMaterials.Nitrogen, 1, GTMaterials.Oxygen, 1);
    public static Material IVNitrophenol = chemicalDust("iv_nitrophenol", 0xffffe0, MaterialIconSet.SHINY,"4-Nitrophenol","4-硝基苯酚",
            GTMaterials.Carbon, 6, GTMaterials.Hydrogen, 5, GTMaterials.Nitrogen, 1, GTMaterials.Oxygen, 3);
    public static Material IINitrophenol = chemicalDust("ii_nitrophenol", 0xffff00, MaterialIconSet.SHINY,"2-Nitrophenol","2-硝基苯酚",
            GTMaterials.Carbon, 6, GTMaterials.Hydrogen, 5, GTMaterials.Nitrogen, 1, GTMaterials.Oxygen, 3);
    public static Material LithiumOxide = chemicalDust("lithium_oxide", 0x808080, MaterialIconSet.DULL,"Lithium Oxide","氧化锂",
            GTMaterials.Lithium, 2, GTMaterials.Oxygen, 1);
    public static Material LithiumCarbonate = chemicalDust("lithium_carbonate", 0x999999, MaterialIconSet.DULL,"Lithium Carbonate","碳酸锂",
            GTMaterials.Lithium, 2, GTMaterials.Carbon, 1, GTMaterials.Oxygen, 3);
    public static Material BoneAsh = simpleDust("bone_ash", 0xFEFDFE, "Bone Ash", "骨灰");

    private static TagPrefix[] without(TagPrefix tagPrefix){
        return TagPrefix.values().stream().filter(prefix->!prefix.equals(tagPrefix)).toArray(TagPrefix[]::new);
    }

    private static TagPrefix[] exclude(TagPrefix... tagPrefixes){
        var prefixes = List.of(tagPrefixes);
        return TagPrefix.values().stream().filter(prefix->!prefixes.contains(prefix)).toArray(TagPrefix[]::new);
    }

    static void addLang(String id,String enLang,String cnLang){
        LANG_MAP.put("material.gtceu."+id,Pair.of(enLang,cnLang));
    }

    private static Material simpleDust(String id,int color,String enLang,String cnLang){
        addLang(id,enLang,cnLang);
        return new Material.Builder(GTCEu.id(id)).color(color).dust().iconSet(MaterialIconSet.DULL).flags(MaterialFlags.DISABLE_MATERIAL_RECIPES).ignoredTagPrefixes(TagPrefix.dustSmall,TagPrefix.dustTiny).buildAndRegister();
    }

    private static Material dust(String id,int color,String enLang,String cnLang){
        addLang(id,enLang,cnLang);
        return new Material.Builder(GTCEu.id(id)).color(color).dust().iconSet(MaterialIconSet.DULL).buildAndRegister();
    }

    private static Material chemicalDust(String id, int color, MaterialIconSet iconSet, String enLang, String cnLang, Object... components){
        addLang(id,enLang,cnLang);
        return new Material.Builder(GTCEu.id(id)).color(color).dust().iconSet(iconSet)
                .components(components)
                .buildAndRegister();
    }

    public static void init(){
        GTMaterials.BismuthBronze.addFlags(MaterialFlags.GENERATE_FRAME);
        GTMaterials.DistilledWater.setProperty(CleanerProperty.CLEANER, new CleanerProperty(2));
        GTMFOFluids.SodiumStearate.setProperty(CleanerProperty.CLEANER, new CleanerProperty(16));
    }

    public static void initENLang(RegistrateLangProvider provider){
        LANG_MAP.forEach((key,value)->provider.add(key,value.getFirst()));
    }

    public static void initCNLang(CNLangProvider provider){
        LANG_MAP.forEach((key,value)->provider.add(key,value.getSecond()));
    }
}
