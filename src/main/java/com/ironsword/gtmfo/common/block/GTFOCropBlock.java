package com.ironsword.gtmfo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.PlantType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Base GTFO crop (6 growth stages, right-click harvest at maturity).
 */
public class GTFOCropBlock extends CropBlock {

    private static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);

    protected final Supplier<? extends ItemLike> seedItem;
    protected final Supplier<? extends ItemLike> cropItem;

    public GTFOCropBlock(Properties properties, Supplier<? extends ItemLike> seedItem,
                         Supplier<? extends ItemLike> cropItem) {
        super(properties);
        this.seedItem = seedItem;
        this.cropItem = cropItem;
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return BlockStateProperties.AGE_5;
    }

    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(this.getAgeProperty());
    }

    @Override
    public int getMaxAge() {
        return 5;
    }

    @Override
    protected Item getBaseSeedId() {
        return seedItem.get().asItem();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public PlantType getPlantType(BlockGetter level, BlockPos pos) {
        return PlantType.CROP;
    }

    @Override
    public void growCrops(Level level, BlockPos pos, BlockState state) {
        int i = this.getAge(state) + this.getBonemealAgeIncrease(level);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }
        level.setBlock(pos, this.getStateForAge(i), 2);
    }

    @Override
    protected int getBonemealAgeIncrease(Level level) {
        return super.getBonemealAgeIncrease(level);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>();
        int age = this.getAge(state);
        RandomSource random = params.getLevel().getRandom();
        if (age >= this.getMaxAge()) {
            ItemStack seedStack = new ItemStack(this.seedItem.get());
            if (random.nextInt(9) == 0) {
                seedStack.grow(1);
            }
            drops.add(seedStack);

            int cropCount = 0;
            for (int i = 0; i < 3; i++) {
                if (random.nextInt(2 * this.getMaxAge()) <= age) {
                    cropCount++;
                }
            }
            if (cropCount > 0) {
                drops.add(new ItemStack(this.cropItem.get(), cropCount));
            }
        }
        return drops;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (this.isMaxAge(state)) {
            if (!level.isClientSide) {
                RandomSource random = level.getRandom();
                List<ItemStack> drops = new ArrayList<>();
                if (random.nextInt(9) == 0) {
                    drops.add(new ItemStack(this.seedItem.get()));
                }
                int cropCount = 0;
                for (int i = 0; i < 3; i++) {
                    if (random.nextInt(2 * this.getMaxAge()) <= this.getAge(state)) {
                        cropCount++;
                    }
                }
                if (cropCount > 0) {
                    drops.add(new ItemStack(this.cropItem.get(), cropCount));
                }
                for (ItemStack drop : drops) {
                    if (!player.getInventory().add(drop)) {
                        player.drop(drop, false);
                    }
                }
                level.setBlock(pos, this.getStateForAge(0), 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(this.seedItem.get());
    }

    public ItemStack getSeedStack() {
        return new ItemStack(this.seedItem.get());
    }

    public ItemStack getCropStack() {
        return new ItemStack(this.cropItem.get());
    }
}
