# World
## Biome range
The radius for the biomes to repeat. An area with the size of (by default) 8192x8192 will contain all the biomes in the
world. If a biome isn't in this area, it's not in the world. It repeats after an invisible "border".

This feature can be disabled and the biomes will not repeat.

**WARNING**: Too small biome range will prevent some structures to generate, if structures are enabled, because some need
a special biome! You may fix this by decreasing the [structure modifier](#structure-modifier).

## Island distance
The distance between two islands. If you choose `the_nether` as [start dimension](spawn.md#dimension), it will be 
divided by 8.

## Offset
The offset from 0, 0 to generate the islands. Can be used to generate them in the middle of .mca files.

## Sea height
The sea level in the world. This has nothing to do with the water height in the world. This does affect the spawn height
of squids for example.

## Structure modifier
The modifier for spacing and separation of structures. These values can be defined by a data pack. However, this is a
multiplier to change these values. Look [here 🔗](https://minecraft.fandom.com/wiki/Custom#Generator_types) at 
`biome_source.structures.structures` for more information about that.

Minimal spacing will be set to 1 if the modifier changes it to be lower than that.
Minimal separation will be set to 0 if the modifier changes it to be lower than that.

**This config option needs a full restart once you already joined a world.**

## Surface
The block settings for generating the different dimensions surfaces. It's in the same format as 
[flat world generation settings 🔗](https://minecraft.fandom.com/wiki/Superflat#Preset_code_format), but will ignore
the biome. You can set it for each dimension. Instead of removing the dimension from the list, just set it to an empty
string to generate it completely void. If the surface is disabled, it will ignore the setting.

## Single biome
### Biome
Specifies the biome for a whole dimension. A list with all possible structures can be found in 
`config/skyblockbuilder/data/biomes.txt`. You can set the dimension you want. If you keep it as `null`, the 
[start dimension](spawn.md#dimension) will be used.

**WARNING**: Some structures need a special biome, e.g. Mansion needs Dark Oak Forest! These structures will not be generated if you have only one biome!