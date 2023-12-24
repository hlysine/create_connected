<p align="center"><img src="https://raw.githubusercontent.com/hlysine/create_connected/main/src/main/resources/create_connected_icon.png" alt="Logo" width="200"></p>

<h1 align="center">Create: Connected</h1>

<p align="center">
<a href="https://www.curseforge.com/minecraft/mc-mods/create-connected/files"><img src="https://cf.way2muchnoise.eu/versions/947914_all.svg"></a>
<a href="https://modrinth.com/mod/create-connected/"><img src="https://img.shields.io/modrinth/dt/Vg5TIO6d?style=flat&label=Modrinth"></a>
<a href="https://www.curseforge.com/minecraft/mc-mods/create-connected"><img src="https://img.shields.io/curseforge/dt/947914?style=flat&label=CurseForge"></a>
</p>

A Create mod add-on adding quality-of-life blocks that you wish existed in Create.

**Pre-release version. Make regular backups!**

## What's new in BETA

A feature toggle system is added, allowing you to selectively disable blocks added by this mod without needing
datapacks/CraftTweaker/KubeJS. When you disable a feature:

- all related recipes are disabled, making the block unobtainable in survival
- the item is hidden in creative inventory and JEI
- existing blocks in the world are not affected, so existing machines won't break
- a config sync system is in place so that the server's config overrides all clients.
- datapacks are still respected, and will override the feature toggle unless the `feature_enabled` condition is
  included.

**Remember to RESTART after you enable/disable features**

![Feature Toggle System](https://cdn.modrinth.com/data/Vg5TIO6d/images/d74a9a1a353caee83b0d5dc69c60305a14699d3a.png)

## Features

- New Item Attributes
    - Item ID contains *word*
    - Item has X max durability
    - Item stacks to X
- New Sequenced Gearshift Instructions
    - Turn until new redstone pulse
    - Turn for time
- Encased Chain Cogwheel
    - An encased chain drive with an additional cogwheel for connectivity
- Inverted Clutch
    - A clutch that is uncoupled unless powered
- Inverted Gearshift
    - A gearshift that changes rotation direction unless powered
- Parallel Gearbox
    - A gearbox with all 4 sides spinning in the same direction
- 6-way Gearbox
    - A gearbox with shafts on all 6 sides. Conceptually uses the same internal mechanism as Parallel Gearbox, so top
      and bottom spins at half speed
- Brass Gearbox
    - A gearbox where the rotation direction of all 4 sides are independently configurable
- Shear Pin
    - An early-game, single-use connector that breaks when the network is overstressed
- Overstress Clutch
    - A clutch that uncouples after a set delay when the network is overstressed. Can be reset with a wrench
- Centrifugal Clutch
    - A clutch that is only coupled if the input RPM is faster than a configurable threshold
- Freewheel Clutch
    - A clutch that is only coupled if the input spins in the correct direction as configured
- Brake
    - A device that produces immense stress when powered, halting the network by overstressing it
- Fan Blasting/Smoking/Washing/Haunting Catalysts
    - A series of blocks that trap liquid/fire for safe and dedicated bulk processing
- Copycat Slab
    - Slab-shaped copycat that has double slab variants and can also be placed vertically
- Copycat Block
    - Clones a block while ignoring its placement rules (such as placing coral blocks above water)
- Copycat Vertical Step
    - A vertical Copycat Step with assisted placement like shafts
- Copycat Beam
    - A centered Copycat Step that can be oriented in all 3 axes with assisted placement like shafts

## Download

This is a pre-release version of the mod. World corruption is not expected, but do expect bugs and rare crashes. Make
regular backups if you decide to play this mod in your long-term worlds.

Find this mod on [**Modrinth**](https://modrinth.com/mod/create-connected) or
[**CurseForge**](https://legacy.curseforge.com/minecraft/mc-mods/create-connected).

**Supported Create versions:**

| Create | Create: Connected |
|--------|-------------------|
| 0.5.1e | 0.0.2             |
| 0.5.1f | 0.0.3 - latest    |

## Usage

**In modpacks:**

- You can include this mod in any modpacks.
- You can make any modifications to the mod with the goal of distributing it in a modpack.

**In other cases:**

- You can use this mod however you like as long as you obtain the mod via its Modrinth or CurseForge page.
- You can make any modifications to the mod, but you cannot redistribute it unless you have modified a substantial
  portion of the mod's code. Changes to resource packs/data packs/mod metadata do not count as code modification.

This mod is open to suggestions, so if you have made any modification to the mod, please leave an issue/PR so I can
consider adding your use case to the mod.

## Credits

**Translation**

- [lollolcheese123](https://github.com/lollolcheese123) for Dutch translation

**Inspiration**

- The Create mod Discord server and subreddit