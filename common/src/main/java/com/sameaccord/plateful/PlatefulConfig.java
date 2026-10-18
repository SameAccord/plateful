package com.sameaccord.plateful;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * The mod's config file, {@code config/plateful.json}. It sets the default value of each Plateful
 * game rule for new worlds, plus a few settings that only make sense globally. Individual worlds can
 * then be changed with {@code /gamerule plateful:<rule> <value>}.
 *
 * Missing keys are filled in with their defaults and written back, so the file always lists every
 * option. Changes take effect after a restart.
 */
public final class PlatefulConfig {
	public static final String FILE_NAME = "plateful.json";

	/** Default for the plateful:diminishing_returns game rule. */
	public boolean diminishingReturns = true;
	/** Default for plateful:repeat_penalty_percent: how much less a food restores per recent repeat. */
	public int repeatPenaltyPercent = 25;
	/** A repeated food never restores less than this percent of its normal value. */
	public int minimumPercent = 25;
	/** How many recent meals count towards the repeat penalty. */
	public int mealsRemembered = 6;

	/** Default for the plateful:food_spoilage game rule. */
	public boolean foodSpoilage = true;
	/** Default for plateful:food_spoil_days: in-game days before carried food rots. */
	public int foodSpoilDays = 10;
	/** Whether vanilla food spoils too, not just Plateful food. */
	public boolean vanillaFoodSpoils = false;

	private static PlatefulConfig instance = new PlatefulConfig();

	public static PlatefulConfig get() {
		return instance;
	}

	/** Reads the config file (creating it if needed). Call once, before the game rules are registered. */
	public static void load(Path configDir) {
		Path file = configDir.resolve(FILE_NAME);
		PlatefulConfig config = new PlatefulConfig();
		JsonObject json = new JsonObject();
		try {
			if (Files.exists(file)) {
				json = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
			}
		} catch (Exception e) {
			Plateful.LOGGER.error("Could not read {}, using default settings: {}", file, e.getMessage());
			instance = config;
			return;                                       // keep the broken file so the user can fix it
		}
		config.diminishingReturns = bool(json, "diminishing_returns", config.diminishingReturns);
		config.repeatPenaltyPercent = clamp(integer(json, "repeat_penalty_percent", config.repeatPenaltyPercent), 0, 100);
		config.minimumPercent = clamp(integer(json, "minimum_percent", config.minimumPercent), 0, 100);
		config.mealsRemembered = clamp(integer(json, "meals_remembered", config.mealsRemembered), 1, 64);
		config.foodSpoilage = bool(json, "food_spoilage", config.foodSpoilage);
		config.foodSpoilDays = clamp(integer(json, "food_spoil_days", config.foodSpoilDays), 1, 1000);
		config.vanillaFoodSpoils = bool(json, "vanilla_food_spoils", config.vanillaFoodSpoils);
		instance = config;
		config.save(file);
	}

	private void save(Path file) {
		JsonObject json = new JsonObject();
		json.addProperty("_about", "Defaults for new worlds. Change a single world with /gamerule plateful:<rule> <value>. "
			+ "Restart the game after editing this file.");
		json.addProperty("_diminishing_returns", "Eating the same food again within the last few meals restores less.");
		json.addProperty("diminishing_returns", diminishingReturns);
		json.addProperty("repeat_penalty_percent", repeatPenaltyPercent);
		json.addProperty("minimum_percent", minimumPercent);
		json.addProperty("meals_remembered", mealsRemembered);
		json.addProperty("_food_spoilage", "Food carried by players goes stale, then rots into rotten flesh after this many in-game days.");
		json.addProperty("food_spoilage", foodSpoilage);
		json.addProperty("food_spoil_days", foodSpoilDays);
		json.addProperty("vanilla_food_spoils", vanillaFoodSpoils);
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		try {
			Files.createDirectories(file.getParent());
			Files.writeString(file, gson.toJson(json) + System.lineSeparator());
		} catch (IOException e) {
			Plateful.LOGGER.error("Could not write {}: {}", file, e.getMessage());
		}
	}

	private static boolean bool(JsonObject json, String key, boolean fallback) {
		return json.has(key) && json.get(key).isJsonPrimitive() ? json.get(key).getAsBoolean() : fallback;
	}

	private static int integer(JsonObject json, String key, int fallback) {
		try {
			return json.has(key) ? json.get(key).getAsInt() : fallback;
		} catch (RuntimeException e) {
			return fallback;
		}
	}

	private static int clamp(int value, int min, int max) {
		return Math.max(min, Math.min(max, value));
	}
}
