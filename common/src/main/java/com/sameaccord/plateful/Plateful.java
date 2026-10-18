package com.sameaccord.plateful;

import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shared constants. The loader-specific entry points (PlatefulFabric, PlatefulNeoForge) register
 * {@link ModBlocks}, {@link ModItems} and {@link ModItemGroups} at the right time for their loader.
 */
public final class Plateful {
	public static final String MOD_ID = "plateful";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private Plateful() {
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
