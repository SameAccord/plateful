package com.sameaccord.plateful;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class ModItems {
	// Crops, each grown from its seeds
	public static final Item RICE_SEEDS = registerBlockItem("rice_seeds", ModBlocks.RICE_CROP, new Item.Properties());
	public static final Item RICE = register("rice", new Item.Properties());

	public static final Item LETTUCE_SEEDS = registerBlockItem("lettuce_seeds", ModBlocks.LETTUCE_CROP, new Item.Properties());
	public static final Item LETTUCE = register("lettuce", new Item.Properties().food(food(1, 0.3f)));

	public static final Item TOMATO_SEEDS = registerBlockItem("tomato_seeds", ModBlocks.TOMATO_CROP, new Item.Properties());
	public static final Item TOMATO = register("tomato", new Item.Properties().food(food(1, 0.3f)));

	// Cooked basics
	public static final Item TOAST = register("toast", new Item.Properties().food(food(6, 0.6f)));
	public static final Item COOKED_EGG = register("cooked_egg", new Item.Properties().food(food(4, 0.6f)));

	// Meals
	public static final Item HAMBURGER = register("hamburger", new Item.Properties().food(food(10, 0.8f)));
	public static final Item CHEESEBURGER = register("cheeseburger", new Item.Properties().food(food(12, 0.9f)));
	public static final Item DELUXE_HAMBURGER = register("deluxe_hamburger", new Item.Properties().food(food(13, 0.9f)));
	public static final Item BLT = register("blt", new Item.Properties().food(food(9, 0.8f)));
	public static final Item EGG_SANDWICH = register("egg_sandwich", new Item.Properties().food(food(7, 0.7f)));
	public static final Item CHICKEN_SANDWICH = register("chicken_sandwich", new Item.Properties().food(food(8, 0.7f)));
	public static final Item DELUXE_CHICKEN_SANDWICH = register("deluxe_chicken_sandwich", new Item.Properties().food(food(11, 0.9f)));
	public static final Item BEEF_STEW = meal("beef_stew", 12, 0.6f);
	public static final Item SALMON_SUSHI = register("salmon_sushi", new Item.Properties().food(food(4, 0.7f)));

	// Bowl meals: stack to 16 and give the bowl back when eaten
	public static final Item GARDEN_SALAD = meal("garden_salad", 6, 0.6f);
	public static final Item CHICKEN_SALAD = meal("chicken_salad", 9, 0.7f);
	public static final Item TOMATO_SOUP = meal("tomato_soup", 6, 0.6f);
	public static final Item CHICKEN_SOUP = meal("chicken_soup", 10, 0.7f);
	public static final Item FRIED_RICE = meal("fried_rice", 10, 0.7f);
	public static final Item BEEF_RICE_BOWL = meal("beef_rice_bowl", 11, 0.8f);
	public static final Item CHICKEN_RICE_BOWL = meal("chicken_rice_bowl", 10, 0.8f);
	public static final Item MASHED_POTATOES = meal("mashed_potatoes", 8, 0.7f);
	public static final Item SPAGHETTI = meal("spaghetti", 10, 0.8f);
	public static final Item CHICKEN_NOODLE_SOUP = meal("chicken_noodle_soup", 11, 0.8f);

	// Handheld food
	public static final Item SALMON_ROLL = register("salmon_roll", new Item.Properties().food(food(8, 0.8f)));
	public static final Item LOADED_BAKED_POTATO = register("loaded_baked_potato", new Item.Properties().food(food(10, 0.8f)));
	public static final Item BROWNIE = register("brownie", new Item.Properties().food(food(4, 0.3f)));
	public static final Item TACO = register("taco", new Item.Properties().food(food(11, 0.8f)));

	// Quesadillas: a tortilla folded over melted cheese
	public static final Item CHEESE_QUESADILLA = register("cheese_quesadilla", new Item.Properties().food(food(7, 0.7f)));
	public static final Item CHICKEN_QUESADILLA = register("chicken_quesadilla", new Item.Properties().food(food(10, 0.8f)));
	public static final Item STEAK_QUESADILLA = register("steak_quesadilla", new Item.Properties().food(food(11, 0.8f)));

	// Dough products and dairy
	public static final Item TORTILLA = register("tortilla", new Item.Properties().food(food(3, 0.4f)));
	public static final Item NOODLES = register("noodles", new Item.Properties());
	public static final Item CHEESE = register("cheese", new Item.Properties());

	public static final Item FOOD_JOURNAL = Registry.register(BuiltInRegistries.ITEM, itemKey("food_journal"),
		new FoodJournalItem(new Item.Properties().stacksTo(1).setId(itemKey("food_journal"))));

	public static final Item CHOCOLATE_CAKE = Registry.register(BuiltInRegistries.ITEM, itemKey("chocolate_cake"),
		new BlockItem(ModBlocks.CHOCOLATE_CAKE,
			new Item.Properties().stacksTo(1).useBlockDescriptionPrefix().setId(itemKey("chocolate_cake"))));

	private static FoodProperties food(int nutrition, float saturation) {
		return new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturation).build();
	}

	private static Item meal(String name, int nutrition, float saturation) {
		return register(name, new Item.Properties().stacksTo(16).usingConvertsTo(Items.BOWL).food(food(nutrition, saturation)));
	}

	private static ResourceKey<Item> itemKey(String name) {
		return ResourceKey.create(Registries.ITEM, Plateful.id(name));
	}

	private static Item register(String name, Item.Properties properties) {
		ResourceKey<Item> key = itemKey(name);
		return Registry.register(BuiltInRegistries.ITEM, key, new Item(properties.setId(key)));
	}

	private static Item registerBlockItem(String name, Block block, Item.Properties properties) {
		ResourceKey<Item> key = itemKey(name);
		return Registry.register(BuiltInRegistries.ITEM, key,
			new BlockItem(block, properties.useItemDescriptionPrefix().setId(key)));
	}

	/** Registers the items (by loading this class). Each loader calls it when items may be registered. */
	public static void init() {
	}
}
