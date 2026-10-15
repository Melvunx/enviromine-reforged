package net.melvunx.envirominereforgedmod.entity;

import net.melvunx.envirominereforgedmod.EnviromineReforged;
import net.melvunx.envirominereforgedmod.oxygen.Oxygen;
import net.melvunx.envirominereforgedmod.oxygen.PlayerOxygen;
import net.melvunx.envirominereforgedmod.temperature.BodyTemperature;
import net.melvunx.envirominereforgedmod.temperature.PlayerBodyTemperature;
import net.melvunx.envirominereforgedmod.thirst.PlayerThirst;
import net.melvunx.envirominereforgedmod.thirst.Thirst;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class EntityModComponents implements EntityComponentInitializer {
    public static final ComponentKey<Thirst> THIRST =
            ComponentRegistry.getOrCreate(Identifier.of(EnviromineReforged.MOD_ID, "thirst"), Thirst.class);

    public static final ComponentKey<Oxygen> OXYGEN =
            ComponentRegistry.getOrCreate(Identifier.of(EnviromineReforged.MOD_ID, "oxygen"), Oxygen.class);

    public static final ComponentKey<BodyTemperature> BODY_TEMPERATURE =
            ComponentRegistry.getOrCreate(Identifier.of(EnviromineReforged.MOD_ID, "body_temperature"), BodyTemperature.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(THIRST, PlayerThirst::new, RespawnCopyStrategy.LOSSLESS_ONLY);
        registry.registerForPlayers(OXYGEN, PlayerOxygen::new, RespawnCopyStrategy.LOSSLESS_ONLY);
        registry.registerForPlayers(BODY_TEMPERATURE, PlayerBodyTemperature::new, RespawnCopyStrategy.LOSSLESS_ONLY);
    }
}