# Plateful

A food mod for Minecraft 26.3, for both Fabric and NeoForge.

![Every Plateful item](docs/items.png)

## Features

- **30+ foods**: burgers, sandwiches, tacos, quesadillas, sushi, soups, salads, rice bowls, a chocolate cake and more, all in their own creative tab.
- **New crops**: lettuce and tomatoes grow on farmland like wheat. Rice only grows in shallow water and becomes a two-block-tall plant.
- **Cheese vat**: use a milk bucket on an empty cauldron and the milk slowly curdles into cheese.
- **Food Journal** (Book + Bread): lists the foods you ate most recently, with the hunger and saturation each one will restore next time.
- Rice, lettuce and tomato seeds can be found in abandoned mineshaft and plains village chests.
- **Diminishing returns**: eating the same food again within your last few meals restores less.
- **Food spoilage**: carried food goes stale, then spoiling (both restore less), and finally rots into rotten flesh. Tooltips show how fresh it is.

## Settings

Both food depreciation systems can be turned on or off and tuned:

- **Per world**, with game rules (also under *Player* in the world creation screen):

  | Game rule | Default | What it does |
  |---|---|---|
  | `plateful:diminishing_returns` | `true` | Repeated foods restore less |
  | `plateful:repeat_penalty_percent` | `25` | How much less, per recent repeat |
  | `plateful:food_spoilage` | `true` | Carried food spoils |
  | `plateful:food_spoil_days` | `10` | In-game days until food rots |

  e.g. `/gamerule plateful:food_spoilage false`

- **For every new world**, in `config/plateful.json`. It sets the default for each game rule above, plus `minimum_percent` (the lowest a repeated food can drop to), `meals_remembered` (how many recent meals count) and `vanilla_food_spoils` (whether vanilla food spoils too, off by default). Restart the game after editing it.

## Installing

Plateful runs on both **Fabric** and **NeoForge** for Minecraft 26.3. Download the jar for your loader:

| Loader | Jar | Requires |
|---|---|---|
| Fabric | `plateful-fabric-26.3-<version>.jar` | [Fabric Loader](https://fabricmc.net/use/) 0.19.3+ and [Fabric API](https://modrinth.com/mod/fabric-api) 0.160.6+ |
| NeoForge | `plateful-neoforge-26.3-<version>.jar` | [NeoForge](https://neoforged.net/) 26.3.0.0-beta+ |

These minimums are the oldest loader builds for Minecraft 26.3, so any newer 26.3 build works too.

## Building

Requires JDK 25 or newer.

```
./gradlew build                # builds both jars
./gradlew :fabric:runClient    # Fabric dev client
./gradlew :neoforge:runClient  # NeoForge dev client
```

The jars end up in `fabric/build/libs/` and `neoforge/build/libs/`.

### Project layout

The repo uses the [MultiLoader template](https://github.com/jaredlll08/MultiLoader-Template) layout:

- `common/`: everything shared between loaders: blocks, items, the Food Journal, food depreciation, the config file, the mixin, and all textures, models, recipes and loot tables.
- `fabric/`: the Fabric entry point and Fabric-specific hooks (player data storage, chest loot, cauldron use, player ticks, tooltips).
- `neoforge/`: the NeoForge entry point and NeoForge-specific hooks (player data storage, chest loot modifiers, cauldron use, player ticks, tooltips).

Common code reaches loader-specific features through `com.sameaccord.plateful.platform.Services`. Loader versions live in `gradle.properties`; they are deliberately the *oldest* 26.3 builds, so the mod never uses an API that older installs lack.

## License

[MIT](LICENSE)
