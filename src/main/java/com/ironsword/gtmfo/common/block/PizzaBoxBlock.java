package com.ironsword.gtmfo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

/**
 * Pizza box: a flat box that turns into a pizza when used (original {@code GTFOPizzaBox}).
 */
public class PizzaBoxBlock extends Block {

    private static final VoxelShape SHAPE = box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);

    private final Supplier<? extends Block> pizzaBlock;

    public PizzaBoxBlock(Properties properties, Supplier<? extends Block> pizzaBlock) {
        super(properties);
        this.pizzaBlock = pizzaBlock;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos,
                                        CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        Block pizza = pizzaBlock.get();
        if (pizza == null) {
            return InteractionResult.PASS;
        }
        level.setBlock(pos, pizza.defaultBlockState(), 3);
        return InteractionResult.CONSUME;
    }
}
