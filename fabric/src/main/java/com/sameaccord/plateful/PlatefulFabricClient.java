package com.sameaccord.plateful;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

import net.minecraft.client.Minecraft;

/** Client-only setup: shows how fresh carried food is in its tooltip. */
public class PlatefulFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ItemTooltipCallback.EVENT.register((stack, context, flag, lines) -> {
			if (Minecraft.getInstance().level != null) {
				Spoilage.appendTooltip(stack, Minecraft.getInstance().level.getGameTime(), lines);
			}
		});
	}
}
