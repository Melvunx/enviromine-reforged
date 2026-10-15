package net.melvunx.envirominereforgedmod.temperature;

import net.melvunx.envirominereforgedmod.EnviromineReforged;
import net.melvunx.envirominereforgedmod.entity.EntityModComponents;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;

public class PlayerBodyTemperature implements BodyTemperature {
    private final PlayerEntity player;

    // Sauvegardé
    private float temperature = TemperatureRules.NORMAL_BODY_TEMPERATURE;

    // Temporaire : recalculé 1 fois par seconde
    private float target = TemperatureRules.NORMAL_BODY_TEMPERATURE;

    public PlayerBodyTemperature(PlayerEntity player) {
        this.player = player;
    }

    @Override
    public float getTemperature() {
        return this.temperature;
    }

    @Override
    public void setTemperature(float value) {
        int tenthsBefore = Math.round(this.temperature * 10f);
        this.temperature = MathHelper.clamp(value,
                TemperatureRules.MIN_BODY_TEMPERATURE, TemperatureRules.MAX_BODY_TEMPERATURE);

        // On synchronise seulement quand la valeur AFFICHÉE (au dixième de degré) change
        if (Math.round(this.temperature * 10f) != tenthsBefore) {
            EntityModComponents.BODY_TEMPERATURE.sync(this.player);
        }
    }

    @Override
    public void serverTick() {
        if (this.player.isCreative() || this.player.isSpectator()) {
            return;
        }

        if (this.player.age % 20 == 0) {
            this.target = TemperatureRules.targetBodyTemperature(this.player.getWorld(), this.player);

            if (TemperatureRules.DEBUG) {
                EnviromineReforged.LOGGER.info("[Temp] ambiant={} cible={} corps={}",
                        TemperatureRules.ambientTemperature(this.player.getWorld(), this.player),
                        this.target, this.temperature);
            }
        }

        // Inertie : le corps comble une petite part de l'écart à chaque tick
        setTemperature(this.temperature + (this.target - this.temperature) * TemperatureRules.BODY_CHANGE_RATE);

        applyConsequences();
    }

    private void applyConsequences() {
        float t = this.temperature;

        // Effets rafraîchis chaque seconde (durée 3 s, sans particules)
        if (this.player.age % 20 == 10) {
            if (t <= TemperatureRules.COLD_SEVERE) {
                applyEffect(StatusEffects.SLOWNESS, 1);
                applyEffect(StatusEffects.MINING_FATIGUE, 0);
            } else if (t <= TemperatureRules.COLD_MILD) {
                applyEffect(StatusEffects.SLOWNESS, 0);
            }

            if (t >= TemperatureRules.HEAT_DEADLY) {
                applyEffect(StatusEffects.WEAKNESS, 0);
                applyEffect(StatusEffects.NAUSEA, 0);
            } else if (t >= TemperatureRules.HEAT_SEVERE) {
                applyEffect(StatusEffects.WEAKNESS, 0);
            }
        }

        // Dégâts en cas de température mortelle (plus tard : types de dégâts personnalisés)
        if (this.player.age % TemperatureRules.DAMAGE_INTERVAL_TICKS == 0) {
            if (t <= TemperatureRules.COLD_DEADLY) {
                this.player.damage(this.player.getDamageSources().freeze(), 1.0f);
            } else if (t >= TemperatureRules.HEAT_DEADLY) {
                this.player.damage(this.player.getDamageSources().generic(), 1.0f);
            }
        }
    }

    private void applyEffect(RegistryEntry<StatusEffect> effect, int amplifier) {
        this.player.addStatusEffect(new StatusEffectInstance(effect, 60, amplifier, true, false, true));
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (tag.contains("BodyTemperature")) {
            this.temperature = MathHelper.clamp(tag.getFloat("BodyTemperature"),
                    TemperatureRules.MIN_BODY_TEMPERATURE, TemperatureRules.MAX_BODY_TEMPERATURE);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putFloat("BodyTemperature", this.temperature);
    }
}
