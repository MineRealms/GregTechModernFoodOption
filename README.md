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

## Credits / 致谢

- **Original mod author**: [Bruberu](https://github.com/Bruberu) and the GTCEu team —
  GregTech Food Option (1.12.2). Without their work this project would not exist.
- **1.20.1 port author**: [Fouriceeee](https://github.com/Fouriceeee) and contributors —
  the original 1.20.1 port this repository continues.
- Thanks to LuckyBlock for code assistance.
- Special thanks to Moonpools, Neo-Tix and Uranium233 for their artworks.
- **Original 1.12.2 mod**: [GregTechFoodOption](https://github.com/GTCEu/gregtech-food-option) (LGPL-3.0).

## License / 许可

This project is licensed under the **GNU Lesser General Public License v3.0 (LGPL-3.0)**,
the same license as the original GregTech Food Option.

- See [`LICENSE.txt`](LICENSE.txt) for the full license text.
- Artwork licenses are listed in [`LICENSE-ARTWORK.txt`](LICENSE-ARTWORK.txt).
- Because this is a derivative work of GregTech Food Option, the LGPL-3.0 obligations
  (source availability, license notices, relinking) apply to this project as well.
  If you distribute a modified version, you must keep it LGPL-3.0 and provide the
  corresponding source code.

本项目遵循 **LGPL-3.0**（与原版 GregTech Food Option 相同）。分发修改版时须继续使用
LGPL-3.0 并提供对应源代码。

## Status / 进度

- **Feature-complete port, client-tested**: materials & fluids, crops/trees & worldgen,
  food items & all 32 recipe chains, machines (slicer/microwave/multicooker/mob
  machines/farmer), multiblocks (baking ovens/greenhouse/kitchen), potion effects &
  lacing, covers, entities, JEI integration, lang & tooltips.
- Only known remaining issue: a harmless Forge performance note from Registrate's
  one-time listener cleanup (`Mod 'gtmfo' took ~2.5s to run a deferred task`).
- Other-mod compatibility (AppleSkin/TOP/Nutrition/TFC/…) is intentionally out of scope.
- Requires **GTCEu Modern 7.5.2** for Minecraft 1.20.1 Forge.

## Documentation / 文档

- [`PORTING_TODO.md`](PORTING_TODO.md) — full porting progress, audit notes, fix log
  and known simplifications.
- [`docs/JEI_EXPORT_FORMAT.md`](docs/JEI_EXPORT_FORMAT.md) — format spec for the optional
  JEI recipe/name JSON export (dev tool, disabled by default; see `devConfigs.exportJeiRecipes`).

## Building / 构建

```bash
./gradlew build
```

The compiled jar will be in `build/libs/`.

## Disclaimer

This is an unofficial fan port. GregTech Food Option, GregTech CEu and GregTech CEu Modern
are the property of their respective authors.
