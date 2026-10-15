package net.melvunx.envirominereforgedmod.temperature;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

/** Température corporelle du joueur, en degrés Celsius (normale : 37.0). */
public interface BodyTemperature extends AutoSyncedComponent, ServerTickingComponent {
    float getTemperature();

    void setTemperature(float temperature);
}
