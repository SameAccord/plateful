package com.sameaccord.plateful;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import com.sameaccord.plateful.ModComponents.Freshness;

/**
 * Food carried by players slowly goes off. The first time a food stack is seen in a player's
 * inventory it is stamped with the current day; it is fresh for the first third of
 * {@code food_spoil_days}, then stale, then spoiling, and finally turns into rotten flesh.
 * Stale and spoiling food restores less. Turning the game rule off removes the stamps.
 */
public final class Spoilage {
	/** Foods that never go off, e.g. golden apples or honey, when vanilla food spoils too. */
	public static final TagKey<Item> NEVER_SPOILS = TagKey.create(Registries.ITEM, Plateful.id("never_spoils"));

	private static final int TICKS_PER_DAY = 24000;
	/** How often (in ticks) a player's inventory is checked. */
	private static final int CHECK_INTERVAL = 40;

	public enum Stage {
		FRESH(100, ChatFormatting.GREEN),
		STALE(75, ChatFormatting.YELLOW),
		SPOILING(50, ChatFormatting.RED);

		public final int percent;
		public final ChatFormatting color;

		Stage(int percent, ChatFormatting color) {
			this.percent = percent;
			this.color = color;
		}
	}

	private Spoilage() {
	}

	public static int today(Level level) {
		return (int) (level.getGameTime() / TICKS_PER_DAY);
	}

	public static boolean canSpoil(ItemStack stack) {
		if (stack.isEmpty() || !stack.has(DataComponents.FOOD) || stack.is(NEVER_SPOILS)) {
			return false;
		}
		return PlatefulConfig.get().vanillaFoodSpoils
			|| BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace().equals(Plateful.MOD_ID);
	}

	public static Stage stage(Freshness freshness, int today) {
		double span = Math.max(1, freshness.rottenDay() - freshness.madeDay());
		double age = (today - freshness.madeDay()) / span;
		return age < 1.0 / 3 ? Stage.FRESH : age < 2.0 / 3 ? Stage.STALE : Stage.SPOILING;
	}

	/** Stamps new food, rots old food, or clears stamps when spoilage is off. Called every player tick. */
	public static void tickPlayer(ServerPlayer player) {
		if (player.tickCount % CHECK_INTERVAL != 0) {
			return;
		}
		ServerLevel level = (ServerLevel) player.level();
		boolean enabled = level.getGameRules().get(ModGameRules.FOOD_SPOILAGE);
		int today = today(level);
		Inventory inventory = player.getInventory();
		for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
			ItemStack stack = inventory.getItem(slot);
			Freshness freshness = stack.get(ModComponents.FRESHNESS);
			if (!enabled || !canSpoil(stack)) {
				if (freshness != null) {
					stack.remove(ModComponents.FRESHNESS);
				}
			} else if (freshness == null) {
				int days = level.getGameRules().get(ModGameRules.FOOD_SPOIL_DAYS);
				stack.set(ModComponents.FRESHNESS, new Freshness(today, today + days));
			} else if (today >= freshness.rottenDay()) {
				inventory.setItem(slot, new ItemStack(Items.ROTTEN_FLESH, stack.getCount()));
			}
		}
	}

	/** Percent of its normal value that this stack restores when eaten. */
	public static int percentFor(ServerPlayer player, ItemStack stack) {
		Freshness freshness = stack.get(ModComponents.FRESHNESS);
		ServerLevel level = (ServerLevel) player.level();
		if (freshness == null || !level.getGameRules().get(ModGameRules.FOOD_SPOILAGE)) {
			return 100;
		}
		return stage(freshness, today(level)).percent;
	}

	/** Adds "Fresh (3 days left)" style lines to a food's tooltip. Client side. */
	public static void appendTooltip(ItemStack stack, long gameTime, List<Component> lines) {
		Freshness freshness = stack.get(ModComponents.FRESHNESS);
		if (freshness == null) {
			return;
		}
		int today = (int) (gameTime / TICKS_PER_DAY);
		Stage stage = stage(freshness, today);
		int daysLeft = Math.max(0, freshness.rottenDay() - today);
		lines.add(Component.translatable("tooltip.plateful.freshness." + stage.name().toLowerCase(), daysLeft)
			.withStyle(stage.color));
	}
}
