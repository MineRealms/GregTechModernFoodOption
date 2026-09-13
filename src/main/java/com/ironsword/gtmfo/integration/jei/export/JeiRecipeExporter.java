package com.ironsword.gtmfo.integration.jei.export;

import com.google.gson.stream.JsonWriter;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

        int categoryCount = 0;
        int recipeCount = 0;
        try (JsonWriter writer = new JsonWriter(new BufferedWriter(
                Files.newBufferedWriter(tmp, StandardCharsets.UTF_8), 1 << 20))) {
            writer.beginObject();
            writer.name("format").value("gtmfo_jei_recipes");
            writer.name("version").value(1);
            writer.name("minecraft_version").value("1.20.1");
            writer.name("exported_at").value(Instant.now().toString());
            writer.name("include_hidden").value(includeHidden);
            writer.name("categories").beginArray();
            for (IRecipeCategory<?> category : categories) {
                int written = exportCategory(writer, recipeManager, ingredientManager, emptyFocus, category,
                        includeHidden);
                if (written > 0) {
                    categoryCount++;
                    recipeCount += written;
                }
            }
            writer.endArray();
            writer.name("summary").beginObject();
            writer.name("category_count").value(categoryCount);
            writer.name("recipe_count").value(recipeCount);
            writer.endObject();
            writer.endObject();
        }
        Files.move(tmp, target, StandardCopyOption.REPLACE_EXISTING);
        LOGGER.info("[jei-export] wrote {} recipes in {} categories to {} ({} ms)",
                recipeCount, categoryCount, target, (System.nanoTime() - start) / 1_000_000);
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
        for (Object recipe : recipes) {
            CapturingRecipeLayoutBuilder builder = new CapturingRecipeLayoutBuilder(ingredientManager);
            try {
                rawCategory.setRecipe(builder, recipe, focus);
            } catch (Throwable t) {
                LOGGER.warn("[jei-export] category {} failed to lay out recipe {}", type.uid, recipe, t);
            }
            ResourceLocation id = recipeId(rawCategory, recipe);
            writer.beginObject();
            writer.name("id").value(id != null ? id.toString() : type.uid + "#" + index);
            writeSlotGroup(writer, "inputs", builder.slots, RecipeIngredientRole.INPUT);
            writeSlotGroup(writer, "outputs", builder.slots, RecipeIngredientRole.OUTPUT);
            writer.endObject();
            index++;
        }
        writer.endArray();
        writer.endObject();
        return recipes.size();
    }

    @SuppressWarnings("rawtypes")
    private static ResourceLocation recipeId(IRecipeCategory category, Object recipe) {
        try {
            return category.getRegistryName(recipe);
        } catch (Throwable t) {
            return null;
        }
    }

    private static void writeSlotGroup(JsonWriter writer, String name,
                                       List<CapturingRecipeLayoutBuilder.CapturedSlot> slots,
                                       RecipeIngredientRole role) throws IOException {
        writer.name(name).beginArray();
        for (CapturingRecipeLayoutBuilder.CapturedSlot slot : slots) {
            if (slot.role != role || slot.ingredients.isEmpty()) {
                continue;
            }
            writer.beginObject();
            if (slot.name != null && !slot.name.isEmpty()) {
                writer.name("name").value(slot.name);
            }
            writer.name("ingredients").beginArray();
            for (ITypedIngredient<?> ingredient : slot.ingredients) {
                writeIngredient(writer, ingredient);
            }
            writer.endArray();
            writer.endObject();
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
