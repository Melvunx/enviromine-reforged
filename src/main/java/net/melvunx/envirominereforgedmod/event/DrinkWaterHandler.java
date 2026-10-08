package net.melvunx.envirominereforgedmod.event;

import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.melvunx.envirominereforgedmod.entity.EntityModComponents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.util.TypedActionResult;

public class DrinkWaterHandler {

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);

            // Verify if the player drinks regular water potion
            if (!world.isClient() && stack.isOf(Items.POTION)) {
                PotionContentsComponent potionContents = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);

                if (potionContents.matches(Potions.WATER)) {
                    // Add 5 thirst value
                    EntityModComponents.THIRST.get(player).addThirst(5);
                }
            }

            return TypedActionResult.pass(stack);
        });
    }
}