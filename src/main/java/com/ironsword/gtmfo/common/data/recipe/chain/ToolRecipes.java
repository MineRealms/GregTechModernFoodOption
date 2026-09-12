package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.ironsword.gtmfo.common.data.GTMFOTools;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

/**
 * Rolling pin crafting recipes (wood uses planks, polymers use plates).
 */
public class ToolRecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        rollingPin(provider, "wood", GTMaterials.Wood, ChemicalHelper.get(TagPrefix.planks, GTMaterials.Wood));
        rollingPin(provider, "rubber", GTMaterials.Rubber, ChemicalHelper.get(TagPrefix.plate, GTMaterials.Rubber));
        rollingPin(provider, "polyethylene", GTMaterials.Polyethylene,
                ChemicalHelper.get(TagPrefix.plate, GTMaterials.Polyethylene));
        rollingPin(provider, "polytetrafluoroethylene", GTMaterials.Polytetrafluoroethylene,
                ChemicalHelper.get(TagPrefix.plate, GTMaterials.Polytetrafluoroethylene));
    }

    private static void rollingPin(Consumer<FinishedRecipe> provider, String materialName,
                                   com.gregtechceu.gtceu.api.data.chemical.material.Material material,
                                   ItemStack plate){
        var entry = GTMFOTools.ROLLING_PINS.get(material);
        if (entry == null) return;
        VanillaRecipeHelper.addShapedRecipe(provider, id("rolling_pin_" + materialName),
                entry.asStack(),
                "  R",
                " P ",
                "R f",
                'P', plate.copy(),
                'R', ChemicalHelper.get(TagPrefix.rod, GTMaterials.Iron));
    }
}
