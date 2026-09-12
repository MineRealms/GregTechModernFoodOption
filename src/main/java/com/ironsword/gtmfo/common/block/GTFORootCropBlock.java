package com.ironsword.gtmfo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.storage.loot.LootParams;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Root crop (onion / horseradish): harvest the produce at intermediate age, seeds at full growth.
 */
public class GTFORootCropBlock extends GTFOCropBlock {

    public GTFORootCropBlock(Properties properties, Supplier<? extends ItemLike> seedItem,
                             Supplier<? extends ItemLike> cropItem) {
        super(properties, seedItem, cropItem);
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return BlockStateProperties.AGE_7;
    }

    @Override
    public int getMaxAge() {
        return 7;
    }

    public int getMinHarvestingAge() {
        return 4;
    }

    public int getMaxHarvestingAge() {
        return 5;
    }

    public boolean seedHarvestable(BlockState state) {
        return this.getAge(state) == this.getMaxAge();
    }

    public boolean cropHarvestable(BlockState state) {
        return this.getAge(state) >= this.getMinHarvestingAge() && this.getAge(state) <= this.getMaxHarvestingAge();
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>();
        int age = this.getAge(state);
        int fortune = getFortune(params);
        if (this.cropHarvestable(state)) {
            drops.add(new ItemStack(this.cropItem.get(), 1 + fortune));
        } else if (age >= this.getMaxAge()) {
            drops.add(new ItemStack(this.seedItem.get(), 3 + fortune));
        }
        return drops;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (this.getAge(state) >= this.getMaxAge()) {
            if (!level.isClientSide) {
                RandomSource random = level.getRandom();
                popResource(level, pos, new ItemStack(this.seedItem.get(), random.nextInt(2) + 1));
                level.setBlock(pos, this.getStateForAge(this.getAge(state) - 1), 2);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(this.seedItem.get());
    }
}
