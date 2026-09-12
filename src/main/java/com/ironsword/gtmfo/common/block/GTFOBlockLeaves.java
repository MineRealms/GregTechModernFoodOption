package com.ironsword.gtmfo.common.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * GTFO leaves (per-tree block). Drops saplings, sticks and (optionally) the tree's fruit.
 */
public class GTFOBlockLeaves extends LeavesBlock {

    private final Supplier<? extends Item> sapling;
    private final Supplier<? extends Item> fruit;

    public GTFOBlockLeaves(Properties properties, Supplier<? extends Item> sapling,
                           Supplier<? extends Item> fruit) {
        super(properties);
        this.sapling = sapling;
        this.fruit = fruit;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>();
        net.minecraft.util.RandomSource random = params.getLevel().getRandom();
        if (random.nextInt(20) == 0) {
            drops.add(new ItemStack(this.sapling.get()));
        }
        if (random.nextInt(50) == 0) {
            drops.add(new ItemStack(net.minecraft.world.item.Items.STICK));
        }
        if (this.fruit != null && random.nextInt(20) == 0) {
            drops.add(new ItemStack(this.fruit.get()));
        }
        return drops;
    }
}
