package net.melvunx.envirominereforgedmod.event;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.melvunx.envirominereforgedmod.entity.EntityModComponents;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class WaterBlockInteractHandler {
    public static void register() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (!world.isClient() && hand == Hand.MAIN_HAND && player.getStackInHand(hand).isEmpty()) {
                // Vérifie si le bloc visé est de l'eau
                if (world.getBlockState(hitResult.getBlockPos()).isOf(Blocks.WATER)) {
                    var thirstComponent = EntityModComponents.THIRST.get(player);

                    // Si le joueur n'a pas sa jauge au maximum
                    if (thirstComponent.getThirst() < 20) {
                        thirstComponent.addThirst(3); // Restaure +3
                        return ActionResult.SUCCESS;
                    }
                }
            }
            return ActionResult.PASS;
        });
    }
}
