package com.sameaccord.plateful.platform;

import java.nio.file.Path;
import java.util.List;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import com.sameaccord.plateful.FoodHistory;
import com.sameaccord.plateful.Plateful;
import com.sameaccord.plateful.platform.services.IPlatformHelper;

public class FabricPlatformHelper implements IPlatformHelper {
	private static final AttachmentType<List<FoodHistory.Entry>> FOOD_HISTORY = AttachmentRegistry.create(
		Plateful.id("food_history"),
		builder -> builder.persistent(FoodHistory.LIST_CODEC).copyOnDeath().initializer(List::of));
	private static final AttachmentType<List<Identifier>> RECENT_MEALS = AttachmentRegistry.create(
		Plateful.id("recent_meals"),
		builder -> builder.persistent(Identifier.CODEC.listOf()).copyOnDeath().initializer(List::of));

	/** Registers the attachment types during mod initialisation. */
	public static void init() {
	}

	@Override
	public List<FoodHistory.Entry> getFoodHistory(ServerPlayer player) {
		return player.getAttachedOrElse(FOOD_HISTORY, List.of());
	}

	@Override
	public void setFoodHistory(ServerPlayer player, List<FoodHistory.Entry> history) {
		player.setAttached(FOOD_HISTORY, history);
	}

	@Override
	public List<Identifier> getRecentMeals(ServerPlayer player) {
		return player.getAttachedOrElse(RECENT_MEALS, List.of());
	}

	@Override
	public void setRecentMeals(ServerPlayer player, List<Identifier> meals) {
		player.setAttached(RECENT_MEALS, meals);
	}

	@Override
	public Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}
}
