package net.melvunx.envirominereforgedmod.thirst;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

/**
 * Contrat du composant "soif".
 * - AutoSyncedComponent : CCA envoie automatiquement les données au client.
 * - ServerTickingComponent : CCA appelle serverTick() chaque tick, par joueur.
 *   (on n'a donc plus besoin d'un handler global ServerTickEvents)
 */
public interface Thirst extends AutoSyncedComponent, ServerTickingComponent {
    int MAX_THIRST = 20;
    int getThirst();
    void setThirst(int thirst);
    void addThirst(int amount);

    /**
     * Boire à une source d'eau à la main.
     * @return true si le joueur a bu (pas en cooldown et pas déjà plein)
     */
    boolean drinkFromSource(int amount);

}
