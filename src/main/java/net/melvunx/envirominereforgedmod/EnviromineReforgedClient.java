package net.melvunx.envirominereforgedmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.melvunx.envirominereforgedmod.client.StatsHud;
import net.melvunx.envirominereforgedmod.entity.EntityModComponents;
import net.melvunx.envirominereforgedmod.network.DrinkWaterPayload;
import net.melvunx.envirominereforgedmod.thirst.ThirstRules;
import net.melvunx.envirominereforgedmod.thirst.WaterRaycast;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Hand;

public class EnviromineReforgedClient implements ClientModInitializer {
    private int drinkCooldown = 0;

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(StatsHud::render);
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    /** Détecte "clic droit maintenu, main vide, regard sur l'eau" et prévient le serveur. */
    private void onClientTick(MinecraftClient client) {
        if (this.drinkCooldown > 0) {
            this.drinkCooldown--;
        }

        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null || client.currentScreen != null) return;
        if (this.drinkCooldown > 0 || !client.options.useKey.isPressed()) return;
        if (player.isSpectator() || !player.getMainHandStack().isEmpty()) return;
        if (!WaterRaycast.isLookingAtWater(client.world, player)) return;

        ClientPlayNetworking.send(new DrinkWaterPayload());
        player.swingHand(Hand.MAIN_HAND);
        this.drinkCooldown = ThirstRules.DRINK_COOLDOWN_TICKS;
    }
}