package com.ironsword.gtmfo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Berry bush: 3 growth stages, efficiency 0-4 (raised by neighbouring efficient bushes),
 * optionally thorny (damages living entities on contact).
 */
public class GTFOBerryBushBlock extends GTFOCropBlock {

    public static final IntegerProperty EFFICIENCY = IntegerProperty.create("efficiency", 0, 4);

    private static final VoxelShape SMALL_SHAPE = box(4.0D, 0.0D, 4.0D, 12.0D, 9.0D, 12.0D);
    private static final VoxelShape LARGE_SHAPE = box(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    private static final VoxelShape STEM_SHAPE = box(6.92D, 0.0D, 6.92D, 9.08D, 4.0D, 9.08D);

    private final boolean thorny;

    public GTFOBerryBushBlock(Properties properties, Supplier<? extends ItemLike> cropItem, boolean thorny) {
        super(properties, cropItem, cropItem);
        this.thorny = thorny;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(this.getAgeProperty(), 0)
                .setValue(EFFICIENCY, 0));
    }

    @Override
    protected IntegerProperty getAgeProperty() {
        return BlockStateProperties.AGE_2;
    }

    @Override
    public int getMaxAge() {
        return 2;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(this.getAgeProperty(), EFFICIENCY);
    }

    public int getEfficiency(BlockState state) {
        return state.getValue(EFFICIENCY);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.getAge(state) == 0 ? SMALL_SHAPE : LARGE_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                        CollisionContext context) {
        return STEM_SHAPE;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (this.thorny && entity instanceof LivingEntity) {
            entity.hurt(level.damageSources().cactus(), 1.0F);
        }
        double dx = entity.getX() - (pos.getX() + 0.5D);
        double dz = entity.getZ() - (pos.getZ() + 0.5D);
        double distance = (dx * dx + dz * dz) / 4.0D + 0.5D;
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(distance, distance, distance));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>();
        if (this.getAge(state) >= this.getMaxAge()) {
            RandomSource random = params.getLevel().getRandom();
            int cropCount = 1;
            for (int i = 0; i < 2 + this.getEfficiency(state); i++) {
                if (random.nextInt(2) == 0) {
                    cropCount++;
                }
            }
            drops.add(new ItemStack(this.cropItem.get(), cropCount));
        }
        return drops;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (this.isMaxAge(state)) {
            if (!level.isClientSide) {
                RandomSource random = level.getRandom();
                int berries = 1;
                for (int i = 0; i < 2 + this.getEfficiency(state); i++) {
                    if (random.nextInt(2) == 0) {
                        berries++;
                    }
                }
                ItemStack stack = new ItemStack(this.cropItem.get(), berries);
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
                level.setBlock(pos, state.setValue(this.getAgeProperty(), this.getMaxAge() - 1), 3);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void growCrops(Level level, BlockPos pos, BlockState state) {
        int newAge = Math.min(this.getAge(state) + 1, this.getMaxAge());
        level.setBlock(pos, state.setValue(this.getAgeProperty(), newAge)
                .setValue(EFFICIENCY, this.calcEfficiency(level, pos)), 3);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (level.getRawBrightness(pos, 0) >= 9) {
            int age = this.getAge(state);
            if (age < this.getMaxAge()) {
                int slowdown = this.getGrowthSlowdown(level, pos, state);
                if (random.nextInt(Math.max(2, slowdown / 8)) == 0) {
                    level.setBlock(pos, state.setValue(this.getAgeProperty(), age + 1)
                            .setValue(EFFICIENCY, this.calcEfficiency(level, pos)), 3);
                }
            }
        }
    }

    public int getGrowthSlowdown(Level level, BlockPos pos, BlockState state) {
        if (this.getAge(state) == 0) {
            return 4;
        }
        int growthSlowdown = 320 >> this.getEfficiency(state);
        if (!level.isDay()) {
            growthSlowdown *= 2;
        }
        if (level.isRaining()) {
            growthSlowdown = growthSlowdown * 2 / 3;
        }
        return growthSlowdown;
    }

    public int calcEfficiency(Level level, BlockPos pos) {
        int[] efficiencies = new int[EFFICIENCY.getPossibleValues().stream().max(Integer::compare).orElse(4) + 1];
        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-1, 0, -1), pos.offset(1, 0, 1))) {
            if (!blockpos.equals(pos)) {
                BlockState neighbor = level.getBlockState(blockpos);
                if (neighbor.getBlock() instanceof GTFOBerryBushBlock bush) {
                    int eff = bush.getEfficiency(neighbor) + 1;
                    if (eff < efficiencies.length) {
                        efficiencies[eff]++;
                    }
                }
            }
        }
        for (int i = efficiencies.length - 1; i >= 0; i--) {
            if (efficiencies[i] > 2) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos,
                                boolean isMoving) {
        if (!(level.getBlockState(fromPos).getBlock() instanceof GTFOBerryBushBlock)) {
            // we don't want crops transmuting to higher efficiencies
            int newEfficiency = Math.min(calcEfficiency(level, pos), this.getEfficiency(state));
            level.setBlock(pos, state.setValue(EFFICIENCY, newEfficiency), 2);
        }
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    @Override
    public boolean canSurvive(BlockState state, net.minecraft.world.level.LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).is(Blocks.DIRT) || super.canSurvive(state, level, pos);
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return new ItemStack(this.cropItem.get());
    }
}
