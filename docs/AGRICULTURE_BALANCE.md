# GTMFO 农业平衡分析与调整（2026-09-14）

> 背景：整合包 "Society: Sunlit Valley" 中 GTMFO 果树产出速度过快（香蕉树约 2 分钟一轮），
> 需要与季节系统联动并重新平衡。本文档记录**代码取证 + 修改 + 数值对照**。
> 适用版本：gtmfo-0.0.7（修改前为 0.0.5/0.0.6）。

---

## 1. 问题定位（代码级）

GTMFO 的果树产出有两条路径，都存在问题：

### 1.1 树叶果实掉率过高（BUG，约 10~12.5 倍）

| 位置 | 修改前 | 修改后 | 依据 |
|---|---|---|---|
| `GTFOBlockLeaves#getDrops` | `nextInt(20 / divisor) == 0` | `nextInt(200 / divisor) == 0` | 原版 1.12.2 `BlockLeaves#getDrops` 传给 `dropApple` 的 chance 是 **200**（苹果 0.5%），GTFO `getAppleDrop(chance)` 用 `chance / divisor`；移植时误用了**树苗**掉率 20 |

**各树单叶果实期望值对照**（每破坏一个树叶方块）：

| 树 | divisor | 修改前概率 | 修改后概率 | 数量 | 整棵树期望（约 45 叶） |
|---|---|---|---|---|---|
| 香蕉 banana | 8 | 50% | **4%** | 3-6 | ~101 → **~8** |
| 橙子 orange | 10 | 50% | 5% | 1-2 | ~34 → **~3.4** |
| 芒果 mango | 10 | 50% | 5% | 0-2 | ~22 → **~2.2** |
| 杏 apricot | 15 | 100% | 7.7% | 1 | ~45 → **~3.5** |
| 柠檬 lemon / 青柠 lime | 10 | 50% | 5% | 1-2 | ~34 → **~3.4** |
| 橄榄 olive | 15 | 100% | 7.7% | 1-4 | ~112 → **~8.7** |
| 肉豆蔻 nutmeg | 10 | 50% | 5% | 1-2 | ~34 → **~3.4** |
| 椰子 coconut | 7 | 50% | 3.6% | 0-1 | ~11 → **~0.8** |

### 1.2 温室树木配方一轮 ~100 秒（可超频，过快）

`GreenhouseRecipes.java`（数值与原版 GTFO 一致）：

| 配方（电路板） | 基础 | EUt |
|---|---|---|
| 电路 1：树苗 → 6 原木 + 树苗 + 1 果实 | 2000 ticks（100 s） | 60 (LV) |
| 电路 2：树苗 → 5 原木 + 20 树叶 | 2000 ticks（100 s） | 60 |
| 电路 3：树苗 → 5 原木 + 3~5 果实 | 3000 ticks（150 s） | 60 |
| 原版树/橡胶树 | 2000~4000 ticks | 60~90 |

GTCEu 多方块按能源仓等级超频（每级 EUt×4、时长÷2），EV 级即可把 100 s 压到 **12.5 s**。

**修改**：新增配置 `gtfoMiscConfig.greenhouseDurationMultiplier`（默认 1.0 = 原速），
整合包内设为 **6.0**，作用于所有温室配方：

| 配方 | 原版 | ×6（LV） | HV(÷4) | EV(÷8) | LuV(÷32) |
|---|---|---|---|---|---|
| 树木 电路1/2 | 100 s | **600 s（10 min）** | 150 s | 75 s | ~19 s |
| 树木 电路3（果实） | 150 s | **900 s（15 min）** | 225 s | 112 s | 28 s |
| 橡胶 电路4 | 200 s | 1200 s | 300 s | 150 s | 37 s |

---

## 2. 季节联动（SereneSeasons 1.20.1）

### 2.1 机制取证（反编译 `SeasonalCropGrowthHandler` / `ModFertility`）

- SereneSeasons 通过 `MixinBlockStateBase#onRandomTick` 拦截**所有方块的 randomTick**，
  交给 `SeasonalCropGrowthHandler.onCropGrowth()`；
- `ModFertility.isCrop(state)` 依据 **方块标签** `sereneseasons:{spring,summer,autumn,winter}_crops`
  判断是否为"作物"；不在任何标签中的方块**不受影响**（默认常年可长）；
- `isGlassAboveBlock()`：方块上方是 `sereneseasons:greenhouse_glass` 标签内的玻璃
  （默认含 `#forge:glass` / `#c:glass_blocks`）时**不受季节限制**；
- `applyBonemeal`：非当季作物**催熟也无效**。

### 2.2 本轮改动

| 内容 | 状态 |
|---|---|
| 19 种作物方块 + 种子/产物 → 四季标签（上一轮已做） | ✅ |
| **10 种树苗 → 季节标签（本轮新增）** | ✅ |
| `gtmfo:greenhouse_glass` → `sereneseasons:greenhouse_glass`（上一轮已做，可盖玻璃全年生长） | ✅ |

**树苗季节分配**（世界生长）：

| 季节 | 树苗 |
|---|---|
| 春季 spring | 杏 apricot、柠檬 lemon、青柠 lime |
| 夏季 summer | 香蕉 banana、芒果 mango、橙子 orange、椰子 coconut |
| 秋季 autumn | 橄榄 olive、肉豆蔻 nutmeg |
| 冬季 winter | （无——冬季需温室玻璃或 GT 温室） |
| 全年 year_round | 彩虹木 rainbowwood（魔法树） |

### 2.3 玩家可用的"反季节"手段
1. 树苗上方盖任意玻璃（含 GTMFO 温室玻璃）→ 全年生长；
2. GT 温室多方块（机器，不受季节影响）——但已通过 `greenhouseDurationMultiplier` 限速；
3. 骨粉：非当季无效（SereneSeasons 内置规则）。

---

## 3. 修改文件清单（gtmfo-0.0.7）

| 文件 | 修改 |
|---|---|
| `common/block/GTFOBlockLeaves.java` | 果实掉率 20 → **200**（修复 10 倍超标） |
| `GTMFOConfigHolder.java` | 新增 `gtfoMiscConfig.greenhouseDurationMultiplier`（1.0~100.0，默认 1.0） |
| `common/machine/GreenhouseMachine.java` | `GreenhouseRecipeLogic.setupRecipe` 应用倍率（仅影响本机 `duration`，不污染共享配方） |
| `resources/data/sereneseasons/tags/blocks/*_crops.json` | +10 树苗（blocks） |
| `resources/data/sereneseasons/tags/items/*_crops.json` | +10 树苗（items） |
| 整合包 `config/gtmfo.yaml` | `greenhouseDurationMultiplier: 6.0` |

---

## 4. 后续可选（未做）

1. **温室电路 4**（原版"树苗 + 肥料 → 10 原木 + 果实"）未移植，可补上作为"加速但消耗肥料"的正规加速通道；
2. 树叶果实掉落可进一步做成"仅当季掉落"（需自定义 `getDrops` 判断季节，代码量小）；
3. 若仍偏快：提高倍率至 8~10，或在整合包通过 KubeJS 移除部分电路配方。
