package com.ironsword.gtmfo;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;

/**
 * Config, mirroring the original's 9 config groups ({@code GTFOConfig}).
 * Options for mods that are not present (AppleCore/NuclearCraft/ActuallyAdditions/Nutrition)
 * are kept for parity but have no effect.
 */
@Config(id = GregTechModernFoodOption.MODID)
public class GTMFOConfigHolder {
    public static GTMFOConfigHolder INSTANCE;
    private static final Object LOCK = new Object();

    public static void init() {
        synchronized (LOCK) {
            if (INSTANCE == null) {
                INSTANCE = Configuration.registerConfig(GTMFOConfigHolder.class, ConfigFormats.yaml()).getConfigInstance();
            }
        }
    }

    @Configurable
    @Configurable.Comment("Development/debug options.")
    public DevConfigs devConfigs = new DevConfigs();

    @Configurable
    @Configurable.Comment("Recipe chain options.")
    public GTFOChainsConfig gtfoChainsConfig = new GTFOChainsConfig();

    @Configurable
    @Configurable.Comment("Vanilla override options.")
    public GTFOVanillaOverridesConfig gtfoVanillaOverridesConfig = new GTFOVanillaOverridesConfig();

    @Configurable
    @Configurable.Comment("Options for other food mods.")
    public GTFOOtherFoodModConfig gtfoOtherFoodModConfig = new GTFOOtherFoodModConfig();

    @Configurable
    @Configurable.Comment("NuclearCraft compatibility options (unused - mod not present).")
    public GTFONCConfig gtfoNCConfig = new GTFONCConfig();

    @Configurable
    @Configurable.Comment("ActuallyAdditions compatibility options (unused - mod not present).")
    public GTFOAAConfig gtfoAAConfig = new GTFOAAConfig();

    @Configurable
    @Configurable.Comment("Food stats options.")
    public GTFOFoodConfig gtfoFoodConfig = new GTFOFoodConfig();

    @Configurable
    @Configurable.Comment("Potion effect options.")
    public GTFOPotionConfig gtfoPotionConfig = new GTFOPotionConfig();

    @Configurable
    @Configurable.Comment("Miscellaneous features for GTFO.")
    public GTFOMiscConfig gtfoMiscConfig = new GTFOMiscConfig();

    @Configurable
    @Configurable.Comment("World generation options.")
    public GTFOWorldGenConfig gtfoWorldGenConfig = new GTFOWorldGenConfig();

    public static class DevConfigs {
        @Configurable
        @Configurable.Comment("Enable the nutrient (dairy/fruit/grain/protein/vegetable) system.")
        public boolean nutrientMode = false;
    }

    public static class GTFOChainsConfig {
        @Configurable
        @Configurable.Comment("Delete vanilla bread recipe?")
        public boolean deleteBreadRecipe = false;

        @Configurable
        @Configurable.Comment("Force chains to be harder? (Most of the content of the hard chains will still exist, but less efficient and easier routes will exist without this configuration.)")
        public boolean makeChainsHarder = false;
    }

    public static class GTFOVanillaOverridesConfig {
        @Configurable
        @Configurable.Comment("Enable most GTFO Vanilla Overrides features, usually adding small chains to food items while removing normal recipes?")
        public boolean vanillaOverrideChain = false;

        @Configurable
        @Configurable.Comment("Make Baking Oven Recipes for Meats in the Vanilla Overrides chain (and delete normal furnace recipes for them), if the chain itself is enabled?")
        public boolean useBakingOvenForMeats = false;

        @Configurable
        @Configurable.Comment("Replace manual paper recipe with one with a Rolling Pin?")
        public boolean useRollingPinForPaper = false;
    }

    public static class GTFOOtherFoodModConfig {
        @Configurable
        @Configurable.Comment("Should AppleCore compatibility be turned on? (unused - mod not present)")
        public boolean appleCoreCompat = false;

        @Configurable
        @Configurable.Comment("Should all foods not from GregTech Food Option have reduced hunger and saturation stats, to incentivize using the foods from GTFO?")
        public boolean reduceForeignFoodStats = false;

        @Configurable
        @Configurable.Comment("Use the default GregTech Food Option food stats reduction (a logistic curve)?")
        public boolean useDefaultForeignFoodStatsReduction = false;

        @Configurable
        @Configurable.Comment("If the above is false, you can set this to divide all vanilla food items by some value.")
        public int constantFoodStatsDivisor = 1;

        @Configurable
        @Configurable.Comment("Turn on default GTFO compat for Nutrition: Unofficial Extended Life? (unused - mod not present)")
        public boolean enableGTFONutrition = true;
    }

    public static class GTFONCConfig {
        @Configurable
        @Configurable.Comment("Should NuclearCraft compatibility be turned on? (unused - mod not present)")
        public boolean nuclearCompat = true;

        @Configurable
        @Configurable.Comment("Add NuclearCraft S'more recipes?")
        public boolean smoreChain = true;

        @Configurable
        @Configurable.Comment("Add NuclearCraft S'more extensions?")
        public boolean addSmogus = true;
    }

    public static class GTFOAAConfig {
        @Configurable
        @Configurable.Comment("Should ActuallyAdditions compatibility be turned on? (unused - mod not present)")
        public boolean actuallyCompat = true;

        @Configurable
        @Configurable.Comment("Disable AA Coffee Maker's recipe? (unused - mod not present)")
        public boolean disableCoffeeMaker = true;
    }

    public static class GTFOFoodConfig {
        @Configurable
        @Configurable.Comment("Popcorn hunger")
        public int popcornHunger = 5;

        @Configurable
        @Configurable.Comment("Popcorn saturation")
        public float popcornSaturation = 0.4f;

        @Configurable
        @Configurable.Comment("Mineral Water hunger")
        public int mineralWaterHunger = 0;

        @Configurable
        @Configurable.Comment("Mineral Water saturation")
        public float mineralWaterSaturation = 0;

        @Configurable
        @Configurable.Comment("Lime hunger")
        public int limeHunger = 1;

        @Configurable
        @Configurable.Comment("Lime saturation")
        public float limeSaturation = 0.5f;

        @Configurable
        @Configurable.Comment("Lemon hunger")
        public int lemonHunger = 1;

        @Configurable
        @Configurable.Comment("Lemon saturation")
        public float lemonSaturation = 0.5f;

        @Configurable
        @Configurable.Comment("Etirps hunger")
        public int etirpsHunger = 0;

        @Configurable
        @Configurable.Comment("Etirps saturation")
        public float etirpsSaturation = 0;

        @Configurable
        @Configurable.Comment("Hard candy hunger")
        public int hardCandyHunger = 1;

        @Configurable
        @Configurable.Comment("Hard candy saturation")
        public float hardCandySaturation = 1;

        @Configurable
        @Configurable.Comment("Sparkling water hunger")
        public int sparklingWaterHunger = 1;

        @Configurable
        @Configurable.Comment("Sparkling water saturation")
        public float sparklingWaterSaturation = 1;

        @Configurable
        @Configurable.Comment("French fries hunger")
        public int friesHunger = 3;

        @Configurable
        @Configurable.Comment("French fries saturation")
        public float friesSaturation = 0;

        @Configurable
        @Configurable.Comment("Chip hunger (also affects the hunger of the other chip items)")
        public int chipHunger = 2;

        @Configurable
        @Configurable.Comment("Chip saturation (also affects the hunger of the other chip items)")
        public float chipSaturation = 0.5f;

        @Configurable
        @Configurable.Comment("Baguette hunger")
        public int baguetteHunger = 2;

        @Configurable
        @Configurable.Comment("Baguette saturation")
        public float baguetteSaturation = 1f;
    }

    public static class GTFOPotionConfig {
        @Configurable
        @Configurable.Comment("Apply effects of Creativity?")
        public boolean creativity = true;

        @Configurable
        @Configurable.Comment("Apply effects of Step Assist?")
        public boolean stepAssist = true;

        @Configurable
        @Configurable.Comment("Apply effects of Snow Golem Spawner?")
        public boolean snowGolemSpawner = true;
    }

    public static class GTFOMiscConfig {
        @Configurable
        @Configurable.Comment("Create methane centrifuging recipes for GTFO seeds?")
        public boolean centrifugeSeeds = true;

        @Configurable
        @Configurable.Comment("Greenhouse replacements for dirt (use block names like minecraft:dirt)")
        public String[] greenhouseDirts = new String[0];

        @Configurable
        @Configurable.Comment("Add furnace recipes for each baking oven recipe?")
        public boolean bakingOvenReplacement = false;

        @Configurable
        @Configurable.Comment("Add GTFO foods to dungeons?")
        public boolean addDungeonFoods = true;

        @Configurable
        @Configurable.Comment("Add GTFO foods laced with cyanide to dungeons?")
        public boolean addLacedDungeonFoods = true;

        @Configurable
        @Configurable.Comment("Weight of unknown seeds from grass drops")
        public int unknownSeedsWeight = 5;
    }

    public static class GTFOWorldGenConfig {
        @Configurable
        @Configurable.Comment("Turn on GTFO tree generation? Required for many mod features.")
        public boolean enableGTFOTrees = true;

        @Configurable
        @Configurable.Comment("Turn on GTFO berry generation? Required for some mod features.")
        public boolean enableGTFOBerries = true;
    }
}
