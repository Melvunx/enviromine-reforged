package net.melvunx.envirominereforgedmod.mixin;

import net.melvunx.envirominereforgedmod.entity.EntityModComponents;
import net.melvunx.envirominereforgedmod.thirst.ThirstRules;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * finishUsing() est appelé quand le joueur A FINI de boire (pas au début du clic).
 * On s'accroche au début de la méthode, sans rien modifier de son comportement.
 */
@Mixin(PotionItem.class)
public class PotionItemMixin {

    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void enviromine$onFinishUsing(ItemStack stack, World world, LivingEntity user,
                                          CallbackInfoReturnable<ItemStack> cir) {
        if (world.isClient() || !(user instanceof ServerPlayerEntity player)) {
            return;
        }

        PotionContentsComponent contents =
                stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);

        if (contents.matches(Potions.WATER)) {
            EntityModComponents.THIRST.get(player).addThirst(ThirstRules.WATER_BOTTLE_RESTORE);
        }
    }
}
