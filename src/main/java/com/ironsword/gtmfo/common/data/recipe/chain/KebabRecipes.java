package com.ironsword.gtmfo.common.data.recipe.chain;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.gregtechceu.gtceu.data.recipe.VanillaRecipeHelper;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import com.ironsword.gtmfo.common.data.material.GTMFOMaterials;
import com.ironsword.gtmfo.common.data.recipe.GTMFORecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.function.Consumer;

import static com.ironsword.gtmfo.GregTechModernFoodOption.id;

// Kebab Chain caus why not?!
public class KebabRecipes {

    private static final List<ItemStack> MEATS = List.of(
            new ItemStack(Items.BEEF),
            new ItemStack(Items.PORKCHOP),
            new ItemStack(Items.CHICKEN),
            new ItemStack(Items.MUTTON),
            new ItemStack(Items.RABBIT));

    public static void init(Consumer<FinishedRecipe> provider){
        skewers(provider);
        kebabBase(provider);
        kebabAssembly(provider);
        baking(provider);
    }

    private static void skewers(Consumer<FinishedRecipe> provider){
        VanillaRecipeHelper.addShapedRecipe(provider, id("hand_skewer"),
                GTMFOItems.SKEWER.asStack(8),
                "BSd", "fB ",
                'S', ChemicalHelper.get(TagPrefix.rodLong, GTMaterials.Steel),
                'B', ChemicalHelper.get(TagPrefix.screw, GTMaterials.Steel));

        GTRecipeTypes.LATHE_RECIPES.recipeBuilder(id("skewer_steel_long"))
                .inputItems(TagPrefix.rodLong, GTMaterials.Steel)
                .outputItems(GTMFOItems.SKEWER.asStack(8))
                .EUt(20).duration(25).save(provider);

        GTRecipeTypes.LATHE_RECIPES.recipeBuilder(id("skewer_steel"))
                .inputItems(TagPrefix.rod, GTMaterials.Steel)
                .outputItems(GTMFOItems.SKEWER.asStack(4))
                .EUt(30).duration(120).save(provider);

        GTRecipeTypes.LATHE_RECIPES.recipeBuilder(id("skewer_stainless_long"))
                .inputItems(TagPrefix.rodLong, GTMaterials.StainlessSteel)
                .outputItems(GTMFOItems.SKEWER.asStack(16))
                .EUt(40).duration(25).save(provider);

        GTRecipeTypes.LATHE_RECIPES.recipeBuilder(id("skewer_stainless"))
                .inputItems(TagPrefix.rod, GTMaterials.StainlessSteel)
                .outputItems(GTMFOItems.SKEWER.asStack(8))
                .EUt(40).duration(120).save(provider);

        GTRecipeTypes.LATHE_RECIPES.recipeBuilder(id("skewer_titanium_long"))
                .inputItems(TagPrefix.rodLong, GTMaterials.Titanium)
                .outputItems(GTMFOItems.SKEWER.asStack(32))
                .EUt(200).duration(200).save(provider);

        GTRecipeTypes.LATHE_RECIPES.recipeBuilder(id("skewer_titanium"))
                .inputItems(TagPrefix.rod, GTMaterials.Titanium)
                .outputItems(GTMFOItems.SKEWER.asStack(16))
                .EUt(200).duration(100).save(provider);
    }

    private static void kebabBase(Consumer<FinishedRecipe> provider){
        // Kubide Line
        VanillaRecipeHelper.addShapedRecipe(provider, id("hand_kubide_kebab_meat"),
                GTMFOItems.KUBIDE_MEAT.asStack(4),
                "STO", "MMM", "FMF",
                'F', GTMFOItems.ANIMAL_FAT.asStack(),
                'M', ChemicalHelper.get(TagPrefix.dust, GTMaterials.Meat),
                'S', ChemicalHelper.get(TagPrefix.dust, GTMaterials.Salt),
                'O', GTMFOItems.ONION.asStack(),
                'T', GTMFOItems.TOMATO.asStack());

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("kubide_meat_1"))
                .inputItems(TagPrefix.dust, GTMaterials.Meat, 4)
                .inputItems(GTMFOItems.ONION_SLICE.asStack(4), GTMFOItems.MUSHROOM_SLICE.asStack(3),
                        GTMFOItems.ANIMAL_FAT.asStack())
                .inputFluids(GTMFOFluids.TomatoSauce.getFluid(400))
                .outputItems(GTMFOItems.KUBIDE_MEAT.asStack(5))
                .circuitMeta(3)
                .EUt(30).duration(90).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("kubide_meat_2"))
                .inputItems(TagPrefix.dust, GTMaterials.Meat, 4)
                .inputItems(GTMFOItems.ONION_SLICE.asStack(4), GTMFOItems.MUSHROOM_SLICE.asStack(4),
                        ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Zest, 4),
                        GTMFOItems.ANIMAL_FAT.asStack())
                .inputFluids(GTMFOFluids.TomatoSauce.getFluid(400))
                .outputItems(GTMFOItems.KUBIDE_MEAT.asStack(15))
                .circuitMeta(1)
                .EUt(30).duration(120).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("kubide_meat_3"))
                .inputItems(GTMFOItems.CHUM.asStack(8), GTMFOItems.ONION_SLICE.asStack(4),
                        ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Zest, 4),
                        GTMFOItems.ANIMAL_FAT.asStack(2))
                .inputFluids(GTMFOFluids.TomatoSauce.getFluid(400))
                .outputItems(GTMFOItems.KUBIDE_MEAT.asStack(30))
                .circuitMeta(2)
                .EUt(30).duration(120).save(provider);

        // Barg Line
        VanillaRecipeHelper.addShapedRecipe(provider, id("hand_barg_kebab_meat"),
                GTMFOItems.BARG_MEAT.asStack(4),
                "SML", "MOM", "ZMZ",
                'S', ChemicalHelper.get(TagPrefix.dust, GTMaterials.Salt),
                'M', ChemicalHelper.get(TagPrefix.dust, GTMaterials.Meat),
                'O', GTMFOItems.ONION.asStack(),
                'L', GTMFOItems.OLIVE.asStack(),
                'Z', ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Zest));

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("barg_meat_1"))
                .inputItems(TagPrefix.dust, GTMaterials.Meat, 4)
                .inputItems(TagPrefix.dust, GTMaterials.Salt, 2)
                .inputItems(GTMFOItems.ONION_SLICE.asStack(8),
                        ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Zest, 2))
                .inputFluids(GTMFOFluids.OliveOil.getFluid(500))
                .outputItems(GTMFOItems.BARG_MEAT.asStack(10))
                .circuitMeta(3)
                .EUt(30).duration(40).save(provider);

        GTRecipeTypes.MIXER_RECIPES.recipeBuilder(id("barg_meat_2"))
                .inputItems(GTMFOItems.CHUM.asStack(8))
                .inputItems(TagPrefix.dust, GTMaterials.Salt, 2)
                .inputItems(GTMFOItems.ONION_SLICE.asStack(8),
                        ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Zest, 2))
                .inputFluids(GTMFOFluids.OliveOil.getFluid(1000))
                .outputItems(GTMFOItems.BARG_MEAT.asStack(20))
                .circuitMeta(1)
                .EUt(30).duration(100).save(provider);
    }

    private static void kebabAssembly(Consumer<FinishedRecipe> provider){
        VanillaRecipeHelper.addShapedRecipe(provider, id("hand_kubide_kebab"),
                GTMFOItems.KEBAB_KUBIDEH_RAW.asStack(),
                "RMM", "SMM", "KTS",
                'K', GTMFOItems.SKEWER.asStack(),
                'M', GTMFOItems.KUBIDE_MEAT.asStack(),
                'S', ChemicalHelper.get(TagPrefix.dustSmall, GTMaterials.Salt),
                'T', GTMFOItems.TOMATO.asStack(),
                'R', GTMFOItems.SKEWER.asStack());

        VanillaRecipeHelper.addShapedRecipe(provider, id("hand_barg_kebab"),
                GTMFOItems.KEBAB_BARG_RAW.asStack(),
                "RMM", "SMM", "KTS",
                'M', GTMFOItems.BARG_MEAT.asStack(),
                'K', GTMFOItems.SKEWER.asStack(),
                'S', ChemicalHelper.get(TagPrefix.dustSmall, GTMaterials.Salt),
                'T', GTMFOItems.TOMATO.asStack(),
                'R', GTMFOItems.SKEWER.asStack());

        for (int i = 0; i < MEATS.size(); i++) {
            ItemStack meat = MEATS.get(i);
            GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("kebab_meat_" + i))
                    .inputItems(meat.copyWithCount(4))
                    .inputItems(TagPrefix.dustTiny, GTMaterials.Salt, 4)
                    .inputItems(GTMFOItems.SKEWER.asStack(4))
                    .outputItems(GTMFOItems.KEBAB_MEAT_RAW.asStack(4))
                    .EUt(16).duration(50).save(provider);
        }

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("kebab_meat_ingot"))
                .inputItems(GTMFOItems.MEAT_INGOT.asStack(4))
                .inputItems(TagPrefix.dustTiny, GTMaterials.Salt, 4)
                .inputItems(GTMFOItems.SKEWER.asStack(4))
                .outputItems(GTMFOItems.KEBAB_MEAT_RAW.asStack(4))
                .EUt(16).duration(50).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("kebab_kubideh"))
                .inputItems(GTMFOItems.KUBIDE_MEAT.asStack(5), GTMFOItems.TOMATO_SLICE.asStack(4),
                        GTMFOItems.SKEWER.asStack(2))
                .inputItems(TagPrefix.dustSmall, GTMaterials.Salt)
                .outputItems(GTMFOItems.KEBAB_KUBIDEH_RAW.asStack(2))
                .EUt(27).duration(20).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("kebab_barg"))
                .inputItems(GTMFOItems.BARG_MEAT.asStack(5),
                        ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Zest, 4),
                        GTMFOItems.TOMATO_SLICE.asStack(4), GTMFOItems.SKEWER.asStack(3))
                .outputItems(GTMFOItems.KEBAB_BARG_RAW.asStack(3))
                .EUt(27).duration(80).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("kebab_barg_sauce"))
                .inputItems(GTMFOItems.BARG_MEAT.asStack(5),
                        ChemicalHelper.get(TagPrefix.dust, GTMFOMaterials.Zest, 4),
                        GTMFOItems.SKEWER.asStack(3))
                .inputFluids(GTMFOFluids.TomatoSauce.getFluid(100))
                .outputItems(GTMFOItems.KEBAB_BARG_RAW.asStack(3))
                .EUt(27).duration(80).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("kebab_soltani"))
                .inputItems(GTMFOItems.KEBAB_BARG.asStack(2), GTMFOItems.KEBAB_KUBIDEH.asStack(),
                        GTMFOItems.TOMATO.asStack(3), GTMFOItems.ONION.asStack(2),
                        GTMFOItems.LEMON.asStack())
                .inputFluids(GTMFOFluids.Stearin.getFluid(1000), GTMFOFluids.LemonExtract.getFluid(250))
                .outputItems(GTMFOItems.KEBAB_SOLTANI.asStack(2), GTMFOItems.SKEWER.asStack())
                .EUt(120).duration(200).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("kebab_onion"))
                .inputItems(GTMFOItems.ONION_SLICE.asStack(2))
                .inputItems(TagPrefix.dustTiny, GTMaterials.Salt)
                .inputItems(GTMFOItems.SKEWER.asStack())
                .outputItems(GTMFOItems.KEBAB_ONION_RAW.asStack())
                .EUt(16).duration(100).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("kebab_tomato"))
                .inputItems(GTMFOItems.TOMATO_SLICE.asStack(2))
                .inputItems(TagPrefix.dustTiny, GTMaterials.Salt)
                .inputItems(GTMFOItems.SKEWER.asStack())
                .outputItems(GTMFOItems.KEBAB_TOMATO_RAW.asStack())
                .EUt(16).duration(100).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("kebab_carrot"))
                .inputItems(GTMFOItems.CARROT_SLICE.asStack(2))
                .inputItems(TagPrefix.dustTiny, GTMaterials.Salt)
                .inputItems(GTMFOItems.SKEWER.asStack())
                .outputItems(GTMFOItems.KEBAB_CARROT_RAW.asStack())
                .EUt(16).duration(100).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("kebab_chum"))
                .inputItems(GTMFOItems.CHUM.asStack(8), GTMFOItems.BANANA_PEEL.asStack(2),
                        GTMFOItems.ONION.asStack(), GTMFOItems.POTATO_MASHED.asStack(4),
                        GTMFOItems.SKEWER.asStack(4))
                .inputFluids(GTMFOFluids.Yolk.getFluid(200), GTMFOFluids.Stearin.getFluid(400))
                .outputItems(GTMFOItems.KEBAB_CHUM_RAW.asStack(4))
                .EUt(16).duration(400).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("kebab_chum_bucket"))
                .inputItems(GTMFOItems.KEBAB_CHUM.asStack(2), GTMFOItems.KEBAB_KUBIDEH.asStack(5),
                        GTMFOItems.KEBAB_FAT.asStack(6), GTMFOItems.BURGER_CHUM.asStack(14),
                        new ItemStack(Items.BUCKET))
                .inputFluids(GTMFOFluids.RabbitStew.getFluid(250))
                .inputFluids(GTMFOFluids.FryingOilHot.getFluid(250))
                .inputFluids(GTMFOFluids.PotatoJuice.getFluid(4000))
                .outputItems(GTMFOItems.CHUM_BUCKET.asStack(), GTMFOItems.SKEWER.asStack(13))
                .EUt(100).duration(1000).save(provider);

        GTMFORecipeTypes.CUISINE_ASSEMBLER_RECIPES.recipeBuilder(id("kebab_fat"))
                .inputItems(GTMFOItems.ANIMAL_FAT.asStack(2), GTMFOItems.SKEWER.asStack(12))
                .inputItems(TagPrefix.dust, GTMaterials.Salt)
                .inputFluids(GTMFOFluids.Stearin.getFluid(400))
                .outputItems(GTMFOItems.KEBAB_FAT_RAW.asStack(12))
                .EUt(16).duration(20).save(provider);
    }

    private static void baking(Consumer<FinishedRecipe> provider){
        int baseDuration = 400;

        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("kebab_kubideh_cooked"))
                .inputItems(GTMFOItems.KEBAB_KUBIDEH_RAW.asStack())
                .outputItems(GTMFOItems.KEBAB_KUBIDEH.asStack())
                .EUt(60).duration(baseDuration + 100).save(provider);
        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("kebab_barg_cooked"))
                .inputItems(GTMFOItems.KEBAB_BARG_RAW.asStack())
                .outputItems(GTMFOItems.KEBAB_BARG.asStack())
                .EUt(60).duration(baseDuration + 100).save(provider);
        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("kebab_tomato_cooked"))
                .inputItems(GTMFOItems.KEBAB_TOMATO_RAW.asStack())
                .outputItems(GTMFOItems.KEBAB_TOMATO.asStack())
                .EUt(60).duration(baseDuration).save(provider);
        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("kebab_onion_cooked"))
                .inputItems(GTMFOItems.KEBAB_ONION_RAW.asStack())
                .outputItems(GTMFOItems.KEBAB_ONION.asStack())
                .EUt(60).duration(baseDuration).save(provider);
        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("kebab_chum_cooked"))
                .inputItems(GTMFOItems.KEBAB_CHUM_RAW.asStack())
                .outputItems(GTMFOItems.KEBAB_CHUM.asStack())
                .EUt(60).duration(baseDuration).save(provider);
        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("kebab_carrot_cooked"))
                .inputItems(GTMFOItems.KEBAB_CARROT_RAW.asStack())
                .outputItems(GTMFOItems.KEBAB_CARROT.asStack())
                .EUt(60).duration(baseDuration).save(provider);
        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("kebab_fat_cooked"))
                .inputItems(GTMFOItems.KEBAB_FAT_RAW.asStack())
                .outputItems(GTMFOItems.KEBAB_FAT.asStack())
                .EUt(60).duration(baseDuration).save(provider);
        GTMFORecipeTypes.BAKING_OVEN_RECIPES.recipeBuilder(id("kebab_meat_cooked"))
                .inputItems(GTMFOItems.KEBAB_MEAT_RAW.asStack())
                .outputItems(GTMFOItems.KEBAB_MEAT.asStack())
                .EUt(60).duration(baseDuration + 50).save(provider);
    }
}
