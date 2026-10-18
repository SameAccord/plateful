package com.sameaccord.plateful.platform;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import com.sameaccord.plateful.FoodHistory;
import com.sameaccord.plateful.Plateful;
import com.sameaccord.plateful.platform.services.IPlatformHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
		DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Plateful.MOD_ID);

	private static final Supplier<AttachmentType<List<FoodHistory.Entry>>> FOOD_HISTORY = ATTACHMENT_TYPES.register(
		"food_history",
		() -> AttachmentType.<List<FoodHistory.Entry>>builder(() -> List.of())
			.serialize(FoodHistory.LIST_CODEC.fieldOf("entries"))
			.copyOnDeath()
			.build());
	private static final Supplier<AttachmentType<List<Identifier>>> RECENT_MEALS = ATTACHMENT_TYPES.register(
		"recent_meals",
		() -> AttachmentType.<List<Identifier>>builder(() -> List.of())
			.serialize(Identifier.CODEC.listOf().fieldOf("meals"))
			.copyOnDeath()
			.build());

	@Override
	public List<FoodHistory.Entry> getFoodHistory(ServerPlayer player) {
		return player.getData(FOOD_HISTORY);
	}

	@Override
	public void setFoodHistory(ServerPlayer player, List<FoodHistory.Entry> history) {
		player.setData(FOOD_HISTORY, history);
	}

	@Override
	public List<Identifier> getRecentMeals(ServerPlayer player) {
		return player.getData(RECENT_MEALS);
	}

	@Override
	public void setRecentMeals(ServerPlayer player, List<Identifier> meals) {
		player.setData(RECENT_MEALS, meals);
	}

	@Override
	public Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}
}
