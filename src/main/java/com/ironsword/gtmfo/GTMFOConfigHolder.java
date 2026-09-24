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
    @Configurable.Comment("Nutrient (dairy/fruit/grain/protein/vegetable) system options.")
    public GTFONutrientConfig gtfoNutrientConfig = new GTFONutrientConfig();

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
        @Configurable.Comment({"Legacy switch for the nutrient system - prefer gtfoNutrientConfig.enabled.",
                "Kept for save/config compatibility; either flag enables the system."})
        public boolean nutrientMode = false;

        @Configurable
        @Configurable.Comment("Export every recipe JEI can display to a JSON file after the recipe set is loaded (client-side, for external recipe graph tooling).")
        public boolean exportJeiRecipes = false;

        @Configurable
        @Configurable.Comment("Export path. Relative paths resolve against the game directory; absolute paths (e.g. H:/tools/jei_recipes.json) are used as-is.")
        public String jeiRecipeExportPath = "gtmfo/jei_recipes.json";

        @Configurable
        @Configurable.Comment("Name/language-key export path (machines, materials, items, fluids). Same path rules as jeiRecipeExportPath.")
        public String jeiNameExportPath = "gtmfo/jei_names.json";

        @Configurable
        @Configurable.Comment("Also export recipes that JEI hides by default.")
        public boolean jeiExportHiddenRecipes = false;
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
        @Configurable.Comment({
                "Multiplier applied to greenhouse recipe durations (trees / sap).",
                "1.0 = original GTFO speed (2000-4000 ticks per cycle).",
                "e.g. 6.0 makes every greenhouse cycle six times longer." })
        @Configurable.DecimalRange(min = 1.0, max = 100.0)
        public double greenhouseDurationMultiplier = 1.0;

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

    /**
     * Nutrient system (dairy / fruit / grain / protein / vegetable).
     *
     * <p>Eating GTMFO foods adds their built-in nutrient values (see {@code Foods}); items that only
     * match a {@code gtmfo:nutrient/<name>} tag add {@link #tagValue} instead, which lets packs
     * cover foods from other mods without touching code.</p>
     */
    public static class GTFONutrientConfig {
        @Configurable
        @Configurable.Comment("Enable the nutrient system (dairy/fruit/grain/protein/vegetable)?")
        public boolean enabled = false;

        @Configurable
        @Configurable.Comment("Maximum stored value per nutrient.")
        @Configurable.DecimalRange(min = 1.0, max = 1000.0)
        public double cap = 30.0;

        @Configurable
        @Configurable.Comment({"Value removed from every nutrient once per in-game day.",
                "0 = no decay. Offline time does not multiply the decay (one step per observed day change)."})
        @Configurable.DecimalRange(min = 0.0, max = 100.0)
        public double decayPerDay = 1.0;

        @Configurable
        @Configurable.Comment({"Value granted for foods that only match a gtmfo:nutrient/<name> tag",
                "(GTMFO foods keep their built-in per-item values)."})
        @Configurable.DecimalRange(min = 0.0, max = 100.0)
        public double tagValue = 1.0;

        @Configurable
        @Configurable.Comment("Reset every nutrient when the player dies?")
        public boolean resetOnDeath = true;

        @Configurable
        @Configurable.Comment({"Each nutrient at/above this threshold grants max health.",
                "0 = no health bonus (use when the pack grants its own nutrient rewards)."})
        @Configurable.DecimalRange(min = 0.0, max = 1000.0)
        public double benefitThreshold = 5.0;

        @Configurable
        @Configurable.Comment("Max health granted per qualifying nutrient (2.0 = 1 heart).")
        @Configurable.DecimalRange(min = 0.0, max = 20.0)
        public double healthPerNutrient = 2.0;

        @Configurable
        @Configurable.Comment("Maximum total max-health bonus from nutrients (10.0 = 5 hearts).")
        @Configurable.DecimalRange(min = 0.0, max = 100.0)
        public double healthBonusCap = 10.0;

        @Configurable
        @Configurable.Comment({"Optional effect granted while ALL nutrients are at/above the threshold",
                "(e.g. minecraft:luck). Empty = disabled."})
        public String balancedEffect = "";

        @Configurable
        @Configurable.Comment("Amplifier for the balanced-diet effect (0 = level I).")
        @Configurable.Range(min = 0, max = 9)
        public int balancedEffectAmplifier = 0;

        @Configurable
        @Configurable.Comment("Mirror nutrient values into scoreboard objectives (gtmfo_nutrient_<name>) for quests/scripts.")
        public boolean scoreboardMirror = true;

        @Configurable
        @Configurable.Comment("Show the nutrient HUD while playing (client side).")
        public boolean hud = true;

        @Configurable
        @Configurable.Comment("Show a food item's nutrient values in its tooltip.")
        public boolean foodTooltips = true;
    }
}
