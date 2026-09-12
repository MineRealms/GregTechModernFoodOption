package com.ironsword.gtmfo.common.data.recipe;

import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.recipe.chain.*;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

public class GTMFORecipes {

    public static void init(Consumer<FinishedRecipe> provider){
        AppleRecipes.init(provider);
        BerryRecipes.init(provider);
        BreadsRecipes.init(provider);
        CapletRecipes.init(provider);
        CoreChain.init(provider);
        PotatoRecipes.init(provider);
        PizzaRecipes.init(provider);
        KebabRecipes.init(provider);
        ItalianRecipes.init(provider);
        CheeseRecipes.init(provider);
        IceCreamRecipes.init(provider);
        PurpleDrinkRecipes.init(provider);
        SorbetRecipes.init(provider);
        ChorusRecipes.init(provider);
        MicrowaveRecipes.init(provider);
        PlateRecipes.init(provider);
        RussianRecipes.init(provider);
        VanillinRecipes.init(provider);
        DyeRecipes.init(provider);
        LithiumRecipes.init(provider);
        ChocolateRecipes.init(provider);
        AlcoholRecipes.init(provider);
        SeedsRecipes.init(provider);
        CoffeeRecipes.init(provider);
        FatRecipes.init(provider);
        BananaRecipes.init(provider);
        BritishRecipes.init(provider);
        AdobeBrickRecipes.init(provider);
        TreeRecipes.init(provider);
        ToolRecipes.init(provider);
        ivBag(provider);
        MobExtractionRecipes.init(provider);
        GreenhouseRecipes.init(provider);
        SmoreRecipes.init(provider);
        if (com.ironsword.gtmfo.GTMFOConfigHolder.INSTANCE.gtfoVanillaOverridesConfig.vanillaOverrideChain) {
            VanillaOverrideRecipes.init(provider);
        }


        doughRecipes(provider);

        GTMFOMachineRecipes.init(provider);

    }

    private static void ivBag(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.ASSEMBLER_RECIPES.recipeBuilder(id("iv_bag"))
                .inputItems(TagPrefix.foil, GTMaterials.PolyvinylChloride)
                .inputItems(TagPrefix.pipeSmallItem, GTMaterials.PolyvinylChloride)
                .inputItems(TagPrefix.bolt, GTMaterials.StainlessSteel)
                .outputItems(GTMFOItems.IV_BAG.asStack())
                .EUt(30).duration(100).save(provider);
    }

    public static void remove(Consumer<ResourceLocation> consumer){
        BreadsRecipes.remove(consumer);
        // original GTFOChainsConfig.deleteBreadRecipe: removes the vanilla bread recipe
        if (com.ironsword.gtmfo.GTMFOConfigHolder.INSTANCE.gtfoChainsConfig.deleteBreadRecipe) {
            consumer.accept(ResourceLocation.tryBuild("minecraft", "bread"));
        }
        vanillaOverrides(consumer);
        // replaced by GTMFO's guaiacol-yielding version (VanillinRecipes)
        consumer.accept(com.gregtechceu.gtceu.GTCEu.id("distill_creosote"));
    }

    private static void vanillaOverrides(Consumer<ResourceLocation> consumer){
        if (!com.ironsword.gtmfo.GTMFOConfigHolder.INSTANCE.gtfoVanillaOverridesConfig.vanillaOverrideChain) return;
        consumer.accept(ResourceLocation.tryBuild("minecraft", "baked_potato"));
        consumer.accept(ResourceLocation.tryBuild("minecraft", "mushroom_stew"));
        consumer.accept(ResourceLocation.tryBuild("minecraft", "beetroot_soup"));
        consumer.accept(ResourceLocation.tryBuild("minecraft", "rabbit_stew"));
        consumer.accept(ResourceLocation.tryBuild("minecraft", "golden_carrot"));
        consumer.accept(ResourceLocation.tryBuild("minecraft", "golden_apple"));
        if (com.ironsword.gtmfo.GTMFOConfigHolder.INSTANCE.gtfoVanillaOverridesConfig.useBakingOvenForMeats) {
            for (String meat : new String[] { "cooked_beef", "cooked_porkchop", "cooked_mutton", "cooked_chicken",
                    "cooked_rabbit", "cooked_cod", "cooked_salmon" }) {
                consumer.accept(ResourceLocation.tryBuild("minecraft", meat));
            }
        }
        if (com.ironsword.gtmfo.GTMFOConfigHolder.INSTANCE.gtfoVanillaOverridesConfig.useRollingPinForPaper) {
            consumer.accept(ResourceLocation.tryBuild("minecraft", "paper"));
            consumer.accept(ResourceLocation.tryBuild("minecraft", "sticky_piston"));
        }
    }

    private static void doughRecipes(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.FORGE_HAMMER_RECIPES.recipeBuilder(id("dough_flat"))
                .inputItems(GTItems.DOUGH.asStack())
                .outputItems(GTMFOItems.DOUGH_FLAT)
                .EUt(60).duration(40)
                .save(provider);
        // original: shaped recipe with any rolling pin (craftingToolRollingPin)
        for (var entry : com.ironsword.gtmfo.common.data.GTMFOTools.ROLLING_PINS.entrySet()) {
            com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper.addShapedRecipe(provider,
                    id("flat_dough_" + entry.getKey().getName()),
                    GTMFOItems.DOUGH_FLAT.asStack(),
                    "RD ",
                    'R', entry.getValue().asStack(),
                    'D', GTItems.DOUGH.asStack());
        }
    }

}
