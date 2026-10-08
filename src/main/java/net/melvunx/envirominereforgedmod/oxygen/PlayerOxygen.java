package net.melvunx.envirominereforgedmod.oxygen;

import net.melvunx.envirominereforgedmod.entity.EntityModComponents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.MathHelper;

public class PlayerOxygen implements Oxygen {
    private final PlayerEntity player;

    // Sauvegardé. Float en interne pour avoir des variations très lentes (< 1 point par tick).
    private float oxygen = MAX_OXYGEN;

    // Temporaire : la qualité de l'air est recalculée 1 fois par seconde seulement.
    private float airQuality = 1.0f;

    public PlayerOxygen(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public int getOxygen() {
        return (int) Math.ceil(this.oxygen);
    }

    @Override
    public boolean isFull() {
        return getOxygen() >= MAX_OXYGEN;
    }

    @Override
    public void setOxygen(float value) {
        int displayedBefore = getOxygen();
        this.oxygen = MathHelper.clamp(value, 0f, MAX_OXYGEN);

        // On ne synchronise que si la valeur AFFICHÉE change (au plus ~20 paquets sur toute la jauge)
        if (getOxygen() != displayedBefore) {
            EntityModComponents.OXYGEN.sync(this.player);
        }
    }

    @Override
    public void addOxygen(float amount) {
        setOxygen(this.oxygen + amount);
    }

    @Override
    public void serverTick() {
        if (this.player.isCreative() || this.player.isSpectator()) {
            return;
        }

        if (this.player.age % 20 == 0) {
            this.airQuality = OxygenRules.airQuality(this.player.getWorld(), this.player);
        }

        addOxygen(OxygenRules.oxygenChangePerTick(this.airQuality, OxygenRules.drainMultiplier(this.player)));

        // Essoufflement : on rafraîchit l'effet chaque seconde (durée 2 s, sans particules)
        if (getOxygen() <= OxygenRules.LOW_OXYGEN_THRESHOLD && this.player.age % 20 == 10) {
            this.player.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.SLOWNESS, 40, 0, true, false, true));
        }

        // Asphyxie : dégâts réguliers (plus tard : type de dégâts personnalisé via datagen)
        if (getOxygen() == 0 && this.player.age % OxygenRules.SUFFOCATION_DAMAGE_INTERVAL_TICKS == 0) {
            this.player.damage(this.player.getDamageSources().generic(), 1.0f);
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("Oxygen")) {
            this.oxygen = MathHelper.clamp(tag.getFloat("Oxygen"), 0f, MAX_OXYGEN);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putFloat("Oxygen", this.oxygen);
    }
}