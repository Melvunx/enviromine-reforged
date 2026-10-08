package net.melvunx.envirominereforgedmod;

import net.fabricmc.api.ModInitializer;
import net.melvunx.envirominereforgedmod.network.ModNetworking;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnviromineReforged implements ModInitializer {
	public static final String MOD_ID = "enviromine-reforged-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String AUTHOR = "melvunx";

	@Override
	public void onInitialize() {
		LOGGER.info("{} by {}", MOD_ID, AUTHOR);

		// La logique de la soif vit dans le composant CCA (PlayerThirst#serverTick)
		// et dans le mixin PotionItemMixin : il ne reste que le réseau à enregistrer.
		ModNetworking.register();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}