package net.melvunx.envirominereforgedmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.melvunx.envirominereforgedmod.entity.EntityModComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class EnviromineReforgedClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(this::onHudRender);
    }

    private void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null || client.player.isSpectator()) {
            return;
        }

        int thirstValue = EntityModComponents.THIRST.get(client.player).getThirst();

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        int x = screenWidth / 2 + 10;
        int y = screenHeight - 49;

        TextRenderer textRenderer = client.textRenderer;
        drawContext.drawText(textRenderer, "Soif: " + thirstValue + "/20", x, y, 0x33B5E5, true);
    }
}