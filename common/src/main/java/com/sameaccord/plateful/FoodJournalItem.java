package com.sameaccord.plateful;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.Level;

/**
 * A book listing the foods the player ate most recently, with how much hunger and saturation
 * each one restores next time (after any repeat penalty). The pages are written fresh every time it
 * is opened, then shown with the vanilla book screen.
 */
public class FoodJournalItem extends Item {
	/** Entries that fit on a page; the first page also holds the heading. */
	private static final int FIRST_PAGE_ENTRIES = 2;
	private static final int ENTRIES_PER_PAGE = 3;

	public FoodJournalItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (player instanceof ServerPlayer serverPlayer) {
			ItemStack stack = player.getItemInHand(hand);
			stack.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(
				Filterable.passThrough("Food Journal"), player.getPlainTextName(), 0,
				pages(serverPlayer, FoodHistory.get(serverPlayer)), true));
			// send the new pages before asking the client to open the book
			serverPlayer.containerMenu.broadcastChanges();
			serverPlayer.openItemGui(stack, hand);
		}
		return InteractionResult.SUCCESS;
	}

	private static List<Filterable<Component>> pages(ServerPlayer player, List<FoodHistory.Entry> history) {
		List<Filterable<Component>> pages = new ArrayList<>();
		if (history.isEmpty()) {
			pages.add(Filterable.passThrough(heading().append(Component.translatable("book.plateful.food_journal.empty"))));
			return pages;
		}
		for (int start = 0; start < history.size(); ) {
			int count = start == 0 ? FIRST_PAGE_ENTRIES : ENTRIES_PER_PAGE;
			MutableComponent page = start == 0 ? heading() : Component.empty();
			List<FoodHistory.Entry> onPage = history.subList(start, Math.min(start + count, history.size()));
			start += count;
			for (FoodHistory.Entry entry : onPage) {
				Item item = BuiltInRegistries.ITEM.getValue(entry.item());
				// what it restores the next time it's eaten, after any repeat penalty
				int percent = DiminishingReturns.percentFor(player, entry.item());
				int nutrition = entry.nutrition() > 0 ? Math.max(1, Math.round(entry.nutrition() * percent / 100f)) : 0;
				MutableComponent values = Component.translatable("book.plateful.food_journal.values",
					nutrition, formatSaturation(entry.saturation() * percent / 100f));
				page.append(new ItemStack(item).getHoverName().copy().withStyle(ChatFormatting.DARK_BLUE, ChatFormatting.BOLD))
					.append("\n")
					.append(values.withStyle(percent < 100 ? ChatFormatting.DARK_RED : ChatFormatting.DARK_GREEN));
				if (percent < 100) {
					page.append(Component.translatable("book.plateful.food_journal.reduced", percent)
						.withStyle(ChatFormatting.DARK_RED));
				}
				page.append("\n\n");
			}
			pages.add(Filterable.passThrough(page));
		}
		return pages;
	}

	private static MutableComponent heading() {
		return Component.empty()
			.append(Component.translatable("book.plateful.food_journal.heading").withStyle(ChatFormatting.BOLD))
			.append("\n\n");
	}

	private static String formatSaturation(float saturation) {
		String text = String.format(Locale.ROOT, "%.1f", saturation);
		return text.endsWith(".0") ? text.substring(0, text.length() - 2) : text;
	}
}
