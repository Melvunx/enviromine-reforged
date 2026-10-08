package net.melvunx.envirominereforgedmod.network;

import net.melvunx.envirominereforgedmod.EnviromineReforged;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

/** Paquet client -> serveur : "j'essaie de boire dans l'eau devant moi". Il ne contient aucune donnée. */
public record DrinkWaterPayload() implements CustomPayload {
    public static final Id<DrinkWaterPayload> ID =
            new Id<>(EnviromineReforged.id("drink_water"));

    public static final PacketCodec<RegistryByteBuf, DrinkWaterPayload> CODEC =
            PacketCodec.unit(new DrinkWaterPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
