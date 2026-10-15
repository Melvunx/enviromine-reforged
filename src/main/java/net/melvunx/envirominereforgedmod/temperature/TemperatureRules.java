package net.melvunx.envirominereforgedmod.temperature;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

/**
 * Le modèle en 3 étapes :
 *   1. AMBIANCE : quelle température fait-il autour du joueur ? (°C ambiants)
 *   2. CIBLE    : vers quelle température corporelle le corps veut-il tendre ?
 *   3. INERTIE  : le corps s'en approche lentement (PlayerBodyTemperature)
 */
public final class TemperatureRules {
    private TemperatureRules() {}

    /** Mets à true pour afficher dans la console les valeurs intermédiaires (réglage de l'équilibrage). */
    public static final boolean DEBUG = false;

    // --- Corps ---
    public static final float NORMAL_BODY_TEMPERATURE = 37.0f;
    public static final float MIN_BODY_TEMPERATURE = 28.0f;
    public static final float MAX_BODY_TEMPERATURE = 44.0f;
    /** Température ambiante où le corps est parfaitement à l'aise (°C). */
    public static final float COMFORT_AMBIENT = 22.0f;
    /** °C corporels gagnés/perdus par °C ambiant d'écart avec le confort. */
    public static final float BODY_SENSITIVITY = 0.09f;
    /** Part de l'écart comblée à chaque tick (1/1200 ≈ constante de temps de 60 s). */
    public static final float BODY_CHANGE_RATE = 1f / 1200f;
    /** Chaleur produite en sprintant (°C corporels sur la cible). */
    public static final float SPRINT_HEAT = 0.4f;

    // --- Environnement ---
    public static final float BASE_AMBIENT = 5f;
    /** Température vanilla du biome (0.0 neige, 0.8 plaine, 2.0 désert) -> °C : 5 + 14 * t. */
    public static final float BIOME_SCALE = 14f;
    public static final float DAY_NIGHT_AMPLITUDE = 6f;
    public static final float DESERT_EXTRA_AMPLITUDE = 6f;
    public static final float CAVE_AMBIENT = 14f;
    public static final float NETHER_AMBIENT = 50f;
    public static final float END_AMBIENT = 5f;
    public static final float WATER_COOLING = 8f;
    public static final float RAIN_COOLING = 4f;

    // --- Seuils (°C corporels) ---
    public static final float COLD_MILD = 35.0f;
    public static final float COLD_SEVERE = 33.0f;
    public static final float COLD_DEADLY = 31.0f;
    public static final float HEAT_MILD = 38.5f;
    public static final float HEAT_SEVERE = 39.5f;
    public static final float HEAT_DEADLY = 41.0f;
    public static final int DAMAGE_INTERVAL_TICKS = 40;

    /** Étapes 1 + 2 : température corporelle visée dans les conditions actuelles. */
    public static float targetBodyTemperature(World world, PlayerEntity player) {
        float ambient = ambientTemperature(world, player);
        float delta = ambient - COMFORT_AMBIENT;

        // Les protections (armures, objets) réduisent l'écart ressenti
        if (delta < 0) {
            delta *= 1f - MathHelper.clamp(coldProtection(player), 0f, 1f);
        } else {
            delta *= 1f - MathHelper.clamp(heatProtection(player), 0f, 1f);
        }

        float activityHeat = player.isSprinting() ? SPRINT_HEAT : 0f;

        return MathHelper.clamp(
                NORMAL_BODY_TEMPERATURE + delta * BODY_SENSITIVITY + activityHeat,
                MIN_BODY_TEMPERATURE, MAX_BODY_TEMPERATURE);
    }

    /** Étape 1 : température ambiante ressentie, en °C. */
    public static float ambientTemperature(World world, PlayerEntity player) {
        BlockPos pos = player.getBlockPos();

        float ambient;
        if (world.getRegistryKey() == World.NETHER) {
            ambient = NETHER_AMBIENT;
        } else if (world.getRegistryKey() == World.END) {
            ambient = END_AMBIENT;
        } else {
            ambient = overworldAmbient(world, pos);
        }

        if (player.isTouchingWater()) {
            ambient -= WATER_COOLING;
        } else if (world.hasRain(pos)) {
            ambient -= RAIN_COOLING;
        }

        return ambient + HeatSources.heatAround(world, pos);
    }

    private static float overworldAmbient(World world, BlockPos pos) {
        float biomeTemp = world.getBiome(pos).value().getTemperature();
        float ambient = BASE_AMBIENT + BIOME_SCALE * biomeTemp;

        // Cycle jour/nuit : maximum à midi (tick 6000), minimum à minuit (tick 18000).
        // Il ne se ressent qu'à l'air libre (lumière du ciel). Les déserts ont de plus grands écarts.
        float amplitude = DAY_NIGHT_AMPLITUDE
                + DESERT_EXTRA_AMPLITUDE * MathHelper.clamp(biomeTemp - 1f, 0f, 1f);
        float skyExposure = world.getLightLevel(LightType.SKY, pos) / 15f;
        float dayPhase = (world.getTimeOfDay() % 24000L) / 24000f;
        float dayCycle = MathHelper.cos((dayPhase - 0.25f) * MathHelper.TAU);
        ambient += dayCycle * amplitude * skyExposure;

        // Sous terre : la température converge vers celle des grottes (stable),
        // un peu plus chaude sous Y=0 (chaleur géothermique).
        int surfaceY = world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ());
        float depth = surfaceY - pos.getY();
        float underground = MathHelper.clamp((depth - 8f) / 40f, 0f, 1f);
        float geothermal = MathHelper.clamp(-pos.getY(), 0, 64) * 0.15f;
        return MathHelper.lerp(underground, ambient, CAVE_AMBIENT + geothermal);
    }

    /**
     * Points d'extension pour tes futurs vêtements / équipements : 0.0 = rien, 1.0 = immunité totale.
     * Ex. : une combinaison thermique renverra 0.6 dans coldProtection().
     */
    public static float coldProtection(PlayerEntity player) {
        return 0f;
    }

    public static float heatProtection(PlayerEntity player) {
        return 0f;
    }
}
