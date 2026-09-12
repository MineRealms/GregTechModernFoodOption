package com.ironsword.gtmfo.common.loot;

import com.ironsword.gtmfo.GTMFOConfigHolder;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import org.jetbrains.annotations.NotNull;

/**
 * Adds undetermined GTFO seeds to grass drops (original {@code MinecraftForge.addGrassSeed}).
 * <p>
 * 1.12 weights were relative to wheat seeds (weight 1). Vanilla 1.20.1 grass drops wheat seeds with a
 * 12.5% chance, so the equivalent chance here is {@code 0.125 * weight / (weight + 1)}.
 */
public class UnknownSeedsLootModifier extends LootModifier {

    public static final Codec<UnknownSeedsLootModifier> CODEC = RecordCodecBuilder.create(instance ->
            codecStart(instance).apply(instance, UnknownSeedsLootModifier::new));

    public UnknownSeedsLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @NotNull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        int weight = GTMFOConfigHolder.INSTANCE.gtfoMiscConfig.unknownSeedsWeight;
        if (weight <= 0) return generatedLoot;
        float chance = 0.125F * weight / (weight + 1F);
        if (context.getRandom().nextFloat() < chance) {
            generatedLoot.add(GTMFOItems.SEED_UNKNOWN.asStack());
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
