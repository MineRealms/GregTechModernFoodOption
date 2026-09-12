package com.ironsword.gtmfo.api.item;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.ironsword.gtmfo.api.item.component.GTMFOFoodStats;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ExComponentItem extends ComponentItem {
    protected ExComponentItem(Properties properties) {
        super(properties);
    }

    public static ExComponentItem create(Item.Properties properties){
        return new ExComponentItem(properties);
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        for (IItemComponent component : components){
            if (component instanceof GTMFOFoodStats foodStats){
                return foodStats.getEatingDuration();
            }
        }
        return super.getUseDuration(pStack);
    }

    /** Returns the GTFO food stats attached to the item, or {@code null} if it is not a GTFO food. */
    public static GTMFOFoodStats getFoodStats(ItemStack stack) {
        if (stack.getItem() instanceof ExComponentItem item) {
            for (IItemComponent component : item.components) {
                if (component instanceof GTMFOFoodStats foodStats) {
                    return foodStats;
                }
            }
        }
        return null;
    }
}
