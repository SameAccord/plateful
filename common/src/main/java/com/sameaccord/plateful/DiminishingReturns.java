package com.sameaccord.plateful;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import com.sameaccord.plateful.platform.Services;

/**
 * Eating the same food again within your last few meals restores less: each earlier serving in that
 * window takes {@code repeat_penalty_percent} off, down to the config's {@code minimum_percent}.
 * Eating something else lets a food drop out of the window again.
 */
public final class DiminishingReturns {
	private DiminishingReturns() {
	}

	/** Percent of its normal value that this food would restore for the player right now. */
	public static int percentFor(ServerPlayer player, Identifier food) {
		ServerLevel level = (ServerLevel) player.level();
		if (!level.getGameRules().get(ModGameRules.DIMINISHING_RETURNS)) {
			return 100;
		}
		long repeats = Services.PLATFORM.getRecentMeals(player).stream().filter(food::equals).count();
		int penalty = level.getGameRules().get(ModGameRules.REPEAT_PENALTY_PERCENT);
		return (int) Math.max(Math.min(100, PlatefulConfig.get().minimumPercent), 100 - penalty * repeats);
	}

	public static int percentFor(ServerPlayer player, ItemStack stack) {
		return percentFor(player, BuiltInRegistries.ITEM.getKey(stack.getItem()));
	}

	/** Remembers that the player just ate this food. */
	public static void recordMeal(ServerPlayer player, ItemStack stack) {
		List<Identifier> meals = new ArrayList<>();
		meals.add(BuiltInRegistries.ITEM.getKey(stack.getItem()));
		for (Identifier meal : Services.PLATFORM.getRecentMeals(player)) {
			if (meals.size() >= PlatefulConfig.get().mealsRemembered) {
				break;
			}
			meals.add(meal);
		}
		Services.PLATFORM.setRecentMeals(player, List.copyOf(meals));
	}
}
