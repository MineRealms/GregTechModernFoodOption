package com.ironsword.gtmfo.integration.jei;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.api.item.ExComponentItem;
import com.ironsword.gtmfo.common.data.GTMFOLacing;
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
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * JEI category showing the lacing entries: a lacing item can be applied to any GTFO food in the
 * canner, carrying an extra effect (original {@code LacingCategory} / {@code LacingInfo}).
 */
public class LacingCategory implements IRecipeCategory<LacingCategory.LacingInfo> {

    public static final RecipeType<LacingInfo> TYPE = new RecipeType<>(GregTechModernFoodOption.id("lacing"),
            LacingInfo.class);

    private final IDrawable icon;

    public LacingCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper
                        .get(com.gregtechceu.gtceu.api.data.tag.TagPrefix.dust,
                                com.ironsword.gtmfo.common.data.material.GTMFOMaterials.SodiumCyanide));
    }

    @Override
    public RecipeType<LacingInfo> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        // original used the lang key "lacing.item_list" (the code's ".name" suffix was a bug)
        return Component.translatable("lacing.item_list");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 150;
    }

    @Override
    public int getHeight() {
        return 54;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LacingInfo recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 65, 5).addItemStack(recipe.lacingItem());
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 5).addItemStacks(recipe.foods());
    }

    @Override
    public void draw(LacingInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX,
                     double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, Component.translatable("gtmfo.lacing.info.1"), 6, 28, 0x111111, false);
        graphics.drawString(font, "  " + recipe.effectName(), 6, 38, 0x111111, false);
        graphics.drawString(font, Component.translatable("gtmfo.lacing.info.2", recipe.duration()), 6, 48,
                0x111111, false);
    }

    /** One lacing entry: the lacing item plus every GTFO food it can be applied to. */
    public record LacingInfo(ItemStack lacingItem, List<ItemStack> foods, String effectName, int duration) {

        public static List<LacingInfo> collect() {
            List<ItemStack> foods = new ArrayList<>();
            for (var entry : com.ironsword.gtmfo.common.registry.GTMFORegistries.REGISTRATE
                    .getAll(net.minecraft.core.registries.Registries.ITEM)) {
                ItemStack stack = new ItemStack(entry.get());
                if (ExComponentItem.getFoodStats(stack) != null) {
                    foods.add(stack);
                }
            }

            List<LacingInfo> list = new ArrayList<>();
            for (var entry : GTMFOLacing.ENTRIES) {
                list.add(new LacingInfo(entry.lacingItem().copy(), List.copyOf(foods),
                        entry.effect().getDisplayName().getString(), entry.duration()));
            }
            return list;
        }
    }
}
