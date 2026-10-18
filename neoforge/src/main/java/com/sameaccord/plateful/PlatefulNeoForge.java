package com.sameaccord.plateful;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import com.sameaccord.plateful.platform.NeoForgePlatformHelper;
import com.sameaccord.plateful.platform.Services;

/**
 * NeoForge entry point. NeoForge only allows registering inside its RegisterEvent, so the shared
 * registration classes are loaded from there, one registry at a time (blocks come before items).
 */
@Mod(Plateful.MOD_ID)
public class PlatefulNeoForge {
	public PlatefulNeoForge(IEventBus modBus) {
		PlatefulConfig.load(Services.PLATFORM.getConfigDir());   // before the game rules, which use it
		modBus.addListener(PlatefulNeoForge::register);
		NeoForgePlatformHelper.ATTACHMENT_TYPES.register(modBus);
		NeoForge.EVENT_BUS.addListener(PlatefulNeoForge::onRightClickBlock);
		NeoForge.EVENT_BUS.addListener(PlatefulNeoForge::onPlayerTick);
		NeoForge.EVENT_BUS.addListener(PlatefulNeoForge::onTooltip);
		Plateful.LOGGER.info("Plateful loaded");
	}

	private static void register(RegisterEvent event) {
		if (event.getRegistryKey().equals(Registries.DATA_COMPONENT_TYPE)) {
			ModComponents.init();
		} else if (event.getRegistryKey().equals(Registries.GAME_RULE)) {
			ModGameRules.init();
		} else if (event.getRegistryKey().equals(Registries.BLOCK)) {
			ModBlocks.init();
		} else if (event.getRegistryKey().equals(Registries.ITEM)) {
			ModItems.init();
		} else if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) {
			ModItemGroups.init();
		}
	}

	private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		InteractionResult result = CheeseVatBlock.useMilkOnCauldron(event.getEntity(), event.getLevel(), event.getHand(),
			event.getHitVec());
		if (result.consumesAction()) {
			event.setCanceled(true);
			event.setCancellationResult(result);
		}
	}

	private static void onPlayerTick(PlayerTickEvent.Post event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			Spoilage.tickPlayer(player);
		}
	}

	/** Only fires on the client; shows how fresh carried food is. */
	private static void onTooltip(ItemTooltipEvent event) {
		if (event.getEntity() != null) {
			Spoilage.appendTooltip(event.getItemStack(), event.getEntity().level().getGameTime(), event.getToolTip());
		}
	}
}
