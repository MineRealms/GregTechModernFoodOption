package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.StringTag;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

/**
 * Mob extractor recipes: entity id stored in {@code mob_on_top}, optional damage in {@code cause_damage}.
 */
public class MobExtractionRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        mob(provider, "milk_from_cow", 3, "minecraft:cow", GTMaterials.Milk.getFluid(10), 0f);
        mob(provider, "mushroom_soup_from_mooshroom", 2, "minecraft:mooshroom",
                GTMFOFluids.MushroomSoup.getFluid(1), 0f);
        mob(provider, "blood_from_cow", 4, "minecraft:cow", GTMFOFluids.Blood.getFluid(10), 0.5f);
        mob(provider, "blood_from_chicken", 5, "minecraft:chicken", GTMFOFluids.Blood.getFluid(1), 0.5f);
        mob(provider, "blood_from_sheep", 6, "minecraft:sheep", GTMFOFluids.Blood.getFluid(5), 0.5f);
        mob(provider, "blood_from_pig", 7, "minecraft:pig", GTMFOFluids.Blood.getFluid(5), 0.5f);
        mob(provider, "blood_from_villager", 8, "minecraft:villager", GTMFOFluids.Blood.getFluid(100), 0.5f);
        mob(provider, "blood_from_player", 9, "minecraft:player", GTMFOFluids.Blood.getFluid(200), 1.5f);
        mob(provider, "glue_from_horse", 10, "minecraft:horse", GTMaterials.Glue.getFluid(100), 1f);
    }

    private static void mob(Consumer<FinishedRecipe> provider, String name, int circuit, String entityId,
                            FluidStack output, float damage){
        var builder = GTMFORecipeTypes.MOB_EXTRACTOR_RECIPES.recipeBuilder(id(name))
                .circuitMeta(circuit)
                .addData("mob_on_top", StringTag.valueOf(entityId))
                .outputFluids(output)
                .EUt(16).duration(20);
        if (damage > 0) {
            builder.addData("cause_damage", FloatTag.valueOf(damage));
        }
        builder.save(provider);
    }
}
