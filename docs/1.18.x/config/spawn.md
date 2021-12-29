# Spawn
## Dimension
This sets the dimension where you will spawn and the island be generated.

This can be any dimension found in `config/skyblockbuilder/data/dimensions.txt`. This file will **only** be generated
when joining a skyblock world.

## Direction
This sets the direction in which the user will be looking at.

Allowed values:

- `north`
- `east`
- `south`
- `west`

## Height
This sets the bottom layer of the template.

This affects where exactly the island will be generated.

Would be smart to choose a value within the world height (default -64 and 319)

## Dynamic height
If this config option is enabled, the height will be based on the internal dimension surface height.

This is **only** be used if the spawn dimension is set to [default](#dimension).

## Radius
The radius to find a valid spawn if no given spawn point is valid.

Minimum: 0