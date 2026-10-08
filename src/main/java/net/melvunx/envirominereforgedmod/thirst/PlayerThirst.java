package net.melvunx.envirominereforgedmod.thirst;

import net.melvunx.envirominereforgedmod.entity.EntityModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;

public class PlayerThirst implements Thirst {
    private final PlayerEntity player;

    // Données sauvegardées
    private int thirst = MAX_THIRST;
    private float exhaustion = 0f;

    // Données temporaires (non sauvegardées)
    private int drinkCooldown = 0;
    private float environmentMultiplier = 1.0f;

    public PlayerThirst(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public int getThirst() {
        return this.thirst;
    }

    @Override
    public void setThirst(int value) {
        int clamped = Math.clamp(value, 0, MAX_THIRST);
        if (clamped == this.thirst) {
            return; // rien n'a changé : pas de paquet réseau inutile
        }
        this.thirst = clamped;
        EntityModComponents.THIRST.sync(this.player);
    }

    @Override
    public void addThirst(int amount) {
        setThirst(this.thirst + amount);
    }

    @Override
    public boolean drinkFromSource(int amount) {
        if (this.drinkCooldown > 0 || this.thirst >= MAX_THIRST) {
            return false;
        }
        addThirst(amount);
        this.drinkCooldown = ThirstRules.DRINK_COOLDOWN_TICKS;
        return true;
    }

    /** Appelé chaque tick serveur par CCA, pour CE joueur uniquement. */
    @Override
    public void serverTick() {
        if (this.drinkCooldown > 0) {
            this.drinkCooldown--;
        }

        if (this.player.isCreative() || this.player.isSpectator()) {
            return;
        }

        // Le biome change rarement : inutile de le relire 20 fois par seconde.
        if (this.player.age % 20 == 0) {
            this.environmentMultiplier = ThirstRules.environmentMultiplier(this.player);
        }

        this.exhaustion += ThirstRules.exhaustionPerTick(this.player, this.environmentMultiplier);
        if (this.exhaustion >= ThirstRules.EXHAUSTION_THRESHOLD) {
            this.exhaustion -= ThirstRules.EXHAUSTION_THRESHOLD;
            addThirst(-1);
        }

        // Déshydraté : dégâts réguliers (à remplacer plus tard par un vrai type de dégâts via datagen)
        if (this.thirst == 0 && this.player.age % ThirstRules.DAMAGE_INTERVAL_TICKS == 0) {
            this.player.damage(this.player.getDamageSources().starve(), 1.0f);
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        // contains() : getInt renvoie 0 si la clé manque, ce qui te mettrait à sec !
        if (tag.contains("Thirst")) {
            this.thirst = Math.clamp(tag.getInt("Thirst"), 0, MAX_THIRST);
        }
        if (tag.contains("Exhaustion")) {
            this.exhaustion = tag.getFloat("Exhaustion");
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("Thirst", this.thirst);
        tag.putFloat("Exhaustion", this.exhaustion);
    }
}