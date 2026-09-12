package com.ironsword.gtmfo.common.entity;

import com.ironsword.gtmfo.common.data.GTMFOEntities;
import com.ironsword.gtmfo.common.data.material.GTMFOFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;

public class ItalianBuffaloEntity extends Cow {

    public ItalianBuffaloEntity(EntityType<? extends Cow> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnType) {
        return hasNearbyWaterBiome(level, this.blockPosition()) && super.checkSpawnRules(level, spawnType);
    }

    private static boolean hasNearbyWaterBiome(LevelAccessor level, BlockPos pos) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                Holder<Biome> biome = level.getBiome(pos.offset(i * 16, 0, j * 16));
                if (biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_RIVER) || biome.is(BiomeTags.IS_BEACH)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(Items.BUCKET) && !this.isBaby()) {
            player.playSound(SoundEvents.COW_MILK, 1.0F, 1.0F);
            ItemStack milk = ItemUtils.createFilledResult(stack, player,
                    GTMFOFluids.ItalianBuffaloMilk.getBucket().getDefaultInstance());
            player.setItemInHand(hand, milk);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Nullable
    @Override
    public ItalianBuffaloEntity getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return GTMFOEntities.ITALIAN_BUFFALO.get().create(level);
    }
}
