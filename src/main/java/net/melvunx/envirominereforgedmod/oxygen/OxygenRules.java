package net.melvunx.envirominereforgedmod.oxygen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

/**
 * Règles de l'oxygène. Fonctions pures, appelées par le serveur (gameplay)
 * ET par le client (pour savoir s'il faut afficher le HUD) : une seule source de vérité.
 */
public final class OxygenRules {
    private OxygenRules() {}

    // --- Qualité de l'air : 1.0 = air pur, 0.0 = irrespirable ---
    /** Profondeur (en blocs sous la surface) jusqu'à laquelle l'air reste parfait. */
    public static final float SAFE_DEPTH = 10f;
    /** Profondeur à partir de laquelle l'air est irrespirable (sans ventilation). */
    public static final float MAX_DEPTH = 130f;
    /** Un trou ouvert vers le ciel améliore l'air d'au plus cette part (0.5 = jusqu'à +50 %). */
    public static final float VENTILATION_BONUS = 0.5f;
    /** En dessous de cette qualité, l'oxygène baisse. Au-dessus, il remonte. */
    public static final float GOOD_AIR_QUALITY = 0.8f;

    // --- Vitesse (en points d'oxygène) ---
    /** Pire cas (air à 0) : 1 point toutes les 12 s, soit 20 points en 4 min. */
    public static final float MAX_DRAIN_PER_TICK = 1f / (12f * 20f);
    /** Récupération en air sain : 0,5 point par seconde (jauge pleine en 40 s). */
    public static final float REGEN_PER_TICK = 0.5f / 20f;

    // --- Conséquences du manque d'air ---
    public static final int LOW_OXYGEN_THRESHOLD = 6;
    public static final int SUFFOCATION_DAMAGE_INTERVAL_TICKS = 40;

    /**
     * Qualité de l'air à la position des yeux du joueur (0.0 à 1.0).
     *  - la profondeur sous la surface (heightmap) fait baisser la qualité ;
     *  - la lumière du ciel (= ouverture vers l'extérieur) la remonte en partie.
     * Seule l'Overworld est concernée pour l'instant.
     */
    public static float airQuality(World world, PlayerEntity player) {
        if (world.getRegistryKey() != World.OVERWORLD) {
            return 1.0f;
        }

        BlockPos pos = BlockPos.ofFloored(player.getEyePos());

        int surfaceY = world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ());
        float depth = surfaceY - pos.getY();
        float depthQuality = 1.0f - MathHelper.clamp((depth - SAFE_DEPTH) / (MAX_DEPTH - SAFE_DEPTH), 0f, 1f);

        // Lumière du ciel 0..15 : 15 = à l'air libre. Elle ne dépend PAS de l'heure de la journée.
        float ventilation = world.getLightLevel(LightType.SKY, pos) / 15f;

        // Plus on est déjà bien, moins la ventilation apporte : le résultat reste dans [0, 1].
        return depthQuality + (1.0f - depthQuality) * ventilation * VENTILATION_BONUS;
    }

    /**
     * Variation d'oxygène (en points) pour CE tick.
     * Positif = on récupère, négatif = on en perd.
     */
    public static float oxygenChangePerTick(float airQuality, float drainMultiplier) {
        if (airQuality >= GOOD_AIR_QUALITY) {
            return REGEN_PER_TICK;
        }
        // 0.0 juste sous le seuil, 1.0 quand l'air est totalement irrespirable
        float severity = (GOOD_AIR_QUALITY - airQuality) / GOOD_AIR_QUALITY;
        return -MAX_DRAIN_PER_TICK * severity * drainMultiplier;
    }

    /**
     * Point d'extension pour les futurs équipements.
     * 1.0 = aucune protection. Avec un masque à gaz tu pourras renvoyer 0.0
     * (aucune perte) ou 0.25 (perte réduite de 75 %), en lisant l'item de l'emplacement de tête.
     */
    public static float drainMultiplier(PlayerEntity player) {
        return 1.0f;
    }
}