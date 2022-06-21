# Spawn
## Spawn Protection Radius
This sets the radius around the spawn which should be protected and only op players can interact with.
This prevents (tool) interactions, mob griefing, explosions, breaking blocks, farmland trampling, growing crops, and
attacking. Additionally, the player will always have max health, max food level, can't drown, and can't be set on fire.
This is default set to 0, so nothing is protected. Look at the table here to see more options:

| **3** | **3** | **3** | **3** | **3** |
|-------|-------|-------|-------|-------|
| **3** | **2** | **2** | **2** | **3** |
| **3** | **2** | **1** | **2** | **3** |
| **3** | **2** | **2** | **2** | **3** |
| **3** | **3** | **3** | **3** | **3** |

## Dimension
This sets the dimension where you will spawn and the island be generated.

This can be any dimension found in `config/skyblockbuilder/data/dimensions.txt`. This file will **only** be generated
when joining a skyblock world.

## Height
### Range
You can set a range from `minY` to `maxY`. `minY` is the bottom spawn position. `maxY` is the top spawn dimension.
If you set the [spawn height type](#type) (see below) to `set`, the bottom value will be used for a set height.
Otherwise, the height will be calculated.

### Type
`set`: This is a fixed spawn position. For users of the older versions of skyblock builder, this is the normal mode as
before. It will use the `bottom` value of the [range](#range) as fixed height. The `top` value will be ignored.

`range_top`: This will search for a valid position beginning at the `top` position, down to the `bottom`. If no valid
spawn was found, it uses the `top` value as fixed height.

`range_bottom`: Same as `range_top`. The only difference is that it uses the `bottom` value as fixed height if there was
no valid spawn found.

Valid spawns are defined as: Two block air on top of each other. Under that, it's a normal block/slab, or block inside
the valid spawn [block tag 🔗](https://minecraft.fandom.com/wiki/Tutorials/Creating_a_data_pack#Tags)
`#skyblockbuilder:additional_valid_spawns`.

### Offset
If the spawn height type is set to `range`, this offset will be used to slightly move the spawn height in any 
direction. Negative values go down, positive values go up.

## Radius
The radius to find a valid spawn if no given spawn point is valid.

Minimum: 0