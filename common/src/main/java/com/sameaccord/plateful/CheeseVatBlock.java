package com.sameaccord.plateful;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * A cauldron full of milk that slowly turns into cheese.
 *
 * Right-click an empty cauldron with a milk bucket to start it. After a couple of random ticks
 * (a few minutes) it is ready; right-click it with any hand to collect the cheese and get the
 * empty cauldron back.
 */
public class CheeseVatBlock extends Block {
	public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 2);
	public static final int MAX_AGE = 2;
	public static final int CHEESE_PER_BATCH = 3;

	public CheeseVatBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(AGE, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AGE);
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return Blocks.CAULDRON.defaultBlockState().getShape(level, pos, context);
	}

	@Override
	protected boolean isRandomlyTicking(BlockState state) {
		return state.getValue(AGE) < MAX_AGE;
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		int age = state.getValue(AGE);
		if (age < MAX_AGE) {
			level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_CLIENTS);
		}
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (state.getValue(AGE) < MAX_AGE) {
			if (!level.isClientSide()) {
				player.sendOverlayMessage(Component.translatable("message.plateful.cheese_vat.curdling"));
			}
			return InteractionResult.SUCCESS;
		}
		if (!level.isClientSide()) {
			Block.popResource(level, pos.above(), new ItemStack(ModItems.CHEESE, CHEESE_PER_BATCH));
			level.setBlockAndUpdate(pos, Blocks.CAULDRON.defaultBlockState());
			level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
		}
		return InteractionResult.SUCCESS;
	}

	/**
	 * Starts a batch when a player uses a milk bucket on an empty cauldron. Each loader calls this
	 * from its "use item on block" event; returns PASS when it doesn't apply.
	 */
	public static InteractionResult useMilkOnCauldron(Player player, Level level, InteractionHand hand, BlockHitResult hit) {
		ItemStack held = player.getItemInHand(hand);
		BlockPos pos = hit.getBlockPos();
		if (player.isSpectator() || !held.is(Items.MILK_BUCKET) || !level.getBlockState(pos).is(Blocks.CAULDRON)) {
			return InteractionResult.PASS;
		}
		if (!level.isClientSide()) {
			level.setBlockAndUpdate(pos, ModBlocks.CHEESE_VAT.defaultBlockState());
			player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, new ItemStack(Items.BUCKET)));
			level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
		}
		return InteractionResult.SUCCESS;
	}
}
