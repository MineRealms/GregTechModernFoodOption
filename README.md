# GregTech Modern Food Option (GTMFO)

A [GregTech CEu Modern](https://github.com/GregTechCEu/GregTech-Modern) addon that ports
**GregTech Food Option (GTFO)** from Minecraft 1.12.2 to **1.20.1 Forge**.

> **⚠️ AI Vibe-Coded Fast-Track Port**
>
> This version of the port is being rapidly advanced with **AI-assisted "vibe coding"**.
> It is a work-in-progress: expect missing features, bugs, unbalanced recipes and
> rough edges. It is **not** a polished release — please report issues instead of
> assuming everything works.
>
> 本版本使用 **AI Vibe coding** 快速推进，仍有很多问题（缺功能、Bug、数值失衡等），
> 请勿当作稳定版使用；发现问题欢迎提 Issue。

## Status / 进度

Current development version: **0.0.9**. Status reviewed on **2026-09-25** against
the source and [`gradle.properties`](gradle.properties).

The core port covers food production, agriculture, machines and multiblocks. Recent
work adds pack integration and a configurable five-category nutrient system with
KubeJS support. Some original behavior remains simplified, and the latest nutrient
features still need in-game validation.

当前已实现主体移植、农业与部分整合包联动，以及默认关闭的五类营养系统。
早期版本有客户端测试记录；新增 KubeJS 营养定义、重载和多人同步仍待游戏内联调，
不能把历史测试记录视为当前版本的全面验收。

| Area | Implemented content |
|---|---|
| Food and chemistry | Foods, drinks, ingredients, materials and fluids; bread, cheese, pizza, pasta, meat, coffee, alcohol, chocolate and chemical processing chains |
| Agriculture | Crops, berry bushes, fruit trees, seeds, world generation and greenhouse recipes |
| Single-block machines | Slicer, cuisine assembler, microwave, multicooker, farmer, mob age sorter, mob exterminator and mob extractor |
| Multiblocks | Primitive, electric and steam baking ovens, greenhouse and kitchen |
| Other gameplay | Rolling pins, electric butchery knife, sprinkler cover, Italian buffalo, strong snow golem/snowball, effects and food lacing |
| Interfaces | JEI food/effect information, nutrient HUD and tooltips, nutrient commands, KubeJS bindings and configurable JSON exports |

## Requirements / 运行环境

The development baseline is defined in [`gradle.properties`](gradle.properties)
and [`build.gradle`](build.gradle):

| Component | Version |
|---|---|
| Minecraft | 1.20.1 |
| Mod loader | Forge 47.4.9 |
| GTCEu Modern | 7.5.2 |
| Java toolchain | 17 |
| Gradle Wrapper | 8.8 |

Install GTMFO alongside GTCEu Modern and its required dependencies. The development
build also uses LDLib `1.0.50`, Registrate `MC1.20-1.3.11` and Configuration `2.2.0`.
These are build baselines, not a tested compatibility range for newer versions.

- JEI `15.20.0.115` is the development version for recipe and food information.
- KubeJS `2001.6.5-build.16`, Rhino `2001.2.3-build.10` and Architectury `9.2.14`
  are the development versions for the script API below.
- AppleSkin `mc1.20.1-2.5.1` is included in the development runtime. GTMFO uses
  standard food properties for hunger/saturation display.
- Multiplayer requires matching GTMFO builds on the client and server. The
  nutrient channel uses strict protocol matching; this is not a server-only mod.

**Metadata gap:** [`mods.toml`](src/main/resources/META-INF/mods.toml) currently
declares only Forge and Minecraft dependencies. GTCEu is required by the code even
though it is missing from that declaration; loader metadata is not a complete
installation checklist.

## Nutrient system / 营养系统

The five fixed categories are `dairy`, `fruit`, `grain`, `protein` and `vegetable`.
The system is **disabled by default**. Enable it in `config/gtmfo.yaml`:

```yaml
gtfoNutrientConfig:
  enabled: true
```

The legacy `devConfigs.nutrientMode` switch also enables the system. Keep nutrient
settings consistent between client and server for HUD and tooltip display.

With the default nutrient settings:

- Each category is capped at `30`; decay removes `1` per observed game-day change
  (24,000 game ticks), without multiplying decay for days spent offline.
- Death resets nutrients. Each category at or above `5` grants `2` maximum health
  (one heart), up to `10` maximum health (five hearts) in total.
- A configurable effect can reward having all five categories at or above the threshold;
  it is disabled by default. There is no nutrient-deficiency debuff.
- Player values are mirrored to the HUD, integer scoreboard objectives
  `gtmfo_<name>` and precise `persistentData` values `gtmfo_nutrient_<name>`.

GTMFO food values, `gtmfo:nutrient/<name>` item tags and per-item KubeJS definitions
provide nutrient inputs. For example, place this in `kubejs/server_scripts/`:

```js
GTMFO.nutrients.addMany({
  "minecraft:bread": { grain: 1.5 },
  "minecraft:apple": { fruit: 1.0 },
  "minecraft:cooked_beef": { protein: 2.5 }
});
```

`add(itemId, values)` registers one item; `addAll()` is an alias for `addMany()`.
An explicit script value overrides **that category only**, including a value of
`0`. Categories without an override use built-in values plus tag values. Script
reloads rebuild the definitions and send the resulting snapshot to clients.

See [`docs/NUTRIENT_SYSTEM.md`](docs/NUTRIENT_SYSTEM.md) for configuration defaults,
input validation, commands, reload timing and API examples. Automatic consumption
handling covers GTMFO component foods and standard item-use completion events;
block foods such as cakes and custom consumption paths need separate integration.
External scripted foods use ordinary tooltips; JEI does not dynamically create
food-information entries for every external nutrient definition.

## Pack integration / 整合包联动

The repository includes common food tags, Serene Seasons crop/sapling tags,
conditional Farmer's Delight cutting and Farm & Charm mincer recipes, and Society
husbandry/milkable tags for the Italian buffalo. These provide integration points;
pack-specific prices, progression and balance still belong to the modpack.

The nutrient system exposes values for quests and scripts. This repository does
not ship a complete Sunlit Valley food-value table, nutrient skill tree, quest
line or nutrient-based selling-price rules. Nutrition is not automatically linked
to SoLOnion diversity or Quality Food quality.

Configuration is in `config/gtmfo.yaml`. Review vanilla recipe overrides and the
greenhouse duration multiplier when adding GTMFO to a pack. Some legacy compatibility
switches (AppleCore, NuclearCraft, ActuallyAdditions and the old Nutrition mod)
remain as placeholders and have no effect.

## Known differences and validation / 已知差异与验证

- The kitchen uses a fixed structure and phantom-slot orders instead of the original
  dynamic structure and programmable recipe cards. Some tree shapes and chocolate
  processing stages are simplified; the farmer uses a fixed output side. See
  [`PORTING_TODO.md`](PORTING_TODO.md) for the detailed port history.
- Historical core-port client tests are recorded in `PORTING_TODO.md` sections
  16.7–16.10. They predate the latest nutrient/KubeJS work.
- The nutrient implementation has recorded successful compilation and packaging.
  On 2026-09-25, `gradlew.bat compileJava --console=plain` also completed successfully
  with `compileJava` up to date. This does not validate gameplay.
- Current runtime checks still needed: client/dedicated-server startup with the
  latest nutrient code, actual KubeJS registration, fractional values and explicit
  zero overrides, `/reload`, late-joining clients, and no double accumulation on
  GTMFO foods. Pack-level balance and regression checks are also outstanding.
- Several documents contain dated snapshots or proposals. For current nutrient
  behavior, use `docs/NUTRIENT_SYSTEM.md` and the source; older blanket statements
  that all compatibility is out of scope no longer describe the repository.
- License metadata is inconsistent with the repository license; see below.

## Building / 构建

Use the included Gradle Wrapper with a Java 17 toolchain available. Before building,
review the HTTP/HTTPS proxy entries in `gradle.properties`: they currently point to
`127.0.0.1:7890`. Remove or adjust those local entries if that proxy is unavailable.

Windows PowerShell:

```powershell
.\gradlew.bat build --console=plain
```

Linux/macOS:

```bash
./gradlew build --console=plain
```

The current artifact is `build/libs/gtmfo-0.0.9.jar`; its version comes from
`mod_version`. Development tasks include `compileJava`, `runClient`, `runServer`
and `runData`. Data generation writes to `src/generated/resources`, which is
included in the JAR; review generated changes before committing them.

`legacy-resources/` holds porting references. The local original-source directory
`GregTechFoodOption-1.12.2-ORIGIN/` is ignored by Git and is not included in a fresh clone.

## Documentation / 文档

| Document | Purpose |
|---|---|
| [`NUTRIENT_SYSTEM.md`](docs/NUTRIENT_SYSTEM.md) | Current nutrient configuration, KubeJS API, synchronization and limitations |
| [`NUTRIENT_AI_HANDOFF.md`](docs/NUTRIENT_AI_HANDOFF.md) | Source map and handoff checklist for further nutrient work |
| [`NUTRIENT_INTEGRATION_ANALYSIS.md`](docs/NUTRIENT_INTEGRATION_ANALYSIS.md) | Sunlit Valley nutrient integration analysis and proposed pack work |
| [`PORTING_TODO.md`](PORTING_TODO.md) | Porting checklist, historical validation, fixes and simplifications |
| [`PACK_INTEGRATION_ANALYSIS.md`](docs/PACK_INTEGRATION_ANALYSIS.md) | Pack dependency snapshot, food systems and integration planning |
| [`AGRICULTURE_BALANCE.md`](docs/AGRICULTURE_BALANCE.md) | Crop, tree and greenhouse balance analysis |
| [`REGISTRY_AUDIT.md`](docs/REGISTRY_AUDIT.md) | Registry/resource audit snapshot |
| [`CHEMICALS.md`](docs/CHEMICALS.md) | Material names, formulas and chemical reference table |
| [`JEI_EXPORT_FORMAT.md`](docs/JEI_EXPORT_FORMAT.md) | Recipe/name JSON export format; opt in with `devConfigs.exportJeiRecipes` (default `false`) |

## Credits / 致谢

- **Original mod author**: [Bruberu](https://github.com/Bruberu) and the GTCEu team —
  GregTech Food Option (1.12.2). Without their work this project would not exist.
- **1.20.1 port author**: [Fouriceeee](https://github.com/Fouriceeee) and contributors —
  the original 1.20.1 port this repository continues.
- Thanks to LuckyBlock for code assistance.
- Special thanks to Moonpools, Neo-Tix and Uranium233 for their artworks.
- **Original 1.12.2 mod**: [GregTechFoodOption](https://github.com/GTCEu/gregtech-food-option) (LGPL-3.0).

## License / 许可

This project carries the **GNU Lesser General Public License v3.0 (LGPL-3.0)**,
the same license as the original GregTech Food Option.

- See [`LICENSE.txt`](LICENSE.txt) for the license text.
- Artwork licenses are listed in [`LICENSE-ARTWORK.txt`](LICENSE-ARTWORK.txt).
- **Known metadata inconsistency:** `mod_license` in `gradle.properties` is still
  `All Rights Reserved` and is expanded into the JAR's `mods.toml`. That field has
  not yet been aligned with the repository's LGPL-3.0 license text.

仓库保留 LGPL-3.0 许可证及原作者致谢；构建元数据中的许可证字段仍待统一。

## Disclaimer

This is an unofficial fan port. GregTech Food Option, GregTech CEu and GregTech CEu Modern
are the property of their respective authors.
