# Hellblock

![Starting template](../../assets/examples/hellblock/start_template.png)
_[Download](https://github.com/MelanX/SkyblockBuilder/raw/gh-pages/assets/examples/downloads/1.16.x/hellblock.zip)_

To create a hellblock like modpack, you can simply set the spawn dimension to `minecraft:the_nether` as you can see in
the config below.

`config/skyblockbuilder/common-config.json5`:
```json
{
  "Spawn": {
    "dimension": "minecraft:the_nether"
  }
}
```

This world will have a default overworld and a default end. The nether is "sky". The image shows lava layers below the
island but that's implemented in 1.17.1 and the image was made with this newer version. That means: no custom surface.
I also added some structures for the nether. Because overworld is default, we don't have to care about the overworld
structures and can just ignore them. They will be generated as normal.

Thanks to [benbenlaw 🔗](https://www.curseforge.com/members/benbenlaw/projects) for giving me permission to use the 
template from his well known modpack [Infernopolis 🔗](https://www.curseforge.com/minecraft/modpacks/infernopolis) for 
this example. I tweaked a bit more on the template to avoid using mod blocks.