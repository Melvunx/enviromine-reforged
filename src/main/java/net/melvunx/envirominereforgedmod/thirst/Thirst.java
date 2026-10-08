package net.melvunx.envirominereforgedmod.thirst;

import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public interface Thirst extends AutoSyncedComponent {
    int getThirst();
    void setThirst(int thirst);
    void addThirst(int amount);
}
