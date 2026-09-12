package com.ironsword.gtmfo.common.data.recipe;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import com.ironsword.gtmfo.common.machine.ElectricBakingOvenMachine;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

/**
 * Faithful port of the original {@code GTFOUtils.addBakingOvenRecipes}.
 * <p>
 * For every food this registers:
 * <ul>
 * <li>a fuel recipe (input + wood planks) carrying the baking temperature, for the primitive/steam ovens;</li>
 * <li>x4 fuel recipes with coal/charcoal (gem or dust) and x8 recipes with anthracite;</li>
 * <li>an electric baking oven recipe with a quarter of the duration and no fuel
 * (original {@code onRecipeBuild} of {@code BAKING_OVEN_RECIPES}).</li>
 * </ul>
 */
public class GTMFOBakingOvenRecipes {

    public static void add(Consumer<FinishedRecipe> provider, String name, ItemStack input, ItemStack output,
                           int duration, int temperature, int fuelAmount) {
        addFuelRecipe(provider, name, input, output, duration, temperature, fuelAmount * 2);
        addFuelRecipe(provider, name + "_x4_coal", scale(input, 4), scale(output, 4), duration, -1,
                Math.max(fuelAmount, 1), Items.COAL);
        addFuelRecipe(provider, name + "_x4_charcoal", scale(input, 4), scale(output, 4), duration, -1,
                Math.max(fuelAmount, 1), Items.CHARCOAL);
        // NOTE: the original also had x8 anthracite variants, but GTCEu Modern has no anthracite item.

        // electric baking oven: quarter duration, no fuel, temperature property
        GTMFORecipeTypes.ELECTRIC_BAKING_OVEN_RECIPES.recipeBuilder(id(name + "_electric"))
                .inputItems(input.copy())
                .outputItems(output.copy())
                .duration(Math.max(1, duration / 4))
                .EUt(ElectricBakingOvenMachine.temperatureEnergyCost(temperature, 1))
                .addData(ElectricBakingOvenMachine.TEMPERATURE_KEY, temperature)
                .save(provider);
    }

    private static void addFuelRecipe(Consumer<FinishedRecipe> provider, String name, ItemStack input, ItemStack output,
                                      int duration, int temperature, int fuelAmount) {
        var builder = GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id(name))
                .inputItems(input.copy())
                .inputItems(ChemicalHelper.get(TagPrefix.planks, GTMaterials.Wood), fuelAmount)
                .outputItems(output.copy())
                .duration(duration);
        if (temperature > 0) {
            builder.addData(ElectricBakingOvenMachine.TEMPERATURE_KEY, temperature);
        }
        builder.save(provider);
    }

    private static void addFuelRecipe(Consumer<FinishedRecipe> provider, String name, ItemStack input, ItemStack output,
                                      int duration, int temperature, int fuelAmount, net.minecraft.world.item.Item fuel) {
        var builder = GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id(name))
                .inputItems(input.copy())
                .inputItems(fuel, fuelAmount)
                .outputItems(output.copy())
                .duration(duration);
        if (temperature > 0) {
            builder.addData(ElectricBakingOvenMachine.TEMPERATURE_KEY, temperature);
        }
        builder.save(provider);
    }

    private static ItemStack scale(ItemStack stack, int multiplier) {
        ItemStack copy = stack.copy();
        copy.setCount(copy.getCount() * multiplier);
        return copy;
    }
}
