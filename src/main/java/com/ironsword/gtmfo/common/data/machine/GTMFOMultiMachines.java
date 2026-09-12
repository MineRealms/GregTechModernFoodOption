package com.ironsword.gtmfo.common.data.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.mojang.datafixers.util.Pair;
import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.common.data.GTMFOBlocks;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import com.ironsword.gtmfo.common.machine.GreenhouseMachine;
import com.ironsword.gtmfo.common.machine.PrimitiveBakingOvenMachine;
import com.ironsword.gtmfo.common.machine.kitchen.KitchenMachine;

import static com.gregtechceu.gtceu.api.pattern.util.RelativeDirection.*;
import static com.ironsword.gtmfo.common.registry.GTMFORegistries.REGISTRATE;

public class GTMFOMultiMachines {

    public static final MultiblockMachineDefinition PRIMITIVE_BAKING_OVEN = REGISTRATE
            .multiblock("primitive_baking_oven", PrimitiveBakingOvenMachine::new)
            .langValue("Primitive Baking Oven")
            .rotationState(RotationState.ALL)
            .recipeType(GTMFORecipeTypes.BAKING_OVEN_RECIPES)
            .appearanceBlock(GTMFOBlocks.ADOBE_BRICKS)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("XXX", "XXX")
                    .aisle("XFX", "X#X")
                    .aisle("XYX", "XXX")
                    .where('X', Predicates.blocks(GTMFOBlocks.ADOBE_BRICKS.get()))
                    .where('F', Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.Iron))
                            .or(Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt, GTMaterials.Bronze))))
                    .where('#', Predicates.air())
                    .where('Y', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .build())
            .workableCasingModel(
                    GregTechModernFoodOption.id("block/adobe_bricks"),
                    GTCEu.id("block/machines/baking_oven"))
            .register();

    public static final MultiblockMachineDefinition ELECTRIC_BAKING_OVEN = REGISTRATE
            .multiblock("electric_baking_oven", WorkableElectricMultiblockMachine::new)
            .langValue("Electric Baking Oven")
            .rotationState(RotationState.ALL)
            .recipeType(GTMFORecipeTypes.BAKING_OVEN_RECIPES)
            .appearanceBlock(GTMFOBlocks.BISMUTH_BRONZE_CASING)
            .pattern(definition-> FactoryBlockPattern.start(BACK, UP, RIGHT)
                    .aisle("XXXX", "YXXX", "XXXX", "####")
                    .aisle("XXXX", "GFFX", "GOOX", "XXXX").setRepeatable(2, 14)
                    .aisle("XXXX", "XXXX", "XXXX", "####")
                    .where('X', Predicates.blocks(GTMFOBlocks.BISMUTH_BRONZE_CASING.get()).setMinGlobalLimited(10).or(Predicates.autoAbilities(definition.getRecipeTypes())))
                    .where('F', Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt,GTMaterials.Steel)))
                    .where('G', Predicates.blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                    .where('#', Predicates.any())
                    .where('O', Predicates.air())
                    .where('Y', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .build())
            .workableCasingModel(
                    GregTechModernFoodOption.id("block/bismuth_bronze_casing"),
                    GTCEu.id("block/machines/baking_oven"))
            .register();

    public static final MultiblockMachineDefinition STEAM_BAKING_OVEN = REGISTRATE
            .multiblock("steam_baking_oven", WorkableElectricMultiblockMachine::new)
            .langValue("Steam Baking Oven")
            .rotationState(RotationState.ALL)
            .recipeType(GTMFORecipeTypes.BAKING_OVEN_RECIPES)
            .appearanceBlock(GTBlocks.CASING_BRONZE_BRICKS)
            .pattern(definition->FactoryBlockPattern.start()
                    .aisle("XXXX", "XGGX", "XXXX")
                    .aisle("XXXX", "GFFG", "XFFX")
                    .aisle("XXXX", "GFFG", "XFFX")
                    .aisle("XXXX", "YGGX", "XXXX")
                    .where('X',Predicates.blocks(GTBlocks.CASING_BRONZE_BRICKS.get())
                            .or(Predicates.abilities(PartAbility.STEAM_IMPORT_ITEMS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.STEAM_EXPORT_ITEMS).setPreviewCount(1))
                            .or(Predicates.abilities(PartAbility.STEAM).setExactLimit(1)))
                    .where('F',Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt,GTMaterials.Steel)))
                    .where('G',Predicates.blocks(GTBlocks.CASING_BRONZE_BRICKS.get(),GTBlocks.CASING_TEMPERED_GLASS.get()))
                    .where('Y',Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .build())
            .workableCasingModel(
                    GTCEu.id("block/casings/solid/machine_casing_bronze_plated_bricks"),
                    GTCEu.id("block/machines/baking_oven"))
            .register();

    public static final MultiblockMachineDefinition GREENHOUSE = REGISTRATE
            .multiblock("greenhouse", GreenhouseMachine::new)
            .langValue("Greenhouse")
            .rotationState(RotationState.ALL)
            .recipeType(GTMFORecipeTypes.GREENHOUSE_RECIPES)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("CCCCCCC", "GGGGGGG", "GGGGGGG", "GGGGGGG", "GGGGGGG", "GGGGGGG", "GGGGGGG", "GGGGGGG", "GGGGGGG")
                    .aisle("CDDDDDC", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "GGGGGGG")
                    .aisle("CDDDDDC", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "GGGGGGG")
                    .aisle("CDDDDDC", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "GGGGGGG")
                    .aisle("CDDDDDC", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "GGGGGGG")
                    .aisle("CDDDDDC", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "G#####G", "GGGGGGG")
                    .aisle("CCCYCCC", "GGGGGGG", "GGGGGGG", "GGGGGGG", "GGGGGGG", "GGGGGGG", "GGGGGGG", "GGGGGGG", "GGGGGGG")
                    .where('C', Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .setMinGlobalLimited(20)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes())))
                    .where('G', Predicates.blocks(GTMFOBlocks.GREENHOUSE_GLASS.get()))
                    .where('D', Predicates.blocks(net.minecraft.world.level.block.Blocks.DIRT,
                            net.minecraft.world.level.block.Blocks.GRASS_BLOCK))
                    .where('#', Predicates.air())
                    .where('Y', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .build())
            .workableCasingModel(
                    GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/machines/greenhouse"))
            .register();

    public static final MultiblockMachineDefinition KITCHEN = REGISTRATE
            .multiblock("kitchen", KitchenMachine::new)
            .langValue("Kitchen")
            .rotationState(RotationState.ALL)
            .appearanceBlock(GTBlocks.CASING_STEEL_PIPE)
            .pattern(definition -> FactoryBlockPattern.start(BACK, UP, RIGHT)
                    .aisle("BBBBB", "     ")
                    .aisle("BFFFB", " III ")
                    .aisle("BFFFB", " III ")
                    .aisle("BFFFB", " III ")
                    .aisle("BFFFB", " III ")
                    .aisle("BBSBB", "     ")
                    .where('S', Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where('B', Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get())
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS, PartAbility.EXPORT_ITEMS,
                                    PartAbility.IMPORT_FLUIDS, PartAbility.EXPORT_FLUIDS,
                                    PartAbility.INPUT_ENERGY)))
                    .where('F', Predicates.blocks(GTMFOBlocks.PORCELAIN_TILE.get(),
                            GTMFOBlocks.DARK_PORCELAIN_TILE.get()))
                    .where('I', Predicates.any())
                    .build())
            .workableCasingModel(
                    GTCEu.id("block/casings/pipe/machine_casing_pipe_steel"),
                    GTCEu.id("block/multiblock/electric_blast_furnace"))
            .register();

    public static void init(){
        GTMFOMachines.addJEILang("baking_oven","Baking Oven","烤炉");
        GTMFOMachines.CNLangMap.put("block."+GregTechModernFoodOption.MODID+".primitive_baking_oven","原始烤炉");
        GTMFOMachines.CNLangMap.put("block."+GregTechModernFoodOption.MODID+".electric_baking_oven","电力烤炉");
        GTMFOMachines.CNLangMap.put("block."+GregTechModernFoodOption.MODID+".steam_baking_oven","蒸汽烤炉");
        GTMFOMachines.CNLangMap.put("block."+GregTechModernFoodOption.MODID+".greenhouse","温室");
        GTMFOMachines.CNLangMap.put("block."+GregTechModernFoodOption.MODID+".kitchen","厨房");

        GTMFOMachines.JEILangPairMap.put("gtmfo.machine.kitchen.target", Pair.of("Target: %s", "目标：%s"));
        GTMFOMachines.JEILangPairMap.put("gtmfo.machine.kitchen.none", Pair.of("None", "无"));
        GTMFOMachines.JEILangPairMap.put("gtmfo.machine.kitchen.order", Pair.of("Order size: %s", "订单数量：%s"));
        GTMFOMachines.JEILangPairMap.put("gtmfo.machine.kitchen.machines", Pair.of("Machines: %s", "机器数：%s"));
        GTMFOMachines.JEILangPairMap.put("gtmfo.machine.kitchen.dirtiness", Pair.of("Dirtiness: %s", "脏污度：%s"));
        GTMFOMachines.JEILangPairMap.put("gtmfo.machine.kitchen.order_button", Pair.of("Change order size", "调整订单数量"));
        GTMFOMachines.JEILangPairMap.put("gtmfo.machine.kitchen.status.probably_fine", Pair.of("Status: Working", "状态：运行中"));
        GTMFOMachines.JEILangPairMap.put("gtmfo.machine.kitchen.status.order_complete", Pair.of("Status: Order complete", "状态：订单完成"));
        GTMFOMachines.JEILangPairMap.put("gtmfo.machine.kitchen.status.no_ingredients", Pair.of("Status: Missing ingredients", "状态：缺少原料"));
        GTMFOMachines.JEILangPairMap.put("gtmfo.machine.kitchen.status.machines_not_working", Pair.of("Status: Not enough energy", "状态：能量不足"));
        GTMFOMachines.JEILangPairMap.put("gtmfo.machine.kitchen.status.no_recipe", Pair.of("Status: No recipe for target", "状态：目标无配方"));
        GTMFOMachines.JEILangPairMap.put("gtmfo.machine.kitchen.status.hatches_full", Pair.of("Status: Fluid hatches full", "状态：流体仓已满"));
        GTMFOMachines.JEILangPairMap.put("gtmfo.machine.kitchen.status.buses_full", Pair.of("Status: Item buses full", "状态：物品总线已满"));
        GTMFOMachines.JEILangPairMap.put("gtmfo.machine.kitchen.status.bad_machines", Pair.of("Status: Missing machines", "状态：缺少机器"));
    }
}
