package net.melvunx.envirominereforgedmod.entity;

import net.melvunx.envirominereforgedmod.thirst.PlayerThirst;
import net.melvunx.envirominereforgedmod.thirst.Thirst;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;

public class EntityModComponents implements EntityComponentInitializer {
    // Clé pour accéder au composant
    public static final ComponentKey<Thirst> THIRST =
            ComponentRegistry.getOrCreate(Identifier.of("enviromine", "thirst"), Thirst.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        // Attaché à tous les joueurs
        registry.registerForPlayers(
                THIRST,
                PlayerThirst::new,
                RespawnCopyStrategy.ALWAYS_COPY // Conserve la valeur à la mort/respawn (si désiré)
        );
    }
}
