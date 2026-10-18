package com.sameaccord.plateful;

import com.mojang.serialization.JsonOps;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.Set;

/** Puts rice seeds, lettuce seeds and tomato seeds into abandoned mineshaft and plains village chests. */
public class FabricLootTables {
	private static final Set<ResourceKey<LootTable>> SEED_CHESTS = Set.of(
		chest("abandoned_mineshaft"),
		chest("village/village_plains_house"));

	/**
	 * One roll per chest; 35% of the time it adds 2-4 of a random crop item. This is the same pool as
	 * data/plateful/loot_table/chests/seeds.json, which NeoForge adds through data/plateful/loot_modifiers.
	 */
	private static final String SEED_POOL = """
		{
			"rolls": 1,
			"condition": { "type": "minecraft:random_chance", "chance": 0.35 },
			"entries": [
				{ "type": "minecraft:item", "name": "plateful:rice_seeds", "weight": 1,
				  "modifier": { "type": "minecraft:set_count", "count": { "type": "minecraft:uniform", "min": 2, "max": 4 } } },
				{ "type": "minecraft:item", "name": "plateful:lettuce_seeds", "weight": 1,
				  "modifier": { "type": "minecraft:set_count", "count": { "type": "minecraft:uniform", "min": 2, "max": 4 } } },
				{ "type": "minecraft:item", "name": "plateful:tomato_seeds", "weight": 1,
				  "modifier": { "type": "minecraft:set_count", "count": { "type": "minecraft:uniform", "min": 2, "max": 4 } } }
			]
		}
		""";

	private static ResourceKey<LootTable> chest(String path) {
		return ResourceKey.create(Registries.LOOT_TABLE, Identifier.withDefaultNamespace("chests/" + path));
	}

	public static void init() {
		LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
			// Leave datapack overrides of these chests alone.
			if (!source.isBuiltin() || !SEED_CHESTS.contains(key)) {
				return;
			}
			LootPool.CODEC
				.parse(registries.createSerializationContext(JsonOps.INSTANCE), GsonHelper.parse(SEED_POOL))
				.resultOrPartial(error -> Plateful.LOGGER.error("Bad seed loot pool: {}", error))
				.ifPresent(tableBuilder::pool);
		});
	}
}
