package net.melvunx.envirominereforgedmod.thirst;

import net.melvunx.envirominereforgedmod.entity.EntityModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;


public class PlayerThirst implements Thirst{
    private final PlayerEntity player;
    private int thirst = 20; // Initial value

    public PlayerThirst(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public int getThirst() {
        return this.thirst;
    }

    @Override
    public void setThirst(int thirst) {
        this.thirst = Math.clamp(thirst, 0, 20);
        EntityModComponents.THIRST.sync(this.player);
    }

    @Override
    public void addThirst(int amount) {
        setThirst(this.thirst + amount);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("Thirst")) {
            this.thirst = tag.getInt("Thirst");
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("Thirst", this.thirst);
    }
}
