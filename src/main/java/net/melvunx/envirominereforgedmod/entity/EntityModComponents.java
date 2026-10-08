package net.melvunx.envirominereforgedmod.entity;

import net.melvunx.envirominereforgedmod.EnviromineReforged;
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

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        // LOSSLESS_ONLY : conservé au retour de l'End, mais remis à zéro à la mort (comme la faim).
        // (vérifie la Javadoc de RespawnCopyStrategy dans ton IDE si le nom diffère)
        registry.registerForPlayers(THIRST, PlayerThirst::new, RespawnCopyStrategy.LOSSLESS_ONLY);
    }
}