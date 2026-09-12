package com.ironsword.gtmfo.integration.jei;

import com.ironsword.gtmfo.GregTechModernFoodOption;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * JEI category showing lacing recipes: lacing dust + food -> laced food carrying the effect
 * (original {@code LacingCategory}).
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
        return Component.translatable("gtmfo.jei.lacing");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return 120;
    }

    @Override
    public int getHeight() {
        return 40;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, LacingInfo recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 6, 12).addItemStack(recipe.food());
        builder.addSlot(RecipeIngredientRole.INPUT, 30, 12).addItemStack(recipe.lacingItem());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 12).addItemStack(recipe.lacedFood());
    }

    @Override
    public void draw(LacingInfo recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics graphics, double mouseX,
                     double mouseY) {
        var font = Minecraft.getInstance().font;
        graphics.drawString(font, "+", 50, 17, 0x404040, false);
        graphics.drawString(font, "\u2192", 60, 17, 0x404040, false);
        graphics.drawString(font, Component.translatable("gtmfo.jei.lacing.effect",
                recipe.effectName()), 6, 30, 0x606060, false);
    }

    /** One lacing combination. */
    public record LacingInfo(ItemStack food, ItemStack lacingItem, ItemStack lacedFood, String effectName) {

        public static List<LacingInfo> collect() {
            List<LacingInfo> list = new ArrayList<>();
            // representative foods that the lacing recipes are generated for
            List<ItemStack> foods = List.of(
                    com.ironsword.gtmfo.common.data.GTMFOItems.BREAD_SLICE.asStack(),
                    com.ironsword.gtmfo.common.data.GTMFOItems.TOAST.asStack(),
                    com.ironsword.gtmfo.common.data.GTMFOItems.BUN.asStack(),
                    com.ironsword.gtmfo.common.data.GTMFOItems.PIZZA_CHEESE_SLICE.asStack(),
                    com.ironsword.gtmfo.common.data.GTMFOItems.BURGER_STEAK.asStack(),
                    com.ironsword.gtmfo.common.data.GTMFOItems.PORCHETTA_SLICE.asStack());
            for (int i = 0; i < GTMFOLacing.ENTRIES.size(); i++) {
                var entry = GTMFOLacing.ENTRIES.get(i);
                for (ItemStack food : foods) {
                    ItemStack laced = food.copyWithCount(1);
                    CompoundTag tag = laced.getOrCreateTag();
                    tag.putInt(GTMFOLacing.NBT_KEY, i);
                    list.add(new LacingInfo(food.copyWithCount(1), entry.lacingItem().copy(), laced,
                            entry.effect().getDisplayName().getString()));
                }
            }
            return list;
        }
    }
}
