package net.melvunx.envirominereforgedmod.event;

import net.melvunx.envirominereforgedmod.entity.EntityModComponents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerThirstTickHandler {
    private static int tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            tickCounter++;

            // Every 100 ticks
            if (tickCounter >= 100) {
                tickCounter = 0;

                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    if (!player.isCreative() && !player.isSpectator()) {
                        EntityModComponents.THIRST.get(player).addThirst(-1);
                    }
                }
            }
        });
    }
}
