# Main setup
## Setting world type on single player as default
You simply start the game once. It will generate a config called `forge-common.toml`. Change the only value in this
config to `skyblockbuilder:custom_skyblock`, or use this example and create a file called `forge-common.toml` in the
config directory with this content:
```toml
[general]
	defaultWorldType = "skyblockbuilder:custom_skyblock"
```

## Setting world type on server as default
If you want to provide a custom `server.properties` file, set the `level-type` to `skyblockbuilder:custom_skyblock`.
Otherwise, it's the same as in [single player](#setting-world-type-on-single-player-as-default).

## Creating a custom skyblock island
1. Build an island.
2. Use the custom item `Structure Saver` from the vanilla Tools tab in creative inventory. The output directory for this
   is `<minecraft>/skyblock_exports/<name>.nbt`.
   Alternatively, you can also use the vanilla Structure Block. Keep in mind that this can only save islands up to 
   48x48x48 blocks and the output is in `<minecraft>/saves/<world>/generated/minecraft/structures/<name>.nbt`.
3. Copy the generated file from its directory (see previous step) to `config/skyblockbuilder/templates/<name>.nbt`.
4. Set the possible spawns in `config/skyblockbuilder/spawns.json`. There can be multiple spawns, each one is an array
   with `[x, y, z]` relative to the 0, 0, 0 from the template structure. You can also 
   [modify existing spawns](../user/user.md#modify-spawns) and export them with `/skyblock spawns EXPORT`.
   IMPORTANT: You need to be in a world with world-type `Skyblock` to use the commands.
5. To view your current spawns, you need to use the `/reload` command to reload the config. After that, you need to use
   the `/skyblock spawns true` command to view all possible spawn points.
6. Repeat step 4 and 5 until everything is correct.

## Setting multiple templates
You can set multiple schematics by putting them into `config/skyblockbuilder/templates/`. These schematics can the user
use by pressing the `Customize` button in world options screen or changing the schematic with
command `/skyblock manage islandShape <template>`.

**IMPORTANT**: DO NOT name any of these schematic files `template.nbt` because it would be overwritten by the default
schematic in `config/skyblockbuilder/template.nbt`.

## Possible spawns
Possible spawns are set in `config/skyblockbuilder/spawns.json`. For each player, the game will choose a random position
and places the player on that position. Good on big islands when adding a lot of players at once at one team. You can
also export your current spawn points with command `/skyblock spawns EXPORT`. For this, you should first
[modify spawns](../user/user.md#modify-spawns). You will find your exported spawns in `skyblock_exports/spawns.json`. 
Copy it in the main config folder and override the existing one to apply your new spawns.
If you spawn inside a block, you could add this block to the 
[block tag 🔗](https://minecraft.fandom.com/wiki/Tutorials/Creating_a_data_pack#Tags) 
`#skyblockbuilder:additional_valid_spawns`.

## Loot chests on island
If you want a loot chest on an island, you need to set the NBT data to the chest with the `/data merge block <x y z>
{LootTable: modid:path/to/loot_table}` command to set it as loot chest.

**WARNING**! Do not open that chest after merging this data into the chest.
