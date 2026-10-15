package net.melvunx.envirominereforgedmod.thirst;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;

/**
 * Toutes les règles et constantes d'équilibrage de la soif au même endroit.
 * Ce sont des fonctions "pures" : faciles à tester et à ajuster.
 */
public final class ThirstRules {
    private ThirstRules() {}

    // --- Mécanique d'épuisement (même idée que la faim) ---
    /** Quand l'épuisement atteint ce seuil, on perd 1 point de soif. */
    public static final float EXHAUSTION_THRESHOLD = 4.0f;
    /** Au repos à température normale : 1 point de soif toutes les 240 s (20 pts ≈ 80 min). */
    public static final float BASE_EXHAUSTION_PER_TICK = EXHAUSTION_THRESHOLD / (240f * 20f);

    // --- Boire ---
    public static final int WATER_BOTTLE_RESTORE = 6;
    public static final int WATER_HAND_RESTORE = 2;
    public static final int DRINK_COOLDOWN_TICKS = 10;

    // --- Déshydratation totale ---
    public static final int DAMAGE_INTERVAL_TICKS = 80;

    /** Épuisement gagné ce tick, selon l'activité et la température corporelle. */
    public static float exhaustionPerTick(PlayerEntity player, float temperatureMultiplier) {
        float rate = BASE_EXHAUSTION_PER_TICK;

        if (player.isSwimming()) {
            rate *= 2.0f;
        } else if (player.isSprinting()) {
            rate *= 3.0f;
        }
        return rate * temperatureMultiplier;
    }

    /**
     * Multiplicateur de soif selon la température du CORPS (et non plus du biome :
     * le biome, l'heure, le feu, l'eau... passent tous par la température corporelle).
     *   35 °C ou moins -> x0.8 (on transpire moins)
     *   37 °C          -> x1.0
     *   38 °C          -> x1.75
     *   39 °C          -> x2.5
     *   39.7 °C et +   -> x3.0 (plafond)
     */
    public static float temperatureMultiplier(float bodyTemperature) {
        if (bodyTemperature <= 37.0f) {
            return MathHelper.clamp(0.8f + (bodyTemperature - 35.0f) * 0.1f, 0.8f, 1.0f);
        }
        return MathHelper.clamp(1.0f + (bodyTemperature - 37.0f) * 0.75f, 1.0f, 3.0f);
    }
}
