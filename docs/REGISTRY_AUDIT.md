# GTMFO ↔ GTFO 注册表级移植审计（2026-09-14）

> **审计目的**：不依赖 `PORTING_TODO.md` 的既有结论，直接从**源码注册调用 + 生成的 lang**
> 两个注册表层面的证据出发，逐类对比原版 1.12.2 GTFO 与本 1.20.1 端口，找出**确证未移植**的内容。
>
> **证据来源**：
> - 原版：`GregTechFoodOption-1.12.2-ORIGIN/src/main/java/gregtechfoodoption/` + `assets/gregtechfoodoption/lang/en_us.lang`
> - 端口：`src/main/java/com/ironsword/gtmfo/` + `src/generated/resources/assets/gtmfo/lang/en_us.json`
>
> **判定规则**：注册调用为准；名称不同但功能对应（改名）不算缺口；原版中已注释/无消费方的条目不算缺口。
> 未覆盖：配方数值逐条核对、贴图/模型保真度、lang 文本质量（属另外的审计范畴）。

---

## 1. 物品（Items）

| 项 | 原版 | 端口 |
|---|---|---|
| 注册调用 | `GTFOMetaItem.addItem(id, "name")` 共 **325 条**（另有 4 条 culture 被注释） | `GTMFOItems.java` 的 `item()/foodItem()/smore()/smogus()` 等 + 生成 lang |
| lang 条目 | `metaitem.*.name` **490** 条（含材料物品如粉尘） | `item.gtmfo.*` **487** 条（含 106 条 tooltip，纯物品 **381**） |
| 按显示名匹配 | — | 325 条原版已注册物品中 **302 条直接匹配** |

### 1.1 26 条名字未匹配的逐条判定（全部有代码证据）

| 原版条目 | 端口对应 | 判定 |
|---|---|---|
| `component.kebab.{kubide,onion,tomato,chum,carrot,fat,meat}`（7） | `KEBAB_*_RAW` | **改名**（如 "Raw Koobideh Kebab"→"Raw Kubideh Kebab"、"Nailed Onions Kebab"→"Raw Onion Kebab"） |
| `food.kebab.{kubide,tomato,carrot,meat}`（4） | `KEBAB_*` | **改名** |
| `food.pizza.{cheese,veggie,mince_meat}`（3） | `PIZZA_*_SLICE` | **改名**（补 Slice 后缀） |
| `component.garlic_bulb` "Garlic Bulb" | `GARLIC_PURPLE/WHITE` | **重构**（大蒜拆成紫/白两品种） |
| `seed.garlic` "Garlic Clove" | `SEED_GARLIC_PURPLE/WHITE` | **改名/拆分** |
| `seed.unknown` "Undetermined GTFO Seeds" | `SEED_UNKNOWN` "Undetermined GTMFO Seeds" | **改名** |
| `food.apple_hard_candy` "Apple Hard Candy" | `APPLE_CANDY` "Apple Candy" | **改名** |
| `component.partially_filled_chip_bag` "Partially Filled Bag of Chips" | `CHIPS_BAG_EMPTY` "Empty Chip Bag" | **链重构**（原版 PAPER_BAG→PARTIALLY_FILLED→成品；端口改为组装 CHIPS_BAG_EMPTY→罐装成品） |
| `component.corn.dried_ear` "Dried Corn Ear" | 无 | **原版即无消费方**（仅注册，无任何配方/引用），不算缺口 |
| `component.dewar_flask.{new,used,cap,casing,casing_leached}`（5） | 无（`GTMFOItems.java` 中被注释） | **确证缺口**（见 §6.1） |
| `utility.kitchen_recipe` "Kitchen Recipe" | 无（`GTMFOItems.java` 中被注释） | **确证缺口**（见 §6.2，机制重构待验证） |

---

## 2. 方块（Blocks）✅ 全部移植

| 原版（注册名/变体） | 端口 | 证据 |
|---|---|---|
| `gtfo_casing`：ADOBE_BRICKS / REINFORCED_ADOBE_BRICKS / PORCELAIN_TILE / DARK_PORCELAIN_TILE | 4 个独立方块 `adobe_bricks` / `reinforced_adobe_bricks` / `porcelain_tile` / `dark_porcelain_tile` | `GTMFOBlocks.java:94-97` |
| `gtfo_metal_casing`：casing_bismuth_bronze | `bismuth_bronze_casing` | `GTMFOBlocks.java:99` |
| `gtfo_glass_casing`：GREENHOUSE_GLASS | `greenhouse_glass` | `GTMFOBlocks.java:101` |
| `pizza_{mincemeat,cheese,veggie}` + `pizza_box_*` | `PIZZA_MEAT/CHEESE/VEGGIE` + `PIZZA_BOX_*` | `GTMFOBlocks.java:126-132` |
| `gtfo_leaves/log/sapling/planks_N`（由 10 树动态生成） | `LOGS/PLANKS/SAPLINGS/LEAVES`（10 树） | `GTMFOTrees.java` |
| 作物/浆果丛方块 | `crop_*`（19 作物 + 10 浆果） | `GTMFOCrops.java:82-117` |
| （原版无） | `SMOGUS_*` 4 块 | `GTMFOBlocks.java:137-183`（原版 smogus 为物品，端口补了方块形态） |

---

## 3. 材料 / 流体（Materials & Fluids）

| 项 | 原版 `GTFOMaterialHandler` | 端口 `GTMFOMaterials` + `GTMFOFluids` |
|---|---|---|
| 材料名（去重） | **134** | **132** |

- 27 个原名在端口按名搜索不到，逐个核对后 **26 个是改名**，例如：
  `hot_frying_oil`→`frying_oil_hot`、`unheated_cane_syrup`→`cane_syrup_unheated`、
  `x_phenothiazine_ii_propyl_chloride`→端口同名（下划线变体）等。
- `stearic_acid`：**原版即被注释**（`GTFOMaterialHandler.java:180`、`FatChain.java:138/145` 均为注释），
  不算缺口。
- 端口新增名（`penicillium_roqueforti`、`zest`、`sodium_cyanide`、各类 curd、porcelain 等）：
  多为原版存在的同义名或原版 `brick.*` 系列材料，属命名差异。
- **结论：材料表完整，无确证缺口。**

---

## 4. 机器 / 配方类型 / 覆盖板 / 工具 ✅

| 类别 | 原版 | 端口 | 判定 |
|---|---|---|---|
| 单方块机器 | slicer、cuisine_assembler、microwave、multicooker、mob_age_sorter、mob_exterminator、mob_extractor、farmer | 同名 8 类（tiers 与原版一致） | ✅ |
| 多方块 | baking_oven、electric_baking_oven、steam_baking_oven、greenhouse、kitchen | 同名 5 类 | ✅ |
| 配方类型 | slicer、cuisine_assembler、microwave、multicooker、electric_baking_oven、baking_oven、mob_extractor、greenhouse（8） | 同名 8 个（`GTMFORecipeTypes.java`） | ✅ |
| 覆盖板 | CoverSprinkler | `CoverSprinkler` + `sprinkler_cover` | ✅ |
| 工具 | rolling_pin（4 材料）、butchery_knife.hv | `ROLLING_PIN`×4 + `BUTCHERY_KNIFE`（HV） | ✅ |
| 农民模式 | 22 种默认模式 + AgriCraft 条件模式 | 22 种（1.20 方块名映射：REEDS→SUGAR_CANE、RED_FLOWER→POPPY 等；无 AgriCraft，1.12 专属） | ✅ |

---

## 5. 效果 / 实体 / 作物 / 树 / 战利品 ✅

| 类别 | 原版 | 端口 | 判定 |
|---|---|---|---|
| 药水效果 | 10（Creativity、StepAssist、SnowGolemSpawner、CyanidePoisoning、Venting、PotionAmplifier、PotionLengthener、AntiSchizo、LungCancer、EnhancedChorus） | 10（`GTMFOEffects.java`，Creativity→`fly`） | ✅ |
| 实体 | italian_buffalo、strong_snowman、strong_snowball | 3 同名（`GTMFOEntities.java`） | ✅ |
| 作物 | 19（coffee…cotton，含 2 根茎类） | 19（`GTMFOCrops.java`） | ✅ |
| 浆果丛 | 10 | 10（thorny 标记一致） | ✅ |
| 树 | 10（banana…coconut，含 rainbowwood） | 10（索引一致，`GTMFOTrees.java`） | ✅ |
| 战利品表 | 44 物品条目（含 3 披萨） | 全部对应（披萨以**方块**形式 `PIZZA_*`，`GTFODungeonLoot.java:40-42`） | ✅ |
| 配方链 | 32 个 chain 类 | 35 个 chain 类（重组：Popcorn/Pasta/IVBag 并入 Core/Italian/GTMFORecipes 等） | ✅（逐链核对属另一审计） |

---

## 6. 确证未移植清单（带证据）

### 6.1 Dewar Flask 系列 + 矿泉水产线（功能缺口，中优先级）

- 原版：`DEWAR_FLASK / USED_DEWAR_FLASK / DEWAR_FLASK_CAP / DEWAR_FLASK_CASING / LEACHED_DEWAR_FLASK_CASING`
  5 个物品（`GTFOMetaItem.java:446-450`）+ `MineralWaterChain`（矿泉水瓶装、清洗、PolymoscoviumPentahalide 材料链）。
- 端口：仅 `GTMFOItems.java:315-319` 保留**注释**定义；全源码无任何配方/使用引用（`MINERAL_WATER` 物品存在但只作为食物/战利品，无生产链）。
- 影响：矿泉水无法通过产线获得；相关 5 个物品缺失。原版配置 `mineralWaterChain` 开关也未保留（一致）。

### 6.2 Kitchen Recipe 物品 + 厨房"配方卡"机制（机制重构，待实机验证）

- 原版：`KITCHEN_RECIPE = addItem(343, "utility.kitchen_recipe")`（`GTFOMetaItem.java:1346`）
  + `GTFOKitchenRecipeBehaviour`（记录最终产物、作为厨房幻影合成的配方卡）+ 铅笔合成配方（`GTFOMachineRecipes.java:131`）。
- 端口：`GTMFOItems.java:569` 注释掉该物品；厨房以 `KitchenCraftNode`/`KitchenMachine`/`KitchenState`
  直接包装 `GTRecipe` 重构实现（`common/machine/kitchen/`）。
- 影响：若端口厨房不再需要配方卡，则属**有意的机制重构**；需实机验证厨房功能是否等价。

### 6.3 集成缺口（低优先级，1.20.1 存在对应 mod）

| 原版集成 | 内容 | 端口 | 说明 |
|---|---|---|---|
| `sereneseasons` | `GTFOSSTooltipHandler`：作物"Fertile Seasons"tooltip | 无 | 仅 tooltip；1.20.1 有 SereneSeasons |
| `top` | `GTFORootCropProvider`：根作物探测信息 | 无 | 仅 HUD 信息；1.20.1 有 TOP |
| `appleskin` | `GTFOMetaHUDOverlay` 等（基于 1.12 AppleCore API） | 无 | 1.20.1 AppleSkin 自动读取食物属性，理论无需专用集成 |

### 6.4 配置项差异（`GTFOConfig` 50 字段 ↔ `GTMFOConfigHolder` 50 字段）

- 原版有、端口无（5）：`showTooltipsOnShift`、`showTooltipsAlways`、`popcornChain`、`mineralWaterChain`、`purpleDrinkChain`
  （前两个被端口统一 tooltip 体系取代；后三个链开关未保留——其中 `mineralWaterChain` 与 §6.1 缺口一致）。
- 端口有、原版无（5）：`nutrientMode`、`exportJeiRecipes`、`jeiRecipeExportPath`、`jeiNameExportPath`、`jeiExportHiddenRecipes`（端口新增功能）。

### 6.5 明确不算缺口（1.12 专属，无 1.20.1 对应或已由平台替代）

| 类别 | 内容 |
|---|---|
| 1.12 专属集成 | `agricraft`、`applecore`、`enderio`、`nc`（NuclearCraft 辐射）、`nutrition`、`tfc`、`GTFOGAMaterialHandler`（Gregicality） |
| 机制替代 | `GTFOOredictItem`/`GTFOProxyItem`（1.12 矿物词典/代理物品）→ 1.20.1 由 Tag 体系替代 |
| 未使用注册 | 4 个 `culture.*`（原版 addItem 行本身被注释）、`stearic_acid`（原版注释）、`Dried Corn Ear`（原版无消费方）、`BioReactor` 机器（原版注释） |

---

## 7. 端口新增（原版没有，供反向核对）

- **营养系统**：`api/capability/Nutrients` + `NutrientsTracker` + `NutrientCommands` + `nutrientMode` 配置。
- **JEI 全配方导出工具**：`integration/jei/export/`（4 个配置项）。
- **大量 tooltip 文本**（`GTMFOTooltips.java`，106 条）。
- **Smogus 方块形态**（原版仅物品）、披萨切片等命名细化。

---

## 8. 结论

1. **注册表主体移植完整**：物品（325→302 直接对应 + 23 条改名/重构）、方块、材料、机器、
   配方类型、效果、实体、覆盖板、工具、作物/浆果/树、农民模式、战利品表均已对齐。
2. **确证功能缺口 2 项**：Dewar Flask 矿泉水产线（§6.1）、厨房配方卡机制（§6.2，疑为有意重构）。
3. **低优先级缺口 3 项**：SereneSeasons/TOP/AppleSkin 集成（§6.3）。
4. **配置差异**：3 个链开关未保留、2 个 tooltip 开关被替代（§6.4）。
5. 除上述外，未发现其他注册表层面的缺失；`PORTING_TODO.md` 中未提及的差异以本文档为准。
