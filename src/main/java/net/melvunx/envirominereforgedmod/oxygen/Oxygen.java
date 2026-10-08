package net.melvunx.envirominereforgedmod.oxygen;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

/**
 * Stat "oxygène" : indépendante de la respiration sous l'eau de Minecraft.
 * Elle dépend de la qualité de l'air là où se trouve le joueur (profondeur, ventilation).
 */
public interface Oxygen extends AutoSyncedComponent, ServerTickingComponent {
    int MAX_OXYGEN = 20;

    /** Valeur affichable (arrondie au supérieur) : 0 seulement quand on est vraiment à sec. */
    int getOxygen();

    /** Vrai quand la jauge AFFICHÉE est au maximum (20/20). */
    boolean isFull();

    void setOxygen(float oxygen);

    void addOxygen(float amount);
}