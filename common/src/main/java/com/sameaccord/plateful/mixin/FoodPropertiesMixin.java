package com.sameaccord.plateful.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

import com.sameaccord.plateful.DiminishingReturns;
import com.sameaccord.plateful.FoodHistory;
import com.sameaccord.plateful.Spoilage;

/**
 * Runs whenever a player finishes eating any food (vanilla or modded): records it for the Food
 * Journal and scales what it restores for repeated meals and stale food.
 */
@Mixin(FoodProperties.class)
public class FoodPropertiesMixin {
	@WrapOperation(method = "onConsume",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(Lnet/minecraft/world/food/FoodProperties;)V"))
	private void plateful$adjustFood(FoodData foodData, FoodProperties food, Operation<Void> original,
			@Local(argsOnly = true) LivingEntity entity, @Local(argsOnly = true) ItemStack stack) {
		if (entity instanceof ServerPlayer player) {
			// work out the penalty before this meal counts as a repeat
			int percent = DiminishingReturns.percentFor(player, stack) * Spoilage.percentFor(player, stack) / 100;
			FoodHistory.record(player, stack, food);
			DiminishingReturns.recordMeal(player, stack);
			if (percent < 100) {
				int nutrition = food.nutrition() > 0 ? Math.max(1, Math.round(food.nutrition() * percent / 100f)) : 0;
				food = new FoodProperties(nutrition, food.saturation() * percent / 100f, food.canAlwaysEat());
			}
		}
		original.call(foodData, food);
	}
}
