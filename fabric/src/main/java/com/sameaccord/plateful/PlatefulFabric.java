package com.sameaccord.plateful;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import com.sameaccord.plateful.platform.FabricPlatformHelper;
import com.sameaccord.plateful.platform.Services;

/** Fabric entry point: Fabric lets mods register straight away, so everything happens here. */
public class PlatefulFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		PlatefulConfig.load(Services.PLATFORM.getConfigDir());   // before the game rules, which use it
		ModComponents.init();
		ModGameRules.init();
		ModBlocks.init();
		ModItems.init();
		ModItemGroups.init();
		FabricPlatformHelper.init();
		FabricLootTables.init();
		UseBlockCallback.EVENT.register(CheeseVatBlock::useMilkOnCauldron);
		ServerTickEvents.END_SERVER_TICK.register(server -> server.getPlayerList().getPlayers().forEach(Spoilage::tickPlayer));
		Plateful.LOGGER.info("Plateful loaded");
	}
}
