package com.ironsword.gtmfo.common.data.recipe;

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
        BananaRecipes.init(provider);
        BritishRecipes.init(provider);
        AdobeBrickRecipes.init(provider);
        TreeRecipes.init(provider);
        ToolRecipes.init(provider);
        MobExtractionRecipes.init(provider);
        GreenhouseRecipes.init(provider);
        LacingRecipes.init(provider);
        SmoreRecipes.init(provider);


        doughRecipes(provider);

        GTMFOMachineRecipes.init(provider);

    }

    public static void remove(Consumer<ResourceLocation> consumer){
        BreadsRecipes.remove(consumer);
        // replaced by GTMFO's guaiacol-yielding version (VanillinRecipes)
        consumer.accept(com.gregtechceu.gtceu.GTCEu.id("distill_creosote"));
    }

    private static void doughRecipes(Consumer<FinishedRecipe> provider){
        GTRecipeTypes.FORGE_HAMMER_RECIPES.recipeBuilder(id("dough_flat"))
                .inputItems(GTItems.DOUGH.asStack())
                .outputItems(GTMFOItems.DOUGH_FLAT)
                .EUt(60).duration(40)
                .save(provider);
    }

}
