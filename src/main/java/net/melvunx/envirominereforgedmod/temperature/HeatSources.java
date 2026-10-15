package net.melvunx.envirominereforgedmod.temperature;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

/**
 * Sources de chaleur / de froid proches du joueur.
 * Les valeurs sont des °C AJOUTÉS à la température ambiante (à bout portant).
 * Plus la source est loin, moins elle compte (décroissance linéaire jusqu'à RADIUS + 1).
 *
 * Pour l'instant les valeurs sont écrites ici en Java. Plus tard : un fichier JSON / des tags
 * de blocs pour que les joueurs et les modpacks puissent les modifier (c'était la force de l'original).
 */
public final class HeatSources {
    private HeatSources() {}

    public static final int RADIUS = 3;
    public static final float MAX_HEAT = 40f;
    public static final float MAX_COLD = -30f;

    /** Scanne un cube de 7x7x7 (343 blocs). Appelé 1 fois par seconde et par joueur : coût négligeable. */
    public static float heatAround(World world, BlockPos center) {
        float total = 0f;
        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dy = -RADIUS; dy <= RADIUS; dy++) {
                for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                    pos.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    float heat = heatOf(world.getBlockState(pos));
                    if (heat == 0f) continue;

                    float distance = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
                    total += heat * Math.max(0f, 1f - distance / (RADIUS + 1));
                }
            }
        }
        return MathHelper.clamp(total, MAX_COLD, MAX_HEAT);
    }

    private static float heatOf(BlockState state) {
        if (state.isAir()) return 0f; // sortie rapide : la majorité des blocs scannés

        if (state.isOf(Blocks.LAVA)) return 20f;
        if (state.isIn(BlockTags.FIRE)) return 12f;
        if (state.isIn(BlockTags.CAMPFIRES)) return state.get(CampfireBlock.LIT) ? 14f : 0f;
        if (state.getBlock() instanceof AbstractFurnaceBlock) return state.get(AbstractFurnaceBlock.LIT) ? 8f : 0f;
        if (state.isOf(Blocks.MAGMA_BLOCK)) return 6f;
        if (state.isOf(Blocks.TORCH) || state.isOf(Blocks.WALL_TORCH)) return 2f;

        if (state.isIn(BlockTags.ICE)) return -6f;
        if (state.isOf(Blocks.SNOW_BLOCK)) return -3f;
        if (state.isOf(Blocks.POWDER_SNOW)) return -6f;

        return 0f;
    }
}
