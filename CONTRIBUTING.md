# Contributing to Create: Connected

Thank you for showing interest in the mod! This file aims to answer some of the common questions regarding
contributions.
This is not a set of hard rules, so feel free to reach out if you have additional questions.

<!-- TOC start (generated with https://github.com/derlin/bitdowntoc) -->

- [Issues](#issues)
    * [Reporting bugs](#reporting-bugs)
    * [Feature suggestions](#feature-suggestions)
- [Pull requests](#pull-requests)
    * [Translations](#translations)
    * [Ponders](#ponders)
    * [Fabric port](#fabric-port)
    * [New features](#new-features)

<!-- TOC end -->

## Issues

### Reporting bugs

- For crashes, please paste your crash log in https://mclo.gs/ and include it in your issue.
- Please include steps to trigger the bug and screenshots in your issue if applicable.
- Mod incompatibilities may not always be fixed if the issue is not on C:Connected's side.

### Feature suggestions

New ideas are always welcome, but here's a general rule on things that I probably **won't** add to the mod:

- Features that involve major gameplay changes or large system reworks, such as overhauling how trains/steam engines
  work
- Features that remove/replace base Create functionality, such as nerfing a Create feature just to reintroduce it in a
  higher-tier item
- Features that are too far away from base Create, such as electrical networks

## Pull requests

### Translations

Translations are very welcomed! Similar to
Create, [the English text is located in `src/generated`](https://github.com/hlysine/create_connected/tree/main/src/generated/resources/assets/create_connected/lang)
while [other languages are located in `src/main`](https://github.com/hlysine/create_connected/tree/main/src/main/resources/assets/create_connected/lang).

### Ponders

Ponders are very welcomed! My goal is to set up ponder scenes for all items that would have gotten a ponder in base
Create. There
is [a short guide on Create's wiki](https://github.com/Creators-of-Create/Create/wiki/Internal---Ponder-UI) on how to
create ponders.

### Fabric port

I have no plans of porting the mod to Fabric, but Fabric forks are welcome. Please let me know if you decide to work on
it. I can also provide help by explaining some of the mod's inner workings.

### New features

Please open an issue before working on new features so that no effort is wasted. A complete feature would include block
logic, textures, models, feature toggle, configs and ponder, but I'll accept PRs as long as the first 3 are done.