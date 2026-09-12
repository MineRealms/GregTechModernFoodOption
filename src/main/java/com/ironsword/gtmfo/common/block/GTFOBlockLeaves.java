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

    /** original fancy-graphics sapling drop chance passed to dropApple */
    private static final int FANCY_APPLE_CHANCE = 20;

    private final Supplier<? extends Item> sapling;
    private final Supplier<? extends Item> fruit;
    private final int fruitDivisor;
    private final int fruitMin;
    private final int fruitMax;

    public GTFOBlockLeaves(Properties properties, Supplier<? extends Item> sapling,
                           Supplier<? extends Item> fruit, int fruitDivisor, int fruitMin, int fruitMax) {
        super(properties);
        this.sapling = sapling;
        this.fruit = fruit;
        this.fruitDivisor = fruitDivisor;
        this.fruitMin = fruitMin;
        this.fruitMax = fruitMax;
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
        // original GTFOTree.getAppleDrop: nextInt(chance / divisor) == 0, chance = 20
        if (this.fruit != null && this.fruitDivisor > 0
                && random.nextInt(Math.max(1, FANCY_APPLE_CHANCE / this.fruitDivisor)) == 0) {
            int count = this.fruitMin + random.nextInt(this.fruitMax - this.fruitMin + 1);
            if (count > 0) {
                drops.add(new ItemStack(this.fruit.get(), count));
            }
        }
        return drops;
    }
}
