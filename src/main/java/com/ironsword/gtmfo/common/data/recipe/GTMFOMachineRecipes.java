package com.ironsword.gtmfo.common.data.recipe;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.*;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.gregtechceu.gtceu.data.recipe.GTCraftingComponents;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.gregtechceu.gtceu.data.recipe.misc.MetaTileEntityLoader;
import com.ironsword.gtmfo.common.data.GTMFOBlocks;
import com.ironsword.gtmfo.common.data.machine.GTMFOMachines;
import com.ironsword.gtmfo.common.data.machine.GTMFOMultiMachines;
import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class GTMFOMachineRecipes {

    /** Dense plate component (GTCEu 1.12's DENSE_PLATE, absent from 7.5.2's GTCraftingComponents). */
    public static final com.gregtechceu.gtceu.data.recipe.CraftingComponent DENSE_PLATE =
            com.gregtechceu.gtceu.data.recipe.CraftingComponent.of("dense_plate", TagPrefix.plateDense, GTMaterials.Iron)
                    .add(com.gregtechceu.gtceu.api.GTValues.ULV, TagPrefix.plateDense, GTMaterials.WroughtIron)
                    .add(com.gregtechceu.gtceu.api.GTValues.LV, TagPrefix.plateDense, GTMaterials.Steel)
                    .add(com.gregtechceu.gtceu.api.GTValues.MV, TagPrefix.plateDense, GTMaterials.Aluminium)
                    .add(com.gregtechceu.gtceu.api.GTValues.HV, TagPrefix.plateDense, GTMaterials.StainlessSteel)
                    .add(com.gregtechceu.gtceu.api.GTValues.EV, TagPrefix.plateDense, GTMaterials.Titanium)
                    .add(com.gregtechceu.gtceu.api.GTValues.IV, TagPrefix.plateDense, GTMaterials.TungstenSteel)
                    .add(com.gregtechceu.gtceu.api.GTValues.LuV, TagPrefix.plateDense,
                            GTMaterials.RhodiumPlatedPalladium)
                    .add(com.gregtechceu.gtceu.api.GTValues.ZPM, TagPrefix.plateDense, GTMaterials.NaquadahAlloy)
                    .add(com.gregtechceu.gtceu.api.GTValues.UV, TagPrefix.plateDense, GTMaterials.Darmstadtium)
                    .add(com.gregtechceu.gtceu.api.GTValues.UHV, TagPrefix.plateDense, GTMaterials.Neutronium);
    public static void init(Consumer<FinishedRecipe> provider){
        MetaTileEntityLoader.registerMachineRecipe(provider, GTMFOMachines.SLICER,
                "PCA",
                "SHC",
                "LOA",
                'P', GTCraftingComponents.PISTON,
                'C', GTCraftingComponents.CIRCUIT,
                'A', GTCraftingComponents.CABLE,
                'S', GTCraftingComponents.SAWBLADE,
                'H', GTCraftingComponents.HULL,
                'L', DENSE_PLATE,
                'O', GTCraftingComponents.CONVEYOR);

        MetaTileEntityLoader.registerMachineRecipe(provider, GTMFOMachines.CUISINE_ASSEMBLER,
                "AOC",
                "RHR",
                "AOC",
                'C', GTCraftingComponents.CIRCUIT,
                'A', GTCraftingComponents.CABLE,
                'R', GTCraftingComponents.ROBOT_ARM,
                'H', GTCraftingComponents.HULL,
                'O', GTCraftingComponents.CONVEYOR);

        MetaTileEntityLoader.registerMachineRecipe(provider, GTMFOMachines.MICROWAVE,
                "LAC",
                "LHE",
                "LMC",
                'H', GTCraftingComponents.HULL,
                'M', GTCraftingComponents.MOTOR,
                'E', GTCraftingComponents.EMITTER,
                'C', GTCraftingComponents.CIRCUIT,
                'A', GTCraftingComponents.CABLE,
                'L', ChemicalHelper.get(TagPrefix.plate, GTMaterials.Lead));

        VanillaRecipeHelper.addShapedRecipe(provider,id("steam_baking_oven"),
                GTMFOMultiMachines.STEAM_BAKING_OVEN.asStack(),
                "dSG",
                "PAR",
                "fSG",
                'S', ChemicalHelper.get(TagPrefix.screw,GTMaterials.Steel),
                'P', GTBlocks.CASING_BRONZE_BRICKS.asStack(),
                'R', ChemicalHelper.get(TagPrefix.pipeSmallFluid,GTMaterials.Bronze),
                'G', ChemicalHelper.get(TagPrefix.gear,GTMaterials.Invar),
                'A', GTMachines.STEAM_FURNACE.first().asStack());

        VanillaRecipeHelper.addShapedRecipe(provider,id("electric_baking_oven"),
                GTMFOMultiMachines.ELECTRIC_BAKING_OVEN.asStack(),
                "CPC",
                "IWI",
                "CAC",
                'C', GTMFOBlocks.BISMUTH_BRONZE_CASING.asStack(),
                'P', GTItems.ELECTRIC_PUMP_MV.asStack(),
                'I', CustomTags.MV_CIRCUITS,
                'W', ChemicalHelper.get(TagPrefix.wireGtQuadruple,GTMaterials.Cupronickel),
                'A', GTBlocks.COIL_CUPRONICKEL.asStack());


        //bismuth_bronze_casing
        VanillaRecipeHelper.addShapedRecipe(provider,true, id("bismuth_bronze_casing"),
                GTMFOBlocks.BISMUTH_BRONZE_CASING.asStack(),
                "PhP",
                "PFP",
                "PwP",
                'P',ChemicalHelper.get(TagPrefix.plate,GTMaterials.BismuthBronze),
                'F',ChemicalHelper.get(TagPrefix.frameGt,GTMaterials.BismuthBronze));

        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(id("bismuth_bronze_casing"))
                .inputItems(TagPrefix.plate,GTMaterials.BismuthBronze,6)
                .inputItems(TagPrefix.frameGt,GTMaterials.BismuthBronze)
                .circuitMeta(6)
                .outputItems(GTMFOBlocks.BISMUTH_BRONZE_CASING.asStack())
                .EUt(16).duration(50).addMaterialInfo(true).save(provider);

    }
}
