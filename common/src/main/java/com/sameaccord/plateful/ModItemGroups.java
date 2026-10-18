package com.sameaccord.plateful;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModItemGroups {
	public static final ResourceKey<CreativeModeTab> PLATEFUL_KEY =
		ResourceKey.create(Registries.CREATIVE_MODE_TAB, Plateful.id("main"));

	public static final CreativeModeTab PLATEFUL = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, PLATEFUL_KEY,
		CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
			.title(Component.translatable("itemGroup.plateful.main"))
			.icon(() -> new ItemStack(ModItems.COOKED_EGG))
			.displayItems((parameters, output) -> {
				output.accept(ModItems.FOOD_JOURNAL);
				output.accept(ModItems.RICE_SEEDS);
				output.accept(ModItems.RICE);
				output.accept(ModItems.LETTUCE_SEEDS);
				output.accept(ModItems.LETTUCE);
				output.accept(ModItems.TOMATO_SEEDS);
				output.accept(ModItems.TOMATO);
				output.accept(ModItems.CHEESE);
				output.accept(ModItems.TORTILLA);
				output.accept(ModItems.NOODLES);
				output.accept(ModItems.TOAST);
				output.accept(ModItems.COOKED_EGG);
				output.accept(ModItems.HAMBURGER);
				output.accept(ModItems.CHEESEBURGER);
				output.accept(ModItems.DELUXE_HAMBURGER);
				output.accept(ModItems.BLT);
				output.accept(ModItems.EGG_SANDWICH);
				output.accept(ModItems.CHICKEN_SANDWICH);
				output.accept(ModItems.DELUXE_CHICKEN_SANDWICH);
				output.accept(ModItems.TACO);
				output.accept(ModItems.CHEESE_QUESADILLA);
				output.accept(ModItems.CHICKEN_QUESADILLA);
				output.accept(ModItems.STEAK_QUESADILLA);
				output.accept(ModItems.SALMON_SUSHI);
				output.accept(ModItems.SALMON_ROLL);
				output.accept(ModItems.FRIED_RICE);
				output.accept(ModItems.BEEF_RICE_BOWL);
				output.accept(ModItems.CHICKEN_RICE_BOWL);
				output.accept(ModItems.SPAGHETTI);
				output.accept(ModItems.GARDEN_SALAD);
				output.accept(ModItems.CHICKEN_SALAD);
				output.accept(ModItems.TOMATO_SOUP);
				output.accept(ModItems.CHICKEN_SOUP);
				output.accept(ModItems.CHICKEN_NOODLE_SOUP);
				output.accept(ModItems.BEEF_STEW);
				output.accept(ModItems.MASHED_POTATOES);
				output.accept(ModItems.LOADED_BAKED_POTATO);
				output.accept(ModItems.BROWNIE);
				output.accept(ModItems.CHOCOLATE_CAKE);
			})
			.build());

	/** Registers the creative tab (by loading this class). Each loader calls it when creative tab may be registered. */
	public static void init() {
	}
}
