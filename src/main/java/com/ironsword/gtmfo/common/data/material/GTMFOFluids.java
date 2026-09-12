package com.ironsword.gtmfo.common.data.material;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.fluids.FluidBuilder;
import com.gregtechceu.gtceu.api.fluids.attribute.FluidAttributes;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.DECOMPOSITION_BY_CENTRIFUGING;
import static com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags.DISABLE_DECOMPOSITION;

public class GTMFOFluids {

    // === 水果提取液/果汁 ===
    public static final Material LemonExtract   = fluid("lemon_extract" ,0xfce80a,"Lemon Extract","柠檬提取液");
    public static final Material LIME_EXTRACT   = fluid("lime_extract"  ,0x85f218,"Lime Extract" ,"酸橙提取液");
    public static final Material ORANGE_EXTRACT = fluid("orange_extract",0xff6100,"Orange Juice" ,"橙汁"      );
    public static final Material AppleExtract   = fluid("apple_extract" ,0xe9ba58,"Apple Juice"  ,"苹果汁"    );
    public static final Material MELON_EXTRACT  = fluid("melon_extract" ,0xfc7996,"Melon Extract","西瓜提取液");
    public static final Material GRAPE_EXTRACT  = fluid("grape_extract" ,0xa83351,"Grape Extract","葡萄汁"    );
    public static final Material ApricotExtract = fluid("apricot_extract",0xe3de9d,"Apricot Extract","杏子提取液");
    public static final Material CranberryExtract = fluid("cranberry_extract",0x8C0D22,"Cranberry Extract","蔓越莓提取液");
    public static final Material AppleCider     = fluid("apple_cider"   ,0xcf9d47,"Apple Cider"  ,"苹果酒"    );

    // === 糖浆/甜味 ===
    public static final Material CaneSyrup         = fluid("cane_syrup"         ,0xf2f1dc,"Cane Syrup"         ,"甘蔗糖浆"      );
    public static final Material CaneSyrupUnheated = fluid("cane_syrup_unheated",0xf0efe4,"Unheated Cane Syrup","未加热甘蔗糖浆");
    public static final Material HighFructoseCornSyrupSolution = fluid("hfcs_solution",0xe3bc20,"High Fructose Corn Syrup Solution","高果糖玉米糖浆溶液");
    public static final Material AppleSyrup        = fluid("apple_syrup"       ,0xf2e1ac,"Apple Syrup"        ,"苹果糖浆"      );
    public static final Material AppleCandySyrup   = fluid("apple_candy_syrup" ,0xe7f5ae,"Apple Candy Syrup"  ,"苹果糖浆"      );
    public static final Material SweetenedDilutedCaneSyrupMixture = fluid("sweetened_diluted_cane_syrup_mixture",0xdedcc8,"Sweetened Diluted Cane Syrup Mixture","稀释加糖甘蔗糖浆混合物");
    public static final Material MarshmallowSyrupMixture = fluid("marshmallow_syrup_mixture",0xe6e0dc,"Marshmallow Syrup Mixture","棉花软糖糖浆混合物");
    public static final Material MarshmallowFoam   = fluid("marshmallow_foam"   ,0xe6e0dc,"Marshmallow Foam"   ,"发泡棉花软糖糖浆");

    // === 油脂 ===
    public static final Material FryingOil         = fluid("frying_oil"    ,0xffe3a1,"Frying Oil"    ,"煎炸油"  );
    public static final Material FryingOilHot      = fluid("frying_oil_hot",0xffd166,483,"Hot Frying Oil","热煎炸油");
    public static final Material OliveOil          = fluid("olive_oil"     ,0xd1db5a,"Olive Oil"     ,"橄榄油"  );
    public static final Material SoyLecithin       = fluid("soy_lecithin"  ,0xa6963a,"Soy Lecithin"  ,"大豆卵磷脂");
    public static final Material RawSoybeanOil     = fluid("raw_soybean_oil"   ,0xad5418,"Raw Soybean Oil"   ,"生大豆油"  );
    public static final Material HydratedSoybeanOil= fluid("hydrated_soybean_oil",0xc99c7d,"Hydrated Soybean Oil","水合大豆油");
    public static final Material SoybeanOil        = fluid("soybean_oil"   ,0xe8e4a9,"Soybean Oil"   ,"大豆油"  );
    public static final Material Stearin           = builder("stearin",0xffcc66,"Stearin","硬脂")
            .liquid()
            .components(GTMaterials.Carbon, 57, GTMaterials.Hydrogen, 110, GTMaterials.Oxygen, 6)
            .flags(DISABLE_DECOMPOSITION)
            .buildAndRegister();
    public static final Material SodiumStearate    = builder("sodium_stearate",0xffffff,"Sodium Stearate","硬脂酸钠")
            .liquid()
            .components(GTMaterials.Carbon, 18, GTMaterials.Hydrogen, 35, GTMaterials.Oxygen, 2, GTMaterials.Sodium, 1)
            .flags(DISABLE_DECOMPOSITION)
            .formula("C17H35COONa", true)
            .buildAndRegister();

    // === 乳制品 ===
    public static final Material HotMilk           = fluid("hot_milk",0xfffbf0,"Hot Milk","热牛奶");
    public static final Material ItalianBuffaloMilk= fluid("italian_buffalo_milk",0xfcfbf5,"Italian Buffalo Milk","意大利水牛奶");
    public static final Material CrudeRennetSolution = fluid("crude_rennet_solution",0xb0631a,"Crude Rennet Solution","粗制凝乳酶溶液");
    public static final Material Whey              = fluid("whey",0xf5ef9a,"Whey","乳清");
    public static final Material ActivatedBuffaloMilk = fluid("activated_buffalo_milk",0xfff8cc,"Activated Buffalo Milk","活化水牛奶");
    public static final Material WheySaltWaterMix  = fluid("whey_salt_water_mix",0xecfc7e,"Whey Salt Water Mix","乳清盐水混合物");
    public static final Material HeatedRicottaStarter = fluid("heated_ricotta_starter",0xdef72f,348,"Heated Ricotta Starter","加热里科塔发酵剂");
    public static final Material AcidicMilkSolution = fluid("acidic_milk_solution",0xb2c71c,"Acidic Milk Solution","酸化牛奶溶液");
    public static final Material CoagulatingRicottaSolution = fluid("coagulating_ricotta_solution",0xeff5c9,"Coagulating Ricotta Solution","凝固里科塔溶液");
    public static final Material PasteurizedMilk   = fluid("pasteurized_milk",0xfefdf3,"Pasteurized Milk","巴氏杀菌奶");
    public static final Material SkimmedMilk       = fluid("skimmed_milk",0xf7ffe3,"Skimmed Milk","脱脂奶");
    public static final Material UnpasteurizedSkimmedMilk = fluid("unpasteurized_skimmed_milk",0xfcfcf0,"Unpasteurized Skimmed Milk","未杀菌脱脂奶");
    public static final Material MilkColloid       = fluid("milk_colloid",0xe0d7bf,"Milk Colloid","牛奶胶体");
    public static final Material Cream             = fluid("cream",0xced2d9,"Cream","奶油");
    public static final Material SourCream         = fluid("sour_cream",0xe3de9d,"Sour Cream","酸奶油");
    public static final Material LacticAcidBacteria = fluid("lactic_acid_bacteria",0x371040,"Lactic Acid Bacteria","乳酸菌");
    public static final Material Butter            = fluid("butter",0xffef82,"Butter","黄油");
    public static final Material IceCreamMixture   = fluid("ice_cream_mixture",0xdebd80,"Ice Cream Mixture","冰淇淋混合物");
    public static final Material ParmigianoReggianoStarter = fluid("parmigiano_reggiano_starter",0xf0eac0,"Parmigiano Reggiano Starter","帕马森发酵剂");
    public static final Material CurdlingParmigianoReggiano = fluid("curdling_parmigiano_reggiano",0xfff4ab,"Curdling Parmigiano Reggiano","凝固帕马森");
    public static final Material FungalRennetSolution = fluid("fungal_rennet_solution",0x2a7b5a,"Fungal Rennet Solution","真菌凝乳酶溶液");

    // === 蛋类 ===
    public static final Material Egg               = fluid("egg",0xFFFF0F,"Egg","蛋");
    public static final Material Albumen           = fluid("albumen",0xfffef7,"Albumen","蛋清");
    public static final Material Yolk              = fluid("yolk",0xffdf00,"Yolk","蛋黄");

    // === 汤/酱汁 ===
    public static final Material TomatoSauce       = fluid("tomato_sauce",0xfc2217,"Tomato Sauce","番茄酱");
    public static final Material MushroomSoup      = fluid("mushroom_soup",0xedcaaf,343,"Mushroom Soup","蘑菇汤");
    public static final Material BeetrootSoup      = fluid("beetroot_soup",0xc25132,343,"Beetroot Soup","甜菜汤");
    public static final Material RabbitStew        = fluid("rabbit_stew",0xe0c0a0,343,"Rabbit Stew","兔肉煲");
    public static final Material BologneseSauce    = fluid("bolognese_sauce",0x782A14,"Bolognese Sauce","博洛尼亚酱");
    public static final Material TomatoBologneseSauce = fluid("tomato_bolognese_sauce",0xCA190F,"Tomato Bolognese Sauce","番茄博洛尼亚酱");
    public static final Material CarbonaraSauce    = fluid("carbonara_sauce",0xCDAF44,"Carbonara Sauce","培根蛋酱");
    public static final Material Pesto             = fluid("pesto",0x309027,"Pesto","青酱");
    public static final Material BechamelSauce     = fluid("bechamel_sauce",0xD1B7AC,"Bechamel Sauce","白酱");
    public static final Material ChickenBroth      = fluid("chicken_broth",0xA4600D,"Chicken Broth","鸡高汤");
    public static final Material VitelloTonnatoSauce = fluid("vitello_tonnato_sauce",0xC6BABE,"Vitello Tonnato Sauce","意式鱼香小牛肉酱");
    public static final Material VitelloTonnatoFlavorant = fluid("vitello_tonnato_flavorant",0xC2ACB0,"Vitello Tonnato Flavorant","意式鱼香小牛肉风味剂");
    public static final Material Agrodolce         = fluid("agrodolce",0xba1430,"Agrodolce","意式酸甜酱");
    public static final Material Polenta           = fluid("polenta",0xBBA844,"Polenta","玉米糊");
    public static final Material RafanataMixture   = fluid("rafanata_mixture",0xDCB239,"Rafanata Mixture","辣根蛋饼混合物");
    public static final Material PastaEFagioliBase = fluid("pasta_e_fagioli_base",0xD4592F,343,"Pasta e Fagioli Base","意面豆汤底");
    public static final Material MixedPastaEFagioli = fluid("mixed_pasta_e_fagioli",0xE48628,343,"Mixed Pasta e Fagioli","混合意面豆汤");

    // === 酒精/饮品 ===
    public static final Material Vodka             = fluid("vodka",0x7d6933,"Vodka","伏特加");
    public static final Material Leninade          = fluid("leninade",0x82661d,"Leninade","列宁檬汁");
    public static final Material WhiteWine         = fluid("white_wine",0xD7C259,"White Wine","白葡萄酒");
    public static final Material RedWine           = fluid("red_wine",0x641126,"Red Wine","红葡萄酒");
    public static final Material MaceratedWhiteGrapes = fluid("macerated_white_grapes",0x8C9D41,"Macerated White Grapes","浸渍白葡萄");
    public static final Material PressedWhiteWort  = fluid("pressed_white_wort",0xDEF37F,"Pressed White Wort","压榨白麦芽汁");
    public static final Material ClarifiedWhiteWort = fluid("clarified_white_wort",0xD8E4A4,"Clarified White Wort","澄清白麦芽汁");
    public static final Material RedGrapesMust     = fluid("red_grapes_must",0xD32552,"Red Grapes Must","红葡萄汁");
    public static final Material FermentedRedGrapesMust = fluid("fermented_red_grapes_must",0xA83351,"Fermented Red Grapes Must","发酵红葡萄汁");
    public static final Material AlcoholicRedGrapeJuice = fluid("alcoholic_red_grape_juice",0xA4002A,"Alcoholic Red Grape Juice","含酒精红葡萄汁");
    public static final Material WheatyJuice       = fluid("wheaty_juice",0xa87b58,"Wheaty Juice","小麦汁");
    public static final Material PoorQualityBeer   = fluid("poor_quality_beer",0xa87b58,"Poor Quality Beer","劣质啤酒");
    public static final Material BeerBatter        = fluid("beer_batter",0xe4cfc0,"Beer Batter","啤酒面糊");
    public static final Material Etirps            = fluid("etirps",0xb0ff73,"Etirps","碧雪");
    public static final Material CranberryEtirps   = fluid("etirps_cranberry",0x5f202a,"Etirps Cranberry","蔓越莓味碧雪");
    public static final Material CranberrySludge   = fluid("cranberry_sludge",0x571722,"Cranberry Sludge","蔓越莓浆");
    public static final Material CranberrySodaSyrup = fluid("cranberry_soda_syrup",0x5f202a,"Cranberry Soda Syrup","蔓越莓苏打水糖浆");
    public static final Material LemonLimeSodaSyrup = fluid("lemon_lime_soda_syrup",0x76ff0d,"Lemon Lime Soda Syrup","柠檬酸橙苏打糖浆");
    public static final Material LemonLimeSolution = fluid("lemon_lime_solution",0xbddb5a,"Lemon Lime Solution","柠檬酸橙溶液");
    public static final Material LemonLimeSludge   = fluid("lemon_lime_sludge",0x779906,"Lemon Lime Sludge","柠檬酸橙浆");
    public static final Material CarbonatedWater   = fluid("carbonated_water",0xf5ffff,"Carbonated Water","碳酸水");
    public static final Material PurpleDrink       = fluid("purple_drink",0xb405ff,"Purple Drink","紫色饮料");
    public static final Material CoughSyrup        = fluid("cough_syrup",0x5c1b5e,"Cough Syrup","止咳糖浆");
    public static final Material Nilk              = fluid("nilk",0x252626,"Nilk","硅岩风味乳");

    // === 浆果酱 ===
    public static final Material BerryJam          = fluid("berry_jam",0x61262D,"Berry Jam","浆果果酱");
    public static final Material LingonberryJam    = fluid("lingonberry_jam",0x61262D,"Lingonberry Jam","越橘果酱");
    public static final Material ElderberryJam     = fluid("elderberry_jam",0x5F414F,"Elderberry Jam","接骨木莓果酱");

    // === 咖啡/可可 ===
    public static final Material Coffee            = fluid("coffee",0x36312e,368,"Coffee","咖啡");
    public static final Material EnergizedCoffee   = fluid("energized_coffee",0x695934,368,"Energized Coffee","提神咖啡");
    public static final Material MoltenUnsweetenedChocolate = fluid("molten_unsweetened_chocolate",0x7b3f00,370,"Molten Unsweetened Chocolate","熔融无糖巧克力");
    public static final Material CocoaButter       = fluid("cocoa_butter",0xe5dbce,"Cocoa Butter","可可脂");
    public static final Material MoltenDarkChocolate = fluid("molten_dark_chocolate",0x490206,360,"Molten Dark Chocolate","熔融黑巧克力");
    public static final Material MoltenMilkChocolate = fluid("molten_milk_chocolate",0x84563c,350,"Molten Milk Chocolate","熔融牛奶巧克力");

    // === 化学/加工 ===
    public static final Material IsopropylChloride = builder("isopropyl_chloride",0xffffff,"Isopropyl Chloride","异丙基氯")
            .liquid()
            .components(GTMaterials.Carbon, 3, GTMaterials.Hydrogen, 7, GTMaterials.Chlorine, 1)
            .formula("(CH3)2CHCl", true)
            .buildAndRegister();
    public static final Material PerchloricAcid    = builder("perchloric_acid",0xffffff,"Perchloric Acid","高氯酸")
            .liquid(new FluidBuilder().attribute(FluidAttributes.ACID))
            .components(GTMaterials.Hydrogen, 1, GTMaterials.Chlorine, 1, GTMaterials.Oxygen, 4)
            .buildAndRegister();
    public static final Material ChloroauricAcid   = builder("chloroauric_acid",0xffffff,"Chloroauric Acid","氯金酸")
            .liquid(new FluidBuilder().attribute(FluidAttributes.ACID))
            .components(GTMaterials.Hydrogen, 1, GTMaterials.Gold, 1, GTMaterials.Chlorine, 4)
            .buildAndRegister();
    public static final Material MoistAir          = builder("moist_air",0x82c8ff,"Moist Air","湿空气")
            .gas(new FluidBuilder().temperature(273))
            .buildAndRegister();
    public static final Material ColdMoistAir      = builder("cold_moist_air",0x72a2ff,"Cold Moist Air","冷湿空气")
            .gas(new FluidBuilder().temperature(243))
            .buildAndRegister();
    public static final Material Sludge            = fluid("sludge",0x24140b,"Sludge","污泥");
    public static final Material AlkalineExtract   = fluid("alkaline_extract",0x121110,"Alkaline Extract","碱性提取液");
    public static final Material PotatoJuice       = fluid("potato_juice",0x786b48,"Potato Juice","马铃薯汁");
    public static final Material StarchFilledWater = fluid("starch_filled_water",0xd1cbbe,"Starch Filled Water","淀粉水");
    public static final Material CitricAcid        = builder("citric_acid",0xccbd61,"Citric Acid","柠檬酸")
            .liquid(new FluidBuilder().attribute(FluidAttributes.ACID))
            .components(GTMaterials.Carbon, 6, GTMaterials.Hydrogen, 8, GTMaterials.Oxygen, 7)
            .formula("HOC(CH2CO2H)2", true)
            .buildAndRegister();
    public static final Material Guaiacol          = builder("guaiacol",0xa63a00,"Guaiacol","愈创木酚")
            .liquid()
            .components(GTMaterials.Carbon, 7, GTMaterials.Hydrogen, 8, GTMaterials.Oxygen, 2)
            .buildAndRegister();
    public static final Material Acetaldehyde      = builder("acetaldehyde",0xf3f2f1,"Acetaldehyde","乙醛")
            .liquid()
            .components(GTMaterials.Carbon, 2, GTMaterials.Hydrogen, 4, GTMaterials.Oxygen, 1)
            .buildAndRegister();
    public static final Material Glyoxal           = builder("glyoxal",0xc9c7ab,"Glyoxal","乙二醛")
            .liquid()
            .components(GTMaterials.Carbon, 2, GTMaterials.Hydrogen, 2, GTMaterials.Oxygen, 2)
            .buildAndRegister();
    public static final Material GlyoxylicAcid     = builder("glyoxylic_acid",0xd9d5a0,"Glyoxylic Acid","乙醛酸")
            .liquid(new FluidBuilder().attribute(FluidAttributes.ACID))
            .components(GTMaterials.Carbon, 2, GTMaterials.Hydrogen, 2, GTMaterials.Oxygen, 3)
            .buildAndRegister();
    public static final Material SodiumArseniteSolution = builder("sodium_arsenite_solution",0x9fa09f,"Sodium Arsenite Solution","亚砷酸钠溶液")
            .liquid()
            .components(GTMaterials.Sodium, 1, GTMaterials.Arsenic, 1, GTMaterials.Oxygen, 2)
            .flags(DISABLE_DECOMPOSITION)
            .buildAndRegister();
    public static final Material RubberSap         = fluid("rubber_sap",0xf7f6dc,"Rubber Sap","橡胶树液");
    public static final Material RainbowSap        = builder("rainbow_sap",0xffffff,"Rainbow Sap","彩虹树液")
            .liquid(new FluidBuilder().customStill())
            .buildAndRegister();
    public static final Material BlueVitriol       = builder("blue_vitriol",0x4242DE,"Blue Vitriol","蓝矾")
            .liquid()
            .components(GTMaterials.Copper, 1, GTMaterials.Sulfur, 1, GTMaterials.Oxygen, 4)
            .buildAndRegister();
    public static final Material BakingSodaSolution = builder("baking_soda_solution",0xffffff,"Baking Soda Solution","小苏打溶液")
            .liquid()
            .components(GTMaterials.SodiumBicarbonate, 1, GTMaterials.Water, 1)
            .flags(DECOMPOSITION_BY_CENTRIFUGING)
            .buildAndRegister();
    public static final Material Blood             = fluid("blood",0x691a15,310,"Blood","血");
    public static final Material FertilizerSolution = fluid("fertilizer_solution",0x947760,"Fertilizer Solution","肥料溶液");
    public static final Material XPhenothiazineIiPropylChloride = builder("x_phenothiazine_ii_propyl_chloride",0xffffff,"X-Phenothiazine II Propyl Chloride","异丙嗪中间体")
            .liquid()
            .components(GTMaterials.Carbon, 15, GTMaterials.Hydrogen, 14, GTMaterials.Nitrogen, 1, GTMaterials.Sulfur, 1, GTMaterials.Chlorine, 1)
            .flags(DISABLE_DECOMPOSITION)
            .buildAndRegister();
    public static final Material Aniline           = builder("aniline",0x4c911d,"Aniline","苯胺")
            .liquid()
            .components(GTMaterials.Carbon, 6, GTMaterials.Hydrogen, 7, GTMaterials.Nitrogen, 1)
            .flags(DISABLE_DECOMPOSITION)
            .formula("C6H5NH2", true)
            .buildAndRegister();
    public static final Material HeatedWater       = fluid("heated_water",0x024B86,343,"Heated Water","加热水");
    public static final Material GelatinSolution   = fluid("gelatin_solution",0xD3D3D3,"Gelatin Solution","明胶溶液");
    public static final Material Nitrophenols      = builder("nitrophenols",0xFFFFFF,"Nitrophenols","硝基苯酚")
            .liquid()
            .formula("(C6H5NO3)(C6H5NO3)", true)
            .buildAndRegister();
    public static final Material EnderPearlSolution = builder("ender_pearl_solution",0x0f7666,"Ender Pearl Solution","末影珍珠溶液")
            .liquid()
            .components(GTMaterials.EnderPearl, 1, GTMaterials.Water, 1)
            .buildAndRegister();
    public static final Material EnderSugarSolution = builder("ender_sugar_solution",0x3fa08c,"Ender Sugar Solution","末影糖溶液")
            .liquid()
            .components(GTMaterials.Beryllium, 1, GTMaterials.Nitrogen, 2, GTMaterials.Potassium, 1, GTMaterials.Water, 1)
            .buildAndRegister();
    public static final Material ChorusJuice       = fluid("chorus_juice",0xa670e0,"Chorus Juice","紫颂果汁");
    public static final Material FermentedChorusJuice = fluid("fermented_chorus_juice",0xb5e8e6,"Fermented Chorus Juice","发酵紫颂果汁");
    public static final Material Antaf             = fluid("antaf",0xd4b5e8,"Antaf","安塔夫");
    public static final Material VibrantExtract    = fluid("vibrant_extract",0x3dfff7,"Vibrant Extract","活力提取液");
    public static final Material SodiumCarbonateSolution = builder("sodium_carbonate_solution",0xaaaaaa,"Sodium Carbonate Solution","碳酸钠溶液")
            .liquid()
            .components(GTMaterials.Water, 1, GTMaterials.SodaAsh, 1)
            .flags(DECOMPOSITION_BY_CENTRIFUGING)
            .buildAndRegister();

    private static Material fluid(String id, int color, String enLang, String cnLang){
        GTMFOMaterials.addLang(id,enLang,cnLang);
        return new Material.Builder(GTCEu.id(id)).color(color).fluid().buildAndRegister();
    }

    private static Material fluid(String id, int color, int temp, String enLang, String cnLang){
        GTMFOMaterials.addLang(id,enLang,cnLang);
        return new Material.Builder(GTCEu.id(id)).color(color).liquid(new FluidBuilder().temperature(temp)).buildAndRegister();
    }

    private static Material.Builder builder(String id, String enLang, String cnLang){
        GTMFOMaterials.addLang(id,enLang,cnLang);
        return new Material.Builder(GTCEu.id(id));
    }

    private static Material.Builder builder(String id, int color, String enLang, String cnLang){
        GTMFOMaterials.addLang(id,enLang,cnLang);
        return new Material.Builder(GTCEu.id(id)).color(color);
    }

    public static void init(){
    }
}
