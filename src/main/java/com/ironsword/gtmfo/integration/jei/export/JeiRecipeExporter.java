package com.ironsword.gtmfo.integration.jei.export;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialStack;
import com.gregtechceu.gtceu.api.item.IGTTool;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.category.GTRecipeCategory;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.ingredient.EnergyStack;
import com.gregtechceu.gtceu.api.recipe.ingredient.FluidIngredient;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.ironsword.gtmfo.GTMFOConfigHolder;
import com.mojang.logging.LogUtils;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.IRecipeCatalystLookup;
import mezz.jei.api.recipe.IRecipeCategoriesLookup;
import mezz.jei.api.recipe.IRecipeLookup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Optional debug feature (config: {@code devConfigs.exportJeiRecipes}): dumps every recipe JEI can
 * display to a JSON file so external tools can build a recipe graph from it.
 * <p>
 * The export is triggered after the client recipe set has settled (debounced {@link RecipesUpdatedEvent}),
 * runs on a daemon thread with a streaming writer and a lightweight layout builder, so it does not
 * block the game and does not allocate GUI objects.
 * <p>
 * JSON shape:
 * <pre>
 * {
 *   "format": "gtmfo_jei_recipes", "version": 1, "minecraft_version": "1.20.1",
 *   "exported_at": "...", "include_hidden": false,
 *   "categories": [
 *     {
 *       "type": "gtceu:macerator", "title": "Macerator", "recipe_class": "...",
 *       "catalysts": [ { "type": "item", "id": "gtceu:lv_macerator", "count": 1 } ],
 *       "recipes": [
 *         {
 *           "id": "gtceu:macerator/macerate_steak",
 *           "inputs":  [ { "name": "slot0", "ingredients": [ { "type": "item", "id": "minecraft:beef", "count": 1 } ] } ],
 *           "outputs": [ { "name": "slot1", "ingredients": [ { "type": "item", "id": "gtceu:meat_dust", "count": 1 } ] } ]
 *         }
 *       ]
 *     }
 *   ],
 *   "summary": { "category_count": 42, "recipe_count": 12345 }
 * }
 * </pre>
 */
public final class JeiRecipeExporter {

    private static final Logger LOGGER = LogUtils.getLogger();

    /** ticks to wait after the last recipe update before exporting (recipe syncs can happen several times) */
    private static final int SETTLE_TICKS = 60;

    private static IJeiRuntime runtime;
    private static boolean listenersRegistered;
    private static boolean pending;
    private static int countdown = -1;
    private static final AtomicBoolean exporting = new AtomicBoolean();

    private JeiRecipeExporter() {}

    /** Called from the JEI plugin once the runtime is available (client-side). */
    public static synchronized void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
        if (!listenersRegistered) {
            listenersRegistered = true;
            MinecraftForge.EVENT_BUS.addListener(JeiRecipeExporter::onRecipesUpdated);
            MinecraftForge.EVENT_BUS.addListener(JeiRecipeExporter::onClientTick);
        }
        schedule();
    }

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        schedule();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!pending) return;
        if (--countdown > 0) return;
        pending = false;
        startExport();
    }

    private static synchronized void schedule() {
        if (runtime == null) return;
        if (!GTMFOConfigHolder.INSTANCE.devConfigs.exportJeiRecipes) return;
        pending = true;
        countdown = SETTLE_TICKS;
    }

    private static void startExport() {
        if (!exporting.compareAndSet(false, true)) return;
        IJeiRuntime jeiRuntime = runtime;
        Thread thread = new Thread(() -> {
            try {
                export(jeiRuntime);
            } catch (Throwable t) {
                LOGGER.error("[jei-export] failed to export JEI recipes", t);
            } finally {
                exporting.set(false);
            }
        }, "gtmfo-jei-recipe-export");
        thread.setDaemon(true);
        thread.start();
    }

    private static void export(IJeiRuntime jeiRuntime) throws IOException {
        long start = System.nanoTime();
        GTMFOConfigHolder.DevConfigs config = GTMFOConfigHolder.INSTANCE.devConfigs;
        boolean includeHidden = config.jeiExportHiddenRecipes;

        // the config library may keep surrounding quotes from hand-edited yaml
        String exportPath = config.jeiRecipeExportPath.trim();
        if (exportPath.length() > 1 && exportPath.startsWith("\"") && exportPath.endsWith("\"")) {
            exportPath = exportPath.substring(1, exportPath.length() - 1);
        }
        Path target = Path.of(exportPath);
        if (!target.isAbsolute()) {
            target = FMLPaths.GAMEDIR.get().resolve(target);
        }
        Path parent = target.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Path tmp = target.resolveSibling(target.getFileName() + ".tmp");

        IRecipeManager recipeManager = jeiRuntime.getRecipeManager();
        IIngredientManager ingredientManager = jeiRuntime.getIngredientManager();
        IFocusGroup emptyFocus = jeiRuntime.getJeiHelpers().getFocusFactory().getEmptyFocusGroup();

        IRecipeCategoriesLookup categoryLookup = recipeManager.createRecipeCategoryLookup();
        if (includeHidden) categoryLookup = categoryLookup.includeHidden();
        List<IRecipeCategory<?>> categories = categoryLookup.get()
                .sorted(Comparator.comparing(category -> category.getRecipeType().getUid().toString()))
                .toList();

        // GT machine categories are rendered through LDLib widgets and never add slots via the JEI
        // layout builder; they are also missing from JEI entirely on some server sessions. Export
        // every GT category directly from the GTCEu API instead (see exportGtCategories).
        Set<String> gtCategoryUids = new HashSet<>();
        for (GTRecipeCategory gtCategory : GTRegistries.RECIPE_CATEGORIES) {
            gtCategoryUids.add(gtCategory.registryKey.toString());
        }

        int categoryCount = 0;
        int recipeCount = 0;
        try (JsonWriter writer = new JsonWriter(new BufferedWriter(
                Files.newBufferedWriter(tmp, StandardCharsets.UTF_8), 1 << 20))) {
            writer.beginObject();
            writer.name("format").value("gtmfo_jei_recipes");
            writer.name("version").value(2);
            writer.name("minecraft_version").value("1.20.1");
            writer.name("exported_at").value(Instant.now().toString());
            writer.name("include_hidden").value(includeHidden);
            writer.name("categories").beginArray();
            for (IRecipeCategory<?> category : categories) {
                if (gtCategoryUids.contains(category.getRecipeType().getUid().toString())) continue;
                int written = exportCategory(writer, recipeManager, ingredientManager, emptyFocus, category,
                        includeHidden);
                if (written > 0) {
                    categoryCount++;
                    recipeCount += written;
                }
            }
            try {
                int[] gtCounts = exportGtCategories(writer);
                categoryCount += gtCounts[0];
                recipeCount += gtCounts[1];
            } catch (Throwable t) {
                LOGGER.error("[jei-export] failed to export GT categories", t);
            }            writer.endArray();
            writer.name("summary").beginObject();
            writer.name("category_count").value(categoryCount);
            writer.name("recipe_count").value(recipeCount);
            writer.endObject();
            writer.endObject();
        }
        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        LOGGER.info("[jei-export] wrote {} recipes in {} categories to {} ({} ms)",
                recipeCount, categoryCount, target, (System.nanoTime() - start) / 1_000_000);

        exportNames(config.jeiNameExportPath);
    }

    /** Writes the name/language-key lookup for machines, materials, items and fluids. */
    private static void exportNames(String configuredPath) {
        try {
            String exportPath = configuredPath.trim();
            if (exportPath.length() > 1 && exportPath.startsWith("\"") && exportPath.endsWith("\"")) {
                exportPath = exportPath.substring(1, exportPath.length() - 1);
            }
            Path target = Path.of(exportPath);
            if (!target.isAbsolute()) {
                target = FMLPaths.GAMEDIR.get().resolve(target);
            }
            Path parent = target.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Path tmp = target.resolveSibling(target.getFileName() + ".tmp");

            long start = System.nanoTime();
            // resolve English and Chinese names independently of the client's current locale
            Map<String, String> enLang = loadLanguageMap("en_us");
            Map<String, String> zhLang = loadLanguageMap("zh_cn");
            int machines = 0;
            int materials = 0;
            int blocks = 0;
            int items = 0;
            int fluids = 0;
            try (JsonWriter writer = new JsonWriter(new BufferedWriter(
                    Files.newBufferedWriter(tmp, StandardCharsets.UTF_8), 1 << 20))) {
                writer.beginObject();
                writer.name("format").value("gtmfo_jei_names");
                writer.name("version").value(1);
                writer.name("minecraft_version").value("1.20.1");
                writer.name("exported_at").value(Instant.now().toString());

                // GT machines
                List<MachineDefinition> machineList = new ArrayList<>();
                GTRegistries.MACHINES.forEach(machineList::add);
                machineList.sort(Comparator.comparing(machine -> machine.getId().toString()));
                writer.name("machines").beginArray();
                for (MachineDefinition machine : machineList) {
                    writer.beginObject();
                    writer.name("id").value(machine.getId().toString());
                    String key = machine.getDescriptionId();
                    writer.name("key").value(key);
                    String en = lookup(enLang, key, stripFormatting(machineName(machine)));
                    writer.name("en").value(en);
                    writer.name("zh").value(lookup(zhLang, key, en));
                    int tier = Math.max(0, Math.min(machine.getTier(), GTValues.VN.length - 1));
                    writer.name("tier").value(GTValues.VN[tier]);
                    writer.name("tier_index").value(tier);
                    writer.endObject();
                    machines++;
                }
                writer.endArray();

                // GT materials
                List<Material> materialList = new ArrayList<>(GTCEuAPI.materialManager.getRegisteredMaterials());
                materialList.sort(Comparator.comparing(material -> material.getResourceLocation().toString()));
                writer.name("materials").beginArray();
                for (Material material : materialList) {
                    writer.beginObject();
                    writer.name("id").value(material.getResourceLocation().toString());
                    String key = material.getUnlocalizedName();
                    writer.name("key").value(key);
                    String en = lookup(enLang, key, stripFormatting(material.getDefaultTranslation()));
                    writer.name("en").value(en);
                    writer.name("zh").value(lookup(zhLang, key, en));
                    writer.endObject();
                    materials++;
                }
                writer.endArray();

                // all blocks
                List<Block> blockList = new ArrayList<>(ForgeRegistries.BLOCKS.getValues());
                blockList.sort(Comparator.comparing(block -> String.valueOf(ForgeRegistries.BLOCKS.getKey(block))));
                writer.name("blocks").beginArray();
                for (Block block : blockList) {
                    ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
                    if (id == null) continue;
                    String key = block.getDescriptionId();
                    String[] names = resolveNames(enLang, zhLang, key, materialOf(block.asItem()), key);
                    writer.beginObject();
                    writer.name("id").value(id.toString());
                    writer.name("key").value(key);
                    writer.name("en").value(names[0]);
                    writer.name("zh").value(names[1]);
                    writer.endObject();
                    blocks++;
                }
                writer.endArray();

                // all items
                List<Item> itemList = new ArrayList<>(ForgeRegistries.ITEMS.getValues());
                itemList.sort(Comparator.comparing(item -> String.valueOf(ForgeRegistries.ITEMS.getKey(item))));
                writer.name("items").beginArray();
                for (Item item : itemList) {
                    ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
                    if (id == null) continue;
                    String key = item.getDescriptionId();
                    String[] names = resolveNames(enLang, zhLang, key, materialOf(item),
                            displayName(new ItemStack(item)));
                    writer.beginObject();
                    writer.name("id").value(id.toString());
                    writer.name("key").value(key);
                    writer.name("en").value(names[0]);
                    writer.name("zh").value(names[1]);
                    writer.endObject();
                    items++;
                }
                writer.endArray();

                // all fluids
                List<Fluid> fluidList = new ArrayList<>(ForgeRegistries.FLUIDS.getValues());
                fluidList.sort(Comparator.comparing(fluid -> String.valueOf(ForgeRegistries.FLUIDS.getKey(fluid))));
                writer.name("fluids").beginArray();
                for (Fluid fluid : fluidList) {
                    ResourceLocation id = ForgeRegistries.FLUIDS.getKey(fluid);
                    if (id == null) continue;
                    String key = fluid.getFluidType().getDescriptionId();
                    writer.beginObject();
                    writer.name("id").value(id.toString());
                    writer.name("key").value(key);
                    String en = lookup(enLang, key, displayName(new FluidStack(fluid, 1)));
                    writer.name("en").value(en);
                    writer.name("zh").value(lookup(zhLang, key, en));
                    writer.endObject();
                    fluids++;
                }
                writer.endArray();
                writer.endObject();
            }
            Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("[jei-export] wrote {} machines, {} materials, {} blocks, {} items, {} fluids to {} ({} ms)",
                    machines, materials, blocks, items, fluids, target, (System.nanoTime() - start) / 1_000_000);
        } catch (Throwable t) {
            LOGGER.error("[jei-export] failed to export name lookup", t);
        }
    }

    private static String displayName(ItemStack stack) {
        try {
            return stripFormatting(stack.getHoverName().getString());
        } catch (Throwable t) {
            return "";
        }
    }

    private static String displayName(FluidStack stack) {
        try {
            return stripFormatting(stack.getDisplayName().getString());
        } catch (Throwable t) {
            return "";
        }
    }

    private static String machineName(MachineDefinition machine) {
        String langValue = machine.getLangValue();
        if (langValue != null && !langValue.isBlank()) {
            return langValue;
        }
        String key = machine.getDescriptionId();
        try {
            String localized = Component.translatable(key).getString();
            if (!localized.isEmpty() && !localized.equals(key)) {
                return localized;
            }
        } catch (Throwable ignored) {}
        return machine.getId().toString();
    }

    /**
     * Loads a language file (en_us / zh_cn) from all resource packs, so both names can be exported
     * regardless of the client's selected locale. JSON first, then legacy {@code .lang}.
     */
    private static Map<String, String> loadLanguageMap(String code) {
        Map<String, String> map = new HashMap<>(1 << 16);
        try {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            for (String namespace : resourceManager.getNamespaces()) {
                ResourceLocation jsonLocation = ResourceLocation.tryBuild(namespace, "lang/" + code + ".json");
                if (jsonLocation != null) {
                    for (Resource resource : resourceManager.getResourceStack(jsonLocation)) {
                        try (BufferedReader reader = resource.openAsReader()) {
                            JsonObject object = JsonParser.parseReader(reader).getAsJsonObject();
                            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                                if (entry.getValue().isJsonPrimitive()) {
                                    map.putIfAbsent(entry.getKey(), entry.getValue().getAsString());
                                }
                            }
                        } catch (Throwable ignored) {}
                    }
                }
                ResourceLocation langLocation = ResourceLocation.tryBuild(namespace, "lang/" + code + ".lang");
                if (langLocation != null) {
                    for (Resource resource : resourceManager.getResourceStack(langLocation)) {
                        try (BufferedReader reader = resource.openAsReader()) {
                            String line;
                            while ((line = reader.readLine()) != null) {
                                line = line.trim();
                                if (line.isEmpty() || line.startsWith("#")) continue;
                                int separator = line.indexOf('=');
                                if (separator <= 0) continue;
                                map.putIfAbsent(line.substring(0, separator).trim(),
                                        line.substring(separator + 1).trim());
                            }
                        } catch (Throwable ignored) {}
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.warn("[jei-export] failed to load language map {}", code, t);
        }
        return map;
    }

    /**
     * Resolves en/zh for an item or block key. GTCEu names material items through templates like
     * {@code "%s Dust"} / {@code "%s粉"} and tool items through {@code "%s Wrench"}; those are
     * composed with the material's localized name.
     */
    private static String[] resolveNames(Map<String, String> enLang, Map<String, String> zhLang,
                                         String key, Material material, String fallback) {
        String enValue = key != null ? enLang.get(key) : null;
        String zhValue = key != null ? zhLang.get(key) : null;
        if (material != null && (isTemplate(enValue) || isTemplate(zhValue) || enValue == null || zhValue == null)) {
            String enMaterial = stripFormatting(material.getDefaultTranslation());
            String zhMaterial = lookup(zhLang, material.getUnlocalizedName(), enMaterial);
            if (isTemplate(enValue)) {
                enValue = composeMaterialName(enValue, enMaterial);
            }
            if (isTemplate(zhValue)) {
                zhValue = composeMaterialName(zhValue, zhMaterial);
            }
        }
        String en = enValue != null && !enValue.isEmpty() ? stripFormatting(enValue)
                : (fallback != null ? fallback : (key == null ? "" : key));
        String zh = zhValue != null && !zhValue.isEmpty() ? stripFormatting(zhValue) : en;
        return new String[] { en, zh };
    }

    private static boolean isTemplate(String value) {
        return value != null && value.contains("%s");
    }

    private static String composeMaterialName(String template, String materialName) {
        try {
            return String.format(template, materialName);
        } catch (Throwable t) {
            return template;
        }
    }

    /** Material behind a GT item (tools included), or null for non-material items. */
    private static Material materialOf(Item item) {
        if (item == null || item == net.minecraft.world.item.Items.AIR) {
            return null;
        }
        if (item instanceof IGTTool tool) {
            try {
                return tool.getMaterial();
            } catch (Throwable ignored) {}
        }
        try {
            MaterialStack stack = ChemicalHelper.getMaterialStack(new ItemStack(item));
            if (stack != null && !stack.isEmpty()) {
                return stack.material();
            }
        } catch (Throwable ignored) {}
        return null;
    }

    private static String lookup(Map<String, String> language, String key, String fallback) {
        if (key != null) {
            String value = language.get(key);
            if (value != null && !value.isEmpty()) {
                return stripFormatting(value);
            }
        }
        return fallback == null ? (key == null ? "" : key) : fallback;
    }

    /** Removes vanilla formatting codes (e.g. the trailing {@code §r} GT adds to machine names). */
    private static String stripFormatting(String value) {
        if (value == null || value.indexOf('\u00a7') < 0) {
            return value;
        }
        StringBuilder builder = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\u00a7') {
                i++;
                continue;
            }
            builder.append(c);
        }
        return builder.toString().trim();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static int exportCategory(JsonWriter writer, IRecipeManager recipeManager,
                                      IIngredientManager ingredientManager, IFocusGroup focus,
                                      IRecipeCategory<?> category, boolean includeHidden) throws IOException {
        IRecipeCategory rawCategory = category;
        RecipeTypeInfo type = RecipeTypeInfo.of(category);
        IRecipeLookup lookup = recipeManager.createRecipeLookup(type.recipeType);
        if (includeHidden) {
            lookup = lookup.includeHidden();
        }
        List<?> recipes = new ArrayList<>(lookup.get().toList());
        if (recipes.isEmpty()) {
            return 0;
        }
        recipes.sort(Comparator.comparing(recipe -> {
            ResourceLocation id = recipeId(rawCategory, recipe);
            return id == null ? "" : id.toString();
        }));

        writer.beginObject();
        writer.name("type").value(type.uid);
        writer.name("title").value(type.title);
        writer.name("recipe_class").value(type.recipeClass);
        writer.name("kind").value(isInformationCategory(type.uid) ? "information" : "recipe");

        writer.name("catalysts").beginArray();
        IRecipeCatalystLookup catalystLookup = recipeManager.createRecipeCatalystLookup(type.recipeType);
        if (includeHidden) {
            catalystLookup = catalystLookup.includeHidden();
        }
        for (ITypedIngredient<?> catalyst : catalystLookup.get().toList()) {
            writeIngredient(writer, catalyst);
        }
        writer.endArray();

        writer.name("recipes").beginArray();
        int index = 0;
        Set<String> usedIds = new HashSet<>();
        for (Object recipe : recipes) {
            CapturingRecipeLayoutBuilder builder = new CapturingRecipeLayoutBuilder(ingredientManager);
            try {
                rawCategory.setRecipe(builder, recipe, focus);
            } catch (Throwable t) {
                LOGGER.warn("[jei-export] category {} failed to lay out recipe {}", type.uid, recipe, t);
            }
            ResourceLocation id = recipeId(rawCategory, recipe);
            String idString = id != null ? id.toString() : null;
            // JEI returns broken ids for some categories ("minecraft:", duplicated ids); synthesize
            // a stable per-category id so external tools can address every recipe uniquely.
            if (!isUsableRecipeId(idString) || !usedIds.add(idString)) {
                String synthesized = type.uid + "#" + index;
                while (!usedIds.add(synthesized)) {
                    synthesized = synthesized + "_";
                }
                idString = synthesized;
            }
            writer.beginObject();
            writer.name("id").value(idString);
            writeSlotGroup(writer, "inputs", builder.slots, RecipeIngredientRole.INPUT);
            writeSlotGroup(writer, "outputs", builder.slots, RecipeIngredientRole.OUTPUT);
            if (recipe instanceof GTRecipe gtRecipe) {
                writeGtRequirements(writer, gtRecipe);
            }
            writer.endObject();
            index++;
        }
        writer.endArray();
        writer.endObject();
        return recipes.size();
    }

    /** GT recipe requirements (EU/t, minimum voltage tier, duration, ...) via the GTCEu API. */
    private static void writeGtRequirements(JsonWriter writer, GTRecipe recipe) throws IOException {
        writer.name("gt").beginObject();
        writer.name("recipe_type").value(recipe.recipeType.registryName.toString());
        writer.name("duration").value(recipe.duration);
        writer.name("parallels").value(recipe.parallels);
        writer.name("oc_level").value(recipe.ocLevel);

        EnergyStack.WithIO realEUt = RecipeHelper.getRealEUtWithIO(recipe);
        EnergyStack energy = realEUt.stack();
        if (!energy.isEmpty()) {
            int tier = Math.max(0, Math.min(RecipeHelper.getRecipeEUtTier(recipe), GTValues.VN.length - 1));
            writer.name("eut").value(energy.voltage());
            writer.name("amperage").value(energy.amperage());
            writer.name("energy_io").value(realEUt.io() == IO.IN ? "in" : "out");
            writer.name("tier").value(GTValues.VN[tier]);
            writer.name("tier_index").value(tier);
            writer.name("voltage").value(GTValues.V[tier]);
            writer.name("total_eu_t").value(energy.getTotalEU());
            writer.name("total_eu").value(energy.getTotalEU() * Math.max(0, recipe.duration));
        }
        writer.endObject();
    }

    @SuppressWarnings("rawtypes")
    private static ResourceLocation recipeId(IRecipeCategory category, Object recipe) {
        try {
            return category.getRegistryName(recipe);
        } catch (Throwable t) {
            return null;
        }
    }

    /** {@code true} when the id is a usable {@code namespace:path} (JEI sometimes returns {@code "minecraft:"}). */
    private static boolean isUsableRecipeId(String id) {
        if (id == null) return false;
        int separator = id.indexOf(':');
        return separator > 0 && separator < id.length() - 1;
    }

    /** Categories that only describe information (JEI information pages, GTFO food info) instead of crafting. */
    private static boolean isInformationCategory(String uid) {
        return uid.equals("jei:information") || uid.endsWith("_info") || uid.endsWith(":information");
    }

    // =========================================================
    // ******* GT recipes, exported from the GTCEu API ******** //
    // =========================================================

    /**
     * GT's own JEI categories render recipes through LDLib widgets and never call the JEI layout
     * builder, so their slots cannot be captured. Worse, on server sessions the GT categories can be
     * missing from JEI altogether (nothing in this exporter depends on their presence any more).
     * <p>
     * This writes one category per GT {@link GTRecipeCategory} straight from the GTCEu recipe
     * registry, with item/fluid/energy contents and the full {@code gt} requirements block.
     */
    private static int[] exportGtCategories(JsonWriter writer) throws IOException {
        List<GTRecipeCategory> gtCategories = new ArrayList<>();
        for (GTRecipeCategory category : GTRegistries.RECIPE_CATEGORIES) {
            if (!category.shouldRegisterDisplays()) continue;
            gtCategories.add(category);
        }
        gtCategories.sort(Comparator.comparing(category -> category.registryKey.toString()));

        Map<GTRecipeCategory, List<ItemStack>> catalystMap = new HashMap<>();
        for (MachineDefinition machine : GTRegistries.MACHINES) {
            try {
                for (GTRecipeType recipeType : machine.getRecipeTypes()) {
                    for (GTRecipeCategory category : recipeType.getCategories()) {
                        catalystMap.computeIfAbsent(category, key -> new ArrayList<>()).add(machine.asStack());
                    }
                }
            } catch (Throwable ignored) {
            }
        }

        int categoryCount = 0;
        int recipeCount = 0;
        for (GTRecipeCategory category : gtCategories) {
            GTRecipeType recipeType = category.getRecipeType();
            List<GTRecipe> recipes = new ArrayList<>(recipeType.getRecipesInCategory(category));
            if (recipes.isEmpty()) continue;
            recipes.sort(Comparator.comparing(recipe -> recipe.id == null ? "" : recipe.id.toString()));

            writer.beginObject();
            writer.name("type").value(category.registryKey.toString());
            writer.name("title").value(gtCategoryTitle(category));
            writer.name("recipe_class").value(GTRecipe.class.getName());
            writer.name("kind").value("recipe");

            writer.name("catalysts").beginArray();
            for (ItemStack catalyst : catalystMap.getOrDefault(category, List.of())) {
                writeItemStack(writer, catalyst);
            }
            writer.endArray();

            writer.name("recipes").beginArray();
            int index = 0;
            for (GTRecipe recipe : recipes) {
                try {
                    writer.beginObject();
                    writer.name("id").value(recipe.id != null ? recipe.id.toString()
                            : category.registryKey + "#" + index);
                    writer.name("inputs").beginArray();
                    writeGtCapabilitySlots(writer, recipe.inputs, false);
                    writeGtCapabilitySlots(writer, recipe.tickInputs, true);
                    writer.endArray();
                    writer.name("outputs").beginArray();
                    writeGtCapabilitySlots(writer, recipe.outputs, false);
                    writeGtCapabilitySlots(writer, recipe.tickOutputs, true);
                    writer.endArray();
                    writeGtRequirements(writer, recipe);
                    writer.endObject();
                } catch (Throwable t) {
                    LOGGER.warn("[jei-export] failed to export GT recipe {}", recipe.id, t);
                }
                index++;
            }
            writer.endArray();
            writer.endObject();
            categoryCount++;
            recipeCount += recipes.size();
        }
        return new int[] { categoryCount, recipeCount };
    }

    private static String gtCategoryTitle(GTRecipeCategory category) {
        try {
            String title = Component.translatable(category.getLanguageKey()).getString();
            if (title != null && !title.isEmpty()) {
                return title;
            }
        } catch (Throwable ignored) {
        }
        return category.registryKey.toString();
    }

    /** One slot per GT {@link Content}; a {@code name} identifies the capability (item, fluid, ...). */
    private static void writeGtCapabilitySlots(JsonWriter writer, Map<RecipeCapability<?>, List<Content>> contents,
                                               boolean perTick) throws IOException {
        for (Map.Entry<RecipeCapability<?>, List<Content>> entry : contents.entrySet()) {
            RecipeCapability<?> capability = entry.getKey();
            if (capability == EURecipeCapability.CAP) {
                continue; // energy is written into the "gt" block
            }
            for (Content content : entry.getValue()) {
                writer.beginObject();
                writer.name("name").value(capability.name + (perTick ? "_tick" : ""));
                if (content.isChanced()) {
                    writer.name("chance").value(content.chance);
                    writer.name("max_chance").value(content.maxChance);
                }
                writer.name("ingredients").beginArray();
                writeGtContent(writer, content.content);
                writer.endArray();
                writer.endObject();
            }
        }
    }

    private static final int MAX_INGREDIENT_ALTERNATIVES = 64;

    private static void writeGtContent(JsonWriter writer, Object value) throws IOException {
        if (value == null) return;
        if (value instanceof ItemStack stack) {
            writeItemStack(writer, stack);
        } else if (value instanceof Ingredient ingredient) {
            ItemStack[] stacks;
            try {
                stacks = ingredient.getItems();
            } catch (Throwable t) {
                return; // leave the slot empty rather than abort the recipe
            }
            int limit = Math.min(stacks.length, MAX_INGREDIENT_ALTERNATIVES);
            for (int i = 0; i < limit; i++) {
                writeItemStack(writer, stacks[i]);
            }
        } else if (value instanceof FluidIngredient fluidIngredient) {
            FluidStack[] stacks;
            try {
                stacks = fluidIngredient.getStacks();
            } catch (Throwable t) {
                return;
            }
            int limit = Math.min(stacks.length, MAX_INGREDIENT_ALTERNATIVES);
            for (int i = 0; i < limit; i++) {
                writeFluidStack(writer, stacks[i]);
            }
        } else if (value instanceof FluidStack fluid) {
            writeFluidStack(writer, fluid);
        } else {
            writer.beginObject();
            writer.name("type").value(value.getClass().getSimpleName());
            writer.name("value").value(String.valueOf(value));
            writer.endObject();
        }
    }

    private static void writeItemStack(JsonWriter writer, ItemStack stack) throws IOException {
        if (stack == null || stack.isEmpty()) return;
        writer.beginObject();
        writer.name("type").value("item");
        writer.name("id").value(String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem())));
        writer.name("count").value(stack.getCount());
        if (stack.hasTag()) {
            writer.name("nbt").value(stack.getTag().toString());
        }
        writer.endObject();
    }

    private static void writeFluidStack(JsonWriter writer, FluidStack stack) throws IOException {
        if (stack == null || stack.isEmpty()) return;
        writer.beginObject();
        writer.name("type").value("fluid");
        writer.name("id").value(String.valueOf(ForgeRegistries.FLUIDS.getKey(stack.getFluid())));
        writer.name("amount").value(stack.getAmount());
        if (stack.hasTag()) {
            writer.name("nbt").value(stack.getTag().toString());
        }
        writer.endObject();
    }

    private static void writeSlotGroup(JsonWriter writer, String name,
                                       List<CapturingRecipeLayoutBuilder.CapturedSlot> slots,
                                       RecipeIngredientRole role) throws IOException {
        writer.name(name).beginArray();
        int slotIndex = 0;
        for (CapturingRecipeLayoutBuilder.CapturedSlot slot : slots) {
            if (slot.role != role || slot.ingredients.isEmpty()) {
                continue;
            }
            writer.beginObject();
            String slotName = slot.name;
            if (slotName == null || slotName.isEmpty()) {
                // several mods never name their slots ("Input"/"Result"/...) - give every slot a
                // stable name so external tools can address it
                String rolePrefix = role == RecipeIngredientRole.INPUT ? "input"
                        : role == RecipeIngredientRole.OUTPUT ? "output" : "slot";
                slotName = rolePrefix + "_" + slotIndex;
            }
            writer.name("name").value(slotName);
            writer.name("ingredients").beginArray();
            for (ITypedIngredient<?> ingredient : slot.ingredients) {
                writeIngredient(writer, ingredient);
            }
            writer.endArray();
            writer.endObject();
            slotIndex++;
        }
        writer.endArray();
    }

    private static void writeIngredient(JsonWriter writer, ITypedIngredient<?> typed) throws IOException {
        Object value = typed.getIngredient();
        if (value instanceof ItemStack stack) {
            writer.beginObject();
            writer.name("type").value("item");
            writer.name("id").value(String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem())));
            writer.name("count").value(stack.getCount());
            if (stack.hasTag()) {
                writer.name("nbt").value(stack.getTag().toString());
            }
            writer.endObject();
        } else if (value instanceof FluidStack fluid) {
            writer.beginObject();
            writer.name("type").value("fluid");
            writer.name("id").value(String.valueOf(ForgeRegistries.FLUIDS.getKey(fluid.getFluid())));
            writer.name("amount").value(fluid.getAmount());
            if (fluid.hasTag()) {
                writer.name("nbt").value(fluid.getTag().toString());
            }
            writer.endObject();
        } else {
            writer.beginObject();
            writer.name("type").value(typed.getType().getUid());
            writer.name("value").value(String.valueOf(value));
            writer.endObject();
        }
    }

    /** Small raw-typed holder so the category lookup can be called without generic gymnastics. */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static final class RecipeTypeInfo {

        final mezz.jei.api.recipe.RecipeType recipeType;
        final String uid;
        final String title;
        final String recipeClass;

        private RecipeTypeInfo(mezz.jei.api.recipe.RecipeType recipeType, String uid, String title,
                               String recipeClass) {
            this.recipeType = recipeType;
            this.uid = uid;
            this.title = title;
            this.recipeClass = recipeClass;
        }

        static RecipeTypeInfo of(IRecipeCategory<?> category) {
            var recipeType = category.getRecipeType();
            String title;
            try {
                title = category.getTitle().getString();
            } catch (Throwable t) {
                title = recipeType.getUid().toString();
            }
            return new RecipeTypeInfo(recipeType, recipeType.getUid().toString(), title,
                    recipeType.getRecipeClass().getName());
        }
    }
}
