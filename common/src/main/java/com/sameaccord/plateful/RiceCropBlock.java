package com.sameaccord.plateful;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

/**
 * Rice grows like a paddy crop: it has to be planted in a still water source that is one block
 * deep, on top of dirt, mud, clay or farmland. The block keeps the water around it (like
 * seagrass), so breaking the plant leaves the water behind.
 *
 * Because it always has water, it grows at the speed of a well-watered farmland crop. From
 * {@link #TALL_AGE} on, the plant is two blocks tall: it places a {@link RiceUpperBlock} above
 * itself, and won't grow past that point while the space above is blocked.
 */
public class RiceCropBlock extends CropBlock implements LiquidBlockContainer {
	/** 1 in this many random ticks advances the crop, matching wheat on moist farmland. */
	private static final int GROWTH_CHANCE = 7;
	/** First age at which the plant grows its top half. */
	public static final int TALL_AGE = 4;

	public RiceCropBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return ModItems.RICE_SEEDS;
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
		return state.is(BlockTags.DIRT) || state.is(Blocks.FARMLAND) || state.is(Blocks.CLAY);
	}

	@Override
	protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
		BlockPos below = pos.below();
		// Only shallow water: nothing but air (or a solid block) may sit above the plant's water.
		return mayPlaceOn(level.getBlockState(below), level, below) && level.getFluidState(pos.above()).isEmpty();
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
		boolean inStillWater = fluid.is(FluidTags.WATER) && fluid.getAmount() == 8;
		return inStillWater ? super.getStateForPlacement(context) : null;
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int age = getAge(state);
		if (level.getRawBrightness(pos, 0) < 9) {
			return;
		}
		if (age < getMaxAge() && random.nextInt(GROWTH_CHANCE) == 0) {
			growTo(level, pos, age + 1);
		} else if (age >= TALL_AGE && level.getBlockState(pos.above()).isAir()) {
			growTo(level, pos, age);      // regrow a top half that was removed (e.g. by an explosion)
		}
	}

	@Override
	public void growCrops(Level level, BlockPos pos, BlockState state) {
		growTo(level, pos, Math.min(getAge(state) + getBonemealAgeIncrease(level), getMaxAge()));
	}

	/** Sets the plant's age, adding or updating the top half; stays short if the space above is taken. */
	private void growTo(Level level, BlockPos pos, int age) {
		if (age >= TALL_AGE) {
			BlockPos above = pos.above();
			BlockState aboveState = level.getBlockState(above);
			if (aboveState.isAir() || aboveState.is(ModBlocks.RICE_CROP_UPPER)) {
				level.setBlock(pos, getStateForAge(age), Block.UPDATE_CLIENTS);
				level.setBlock(above, ModBlocks.RICE_CROP_UPPER.defaultBlockState().setValue(RiceUpperBlock.AGE, age),
					Block.UPDATE_ALL);
				return;
			}
			age = TALL_AGE - 1;
		}
		level.setBlock(pos, getStateForAge(age), Block.UPDATE_CLIENTS);
	}

	@Override
	protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
			Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
		BlockState updated = super.updateShape(state, level, ticks, pos, direction, neighborPos, neighborState, random);
		if (!updated.isAir()) {
			ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
		}
		return updated;
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return Fluids.WATER.getSource(false);
	}

	@Override
	public boolean canPlaceLiquid(LivingEntity entity, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		return false;
	}

	@Override
	public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
		return false;
	}
}
