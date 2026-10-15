package net.melvunx.envirominereforgedmod.client;

import net.melvunx.envirominereforgedmod.entity.EntityModComponents;
import net.melvunx.envirominereforgedmod.oxygen.Oxygen;
import net.melvunx.envirominereforgedmod.oxygen.OxygenRules;
import net.melvunx.envirominereforgedmod.temperature.TemperatureRules;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;

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
        drawTemperature(context, client, player, x, baseY - 79);
    }

    private static void drawThirst(DrawContext context, MinecraftClient client, ClientPlayerEntity player, int x, int y) {
        int thirst = EntityModComponents.THIRST.get(player).getThirst();
        context.drawText(client.textRenderer, "Soif: " + thirst + "/20", x, y, 0x33B5E5, true);
    }

    private static void drawOxygen(DrawContext context, MinecraftClient client, ClientPlayerEntity player, int x, int y) {
        Oxygen oxygen = EntityModComponents.OXYGEN.get(player);

        // Jauge pleine ET air sain (proche de la surface) : rien à signaler, on masque.
        boolean nothingToReport = oxygen.isFull()
                && OxygenRules.airQuality(client.world, player) >= OxygenRules.GOOD_AIR_QUALITY;
        if (nothingToReport) {
            return;
        }

        int value = oxygen.getOxygen();
        int color = value > 12 ? 0x55FF55 : value > OxygenRules.LOW_OXYGEN_THRESHOLD ? 0xFFAA00 : 0xFF5555;
        context.drawText(client.textRenderer, "Oxygène: " + value + "/20", x, y, color, true);
    }

    private static void drawTemperature(DrawContext context, MinecraftClient client, ClientPlayerEntity player, int x, int y) {
        float t = EntityModComponents.BODY_TEMPERATURE.get(player).getTemperature();

        int color;
        if (t <= TemperatureRules.COLD_SEVERE) color = 0x5555FF;        // gelé
        else if (t <= TemperatureRules.COLD_MILD) color = 0x55AAFF;     // froid
        else if (t < TemperatureRules.HEAT_MILD) color = 0x55FF55;      // normal
        else if (t < TemperatureRules.HEAT_SEVERE) color = 0xFFAA00;    // chaud
        else color = 0xFF5555;                                          // brûlant

        context.drawText(client.textRenderer, String.format("Température: %.1f°C", t), x, y, color, true);
    }
}