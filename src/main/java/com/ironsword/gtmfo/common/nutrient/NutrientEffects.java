package com.ironsword.gtmfo.common.nutrient;

import com.ironsword.gtmfo.GTMFOConfigHolder;
import com.ironsword.gtmfo.api.capability.Nutrients;
import com.ironsword.gtmfo.api.capability.NutrientsTracker;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

/**
 * Derived nutrient state, recomputed server side once per second
 * (see {@code ForgeCommonEventListener#onPlayerTick}).
 *
 * <ul>
 *   <li><b>Max health</b>: every nutrient at/above {@code benefitThreshold} grants
 *       {@code healthPerNutrient} health, capped by {@code healthBonusCap}. The bonus is applied as a
 *       transient attribute modifier (stable UUID), so it survives relogs without being written to
 *       the player's saved attributes.</li>
 *   <li><b>Balanced diet</b>: while every nutrient is at/above the threshold, the optional
 *       {@code balancedEffect} is kept refreshed (ambient, hidden).</li>
 *   <li><b>Scoreboard mirror</b>: values are mirrored into {@code gtmfo_<nutrient>} objectives so
 *       KubeJS / FTB Quests / command blocks can read them without a mod API.</li>
 * </ul>
 */
public final class NutrientEffects {

    /** Stable id of the nutrient health modifier - never change it. */
    public static final UUID HEALTH_MODIFIER_ID = UUID.fromString("6f4a1e5a-9d7c-4a5e-9d1b-2c3a4b5c6d7e");
    public static final String HEALTH_MODIFIER_NAME = "gtmfo_nutrients";

    /** Scoreboard objective name for one category (e.g. {@code gtmfo_dairy}); short enough for commands. */
    public static String scoreboardObjective(String nutrient) {
        return "gtmfo_" + nutrient;
    }

    private NutrientEffects() {}

    public static void tick(Player player, NutrientsTracker tracker) {
        GTMFOConfigHolder cfg = GTMFOConfigHolder.INSTANCE;
        if (cfg == null) return;

        if (!Nutrients.isEnabled()) {
            removeHealthBonus(player); // clean up if the system was switched off
            return;
        }

        applyHealthBonus(player, tracker);
        applyBalancedEffect(player, tracker);
        mirror(player, tracker);
    }

    // =========================================================
    // ******* max health bonus ******************************* //
    // =========================================================

    private static void applyHealthBonus(Player player, NutrientsTracker tracker) {
        GTMFOConfigHolder.GTFONutrientConfig cfg = GTMFOConfigHolder.INSTANCE.gtfoNutrientConfig;
        double bonus = 0.0;
        if (cfg.benefitThreshold > 0 && cfg.healthPerNutrient > 0) {
            int qualifying = 0;
            for (String name : Nutrients.LIST) {
                if (tracker.get(name) >= cfg.benefitThreshold) qualifying++;
            }
            bonus = Math.min(Math.max(0.0, cfg.healthBonusCap), qualifying * cfg.healthPerNutrient);
        }
        setHealthModifier(player, bonus);
    }

    private static void setHealthModifier(Player player, double bonus) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) return;
        AttributeModifier existing = maxHealth.getModifier(HEALTH_MODIFIER_ID);
        double current = existing == null ? 0.0 : existing.getAmount();
        if (Math.abs(current - bonus) < 1.0E-4) return;

        if (existing != null) maxHealth.removeModifier(HEALTH_MODIFIER_ID);
        if (bonus > 0) {
            maxHealth.addTransientModifier(new AttributeModifier(HEALTH_MODIFIER_ID, HEALTH_MODIFIER_NAME, bonus,
                    AttributeModifier.Operation.ADDITION));
        }
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private static void removeHealthBonus(Player player) {
        AttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) return;
        if (maxHealth.getModifier(HEALTH_MODIFIER_ID) != null) {
            maxHealth.removeModifier(HEALTH_MODIFIER_ID);
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }
    }

    // =========================================================
    // ******* balanced diet effect *************************** //
    // =========================================================

    private static void applyBalancedEffect(Player player, NutrientsTracker tracker) {
        GTMFOConfigHolder.GTFONutrientConfig cfg = GTMFOConfigHolder.INSTANCE.gtfoNutrientConfig;
        String effectId = cfg.balancedEffect;
        if (effectId == null || effectId.isBlank() || cfg.benefitThreshold <= 0) return;
        for (String name : Nutrients.LIST) {
            if (tracker.get(name) < cfg.benefitThreshold) return;
        }
        ResourceLocation id = ResourceLocation.tryParse(effectId.trim());
        if (id == null) return;
        MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(id);
        if (effect == null) return;
        // refreshed every second (5 s duration) so it never flickers
        player.addEffect(new MobEffectInstance(effect, 100, Math.max(0, cfg.balancedEffectAmplifier),
                true, false, false));
    }

    // =========================================================
    // ******* script / quest mirror ************************** //
    // =========================================================

    /**
     * Exposes the values to scripts:
     * <ul>
     *   <li>scoreboard objectives {@code gtmfo_<nutrient>} (FTB Quests, command blocks, ...)</li>
     *   <li>player {@code persistentData} floats {@code gtmfo_nutrient_<nutrient>} (easy to read
     *       from KubeJS: {@code player.persistentData.getFloat("gtmfo_nutrient_dairy")})</li>
     * </ul>
     */
    private static void mirror(Player player, NutrientsTracker tracker) {
        if (!GTMFOConfigHolder.INSTANCE.gtfoNutrientConfig.scoreboardMirror) return;

        // KubeJS friendly mirror
        net.minecraft.nbt.CompoundTag persistent = player.getPersistentData();
        for (String name : Nutrients.LIST) {
            persistent.putFloat("gtmfo_nutrient_" + name, tracker.get(name));
        }

        if (!(player.level() instanceof ServerLevel server)) return;
        Scoreboard scoreboard = server.getScoreboard();
        String holder = player.getScoreboardName();
        for (String name : Nutrients.LIST) {
            String objectiveName = scoreboardObjective(name);
            Objective objective = scoreboard.getObjective(objectiveName);
            if (objective == null) {
                objective = scoreboard.addObjective(objectiveName, ObjectiveCriteria.DUMMY,
                        Component.translatable(Nutrients.langKey(name)), ObjectiveCriteria.RenderType.INTEGER);
            }
            Score score = scoreboard.getOrCreatePlayerScore(holder, objective);
            score.setScore((int) Math.floor(tracker.get(name)));
        }
    }
}
