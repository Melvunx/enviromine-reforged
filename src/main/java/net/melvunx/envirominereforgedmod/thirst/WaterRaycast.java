package net.melvunx.envirominereforgedmod.thirst;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

/**
 * Le rayon du viseur de Minecraft ignore les fluides. On lance donc le nôtre
 * avec SOURCE_ONLY (comme le fait la bouteille vide).
 * Utilisé par le client (pour éviter d'envoyer des paquets inutiles)
 * ET par le serveur (pour valider : ne jamais faire confiance au client).
 */
public final class WaterRaycast {
    private WaterRaycast() {}

    public static boolean isLookingAtWater(World world, PlayerEntity player) {
        Vec3d start = player.getEyePos();
        Vec3d end = start.add(player.getRotationVec(1.0f).multiply(player.getBlockInteractionRange()));

        BlockHitResult hit = world.raycast(new RaycastContext(
                start, end,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.SOURCE_ONLY,
                player
        ));

        return hit.getType() == HitResult.Type.BLOCK
                && world.getFluidState(hit.getBlockPos()).isIn(FluidTags.WATER);
    }
}
