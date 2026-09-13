package com.ironsword.gtmfo.common;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.ironsword.gtmfo.GTMFOConfigHolder;
import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.api.capability.forge.GTMFOCapability;
import com.ironsword.gtmfo.common.data.*;
import com.ironsword.gtmfo.common.data.machine.GTMFOMachines;
import com.ironsword.gtmfo.common.data.machine.GTMFOMultiMachines;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.material.GTMFOMaterials;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import com.ironsword.gtmfo.common.registry.GTMFORegistries;
import com.ironsword.gtmfo.data.GTMFODataGen;
import com.ironsword.gtmfo.data.GTMFOProviderTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = GregTechModernFoodOption.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy {

    public CommonProxy(IEventBus bus){
        init(bus);
    }

    public static void init(IEventBus bus){
        GTMFOProviderTypes.init();

        GTMFOConfigHolder.init();

        GTMFOCreativeModeTabs.init();
        GTMFOBlocks.init();
        GTMFOItems.init();
        GTMFOCrops.init();
        GTMFOTrees.init();
        // NOTE: GTMFOTools / GTMFOCovers touch GTCEu's gated registries (GTSoundEntries via GTToolType,
        // GTRegistries.COVERS); they are initialised in GTMFOAddon.initializeAddon() instead, which GTCEu
        // calls at the end of its own construction.
        com.ironsword.gtmfo.common.machine.farmer.FarmerModeRegistry.registerDefaultModes();
        GTMFOEntities.init(bus);
        com.ironsword.gtmfo.common.worldgen.GTMFOBiomeModifiers.init(bus);
        com.ironsword.gtmfo.common.worldgen.GTFOFeaturePlacement.init(bus);

        bus.addGenericListener(MachineDefinition.class,CommonProxy::registerMachines);
        bus.addGenericListener(GTRecipeType.class,CommonProxy::registerRecipeTypes);

        GTMFODataGen.init();

        GTMFORegistries.REGISTRATE.registerRegistrate();

        GTMFOEffects.init(bus);
        GTMFOSounds.init(bus);
        GTMFOParticles.init(bus);
        GTMFOLootModifiers.init(bus);
        GTMFODataGen.initEntityLang();
        GTMFODataGen.initDamageTypeLang();
        GTMFODataGen.initTooltipLang();
        GTMFODataGen.initTabLang();
    }

    public static void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event){
        GTMFOMachines.init();
        GTMFOMultiMachines.init();
        GTMFODataGen.initMachineLang();
    }

    public static void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event){
        GTMFORecipeTypes.init();
        // recipe table capacity adjustments (original CommonProxy.preLoad)
        com.gregtechceu.gtceu.common.data.GTRecipeTypes.BREWING_RECIPES.setMaxIOSize(1, 1, 1, 1);
        com.gregtechceu.gtceu.common.data.GTRecipeTypes.EXTRACTOR_RECIPES.setMaxIOSize(2, 1, 0, 1);
        com.gregtechceu.gtceu.common.data.GTRecipeTypes.FERMENTING_RECIPES.setMaxIOSize(1, 1, 1, 1);
        com.gregtechceu.gtceu.common.data.GTRecipeTypes.COMPRESSOR_RECIPES.setMaxIOSize(1, 1, 1, 1);
    }

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        GTMFOCapability.register(event);
    }

    @SubscribeEvent
    public static void registerMaterials(MaterialEvent event){
        GTMFOFluids.init();
        GTMFOMaterials.init();
        GTMFODataGen.initMaterialLang();
    }

    @SubscribeEvent
    public static void commonSetup(net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent event){
        event.enqueueWork(GTMFOCreativeModeTabs::assignTabs);
        event.enqueueWork(GTFODungeonLoot::init);
        // registry objects (effects) are only available after the registry events, so lacing entries are built here
        event.enqueueWork(GTMFOLacing::init);
    }
}
