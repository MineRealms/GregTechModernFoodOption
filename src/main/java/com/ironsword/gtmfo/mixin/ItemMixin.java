package com.ironsword.gtmfo.mixin;

import com.ironsword.gtmfo.api.mixin.IEatingDuration;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {

    /** Custom eating duration from the GTFO food stats (original {@code GTFOFoodDurationSetter}). */
    @Inject(
            method = "getUseDuration",
            at = @At("HEAD"),
            cancellable = true)
    private void injectGetUseDuration(ItemStack pStack, CallbackInfoReturnable<Integer> cir) {
        if (!pStack.getItem().isEdible()) {
            return;
        }
        FoodProperties properties = pStack.getFoodProperties(null);
        if (properties instanceof IEatingDuration duration) {
            cir.setReturnValue(duration.getEatingDuration());
        }
    }
}
