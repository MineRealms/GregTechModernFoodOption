package com.ironsword.gtmfo.forge;

import com.ironsword.gtmfo.GregTechModernFoodOption;
import com.ironsword.gtmfo.common.data.GTMFOItems;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Scrap meat drops from animals (original {@code GTFODropsEventHandler}):
 * 1/3 chance, amount 1 or 1..lootingLevel.
 */
@Mod.EventBusSubscriber(modid = GregTechModernFoodOption.MODID)
public class GTMFODropsHandler {

    @SubscribeEvent
    public static void addDrops(LivingDropsEvent event) {
        net.minecraft.world.entity.LivingEntity entity = event.getEntity();
        if (entity instanceof Pig || entity instanceof Cow || entity instanceof Chicken
                || entity instanceof Sheep || entity instanceof Rabbit) {
            if (entity.getRandom().nextInt(3) == 0) {
                int looting = event.getLootingLevel();
                int count = looting == 0 ? 1 : entity.getRandom().nextInt(looting) + 1;
                entity.spawnAtLocation(new ItemStack(GTMFOItems.SCRAP_MEAT.get(), count));
            }
        }
    }
}
