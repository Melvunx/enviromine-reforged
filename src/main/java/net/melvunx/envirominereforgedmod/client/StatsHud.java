package net.melvunx.envirominereforgedmod.client;

import net.melvunx.envirominereforgedmod.entity.EntityModComponents;
import net.melvunx.envirominereforgedmod.oxygen.Oxygen;
import net.melvunx.envirominereforgedmod.oxygen.OxygenRules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;

import java.util.Objects;

/** Affichage des stats à l'écran. Chaque stat a sa méthode : facile d'en ajouter une. */
public final class StatsHud {
    private StatsHud() {}

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;

        if (player == null || client.world == null || player.isSpectator() || player.isCreative()
                || client.options.hudHidden) {
            return;
        }

        int x = client.getWindow().getScaledWidth() / 2 + 10;
        int baseY = client.getWindow().getScaledHeight();

        // h-39 = faim, h-49 = bulles d'air vanilla
        drawThirst(context, client, player, x, baseY - 59);
        drawOxygen(context, client, player, x, baseY - 69);
    }

    private static void drawThirst(DrawContext context, MinecraftClient client, ClientPlayerEntity player, int x, int y) {
        int thirst = EntityModComponents.THIRST.get(player).getThirst();
        context.drawText(client.textRenderer, "Soif: " + thirst + "/20", x, y, 0x33B5E5, true);
    }

    private static void drawOxygen(DrawContext context, MinecraftClient client, ClientPlayerEntity player, int x, int y) {
        Oxygen oxygen = EntityModComponents.OXYGEN.get(player);

        // Jauge pleine ET air sain (proche de la surface) : rien à signaler, on masque.
        boolean nothingToReport = oxygen.isFull()
                && OxygenRules.airQuality(Objects.requireNonNull(client.world), player) >= OxygenRules.GOOD_AIR_QUALITY;
        if (nothingToReport) {
            return;
        }

        int value = oxygen.getOxygen();
        int color = value > 12 ? 0x55FF55 : value > OxygenRules.LOW_OXYGEN_THRESHOLD ? 0xFFAA00 : 0xFF5555;
        context.drawText(client.textRenderer, "Oxygène: " + value + "/20", x, y, color, true);
    }
}