package com.sameaccord.plateful;

import java.util.function.Supplier;

import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.CropBlock;

/** A farmland crop that grows like wheat and is replanted with the given item. */
public class FoodCropBlock extends CropBlock {
	private final Supplier<? extends ItemLike> seed;

	public FoodCropBlock(Properties properties, Supplier<? extends ItemLike> seed) {
		super(properties);
		this.seed = seed;
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return seed.get();
	}
}
