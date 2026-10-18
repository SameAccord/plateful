package com.sameaccord.plateful;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.level.gamerules.GameRuleType;
import net.minecraft.world.level.gamerules.GameRuleTypeVisitor;

/**
 * Per-world switches for food depreciation, e.g. {@code /gamerule plateful:food_spoilage false}.
 * Their defaults come from {@link PlatefulConfig}, so the config must be loaded first.
 */
public final class ModGameRules {
	public static final GameRule<Boolean> DIMINISHING_RETURNS =
		bool("diminishing_returns", PlatefulConfig.get().diminishingReturns);
	public static final GameRule<Integer> REPEAT_PENALTY_PERCENT =
		integer("repeat_penalty_percent", PlatefulConfig.get().repeatPenaltyPercent, 0, 100);
	public static final GameRule<Boolean> FOOD_SPOILAGE =
		bool("food_spoilage", PlatefulConfig.get().foodSpoilage);
	public static final GameRule<Integer> FOOD_SPOIL_DAYS =
		integer("food_spoil_days", PlatefulConfig.get().foodSpoilDays, 1, 1000);

	private ModGameRules() {
	}

	private static GameRule<Boolean> bool(String name, boolean defaultValue) {
		return Registry.register(BuiltInRegistries.GAME_RULE, Plateful.id(name), new GameRule<>(
			GameRuleCategory.PLAYER, GameRuleType.BOOL, BoolArgumentType.bool(), GameRuleTypeVisitor::visitBoolean,
			Codec.BOOL, value -> value ? 1 : 0, defaultValue, FeatureFlagSet.of()));
	}

	private static GameRule<Integer> integer(String name, int defaultValue, int min, int max) {
		return Registry.register(BuiltInRegistries.GAME_RULE, Plateful.id(name), new GameRule<>(
			GameRuleCategory.PLAYER, GameRuleType.INT, IntegerArgumentType.integer(min, max), GameRuleTypeVisitor::visitInteger,
			Codec.intRange(min, max), value -> value, defaultValue, FeatureFlagSet.of()));
	}

	/** Registers the game rules (by loading this class). Each loader calls it when game rules may be registered. */
	public static void init() {
	}
}
