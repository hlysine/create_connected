# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## 1.3.2 - 2026-06-22

### Fixed

- Ticking block entity crash due to concurrent modification when Kinetic Batteries are connected to specific blocks from
  other mods

## 1.3.1 - 2026-06-21

### Fixed

- Tag errors due to missing compat blocks for Dye Depot and Create: Simulated

## 1.3.0 - 2026-06-21

### **This version onward is not compatible with Minecraft 1.20.1 or earlier**

### Added

- **Item filter support when extracting items via inventory bridges**
    - This can also be used to control which items are visible in stock link networks
- Fan catalyst compatibility with Dye Depot
    - Create: Dragons Plus is required for this to work
- Placement helpers for crank wheels
- Neoforge events before and after the feature toggle system refreshes JEI item list
    - Modpack creators may use these events to add compatibility with stage-based progression mods

### Changed

- **Kinetic Battery rework**
    - Kinetic Batteries now discharge according to actual stress consumption
    - Batteries no longer discharge if other kinetic sources in the network are providing enough stress capacity to
      cover the consumption
    - Battery items now carry NBT data to retain charge
    - Batteries placed in a chain share their redstone signal
- The creative tab now uses a brass gearbox as icon

### Fixed

- Sequenced pulse generator getting stuck after one activation

## 1.2.3 - 2026-06-21 [1.20.1 only]

### Added

- Forge events before and after the feature toggle system refreshes JEI item list
    - Modpack creators may use these events to add compatibility with stage-based progression mods

### Fixed

- Sequenced pulse generator getting stuck after one activation

## 1.2.2 - 2026-06-09

### Fixed

- Crash on dedicated servers due to client class access when rendering mob heads in fan catalysts

## 1.2.1 - 2026-06-09

### Added

- Fan dyeing catalysts for Create: Garnished and Create: Dragons Plus

### Changed

- Fan exploding catalyst now uses an animated model (thanks @JustAGuy4447)

### Fixed

- Missing texture on diagonal encased brass chute
- Brass chute incorrectly accepting industrial iron block for encasing
- Missing sound effects for steam engines on fluid vessels
- Pixel glitch on Sequenced Pulse Generator screen
- Hardcoded "su-hours" unit in Kinetic Battery display source (thanks @VladisCrafter)
- Dashboard not being able to be re-dyed (thanks @VladisCrafter)

## 1.2.0 - 2026-05-25

### Breaking code changes

- Mod registrations moved to `registries` package

### Added

- **Dashboard**
    - A mini-display that can send info to the HUD of players sitting in front
- **Brass Chute**
    - Middle ground between a regular chute and a smart chute. Supports diagonal item transport and can extract 64 items
      at a time.
- **Catalyst compatibilities for Create: More Catalysts, Create: Shimmer and Create: Nether Industry**
    - Enables bulk chocolate coating, honey coating, exploding, glooming, purifying, resonance, sculking, soul stripping
      and transmutation (thanks @HgTlPbBi-4 and @JustAGuy4447 for implementation)

### Changed

- **Reworked Sequenced Pulse Generator**
    - Faster reaction time of 1 game tick instead of 2
    - New instructions for signal strength comparison, arithmetic and bitwise operations
    - GUI improvements
- **Centrifugal clutch now accepts a maximum or minimum speed threshold**
    - Configured via the value panel
- **Linked transmitters can now be locked by waxing and unlocked with an axe**
- Updated ponder and tooltip for Kinetic Battery to explain power saving mode
- Brass gearbox rotation can only be toggled with a wrench now to avoid misclicks
- Inventory access ports and inventory bridges can no longer be used to bypass restriction between packagers and
  portable storage interfaces
- Retextured the Redstone Link Wildcard (thanks @ThatB0i)
- Updated usage and license information in the readme and license files

### Fixed

- Linked receivers losing signal on server restart
- Brass gearbox not rotating properly in schematics and contraption disassembly
- Linked transmitter losing texture transparency with certain mod combinations
- Wrench rotation of crank wheels not updating properly
- Missing audio in crank wheels

## 1.1.16 - 2026-05-06

### Fixed

- Incorrect discharge calculation for Kinetic Batteries

## 1.1.15 - 2026-05-05

### Added

- Kinetic Batteries now support display links
- Linked Transmitters can now be attached to Throttle Levers from Create: Aeronautics

### Changed

- **Kinetic Batteries now have a minimum su consumption of 1**
    - They will now run out of charge after 512 hours if they are used to power structures that have no stress impact

### Fixed

- Item duplication when an Item Silo is assembled into a physics contraption
- Mip level 4 not supported due to incorrect texture size for the Fluid Vessel
- Crank wheel handle rotating at incorrect speed

## 1.1.14 - 2026-04-27

### Fixed

- Cross connector compatibility with Create: Aeronautics - no longer interferes with the swivel bearing (thanks
  @KIN-4096)
- Redstone link wildcard compatibility with Sable - now works across physics contraptions (thanks @electicsteve)
- Ponder entry for Kinetic Bridge

## 1.1.13 - 2026-02-08

### Fixed

- Redstone link compatibility with Valkyrien Skies 2 (#207, thanks @PopSlime)
- Linked transmitter frequency being broken on servers (#206, thanks @PopSlime)

## 1.1.12 - 2026-02-01

### Fixed

- Compatibility with Create: Dreams and Desires due to changed mod ID (#202, thanks @Entropy159)
- Crash on dedicated servers due to LocalPlayer check
- Detached Kinetic Bridge having infinite output due to schematics (#203, thanks @PopSlime)

## 1.1.11 - 2026-01-30

### Changed

- Updated textures for the Control Chip and Sequenced Pulse Generator (thanks @ThatB0i)

### Fixed

- Linked Transmitters being able to delete blocks due to a client desync (#199, thanks @PopSlime)
- Kinetic Bridge with infinite output due to corrupt NBT (#200, thanks @PopSlime)
- Kinetic Battery with infinite output due to state desync from schematics (#201, thanks @PopSlime)

## 1.1.10 - 2025-11-23

### Fixed

- Incorrect mixins in schematic loading (#184, thanks @MoePus)

## 1.1.9 - 2025-11-09

### Fixed

- Crash due to a change in the WRENCH tag in Create 6.0.7+ (#180, thanks @SeaLife)

## 1.1.8 - 2025-10-26

### Fixed

- Incompatibility with Create 6.0.7 due to schematic upload changes
- Block update lag when using Inventory Access Ports on servers (#164, thanks @TheRedXD)

## 1.1.7 - 2025-06-23

### Fixed

- Redo fix: incompatibility with Create: Steam n Rails due to both mods registering the same set of interaction
  behaviors

## 1.1.6 - 2025-06-23

### Fixed

- Incompatibility with Create: Steam n Rails due to both mods registering the same set of interaction behaviors

## 1.1.5 - 2025-06-14

**NOTE: This version is only compatible with Create 6.0.6 and later**

### Fixed

- Crash due to breaking changes in Create 6.0.6

## 1.1.4 - 2025-06-08

### Fixed

- A crash when a Cross Connector is used while Create: Big Cannons is installed
- A rendering crash in copycats due to a null pointer exception in the code for connected textures

## 1.1.3 - 2025-05-31

### Fixed

- Fluid vessels losing their contents when disassembled

## 1.1.2 - 2025-05-30

### Fixed

- Crash due to incorrect block entity when a Kinetic Bridge is being replaced

## 1.1.1 - 2025-05-29

### Fixed

- Redstone Link Wildcard linking unrelated networks together
- Text overflow for certain languages in the Kinetic Bridge value panel

## 1.1.0 - 2025-05-28

### Added

- **Kinetic Bridge**
    - Transfer stress capacity from one network to another while keeping the networks separate
- **Kinetic Battery**
    - Stores kinetic stress for later use. Retains charge in item form to enable long-distance energy transport
- **Redstone Link Wildcard**
    - Allows a redstone link frequency slot to match any item, including air
- **Cross Connector**
    - Relays rotation in two directions independently
- **Catalyst compatibilities for Create: Dragons Plus, Create: Nuclear and Create: Henry**
    - Enables bulk freezing, sanding, ending, enriched and withering for the compatible mods

### Fixed

- JEI errors due to config sync trying to update JEI listing too early

## 1.0.3 - 2025-05-20

### Fixed

- Crash due to invalid resource location when handling modded buttons for linked transmitters (#115)
    - Linked transmitters still do not support modded buttons because it is very difficult to find the appropriate
      textures

## 1.0.2 - 2025-05-19

### Added

- **Support for Minecraft 1.21.1**

### Fixed

- Crash due to modification of fence gate properties by other mods (#97)
- Stress configs not working due to incorrect mod ID (#106)
- Inventory bridge and inventory access port losing connection on chunk unload (#111)

## 1.0.1 - 2025-04-09 [1.20 only]

### Fixed

- Fixed steam engines not working on fluid vessels
- Fixed boiler gauges turning black on fluid vessels

## 1.0.0 - 2025-04-08 [1.20 only]

### **This version onward is not compatible with Create 0.5.1 or earlier**

**Special thanks to @c0nnor263 for their contributions to this update**

### Changed

- Copper texture has been changed to match the new Create style
- Linked transmitters now use andesite instead of brass

### Fixed

- Fixed linked analog levers being unusable on VS2 ships due to a control conflict
- Fixed crank wheel teeth not meshing correctly with other cogwheels

### Removed

- Cherry and bamboo windows and panes

## 0.9.5 - 2025-02-06 [1.18 only]

### Fixed

- Tag compatibility with Create: Dreams & Desires

## 0.9.4 - 2025-02-05

### Added

- Stub for 1.19 and 1.18 backports
- Tons of new translations (thanks [Crowdin contributors](https://crowdin.com/project/create-connected-mod/)!)
    - Translators have been hard at work supporting multiple languages, so I figured it's time to release them while I
      work on the backports

## 0.9.3 - 2025-01-01

### Fixed

- Inventory bridge not working with two inventories when both item filters are empty
- Copycat incompatibility with AdditionalPlacements

## 0.9.2 - 2024-10-12

### Fixed

- Restored compatibility with Create 0.5.1f
- Fixed a crash when contraption data is fixed with a copycat block assembled in a contraption

## 0.9.1 - 2024-09-18

### Changed

- Enabled auto-report for crashes in the mod if you have Neruina installed

### Fixed

- Ticking block entity crash due to incompatibility with contraption changes in Create 0.5.1g
    - This version also attempts to fix worlds that have already been corrupted by the crash, but due to the nature of
      the crash, multi-block silos may be split into individual silos after recovery
- Incorrect rotation propagation for encased chain cogwheels (again)

## 0.9.0 - 2024-09-16

### Added

- **Inventory Access Port**
    - Attaches to an inventory to expand its surface area for other blocks to interact with
- **Inventory Bridge**
    - Attaches to two inventories to access both inventories simultaneously
    - Accepts filters to control which items are allowed to pass through
- **Fan Sanding/Seething Catalyst**
    - Compatibility for bulk sanding/superheating in Create: Dreams & Desires
- **Feature categories**
    - Disable features of the same category at once

### Changed

- Catalysts are now hidden if the corresponding add-on is not installed
- Updated Control Chip texture (credits to @LunarAnticGitHub)

### Fixed

- Crash when copycats from Create: Connected are migrated to newer versions of Create: Copycats+
- Remove debug exports of mixin code
- Incorrect rotation propagation for encased chain cogwheels

## 0.8.2 - 2024-05-29

### Fixed

- A mixin error causing startup crash in 1.19

## 0.8.1 - 2024-05-29

### Added

- A new config for Fluid Vessels to limit the maximum boiler level
- Translations for new features in Russian, Japanese and Simplified Chinese (
  thanks [Crowdin contributors](https://crowdin.com/project/create-connected-mod/reports/top-members)!)

### Fixed

- Boiler gauge not updating on Fluid Vessels
- Heat level for Fluid Vessels is now capped at level 18
- Various issues in Russian translation (thanks VladisCrafter!)
    - Text overflow for sequencer instructions
    - Incorrect ponder strings
    - Inaccurate translations for some entries

## 0.8.0 - 2024-05-29

### Added

- **Fluid Vessel - Horizontal Fluid Tank**
    - A horizontal variant of the Fluid Tank
    - Can be turned into a boiler with reduced efficiency (efficiency is configurable)
    - Cycle through multiple window shapes using a Wrench
- Small and Large Crank Wheels
    - Hand Crank + Cogwheels = Crank Wheels
- Fan Freezing Catalyst
    - Works with bulk freezing in Create: Garnished and Create: Dreams & Desires
- New translations and new languages (
  thanks [Crowdin contributors](https://crowdin.com/project/create-connected-mod/reports/top-members)!)

### Fixed

- A startup crash due to incompatibility when item attributes are accessed too early (#62)
- Missing particles for Linked Transmitters (#58)
- Linked Transmitters being interactable when in spectator mode

## 0.7.4 - 2024-03-21

### Fixed

- Item Silo losing its content when being disassembled from a contraption (#57)
- Incorrect texture uv rotation on Brass and Parallel Gearboxes (#55)

## 0.7.3 - 2024-03-16

### Fixed

- Crash due to Copycat Slabs from C:Dreams and Desires interfering with the copycat migration process (#48)
- Encased Chain Cogwheels not connecting to each other via their cogwheels when they are not in a line (#52)

## 0.7.2 - 2024-02-13

### Changed

- Upgraded gradle version to 8.4

### Fixed

- Unknown error during the publish process, causing a corrupted binary for v0.7.1 (copycats-plus/copycats#6)

## 0.7.1 - 2024-02-12

### Fixed

- Crash when copycats are broken without Create: Copycats+ installed (#44)

## 0.7.0 - 2024-02-11

### Added

- Recipe compatibility and auto-migration with Create: Copycats+
- New translations (thanks
  [Crowdin contributors](https://crowdin.com/project/create-connected-mod/reports/top-members)!)

### Changed

- `feature_enabled` condition no longer has an `invert` field. Please use the `forge:not` condition instead

### Fixed

- Incompatibility with Radium's pathfinding optimizations (#32)
- Incompatibility with Diagonal Fences (#33)
- Crash in certain instances where mod configs are accessed too early (#34)
- Crash in certain instances when wrenching a Brass Gearbox (#36)
- Incompatibility with Velocity due to bad network channel name (#38)
- Item Silos behaving differently from Item Vaults around contraptions (#39)
- Sequenced Pulse Generator not updating the block in front when signal strength changes (#42)

## 0.6.1 - 2024-01-24

### Fixed

- Incorrect item drops with custom loot tables when a Copycat Board is sneak-wrenched
- Various cases where copycats are not being treated the same as their real counterparts:
    - Copycat Walls not able to pave tracks
    - Incorrect placement of Copycat Fences when placed in a filter slot
    - Incorrect pathfinding around Copycat Fence Gates
    - Incorrect position when a passenger on a seat is being ejected due to collision with Copycat Slabs
    - Incorrect transforms when Copycat Stairs are disassembled from a rotated contraption
- Translation fixes in the ponder scene for Linked Transmitter

## 0.6.0 - 2024-01-24

### Added

- **Copycat Stairs**
    - Connected textures may not be perfect due to copycat limitations
- **Copycat Fence**
- **Copycat Fence Gate**
- **Copycat Wall**
- **Copycat Board**
    - Pre-assembled versions: Copycat Box and Copycat Catwalk
- Lock mechanism for linked transmitters to avoid misclicks on the frequency slots

### Fixed

- Crash due to bad MixinExtras version (0.3.3) (#24, #26)
- Crash due to concurrent modification when registering block stress values (#13)
- Copycat Vertical Step not mirroring properly in schematics
- Custom copycats not having their block entities registered
- Linked Analog Lever not responding to sneak-right click when holding item in hand (#25)
- Missing registrations for Linked Stone Buttons in 1.18/1.19 (#25)
- Copycat Slabs connecting their textures across a half-block gap (#27)
- Small error in the ponder scene for the Linked Transmitter

## 0.5.1 - 2024-01-19

### Fixed

- Instant schematic upload not working for schematics in sub-folders

## 0.5.0 - 2024-01-19

### Added

- **Support for schematics in sub-folders**
    - The Schematic Table now searches for schematics in sub-folders
    - Schematics maintain their folder structure when uploaded to servers
    - The Schematic and Quill now saves schematics in sub-folders if their names contain `/` or ` \ `
- **Redstone support for smart clutches**
    - The Overstress Clutch, Centrifugal Clutch and Freewheel Clutch now give comparator signal outputs when they are
      coupled
    - The Overstress Clutch can now be reset with a redstone pulse
- **Cherry window and window pane** (#18)
- **Bamboo window and window pane** (#18)
- **New "Loop" instruction for Sequenced Gearshift**
    - Restart the whole sequence from the beginning, allowing the Sequenced Gearshift to run indefinitely
- A new toggle to disable "crafting remaining item" fix for manual application recipes
- Translations for Russian, Japanese, Dutch, Chinese Simplified, Romanian
  (thanks [Crowdin contributors](https://crowdin.com/project/create-connected-mod/reports/top-members)!)

### Changed

- The block ID of modded linked buttons now include mod ID

### Fixed

- Incompatibility with mods that register new `BlockSetType`s with their mod IDs (#19)
- Vertical brass gearbox returning incorrect drops (#21)
- Connected textures on Copycat Slabs not considering diagonal connections (#20)
- Connected textures on Copycat Slabs break connection on one side if there is a height difference on the other side
- Speed modifiers not working for the "turn for time" instruction in Sequenced Gearshift

## 0.4.2 - 2024-01-12

### Fixed

- Crash due to unsafe early block entry access (#16)
- Jukebox on contraption not stopping after contraption is destroyed

## 0.4.1 - 2024-01-11

### Fixed

- Crash due to incorrect remapping

## 0.4.0 - 2024-01-11

### Added

- **Linked Transmitter**
    - For all button variants
    - For Lever
    - For Linked Lever
- **Contraption support for vanilla blocks**
    - Note Block
        - Can be tuned on contraptions
        - Plays automatically when on trains/elevators
        - Supports contraption controls
    - Jukebox
        - Can swap discs on contraptions
        - Plays/stops automatically when on trains/elevators
        - Supports contraption controls
    - Crafting Table
    - Stonecutter
    - Grindstone
    - Smithing Table
    - Loom
    - Cartography Table
- 2 music discs for iconic elevator music
- Translations for Dutch, Japanese, Chinese Simplified, French, German
  (thanks [Crowdin contributors](https://crowdin.com/project/create-connected-mod/reports/top-members)!)

### Fixed

- Water in Fan Washing Catalyst not transparent
- Copycat Slabs not dropping 2 items when a double variant is broken (#14)

## 0.3.1 - 2023-12-29

### Fixed

- Freewheel clutch/centrifugal clutch not updating on speed change (#9)

## 0.3.0 - 2023-12-29

### Added

- **Sequenced Pulse Generator**
- Control Chip - a crafting ingredient for the Sequenced Pulse Generator
- Ponders for
    - Encased Chain Cogwheel
    - Inverted Clutch
    - Inverted Gearshift
    - Parallel Gearbox
    - Sequenced Pulse Generator

### Fixed

- Z-fighting on the model of Encased Chain Cogwheel
- Crash when JEI is not installed (#7)

## 0.2.0 - 2023-12-25

Merry Christmas!

### Added

- **Stress value config for all kinetic components** (#4) - a server config system similar to base Create
- Separate stress value config for powered/unpowered brakes
- Item Silo
- Simplified Chinese translation (thanks @xiewuzhiying) (#6)

### Fixed

- Clutch variants incorrectly suppressing kinetic network updates
- Mod incompatibility due to item attributes being accessed too early in initialization (#5)

## 0.1.0 - 2023-12-23

### Added

- **Support for Minecraft 1.19.2 and 1.18.2** (#1)
- Dutch translation (thanks @lollolcheese123) (#3)

### Changed

- Freewheel/Centrifugal Clutch no longer uncouple when the network is overstressed

### Fixed

- Copycat stonecutting recipes not being disabled by feature toggle
- Feature toggles not applied instantly when changed via Create's config GUI
- Brake not emitting smoke if spinning in the opposite direction
- Missing translations in the value panel of Overstress Clutch
- Crash due to concurrent modification when an Overstress Clutch and a Freewheel/Centrifugal Clutch are on the same
  network

## 0.0.5 - 2023-12-22

### Added

- **Feature toggle system** - enable/disable blocks individually via mod common config

### Fixed

- Rendering issues with Fan Splashing Catalyst

### Changed

- Mod icon

## 0.0.4 - 2023-12-20

### Fixed

- Crash due to inject not being remapped (#2)

## 0.0.3 - 2023-12-20

### Added

- New instructions for Sequenced Gearshift
    - Turn until new redstone pulse
    - Turn for time
- New item attributes for attribute filter
    - Max durability
    - Max stack size
- Encased chain cogwheel
- Freewheel Clutch
- Copycat Vertical Step
- Advancement for Shear Pin
- Advancement for Overstress Clutch
- Advancement for Brass Gearbox
- Tooltips for all items (Mostly placeholders for future ponder scenes)

### Changed

- **Target Create version to 0.5.1f**
- The recipe for Empty Fan Catalyst to use less brass
- Advancement ID for Brake
- Texture for Brake
- Texture for overstress clutch
- Centrifugal clutch to update itself when placed
- Item ordering of all shapeless crafting recipes

## 0.0.2 - 2023-12-16

### Added

- 6-way Gearbox
- Brake
- Copycat Block
- Copycat Beam

### Changed

- Copycat Slabs to function like normal slabs while having vertical orientations and placement helpers

## 0.0.1 - 2023-12-8

### Added

- Item attribute - "Contains ID"
- Brass Gearbox
- Centrifugal Clutch
- Copycat Slab
- Inverted Clutch
- Inverted Gearshift
- Overstress Clutch
- Parallel Gearbox
- Shear Pin
- Fan Catalysts (Empty and 4 other variants)
