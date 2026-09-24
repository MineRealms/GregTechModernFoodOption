package com.ironsword.gtmfo.forge;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.GTMFOConfigHolder;
import com.ironsword.gtmfo.api.capability.NutrientsTracker;
import com.ironsword.gtmfo.api.capability.forge.GTMFOCapability;
import com.ironsword.gtmfo.common.command.NutrientCommands;
import com.ironsword.gtmfo.common.data.GTMFOEffects;
import com.ironsword.gtmfo.common.data.GTMFOCrops;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = GregTechModernFoodOption.MODID, bus =  Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeCommonEventListener {

    @SubscribeEvent
    public static void registerPlayerCapabilities(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof Player player){
            final NutrientsTracker tracker = new NutrientsTracker(player);
            event.addCapability(GregTechModernFoodOption.id("nutrients_tracker"), new ICapabilitySerializable<CompoundTag>() {
                @Override
                public CompoundTag serializeNBT() {
                    return tracker.serializeNBT();
                }
                @Override
                public void deserializeNBT(CompoundTag nbt) {
                    tracker.deserializeNBT(nbt);
                }

                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    return GTMFOCapability.CAPABILITY_NUTRIENTS_TRACKER.orEmpty(cap,LazyOptional.of(()->tracker));
                }
            });
        }

    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event){
        NutrientCommands.register(event.getDispatcher(),event.getBuildContext());
    }

    /**
     * Nutrient upkeep: decays the stored values on each in-game day change and refreshes the
     * derived state (health bonus / balanced effect / scoreboard mirror).
     */
    @SubscribeEvent
    public static void onPlayerTick(net.minecraftforge.event.TickEvent.PlayerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        Player player = event.player;
        if (player.level().isClientSide) return;
        if (player.tickCount % 20 != 0) return; // once per second is plenty
        NutrientsTracker tracker = GTMFOCapability.getNutrientsTracker(player);
        if (tracker == null) return;
        tracker.tick();
        com.ironsword.gtmfo.common.nutrient.NutrientEffects.tick(player, tracker);

        // mirror changes to the owning client (HUD)
        if (tracker.isDirty()) {
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                com.ironsword.gtmfo.network.NutrientsNetwork.sendToPlayer(serverPlayer, tracker);
            }
            tracker.clearDirty();
        }
    }

    /** Sends an initial nutrient snapshot right after joining so the HUD is populated. */
    @SubscribeEvent
    public static void onPlayerLoggedIn(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
            NutrientsTracker tracker = GTMFOCapability.getNutrientsTracker(player);
            if (tracker != null) {
                com.ironsword.gtmfo.network.NutrientsNetwork.sendToPlayer(player, tracker);
            }
        }
    }

    /**
     * Keeps (or resets) nutrients when the player entity is cloned: on death the config decides,
     * on a dimension/End return the values are always carried over.
     */
    @SubscribeEvent
    public static void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        NutrientsTracker oldTracker = GTMFOCapability.getNutrientsTracker(event.getOriginal());
        NutrientsTracker newTracker = GTMFOCapability.getNutrientsTracker(event.getEntity());
        if (oldTracker == null || newTracker == null) return;

        boolean reset = event.isWasDeath()
                && GTMFOConfigHolder.INSTANCE.gtfoNutrientConfig.resetOnDeath;
        if (!reset) {
            newTracker.copyFrom(oldTracker);
        } else {
            newTracker.clear();
        }
    }

    @SubscribeEvent
    public static void onLivingEntityFall(LivingFallEvent event){
        LivingEntity entity = event.getEntity();
        if (GTMFOEffects.FLY.isPresent() && entity.hasEffect(GTMFOEffects.FLY.get())){
            event.setDamageMultiplier(0);
        }
    }

//    @SubscribeEvent
//    public static void onEffectAdded(MobEffectEvent.Added event){
//        if (event.getEffectInstance().getEffect().equals(GTMFOEffects.CREATIVE_FLY.get()) && event.getEntity() instanceof Player player){
//            Abilities abilities =  player.getAbilities();
//            abilities.flying = true;
//        }
//    }
//
//    @SubscribeEvent
//    public static void onEffectRemoved(MobEffectEvent.Remove event){
//        if (event.getEffectInstance().getEffect().equals(GTMFOEffects.CREATIVE_FLY.get()) && event.getEntity() instanceof Player player){
//            Abilities abilities = player.getAbilities();
//            if (!abilities.instabuild && !player.isSpectator()){
//                abilities.mayfly = false;
//            }
//        }
//    }
//
//    @SubscribeEvent
//    public static void onEffectExpired(MobEffectEvent.Expired event){
//        if (event.getEffectInstance().getEffect().equals(GTMFOEffects.CREATIVE_FLY.get()) && event.getEntity() instanceof Player player){
//            Abilities abilities = player.getAbilities();
//            if (!abilities.instabuild && !player.isSpectator()){
//                abilities.mayfly = false;
//            }
//        }
//    }

    /**
     * Potion amplifier / lengthener (original {@code GTFOEventHandler.onDrinkPotion}).
     * <p>
     * Note: the original computes the lengthener duration from the added effect's <b>amplifier</b>
     * ({@code amplifier * ((durationBonus * 0.5) + 1.5)}) rather than its duration. That looks like an
     * upstream bug, but it is ported as-is; see PORTING_TODO for details.
     */
    private static final ThreadLocal<Boolean> BOOSTING = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @SubscribeEvent
    public static void onEffectApplicable(net.minecraftforge.event.entity.living.MobEffectEvent.Applicable event){
        if (BOOSTING.get()) return;
        if (!GTMFOEffects.AMPLIFIER.isPresent() || !GTMFOEffects.LENGTHENER.isPresent()) return;
        var effect = event.getEffectInstance();
        if (effect == null) return;
        if (effect.getEffect() == GTMFOEffects.AMPLIFIER.get() || effect.getEffect() == GTMFOEffects.LENGTHENER.get()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        var amplifierEffect = entity.getEffect(GTMFOEffects.AMPLIFIER.get());
        var lengthenerEffect = entity.getEffect(GTMFOEffects.LENGTHENER.get());
        if (amplifierEffect == null && lengthenerEffect == null) return;

        int newAmplifier = effect.getAmplifier();
        int newDuration = effect.getDuration();
        if (amplifierEffect != null) {
            newAmplifier += amplifierEffect.getAmplifier() + 1;
        }
        if (lengthenerEffect != null) {
            int durationBonus = lengthenerEffect.getAmplifier();
            newDuration = Math.max(newDuration, (int) (newAmplifier * ((durationBonus * 0.5) + 1.5)));
        }

        event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
        BOOSTING.set(Boolean.TRUE);
        try {
            entity.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect.getEffect(), newDuration,
                    newAmplifier, effect.isAmbient(), effect.isVisible(), effect.showIcon()));
        } finally {
            BOOSTING.set(Boolean.FALSE);
        }
    }

    /**
     * Adds legacy item tooltips from the language files ({@code item.gtmfo.<id>.tooltip}).
     */
    @SubscribeEvent
    public static void onItemTooltip(net.minecraftforge.event.entity.player.ItemTooltipEvent event){
        var stack = event.getItemStack();
        if (stack.isEmpty()) return;
        var id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!id.getNamespace().equals(GregTechModernFoodOption.MODID)) return;
        String key = "item." + GregTechModernFoodOption.MODID + "." + id.getPath() + ".tooltip";
        var tooltip = net.minecraft.network.chat.Component.translatable(key);
        if (!tooltip.getString().equals(key)) {
            event.getToolTip().add(tooltip.copy().withStyle(net.minecraft.ChatFormatting.GRAY));
        }
    }

    /**
     * Food tooltip: shows how much of each nutrient the item grants (built-in values plus the
     * {@code gtmfo:nutrient/<name>} tag values).
     */
    @SubscribeEvent
    public static void onFoodTooltip(net.minecraftforge.event.entity.player.ItemTooltipEvent event) {
        if (!com.ironsword.gtmfo.api.capability.Nutrients.isEnabled()) return;
        if (!GTMFOConfigHolder.INSTANCE.gtfoNutrientConfig.foodTooltips) return;

        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        net.minecraft.world.food.FoodProperties food = stack.getFoodProperties(null);
        if (food == null) return;

        java.util.Map<String, Float> values = new java.util.LinkedHashMap<>();
        if (food instanceof com.ironsword.gtmfo.api.mixin.INutrients access) {
            access.getNutrients().forEach((name, value) -> {
                if (value > 0) values.merge(name, (float) value, Float::sum);
            });
        }
        float[] tagValues = com.ironsword.gtmfo.common.nutrient.NutrientTags.tagValues(stack);
        for (int i = 0; i < com.ironsword.gtmfo.api.capability.Nutrients.LIST.size(); i++) {
            if (tagValues[i] > 0) {
                values.merge(com.ironsword.gtmfo.api.capability.Nutrients.LIST.get(i), tagValues[i], Float::sum);
            }
        }
        if (values.isEmpty()) return;

        StringBuilder line = new StringBuilder();
        for (String name : com.ironsword.gtmfo.api.capability.Nutrients.LIST) {
            Float value = values.get(name);
            if (value == null || value <= 0) continue;
            if (line.length() > 0) line.append("  ");
            line.append(net.minecraft.network.chat.Component
                            .translatable(com.ironsword.gtmfo.api.capability.Nutrients.langKey(name)).getString())
                    .append(' ').append(formatNutrient(value));
        }
        event.getToolTip().add(net.minecraft.network.chat.Component.translatable("gtmfo.tooltip.nutrients")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
        event.getToolTip().add(net.minecraft.network.chat.Component.literal(" " + line)
                .withStyle(net.minecraft.ChatFormatting.DARK_GREEN));
    }

    private static String formatNutrient(float value) {
        if (value == Math.round(value)) {
            return Integer.toString((int) value);
        }
        return String.format(java.util.Locale.ROOT, "%.1f", value);
    }

    /**
     * Plants GTFO crops when a registered seed is right-clicked on farmland (or water for rice).
     */
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event){
        ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        Block crop = GTMFOCrops.getCropFor(stack.getItem());
        if (crop == null) return;

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState clicked = level.getBlockState(pos);
        BlockPos above = pos.above();

        boolean onFarmland = clicked.is(Blocks.FARMLAND) && level.getBlockState(above).isAir();
        boolean onWater = clicked.getFluidState().is(FluidTags.WATER) && level.getBlockState(above).isAir();
        if (!onFarmland && !onWater) return;

        if (!level.isClientSide) {
            level.setBlock(above, crop.defaultBlockState(), 3);
            Player player = event.getEntity();
            if (player == null || !player.isCreative()) {
                stack.shrink(1);
            }
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
    }

}
