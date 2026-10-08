package net.melvunx.envirominereforgedmod;

import net.fabricmc.api.ModInitializer;

import net.melvunx.envirominereforgedmod.event.DrinkWaterHandler;
import net.melvunx.envirominereforgedmod.event.PlayerThirstTickHandler;
import net.melvunx.envirominereforgedmod.event.WaterBlockInteractHandler;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnviromineReforged implements ModInitializer {
	public static final String MOD_ID = "enviromine-reforged-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final String AUTHOR = "melvunx";

	@Override
	public void onInitialize() {
		LOGGER.info(MOD_ID + " by " + AUTHOR);

		// Thrist
		PlayerThirstTickHandler.register();
		DrinkWaterHandler.register();
		WaterBlockInteractHandler.register();
	}

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
