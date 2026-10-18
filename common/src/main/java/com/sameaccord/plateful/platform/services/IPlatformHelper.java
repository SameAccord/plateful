package com.sameaccord.plateful.platform.services;

import java.nio.file.Path;
import java.util.List;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import com.sameaccord.plateful.FoodHistory;

/** The few things common code needs from the mod loader. */
public interface IPlatformHelper {
	/** The player's remembered foods, most recent first. Must survive death and relogging. */
	List<FoodHistory.Entry> getFoodHistory(ServerPlayer player);

	void setFoodHistory(ServerPlayer player, List<FoodHistory.Entry> history);

	/** The player's last few meals (with repeats), most recent first. Must survive death and relogging. */
	List<Identifier> getRecentMeals(ServerPlayer player);

	void setRecentMeals(ServerPlayer player, List<Identifier> meals);

	/** The game's config folder. */
	Path getConfigDir();
}
