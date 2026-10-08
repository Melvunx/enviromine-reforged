package net.melvunx.envirominereforgedmod.thirst;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Toutes les règles et constantes d'équilibrage au même endroit.
 * Ce sont des fonctions "pures" : faciles à tester et à ajuster.
 * Plus tard : lire ces valeurs depuis un fichier de config.
 */
public final class ThirstRules {
    private ThirstRules() {}

    // --- Mécanique d'épuisement (même idée que la faim) ---
    /** Quand l'épuisement atteint ce seuil, on perd 1 point de soif. */
    public static final float EXHAUSTION_THRESHOLD = 4.0f;
    /** Au repos en climat tempéré : 1 point de soif toutes les 240 s (20 pts ≈ 80 min). */
    public static final float BASE_EXHAUSTION_PER_TICK = EXHAUSTION_THRESHOLD / (240f * 20f);

    // --- Boire ---
    public static final int WATER_BOTTLE_RESTORE = 6;
    public static final int WATER_HAND_RESTORE = 2;
    public static final int DRINK_COOLDOWN_TICKS = 10;

    // --- Déshydratation totale ---
    public static final int DAMAGE_INTERVAL_TICKS = 80;

    /** Épuisement gagné ce tick, selon l'activité et l'environnement. */
    public static float exhaustionPerTick(PlayerEntity player, float environmentMultiplier) {
        float rate = BASE_EXHAUSTION_PER_TICK;

        if (player.isSwimming()) {
            rate *= 2.0f;
        } else if (player.isSprinting()) {
            rate *= 3.0f;
        }
        return rate * environmentMultiplier;
    }

    /**
     * Multiplicateur lié à l'environnement.
     * Pour l'instant : température du biome + dimension + pluie.
     * Quand tu auras la stat "température corporelle", c'est CETTE méthode
     * que tu remplaceras par une lecture de la température du joueur.
     */
    public static float environmentMultiplier(PlayerEntity player) {
        World world = player.getWorld();

        if (world.getRegistryKey() == World.NETHER) {
            return 2.5f;
        }

        BlockPos pos = player.getBlockPos();
        // Températures vanilla : neige ~0.0, plaine 0.8, jungle 0.95, désert/savane 2.0
        float biomeTemp = world.getBiome(pos).value().getTemperature();

        // 0.0 -> x0.5 | 0.8 -> x1.0 | 2.0 -> x1.75
        float multiplier = MathHelper.clamp(0.5f + biomeTemp * 0.625f, 0.5f, 2.0f);

        if (world.hasRain(pos)) {
            multiplier *= 0.8f;
        }
        return multiplier;
    }
}
