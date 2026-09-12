package com.ironsword.gtmfo.integration.jei;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.api.mixin.INutrients;
import com.ironsword.gtmfo.api.item.component.GTMFOFoodStats;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.tterrag.registrate.util.entry.ItemEntry;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JEI category showing hunger / saturation / effects / nutrients of GTFO foods
 * (original {@code EatingRecipeCategory}).
 */
public class FoodInfoCategory implements IRecipeCategory<FoodInfoCategory.FoodInfo> {

    public static final RecipeType<FoodInfo> TYPE = new RecipeType<>(GregTechModernFoodOption.id("food_info"),
            FoodInfo.class);

    private final IDrawable icon;

    public FoodInfoCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, GTMFOItems.CHIPS_BAG.asStack());
    }

    @Override
    public int getWidth() {
        return 150;
    }

    @Override
    public int getHeight() {
        return 70;
    }

    @Override
    public RecipeType<FoodInfo> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gtmfo.jei.food_info");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, FoodInfo recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 6).addItemStack(recipe.stack());
    }

    @Override
    public void draw(FoodInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX,
                     double mouseY) {
        var font = Minecraft.getInstance().font;
        int y = 6;
        graphics.drawString(font, recipe.stack().getHoverName(), 30, y, 0x404040, false);
        y += 12;
        graphics.drawString(font, Component.translatable("gtmfo.jei.food_info.hunger", recipe.hunger()), 6, y,
                0x404040, false);
        y += 10;
        graphics.drawString(font,
                Component.translatable("gtmfo.jei.food_info.saturation", String.format("%.1f", recipe.saturation())),
                6, y, 0x404040, false);
        y += 10;
        if (!recipe.effects().isEmpty()) {
            graphics.drawString(font, Component.translatable("gtmfo.jei.food_info.effects"), 6, y, 0x404040, false);
            y += 10;
            for (MobEffectInstance effect : recipe.effects()) {
                String line = " - " + effect.getEffect().getDisplayName().getString() + " "
                        + (effect.getAmplifier() + 1) + " (" + (effect.getDuration() / 20) + "s)";
                graphics.drawString(font, line, 6, y, 0x606060, false);
                y += 10;
            }
        }
        if (!recipe.nutrients().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            recipe.nutrients().forEach((key, value) -> sb.append(key).append(" ").append(value).append("  "));
            graphics.drawString(font, sb.toString().trim(), 6, y, 0x606060, false);
        }
    }

    /** One entry: a food stack plus its stats. */
    public record FoodInfo(ItemStack stack, int hunger, float saturation, List<MobEffectInstance> effects,
                           Map<String, Float> nutrients) {

        public static List<FoodInfo> collect() {
            List<FoodInfo> list = new ArrayList<>();
            for (var entry : com.ironsword.gtmfo.common.registry.GTMFORegistries.REGISTRATE
                    .getAll(net.minecraft.core.registries.Registries.ITEM)) {
                ItemStack stack = new ItemStack(entry.get());
                if (!stack.isEdible()) continue;
                FoodProperties food = stack.getFoodProperties(null);
                if (food == null) continue;
                List<MobEffectInstance> effects = new ArrayList<>();
                for (var pair : food.getEffects()) {
                    effects.add(pair.getFirst());
                }
                Map<String, Float> nutrients = new java.util.LinkedHashMap<>();
                if (food instanceof INutrients nutrientsAccess) {
                    nutrientsAccess.getNutrients().forEach((k, v) -> nutrients.put(k, (float) v));
                }
                list.add(new FoodInfo(stack, food.getNutrition(), food.getSaturationModifier(), effects, nutrients));
            }
            return list;
        }
    }
}
