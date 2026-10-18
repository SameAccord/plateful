package com.sameaccord.plateful;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class ModBlocks {
	public static final Block RICE_CROP = register("rice_crop",
		new RiceCropBlock(properties("rice_crop", Blocks.CARROTS)));

	public static final Block RICE_CROP_UPPER = register("rice_crop_upper",
		new RiceUpperBlock(properties("rice_crop_upper", Blocks.CARROTS).noLootTable()));

	public static final Block LETTUCE_CROP = register("lettuce_crop",
		new FoodCropBlock(properties("lettuce_crop", Blocks.CARROTS), () -> ModItems.LETTUCE_SEEDS));

	public static final Block TOMATO_CROP = register("tomato_crop",
		new FoodCropBlock(properties("tomato_crop", Blocks.CARROTS), () -> ModItems.TOMATO_SEEDS));

	public static final Block CHOCOLATE_CAKE = register("chocolate_cake",
		new ChocolateCakeBlock(properties("chocolate_cake", Blocks.CAKE)));

	public static final Block CHEESE_VAT = register("cheese_vat",
		new CheeseVatBlock(properties("cheese_vat", Blocks.CAULDRON).randomTicks()));

	private static BlockBehaviour.Properties properties(String name, Block copyFrom) {
		return BlockBehaviour.Properties.ofFullCopy(copyFrom).setId(blockKey(name));
	}

	private static ResourceKey<Block> blockKey(String name) {
		return ResourceKey.create(Registries.BLOCK, Plateful.id(name));
	}

	private static Block register(String name, Block block) {
		return Registry.register(BuiltInRegistries.BLOCK, blockKey(name), block);
	}

	/** Registers the blocks (by loading this class). Each loader calls it when blocks may be registered. */
	public static void init() {
	}
}
