package net.melvunx.envirominereforgedmod.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.melvunx.envirominereforgedmod.entity.EntityModComponents;
import net.melvunx.envirominereforgedmod.thirst.ThirstRules;
import net.melvunx.envirominereforgedmod.thirst.WaterRaycast;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public final class ModNetworking {
    private ModNetworking() {}

    public static void register() {
        // Le type de paquet doit être déclaré des deux côtés (appelé depuis le ModInitializer commun)
        PayloadTypeRegistry.playC2S().register(DrinkWaterPayload.ID, DrinkWaterPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(DrinkWaterPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();

            // Le serveur re-vérifie TOUT : le client peut mentir (triche).
            if (player.isSpectator() || !player.getMainHandStack().isEmpty()) return;
            if (!WaterRaycast.isLookingAtWater(player.getWorld(), player)) return;

            boolean drank = EntityModComponents.THIRST.get(player).drinkFromSource(ThirstRules.WATER_HAND_RESTORE);
            if (drank) {
                player.getWorld().playSound(null, player.getBlockPos(),
                        SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
        });
    }
}
