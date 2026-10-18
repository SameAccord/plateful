package com.sameaccord.plateful;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import com.sameaccord.plateful.platform.Services;

/**
 * Remembers the foods each player ate most recently, for the Food Journal. The list itself is stored
 * on the player by each loader (see {@link com.sameaccord.plateful.platform.services.IPlatformHelper}),
 * which keeps it through death and relogging.
 */
public final class FoodHistory {
	/** How many different foods the journal remembers. */
	public static final int MAX_ENTRIES = 12;

	/** One remembered food and what it restores. */
	public record Entry(Identifier item, int nutrition, float saturation) {
		public static final Codec<Entry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("item").forGetter(Entry::item),
			Codec.INT.fieldOf("nutrition").forGetter(Entry::nutrition),
			Codec.FLOAT.fieldOf("saturation").forGetter(Entry::saturation)
		).apply(instance, Entry::new));
	}

	public static final Codec<List<Entry>> LIST_CODEC = Entry.CODEC.listOf();

	private FoodHistory() {
	}

	/** Most recent first. */
	public static List<Entry> get(ServerPlayer player) {
		return Services.PLATFORM.getFoodHistory(player);
	}

	/** Called whenever a player finishes eating something. Re-eating a food moves it back to the top. */
	public static void record(ServerPlayer player, ItemStack stack, FoodProperties food) {
		Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
		List<Entry> updated = new ArrayList<>();
		updated.add(new Entry(id, food.nutrition(), food.saturation()));
		for (Entry entry : get(player)) {
			if (!entry.item().equals(id) && updated.size() < MAX_ENTRIES) {
				updated.add(entry);
			}
		}
		Services.PLATFORM.setFoodHistory(player, List.copyOf(updated));
	}
}
