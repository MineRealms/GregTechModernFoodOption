package com.ironsword.gtmfo.integration.jei.export;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IIngredientAcceptor;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotRichTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.widgets.ISlottedWidgetFactory;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Lightweight {@link IRecipeLayoutBuilder} used by {@link JeiRecipeExporter}.
 * <p>
 * It records exactly the slots and ingredients a category adds through the JEI API and ignores
 * everything GUI related (positions, backgrounds, tooltips, renderers). This avoids creating any
 * drawables/widgets, so exporting tens of thousands of recipes stays cheap.
 */
public final class CapturingRecipeLayoutBuilder implements IRecipeLayoutBuilder {

    public static final class CapturedSlot {

        public final RecipeIngredientRole role;
        public String name;
        public final List<ITypedIngredient<?>> ingredients = new ArrayList<>();

        CapturedSlot(RecipeIngredientRole role) {
            this.role = role;
        }
    }

    private final IIngredientManager ingredientManager;
    public final List<CapturedSlot> slots = new ArrayList<>();

    public CapturingRecipeLayoutBuilder(IIngredientManager ingredientManager) {
        this.ingredientManager = ingredientManager;
    }

    private void addTyped(CapturedSlot slot, Object value) {
        if (value == null) return;
        if (value instanceof ItemStack stack && stack.isEmpty()) return;
        if (value instanceof FluidStack fluid && fluid.isEmpty()) return;
        ingredientManager.createTypedIngredient(value).ifPresent(slot.ingredients::add);
    }

    @Override
    public IRecipeSlotBuilder addSlot(RecipeIngredientRole role) {
        return new SlotBuilder(new CapturedSlot(role));
    }

    @Override
    public IRecipeSlotBuilder addSlotToWidget(RecipeIngredientRole role, ISlottedWidgetFactory<?> widgetFactory) {
        return new SlotBuilder(new CapturedSlot(role));
    }

    @Override
    public IIngredientAcceptor<?> addInvisibleIngredients(RecipeIngredientRole role) {
        return new SlotBuilder(new CapturedSlot(role));
    }

    @Override
    public void moveRecipeTransferButton(int posX, int posY) {}

    @Override
    public void setShapeless() {}

    @Override
    public void setShapeless(int posX, int posY) {}

    @Override
    public void createFocusLink(IIngredientAcceptor<?>... slots) {}

    private final class SlotBuilder implements IRecipeSlotBuilder {

        private final CapturedSlot slot;

        SlotBuilder(CapturedSlot slot) {
            this.slot = slot;
            slots.add(slot);
        }

        // ===== IIngredientAcceptor =====

        @Override
        public <I> IRecipeSlotBuilder addIngredients(IIngredientType<I> type, List<I> ingredients) {
            for (I value : ingredients) {
                addTyped(slot, value);
            }
            return this;
        }

        @Override
        public <I> IRecipeSlotBuilder addIngredient(IIngredientType<I> type, I value) {
            addTyped(slot, value);
            return this;
        }

        @Override
        public IRecipeSlotBuilder addIngredientsUnsafe(List<?> ingredients) {
            for (Object value : ingredients) {
                addTyped(slot, value);
            }
            return this;
        }

        @Override
        public IRecipeSlotBuilder addTypedIngredients(List<ITypedIngredient<?>> ingredients) {
            ingredients.stream().filter(Objects::nonNull).forEach(slot.ingredients::add);
            return this;
        }

        @Override
        public IRecipeSlotBuilder addOptionalTypedIngredients(List<Optional<ITypedIngredient<?>>> ingredients) {
            ingredients.forEach(optional -> optional.ifPresent(slot.ingredients::add));
            return this;
        }

        @Override
        public IRecipeSlotBuilder addFluidStack(Fluid fluid) {
            return addFluidStack(fluid, 1000L, null);
        }

        @Override
        public IRecipeSlotBuilder addFluidStack(Fluid fluid, long amount) {
            return addFluidStack(fluid, amount, null);
        }

        @Override
        public IRecipeSlotBuilder addFluidStack(Fluid fluid, long amount, CompoundTag tag) {
            FluidStack stack = new FluidStack(fluid, (int) Math.min(Integer.MAX_VALUE, Math.max(0L, amount)), tag);
            ingredientManager.createTypedIngredient(ForgeTypes.FLUID_STACK, stack).ifPresent(slot.ingredients::add);
            return this;
        }

        // ===== IPlaceable =====

        @Override
        public IRecipeSlotBuilder setPosition(int posX, int posY) {
            return this;
        }

        @Override
        public int getWidth() {
            return 18;
        }

        @Override
        public int getHeight() {
            return 18;
        }

        // ===== IRecipeSlotBuilder =====

        @Override
        public IRecipeSlotBuilder addTooltipCallback(IRecipeSlotTooltipCallback callback) {
            return this;
        }

        @Override
        public IRecipeSlotBuilder addRichTooltipCallback(IRecipeSlotRichTooltipCallback callback) {
            return this;
        }

        @Override
        public IRecipeSlotBuilder setSlotName(String name) {
            slot.name = name;
            return this;
        }

        @Override
        public IRecipeSlotBuilder setStandardSlotBackground() {
            return this;
        }

        @Override
        public IRecipeSlotBuilder setOutputSlotBackground() {
            return this;
        }

        @Override
        public IRecipeSlotBuilder setBackground(IDrawable background, int xOffset, int yOffset) {
            return this;
        }

        @Override
        public IRecipeSlotBuilder setOverlay(IDrawable overlay, int xOffset, int yOffset) {
            return this;
        }

        @Override
        public IRecipeSlotBuilder setFluidRenderer(long capacity, boolean showCapacity, int width, int height) {
            return this;
        }

        @Override
        public <T> IRecipeSlotBuilder setCustomRenderer(IIngredientType<T> type, IIngredientRenderer<T> renderer) {
            return this;
        }
    }
}
