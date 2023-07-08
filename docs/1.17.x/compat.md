# Compatability with other mods
## Curios
This mod is compatible with [Curios 🔗](https://www.curseforge.com/minecraft/mc-mods/curios). This only means that
items will be dropped from the Curios inventory when leaving a team and 
[the config](config/inventory.md#dropping-inventory) is enabled, too.

## MineMention
This mod is compatible with [MineMention 🔗](https://www.curseforge.com/minecraft/mc-mods/minemention). To write in the 
teams chat, you can use `skyblockbuilder:sky_team` in MineMention config file. This would look like this:
```json
{
  "mentions": {
    "everyone": "minemention:everyone",
    "here": "minemention:here",
    "team": "skyblockbuilder:sky_team"
  }
}
```