package com.ironsword.gtmfo.integration.jei;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.api.item.ExComponentItem;
import com.ironsword.gtmfo.common.data.GTMFOItems;
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
 * JEI category showing what container a food leaves behind when eaten
 * (original {@code EatingRecipeCategory}: food -> substrate/container).
 */
public class EatingRecipeCategory implements IRecipeCategory<EatingRecipeCategory.EatingInfo> {

    public static final RecipeType<EatingInfo> TYPE = new RecipeType<>(GregTechModernFoodOption.id("eating"),
            EatingInfo.class);

    private final IDrawable icon;

    public EatingRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK,
                GTMFOItems.CERAMIC_PLATE_DIRTY.asStack());
    }

    @Override
    public RecipeType<EatingInfo> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        // original used the lang key "eating.output" (the code's ".name" suffix was a bug)
        return Component.translatable("eating.output");
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
        return 50;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, EatingInfo recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 46, 16).addItemStack(recipe.food());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 96, 16).addItemStack(recipe.container());
    }

    @Override
    public void draw(EatingInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX,
                     double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "\u2192", 71, 20, 0x404040, false);
    }

    /** One entry: a food stack and the container it leaves. */
    public record EatingInfo(ItemStack food, ItemStack container) {

        public static List<EatingInfo> collect() {
            List<EatingInfo> list = new ArrayList<>();
            for (var entry : com.ironsword.gtmfo.common.registry.GTMFORegistries.REGISTRATE
                    .getAll(net.minecraft.core.registries.Registries.ITEM)) {
                ItemStack stack = new ItemStack(entry.get());
                if (stack.isEmpty()) continue;
                var stats = ExComponentItem.getFoodStats(stack);
                if (stats == null) continue;
                ItemStack container = stats.getContainerStack();
                if (container.isEmpty()) continue;
                list.add(new EatingInfo(stack, container));
            }
            return list;
        }
    }
}
