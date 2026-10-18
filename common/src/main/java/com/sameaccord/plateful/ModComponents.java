package com.sameaccord.plateful;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public final class ModComponents {
	/**
	 * When a food stack started going off and the in-game day it rots on. Stored in whole days so food
	 * picked up on the same day still stacks together.
	 */
	public record Freshness(int madeDay, int rottenDay) {
		public static final Codec<Freshness> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("made_day").forGetter(Freshness::madeDay),
			Codec.INT.fieldOf("rotten_day").forGetter(Freshness::rottenDay)
		).apply(instance, Freshness::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, Freshness> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, Freshness::madeDay,
			ByteBufCodecs.VAR_INT, Freshness::rottenDay,
			Freshness::new);
	}

	public static final DataComponentType<Freshness> FRESHNESS = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE,
		Plateful.id("freshness"),
		DataComponentType.<Freshness>builder().persistent(Freshness.CODEC).networkSynchronized(Freshness.STREAM_CODEC).build());

	private ModComponents() {
	}

	/** Registers the component types (by loading this class). Each loader calls it when they may be registered. */
	public static void init() {
	}
}
