package com.ironsword.gtmfo.common.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.SimpleTieredMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.ActionResult;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.machines.GTMachineUtils;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Mob Extractor: extracts fluids/items from the entity standing on top of the machine.
 * Recipe data keys: "mob_on_top" (entity id), "cause_damage" (float).
 */
public class MobExtractorMachine extends SimpleTieredMachine {

    private LivingEntity attackableTarget;

    public MobExtractorMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier, GTMachineUtils.defaultTankSizeFunction);
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new MobExtractorRecipeLogic(this);
    }

    /** Finds (and caches) the entity matching {@code entityId} directly above the machine. */
    public LivingEntity findTarget(String entityId) {
        AABB box = new AABB(getPos().above());
        List<LivingEntity> nearby = getLevel().getEntitiesOfClass(LivingEntity.class, box);
        if (attackableTarget != null && nearby.contains(attackableTarget)) {
            return attackableTarget;
        }
        attackableTarget = null;
        for (LivingEntity entity : nearby) {
            ResourceLocation id = getLevel().registryAccess()
                    .registryOrThrow(Registries.ENTITY_TYPE).getKey(entity.getType());
            if (id != null && id.toString().equals(entityId)) {
                attackableTarget = entity;
                return entity;
            }
        }
        return null;
    }

    public void damageTarget(float damage) {
        if (attackableTarget != null && damage > 0) {
            attackableTarget.hurt(getLevel().damageSources().generic(), damage);
        }
    }

    public static class MobExtractorRecipeLogic extends RecipeLogic {

        public MobExtractorRecipeLogic(MobExtractorMachine machine) {
            super(machine);
        }

        @Override
        protected ActionResult checkRecipe(GTRecipe recipe) {
            MobExtractorMachine machine = (MobExtractorMachine) getMachine();
            String mobId = recipe.data.getString("mob_on_top");
            if (!mobId.isEmpty() && machine.findTarget(mobId) == null) {
                return ActionResult.FAIL_NO_REASON;
            }
            return super.checkRecipe(recipe);
        }

        @Override
        public void setupRecipe(GTRecipe recipe) {
            MobExtractorMachine machine = (MobExtractorMachine) getMachine();
            if (recipe.data.contains("cause_damage")) {
                machine.damageTarget(recipe.data.getFloat("cause_damage"));
            }
            super.setupRecipe(recipe);
        }
    }
}
