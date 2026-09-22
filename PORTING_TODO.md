# GTMFO 全面移植 TODO 文档

> GregTech Food Option 1.12.2 → 1.20.1 Forge (GTCEu Modern) 完整移植计划
> 
> 参考源码：`GregTechFoodOption-1.12.2-ORIGIN/`（原版）
> 目标项目：`src/main/java/com/ironsword/gtmfo/`（当前移植版）
> GTCEu 参考（**项目依赖版本**）：`H:\MinecraftMods\GregTech-Modern-7.5.2`（v7.5.2-1.20.1 标签）
> GTCEu 参考（新版，API 有差异）：`H:\MinecraftMods\GregTech-Modern`（1.20.1 分支 HEAD = 8.0.0）
> 
> ⚠️ 注意：项目依赖 GTCEu **7.5.2**，写代码时以 `GregTech-Modern-7.5.2` 为准。
> 已知 API 差异（7.5.2 vs 8.0.0）：`IMachineBlockEntity` vs `BlockEntityCreationInfo`、
> `IExplosionMachine.doExplosion()` vs `GTUtil.doExplosion()`、`getPos()` vs `getBlockPos()`、
> `TagPrefix.rod/rodLong` vs `stick/stickLong`
> 
> ## ⚠️ 移植原则（必读）
> 1. **一切以实现为准**：每个功能先读 `GregTechFoodOption-1.12.2-ORIGIN/` 原版实现（数值、概率、状态机、
>    配方、结构、GUI 行为），再按 1.20.1 + GTCEu 7.5.2 API 等价重写。**不得凭印象编造**。
> 2. **忠实优先**：能 1:1 还原的就 1:1（如微波炉爆炸数值、药水时长、掺加 NBT、雪人生成概率 1/(100/(amp+1))）。
> 3. **API 适配**：1.12 的 `MetaTileEntity`/`RecipeMap`/`ModularUI` → 1.20 的
>    `MachineDefinition`/`GTRecipeType`/LDLib `WidgetGroup`；数据驱动部分（世界生成）用 1.20 的
>    `ConfiguredFeature`/`PlacedFeature`/`BiomeModifier`。
> 4. **简化必须记录**：无法 1:1 实现时（如原版动态尺寸结构、自绘 GUI），在下方对应条目标注
>    `[简化]` 并说明差异与原因，绝不写成"已完成"。
> 5. **验证**：每个里程碑跑 `.\gradlew compileJava --console=plain`；资源/数据生成跑 `.\gradlew runData`；
>    实机验证跑 `.\gradlew runClient`（日志 `run/logs/latest.log`，客户端实测记录见 16.7~16.10）。

---

## 当前状态（2026-09-13 晚）

- 移植内容基本完成并**客户端实测通过**：机器/多方块/配方/世界生成/作物/实体/效果/工具全部可用，
  资源（模型/贴图/语言/JEI）齐全。
- 已知剩余告警（均无功能影响）：
  - `Mod 'gtmfo' took ~2.5s to run a deferred task`：Registrate 全局一次性监听器清理（列表里主要是
    GTCEu 的数百个物品颜色监听器），归因取决于模组构造顺序，非本模组可控。
  - 旧测试存档可能出现 `Unidentified mapping from registry minecraft:item`：因为开发过程中删除了自创
    物品（`MINCE_MEAT`），新存档无此问题。
- 用户明确暂不做：其它模组兼容（AppleSkin/TOP/Nutrition/TFC/NC/AA 等）。
- 新增开发工具：JEI 全配方导出（配置门控，导出 `jei_recipes.json` + `jei_names.json`），
  格式说明见 `docs/JEI_EXPORT_FORMAT.md`。

---

## 0. 资源移植状态（已完成 ✅）

### 0.1 资源对比结果
| 项目 | 原版 (1.12.2) | 移植后 (1.20.1) |
|------|---------------|-----------------|
| 资源文件总数 | 1126 | 1010 (运行时) + 465 (legacy 参考) |
| 纹理 PNG | 639 | 已全部处理 |
| 音效 OGG | 3 | 3（已复制 + sounds.json 修复命名空间） |
| 旧格式模型/blockstate | - | 移至 `legacy-resources/` 供参考 |

### 0.2 已复制到运行时的资源
- [x] 作物纹理 124 个 → `assets/gtmfo/textures/crop/`
- [x] 树木纹理（logs/planks/saplings/leaves/berry）→ `assets/gtmfo/textures/block/`
- [x] 披萨盒纹理 → `assets/gtmfo/textures/block/pizzabox/`
- [x] 机器覆盖纹理（farmer/greenhouse/mob 机器/sprinkler）→ `assets/gtceu/textures/block/machines/`
- [x] 覆盖板纹理（洒水器）→ `assets/gtceu/textures/block/cover/`
- [x] 流体纹理（rainbow_sap）→ `assets/gtceu/textures/block/fluids/`
- [x] 材料集纹理（organic/sand）→ `assets/gtceu/textures/`
- [x] GUI 纹理（gtfo_logo 系列/按钮/potions.png）→ `assets/gtceu/textures/gui/` + `assets/gtmfo/textures/gui/`
- [x] 实体纹理（italian_buffalo）→ `assets/gtmfo/textures/entity/`
- [x] 旧版物品纹理（metaitems 全系列）→ `assets/gtmfo/textures/item/metaitems/`
- [x] 工具纹理 → `assets/gtmfo/textures/item/tools/`
- [x] 音效文件 + `sounds.json`（命名空间已修正为 `gtmfo:`）
- [x] 有机材料集模型转换（crushed/dust/plate_dense → 1.20.1 格式）

### 0.3 移至 `legacy-resources/` 的旧格式文件（已转换 ✅）
- [x] 旧 blockstates（49 个）→ 已转换为 1.20.1 flat variants 格式
- [x] 旧模型（26 个）→ 已修复命名空间并复制到运行时
- [x] 自动生成 193 个模型（作物阶段/浆果丛/原木/树苗/木板/外壳）
- [x] 旧 lang 文件（12 个）→ 已转 JSON 至 `legacy-resources/converted/lang/`
- [x] 原版 mixin 配置 + 访问转换器（`meta/` 目录，移植 mixin 时参考）

**转换脚本**：`C:\Users\ADMINI~1\AppData\Local\Temp\opencode\convert_legacy.py`（可复用）

---

## 0.5 当前进度总览（2026-09-13 更新）

| 指标 | 1.12.2 原版 | 1.20.1 移植版 | 进度 |
|------|-------------|---------------|------|
| Java 文件数 | 200（含未移植集成） | 167 | ~90% |
| 资源文件数 | 1120 | 1465（运行时）+ 802（datagen） | ~100% |
| 机器（单方块） | 7 类（含 Farmer/Mob 机器） | 7 类全电压 + Farmer/Mob/微波炉/烤炉/厨房等 | 100% |
| 多方块 | 5 种 | 5 种（原始/电动烤炉、厨房、温室、Smogus 组装台） | ~95% |
| 配方链 | 32 条 | 35 个链类（含 Caplet/饮料/咖啡/树/浆果等） | ~100% |
| 世界生成 | 完整 | Perlin 聚簇 + 生物群系/配置门控（19 placed feature） | 100% |
| 实体 | 3 种 | 意大利水牛 + 相关 | 100% |
| 药水效果 | 10 种（+2 已注释） | 10 种（含自定义伤害源） | 100% |
| 工具 | 2 种 | 擀面杖 ×4 材质 + 电动屠刀 | 100% |
| JEI 集成 | 有 | 食物信息/掺加/进食分类 + 全配方 JSON 导出工具 | 100%+ |
| 兼容集成 | 10+ | 按用户决定暂不做 | N/A |

> 数据来源：`src/main/java` 167 个文件、`src/main/resources` 1465 个文件、`src/generated/resources` 802 个文件。

---

## 1. 基础设施 / 核心系统
> 本章已于 2026-09-13 逐项对照原版源码复核（核对事实，非凭经验）。

### 1.1 注册系统
- [x] GTRegistrate 注册器 (`GTMFORegistries`)
- [x] 创造模式标签页 (`GTMFOCreativeModeTabs`)
- [x] 拆分创造标签页：主分类 / 食物 / 作物 / 工具 / 方块 / 药品与酒 / 饮品 / 果蔬
      （与原版 `GTFOValues` 的 8 个 `BaseCreativeTab` 一一对应，`assignTabs()` 按原版规则归类）
- [x] 网络包系统 — **不需要**：原版唯一自定义包 `PacketAppleCoreFoodDivisorUpdate` 仅服务
      AppleCore 兼容（1.20.1 无该模组）；其余同步均改用 LDLib `@Persisted/@DescSynced` 同步字段
- [x] 数据码/同步 ID 系统 — **已由现代同步系统替代**：原版 6 个 `assignId()`
      （UPDATE_OPERATION_POS / SPRINKLER_DATA / SPRINKLER_EXISTENCE / FARMER_OUTPUT_FACING /
      KITCHEN_STATUS / KITCHEN_ORDER）在移植版对应 `@Persisted @DescSynced` 字段
      （洒水器/农夫 operationPosition、厨房状态等）

### 1.2 配置系统
- [x] Toma Configuration 基础框架 (`GTMFOConfigHolder`)
- [x] 补全配置项（已逐字段与原版 `GTFOConfig` 对比：9 组字段数完全一致，另加 DevConfigs）：
  - [x] `GTFOChainsConfig` — deleteBreadRecipe / makeChainsHarder（原版 popcornChain/mineralWaterChain/purpleDrinkChain 在原版即为注释，未移植）
  - [x] `GTFOVanillaOverridesConfig` — vanillaOverrideChain / useBakingOvenForMeats / useRollingPinForPaper
  - [x] `GTFOOtherFoodModConfig` — 5 项（AppleCore 相关，1.20.1 无 AppleCore，字段保留但无作用）
  - [x] `GTFONCConfig` — 3 项（NuclearCraft 不存在，字段保留）
  - [x] `GTFOAAConfig` — 2 项（ActuallyAdditions 不存在，字段保留）
  - [x] `GTFOFoodConfig` — 20 项，全部接入 `Foods`
  - [x] `GTFOPotionConfig` — 3 项，全部接入效果
  - [x] `GTFOWorldGenConfig` — enableGTFOTrees / enableGTFOBerries
        （新增 `GTMFOBiomeModifiers` 自定义 biome modifier，20 个世界生成 JSON 改用
        `gtmfo:config_gated_features` 类型，配置实际生效）
  - [x] `GTFOMiscConfig` — 5 项（greenhouseDirts / bakingOvenReplacement 等已接入）

### 1.3 核心 API / Mixin
- [x] `GTMFOFoodStats` 食物组件系统（6 维营养）
- [x] `INutrients` / `Nutrients` / `NutrientsTracker` 营养能力
- [x] FoodPropertiesMixin / ItemMixin（**已修复**：mixin 配置此前为空列表 + plugin 返回 false + refmap 名不匹配，导致进食时长从未生效；现已启用并修正：非食物不再被覆盖为 0，容器返还由 GTCEu FoodStats 组件处理，PlayerMixin 已移除（营养素由组件应用））
- [x] `IEatingDuration` 进食时长接口 — 已实现于 `FoodPropertiesMixin`，`ItemMixin.getUseDuration` 读取
- [x] `IContainerItem` 容器返还接口 — 已实现于 `FoodPropertiesMixin`（`GTMFOFoodStats` 写入）
- [x] `RecipeMapFluidCannerMixin`（原版 late mixin）— **已用现代等价方案实现**：
      `LacingCannerLogic`（`GTRecipeType.ICustomRecipeLogic`）在流体罐装机动态生成掺加配方
- [x] AppleSkin 联动 — **不需要**：移植版使用标准 `FoodProperties`，AppleSkin 可直接读取；
      原版 4 个 AppleSkin 集成类仅为 MetaItem 兼容层（1.20.1 无 MetaItem）

### 1.4 已有但未使用的脚手架（可复用）
- [ ] `WoodBlock` / `LogBlock` — 移植版自有类（原版无对应类），当前未注册（树木使用 `GTFOBlockLog`/`GTFOBlockPlanks`，已注册）
- [ ] `PlacedFoodBlock` / `PlaceableFoodItem` — 移植版自有脚手架，当前未使用（食物方块用 `SmoreBlock`/`PizzaBlock`，已注册）
- [x] `CreativeFlyEffect` — 已注册为 `GTMFOEffects.FLY`（创造之力），飞行权限在效果内管理
- [x] `BoxBuilder` — 已使用（11 处引用）
- [x] `NutrientCommands` — 已接线（`ForgeCommonEventListener` 注册指令）

### 1.5 事件处理器（原版 `GTFOEventHandler` + `GTFODropsEventHandler`，共 300+ 行）
- [x] `onMaterialsInit` 材料初始化 — 移植版在 `GTMFOMaterials`/`GTMFOFluids` 静态注册（等价）
- [x] `onLivingUpdate` 实体更新事件：
  - [x] 创造飞行药水的持久化管理 — `CreativeFlyEffect` 效果内管理（进入授予 mayfly、结束回收，非创造模式）
  - [x] 雪人生成药水逻辑 — `SnowGolemSpawnerEffect`（射线追踪 + 生成强雪傀儡 + 速度 IV）
  - [x] 台阶辅助药水 — `StepAssistEffect`（`setMaxUpStep`，配置门控）
  - [x] 药水颜色计算事件 — 氰化物掺加以 `visible=false` 隐藏粒子（等价）
  - [x] 成瘾/戒断药水 — 原版即为注释，不移植
- [x] 进度事件 `AdvancementEvent` — 仅 AppleCore 食物削减用，1.20.1 无对应，N/A
- [x] 方块事件 `BlockEvent`（浆果生长效率）— 移植版在 `GTFOBerryBushBlock.randomTick` 内处理
- [x] 世界事件 `WorldEvent` — 原版仅初始化假玩家，移植版按需创建（`FakePlayerFactory`）
- [x] 玩家事件 `PlayerEvent` — 仅 AppleCore 登录同步，N/A
- [x] `GTFODropsEventHandler` 掉落事件处理：
  - [x] 猪/牛/鸡/羊/兔死亡 1/3 概率掉落 `SCRAP_MEAT` — `GTMFODropsHandler`
  - [x] 碎肉数量随掠夺等级增加（`rand.nextInt(lootingLevel) + 1`）— 已实现
- [x] 假玩家处理 — 移植版使用 `FakePlayerFactory.getMinecraft`（农夫/屠宰机/提取机）

### 1.6 配方表修改（原版 `CommonProxy.preLoad`）
- [x] `BREWING_RECIPES.setMaxIOSize(1,1,1,1)` 酿造配方表输出扩容
- [x] `EXTRACTOR_RECIPES.setMaxIOSize(2,1,0,1)` 提取器输入扩容
- [x] `FERMENTING_RECIPES.setMaxIOSize(1,1,1,1)` 发酵配方表
- [x] `COMPRESSOR_RECIPES.setMaxIOSize(1,1,1,1)` 压缩机流体扩容
- [x] 杂草种子掉落注册 — `GTMFOLootModifiers` + `UnknownSeedsLootModifier`（概率 0.125×weight/(weight+1)，权重来自配置）
- [x] Lacing 掺加注册表（3 种：氰化物 / 抗精神分裂 / 肺癌）— `GTMFOLacing`，并支持动态罐装配方

---

## 2. 材料与流体（已完成 ✅）
> 本章已于 2026-09-13 用脚本逐条对照原版 `GTFOMaterialHandler` 复核。

### 2.1 流体材料（原版 131 个，ID 21500-21628）
- [x] **全部 131 个原版材料均已在移植版注册**（`GTMFOFluids` / `GTMFOMaterials`，共 133 个字段）。
      脚本对比（注册名归一化后）仅 2 个"缺失"，均已核实为无需移植：
  - `StearicAcid`（21538）— 原版即为注释（连同 FatChain 中的用途），死代码
  - `AceticAnhydride`（21585）— GTCEu Modern 自带 `GTMaterials.AceticAnhydride`，移植版直接使用
- 说明：个别字段名与注册名不同（如原版 `UnheatedCaneSyrup` → 移植版 `CaneSyrupUnheated`、
  `HotFryingOil` → `FryingOilHot`），功能与数值（颜色/温度 483K）一致。
- 移植版另有 4 个原版没有的材料（新版本内容）：`ChloroauricAcid`、`BerryJam`、`BlueVitriol`、`HotMilk`。
- 气体：`MoistAir`（273K）/`ColdMoistAir`（243K）已按原版注册为气体。
- 酸：`ChloroauricAcid` 带 `FluidAttributes.ACID`。

### 2.2 粉/物品材料（原版多为 `GTFOOredictItem` 物品，非材料）
> 原版这些"材料"实为 `SHAPED_ITEM.addOreDictItem(...)` 物品；移植版对应为普通物品（`GTMFOItems`）。

- [x] `Paracetamol` 对乙酰氨基酚（材料）— 移植版 `GTMFOMaterials.Paracetamol` ✓
- [x] `SodiumCyanide` / `Zest` — 移植版材料 ✓
- [x] `PotatoStarch` / `CornStarch` / `BoneAsh` / `BoneChinaClay` — 移植版物品 ✓
- [x] 瓷砖系列（`UnfiredPorcelainTile`/`BiscuitPorcelainTile`/`GlazedPorcelainTile`/`BlackGlazedPorcelainTile`）— 移植版物品/方块 ✓
- [x] 香草醛系列（`VanillylmandelicAcid`/`VanilglycolicAcid`/`Vanillin`）— 移植版物品 ✓
- [x] 药物中间体/药物（`Aminophenol`/`IVNitrophenol`/`IINitrophenol`/`Promethazine`/`Codeine`/`Phenothiazine`/`Diphenylamine`/`CrushedPoppy`）— 移植版物品 ✓
- [x] 化学粉（`AmmoniumPerchlorate`/`PotassiumPerchlorate`/`SodiumPerchlorate`/`SodiumChlorate`/`ArsenicTrioxide`/`CupricHydrogenArsenite`/`LithiumOxide`/`LithiumCarbonate`）— 移植版物品 ✓
- [x] 奶酪凝乳系列（`Large/Small/Dried/SolidifiedMozzarellaCurd`、`CoagulatedMilkCurd`、`CutCurd`、`CookedCurd`、`SaltedCurd`、`GorgonzolaCurd`、`PenicilliumRoqueforti`）— 移植版物品 ✓
- [x] 肉类中间品（`ToughMeat`/`KubideMeat`/`BargMeat`/`Fat`/`MeatIngot`/`CookedMinceMeat`）— 移植版物品 ✓
- [x] 调味料（`ShreddedParmesan`/`BlackPepper`/`Nutmeg`/`GratedHorseradishRoot`）— 移植版物品 ✓
- [x] `MashedPotato` / `BurntBananaPeel` — 移植版物品 ✓
- [x] 糖果系列（`HotAppleHardCandy`/`CrushedHardCandy`/`HardCandyPlate`/`HardCandyResin`）— 移植版物品 ✓
- [x] 咖啡 14 阶段中间品（`COFFEE_GROUNDS`、`SMALL/LARGE/UNSORTED_BASIC`、`WET`、`DRIED`、`HULLED`、`GRADED`、`ROASTED`）— 移植版物品 ✓
- [x] 巧克力/压饼系列（`CHOCOLATE_LIQUOR`×4、`PRESS_CAKE`×3）— 移植版物品 ✓
- [x] 棉花糖/全麦系列（`MATTER_MARSHMALLOW`、`MATTER_GRAHAM`、`WAFER_GRAHAM_HOT`、`CRACKER_GRAHAM_UNGRADED`、`CHUNK_GRAHAM_HOT`）— 移植版物品 ✓
- [x] 玉米系列（`BareCornKernel`/`CornKernel`/`CornCob`/`CornEar`）— 移植版物品 ✓（`DRIED_CORN_EAR` 仅用于原版已注释的 PopcornChain，N/A）
- [x] 可可中间品（`COCOA_HULL`/`COCOA_NIB`）— 移植版物品 ✓
- [x] **本轮新补**：咖啡作物 + `SEED_COFFEE`（原版 `CROP_COFFEE` 此前缺失）：
      - 新增 `seed_coffee` 物品与 `crop_coffee` 作物方块（种子→咖啡果）
      - 未知种子定向合成改为产出咖啡种子（原版 `gtfo_seed_coffee_ungenerify`）
      - 咖啡链恢复原版两段：咖啡果 →(切割机) 9 咖啡种子 →(离心机) 大小生咖啡豆
      - 咖啡种子加入种子榨油列表（原版遍历全部作物种子）
- [x] 命名差异（不影响功能）：`APPLE_HARD_CANDY` → 移植版 `APPLE_CANDY`（"Apple Candy"）、
      `PARTIALLY_FILLED_CHIP_BAG` 中间品被合并进薯片装袋配方、`DEWAR_FLASK` 系列为原版 WIP
      （其生产链整段注释），移植版矿泉水用玻璃瓶。
- [x] `KITCHEN_RECIPE` 厨房食谱卡 — 已知 [简化]（见 16.6.2）

### 2.3 材料属性系统
- [x] `CleanerProperty` 清洁属性（蒸馏水 2 / 硬脂酸钠 16）— `GTMFOMaterials.init()` 实际设置
- [x] `FertilizerProperty` 肥料属性 — 原版为材料属性；移植版以 `CoverSprinkler.FERTILIZER_BOOST`
      硬编码映射实现（水 5% / 血 30% / 肥料溶液 15%），行为一致
- [x] `LacingProperty` 掺加属性 — 移植版以 `GTMFOLacing.ENTRIES`（物品→效果表）实现，行为一致
- [x] `GTFOFireSuppressantProperty` 灭火属性 — 移植版洒水器直接判定水（原版仅水被标记），行为一致
- [x] `Organic` MaterialIconSet — **N/A**：原版用于物品形式的"材料"（Fat/ChocolateLiquor 等），
      移植版这些是普通物品，不需要图标集
- [x] 材料工具提示 `registerPropertyTooltips()` — 原版为肥料/掺加/清洁流体提示；
      移植版以物品提示（`ItemTooltipEvent`）与 JEI 信息页覆盖（见 16.6），流体属性提示未单独实现

### 2.4 原版材料标志修改
- [x] `Iron` + `GENERATE_FRAME`（本轮补上，GTCEu 默认无）
- [x] `BismuthBronze` + `GENERATE_FRAME`（已有）
- [x] `Aluminium` + `GENERATE_DENSE`（本轮补上）
- [x] `StainlessSteel` + `GENERATE_DENSE, GENERATE_SPRING_SMALL`（本轮补上）
- [x] `Titanium` + `GENERATE_DENSE`（本轮补上）

---

## 3. 物品（已完成 ✅）
> 本章已于 2026-09-13 逐项对照原版 `GTFOMetaItem`（325 个有英文名的物品）复核。
> 脚本按英文名对比：移植版 422 个物品，原版物品仅 26 个"未匹配"，逐一核实后
> 均为命名差异 / 原版死代码 / 已知简化（见下），无功能性缺失。

### 3.1 容器类
- [x] 烤盘 / 易拉罐 / 瓷碗 / 脏碗 / 瓷盘 / 脏盘 / 空杯 / 纸袋 / 塑料袋
- [x] 未烧制容器系列（`ceramic_bowl_unfired` 未烧制骨瓷碗 / `ceramic_plate_unfired` 未烧制骨瓷盘 /
      `cup_unfired` 未烧制杯子）
- [x] `Dewar Flask` 保温杯系列 — **N/A**：原版 `MineralWaterChain` 整段注释（死代码），
      移植版矿泉水使用玻璃瓶容器

### 3.2 食物物品（对照 `GTMFOItems` vs 原版 `GTFOMetaItem`）
- [x] **食物属性接线审计（2026-09-12/13）**：163 个原版 `GTFOFoodStats` 物品全部接线
  - [x] 72 个批量修复 + 补充接线（披萨片×3、PASTA_ALL_AMOGUS、原味冰淇淋、CHUM_BUCKET、
        BRICK_MUD/ADOBE、SORBET_CHORUS/VIBRANT 等）
  - [x] 取消注释并接线 5 个 `Foods` 条目
  - [x] `ROTTEN_MEAT`/`ROTTEN_FISH` 已补（1 饥饿 / alwaysEat / 中毒 500t）
  - [x] 数值/效果/概率/alwaysEdible/时长逐项审计（见 16.6），修复 BRUSCHETTA、VITELLO_TONNATO、
        RED_WINE、SORBET/FERMENTED/SANDWICH_VIBRANT、培根/吐司/胶囊 alwaysEdible、咖啡饮品等
- [x] ID/命名差异表（本次审计结论）：
  - 命名现代化（同物品）：`Apple Hard Candy`→`Apple Candy`、`Undetermined GTFO Seeds`→
    `seed_unknown`、`Garlic Bulb/Clove`→`Purple/White Garlic`（新版内容）
  - 烤肉串名称词序不同（`Kebab Meat Raw`→`Raw Meat Kebab` 等），物品齐全
  - 披萨为方块（`block.gtmfo.pizza_*`），名称与原版一致
  - `PARTIALLY_FILLED_CHIP_BAG` 中间品被合并进薯片装袋配方
  - `KITCHEN_RECIPE` 厨房食谱卡 — 已知 [简化]（见 16.6.2）
- [x] `POPCORN_BAG` 爆米花袋 — 已接线原版数值（5 饥饿 / 0.4 饱和 / alwaysEat / 跳跃提升）
      + 组装机配方（本轮修复）
- [x] `MINERAL_WATER` 矿泉水 — `Foods.MINERAL_WATER`（配置饥饿/饱和 + alwaysEat + 创造飞行 5000t）
- [x] Sorbet 系列 / Fermented Chorus 系列 / British 系列 / IV Bag（`iv_bag` 物品 + 组装机配方）

### 3.3 种子类
- [x] 全部作物种子物品（番茄/黄瓜/葡萄/洋葱/大豆/豆/豌豆/牛至/辣根/大蒜（紫/白）/罗勒/茄子/
      玉米/洋蓟/黑胡椒/大米/白葡萄/棉花/**咖啡（本轮补）**）
- [x] `seed_unknown` 未鉴定种子（杂草掉落 + 定向合成 19 种）

### 3.4 工具类
- [x] `ROLLING_PIN` 擀面杖（GTCEu ToolItem 系统，木/橡胶/聚乙烯/聚四氟乙烯 4 材质 + 合成配方）
- [x] `BUTCHERY_KNIFE` 电动屠刀 HV（Looting 5、HV 电动、损坏返还 HV 电源单元 + 能量转移合成配方）

---

## 4. 方块（已完成 ✅）
> 本章已于 2026-09-13 逐项对照原版方块注册复核。

### 4.1 作物方块（已完成 ✅）
- [x] `GTFOCropBlock` 基础作物方块系统（age 0-5）
- [x] `GTFORootCropBlock` 根茎类作物（洋葱/辣根，中期收获机制）
- [x] `GTFOWaterCropBlock` 水生作物（大米）
- [x] `GTFOBerryBushBlock` 浆果丛（含荆棘/效率育种机制）
- [x] **20 种作物**：咖啡（本轮补）/番茄/洋葱/黄瓜/葡萄/大豆/豆/豌豆/牛至/辣根/大蒜（紫/白）/
      罗勒/茄子/玉米/洋蓟/黑胡椒/大米/白葡萄/棉花
- [x] 10 种浆果丛：蓝莓/黑莓/树莓/草莓/红加仑/黑加仑/白加仑/越橘/接骨木莓/蔓越莓
- [x] 种子右键种植（事件拦截）
- [x] 作物自然生成 — **N/A**：原版世界生成只有树木与浆果丛，没有作物
- [x] 农场机模式集成 — 11 种模式（见 16.6）

### 4.2 树木方块（已完成 ✅）
- [x] `GTFOBlockSapling` 树苗系统（每树独立，含生长）
- [x] `GTFOBlockPlanks` 木板系统
- [x] `GTFOBlockLog` 原木系统
- [x] `GTFOBlockLeaves` 树叶系统（果实掉落）
- [x] 10 种树：香蕉/橙子/芒果/杏子/柠檬/酸橙/橄榄/彩虹木/肉桂/椰子
- [x] 树木配方（原木→木板/木棍）
- [x] 树木世界生成（数据驱动 JSON，见第 7 章说明）
- [x] 树叶着色（10 树独立颜色 + 彩虹木坐标彩虹色，`GTMFOClientEvents`）

### 4.3 建筑方块
- [x] `BISMUTH_BRONZE_CASING` 食品级铋青铜外壳
- [x] `ADOBE_BRICKS` 土坯砖块 / `REINFORCED_ADOBE_BRICKS` 加固土坯砖块
- [x] `PORCELAIN_TILE` 瓷砖 / `DARK_PORCELAIN_TILE` 暗色瓷砖
- [x] `GREENHOUSE_GLASS` 温室玻璃
- [x] `GTFOBlockCasing` / `GTFOMetalCasing` 外壳系统（移植版按 1.20.1 方式注册为独立方块）
- [x] CTM 连接纹理 — **N/A**：原版经 sussypatches（1.12 附属）实现；1.20.1 无对应系统，
      移植版使用普通纹理（纯视觉差异）

### 4.4 食物方块
- [x] 披萨方块（芝士/肉末/橄榄蘑菇，切片机制）
- [x] Smogus 方块（4 级）
- [x] S'more 系列 — 原版即为**物品**（8 层以上使用方块模型），移植版 `SMORE_1..64` 物品与
      `blockModel` 标志一致（`smore_block_*` 为移植版遗留未用脚手架）
- [x] 披萨盒（GTFOPizzaBox）：3 种方块 + 右键变披萨 + 打包机配方（原版 id）

---

## 5. 机器（单方块）（已完成 ✅）
> 本章已于 2026-09-13 对照原版 `GTFORecipeMaps` / 各 `MetaTileEntity` 复核。

### 5.1 已有机器
- [x] Slicer 切片机（LV-UV；配方表 IO 修正为原版 **2/2/1/1**）
- [x] Cuisine Assembler 菜肴组装机（LV-UV，IO 6/2/3/1 与原版一致）
- [x] Microwave 微波炉（LV-UV，IO 1/1/0/0 与原版一致）
  - [x] 放入锭/易燃/易爆/熔炉燃料物品时爆炸（伤害 = 等级 × 4）— `MicrowaveMachine`
  - [x] 完成时播放 `MICROWAVE_FINISH` 音效
- [x] Multicooker 多功能烹饪机（LV-UV，IO 6/3/3/2 与原版一致）
- [x] GTFO GUI 标志（`GTMFOGuiUtils.withLogo`，切片机/菜肴组装机/多功锅/微波炉/生物提取机）

### 5.2 机器
- [x] `FarmerMachine` 农场机（LV-EV 4 级）
  - [x] `FarmerMode` 模式系统 + `FarmerModeRegistry`（**11 种模式全部移植**：原版作物/可可/
        地狱疣/茎秆/高杆作物/地面清理/紫颂/GTFO 作物/根茎/浆果丛）
  - [x] 9x9 工作区域逻辑 + 原版两阶段算法（收集作物 → 种植种子）
  - [x] 假玩家收获系统 + 种子输入/作物输出 9+9 槽
  - [x] 激光音效 `FARMER_LASER`（原版激光束粒子为客户端效果，未移植）
- [x] `MobAgeSorterMachine` 生物年龄分拣机（LV-EV 4 级）：红石控制 / 幼年成年过滤 / 范围 1/3/5/9
- [x] `MobExterminatorMachine` 生物灭绝机（LV-EV 4 级）：红石控制 + 9x9 / 一氧化二氮加速 /
      掠夺等级 = 等级-1（假玩家+附魔剑）/ 流体槽
- [x] `MobExtractorMachine` 生物提取机（LV-UV 8 级）：`mob_on_top` / `cause_damage` 配方数据 +
      实体检测攻击 + **自定义伤害源**（本轮补：`gtmfo:extraction`，无视护甲，同原版）
- [x] 自定义伤害源（本轮补全）：`extraction` / `extermination` / `cyanide` / `lung_cancer`
      （原版 `GTFODamageSources`，全部 `BYPASSES_ARMOR`，含原版死亡消息 EN/CN）
- [x] `MetaTileEntityBioReactor` 生物反应器 — **N/A**：原版整段注释（死代码）

---

## 6. 多方块（已完成 ✅）
> 本章已于 2026-09-13 对照原版多方块类复核。

### 6.1 烤炉三件套
- [x] `PRIMITIVE_BAKING_OVEN` 原始烤炉（土坯砖结构，燃料配方）
- [x] `ELECTRIC_BAKING_OVEN` 电力烤炉（温度系统）
- [x] `STEAM_BAKING_OVEN` 蒸汽烤炉（蒸汽=温度/100，时长 x4）
- [x] 烤炉配方系统：
  - [x] 温度机制（`GTMFOBakingOvenRecipes` 写入 recipe data + `ElectricBakingOvenMachine` 精确匹配）
  - [x] 温度匹配（`ELECTRIC_BAKING_OVEN_RECIPES`，并行=多方块长度，无超频）
  - [x] 原始烤炉 → 电力烤炉配方自动转换（`addBakingOvenRecipes` 同时生成两套）
  - [x] 烤炉替换熔炉配方选项（`bakingOvenReplacement` 配置门控）
  - [x] 温室/厨房/洒水器配方链

### 6.2 温室与厨房
- [x] `GREENHOUSE` 温室
  - [x] 7x9x7 玻璃结构（含钢框架柱）
  - [x] 可配置土壤方块（`greenhouseDirts`，支持 `namespace:block[prop=value]`）
  - [x] 阳光判定 + 无阳光时长翻倍（原版 `GreenhouseWorkable`）
  - [x] `GreenhouseRecipes` 温室配方链
- [x] `KITCHEN` 厨房
  - [x] 5x2x6 结构（钢管外壳 + 瓷地板，原版可扩展规则）
  - [x] `KitchenMachine`/`KitchenCraftNode`/`KitchenState` 订单系统（BFS 合成树）
  - [x] 幻影目标槽 + 订单数量按钮（替代原版食谱卡槽，见 16.6.2）
  - [x] 能量消耗 = VA[tier] / 2 + 维持运行中机器
  - [x] 脏污/清洁剂机制（蒸馏水 2 / 硬脂酸钠 16）
  - [x] 输入/输出物品+流体仓

---

## 7. 世界生成（已完成 ✅）
> 本章已于 2026-09-13 对照原版 `worldgen/` 包复核。原版为 `IWorldGenerator` + Perlin 噪声聚簇系统；
> 移植版改用 1.20.1 数据驱动（ConfiguredFeature / PlacedFeature / BiomeModifier），
> 并**移植了原版的 Perlin 聚簇逻辑**（自定义 placement modifier）。

### 7.1 框架
- [x] 特征系统改用 1.20.1 的 `ConfiguredFeature` / `PlacedFeature` / `BiomeModifier`
- [x] **Perlin 噪声聚簇**（原版 `GTFOFeature.getRandomStrength` + `GTFOFeatureGen.getAmountInChunk`）：
  - [x] `GTFOSimplexNoise` — 1.12 `NoiseGeneratorSimplex` 的忠实移植（`java.util.Random` 种子，
        同种子产生相同噪声场）
  - [x] `GTFOFeaturePlacement` — 自定义 placement modifier `gtmfo:gtfo_feature`：
        找到第一个满足的条件 → 计算 `(chunkX*0.04, chunkZ*0.04)` 噪声 →
        噪声 > perlinCutoff 时生成 `ceil(maxAmount - cutoff*maxAmount)` 个特征
  - [x] `BiomeAccessor` mixin 读取原版私有 `climateSettings`（downfall）
- [x] 条件系统：
  - [x] `FeatureCondition` 基础条件（JSON 条件列表，首个满足者生效）
  - [x] `BiomeCondition` 生物群系条件（每条件独立 biome tag + maxAmount + cutoff）
  - [x] `TemperatureRainfallCondition` 温度/降雨条件（运行时按生物群系气候计算 habitability）
- [x] `GTFOWorldGenConfig.enableGTFOTrees/enableGTFOBerries` 实际生效
      （`GTMFOBiomeModifiers` 自定义 biome modifier，见 1.2）

### 7.2 树木世界生成（10 种，全部按原版条件）
- [x] 香蕉（种子 0）/ 橙子（1）/ 芒果（2）/ 杏子（3）/ 柠檬（4）/ 酸橙（5）
- [x] 橄榄（6）/ 彩虹木（7）/ 肉桂（8）/ 椰子（9）— feature_seed 与原版一致
- [x] 各自的生成条件（原版 BiomeCondition + TemperatureRainfallCondition 参数逐条写入
      placed_feature JSON；生物群系集合按原版公式重新计算）
- [x] 树木生成器（1.20.1 tree feature 等价）

### 7.3 浆果世界生成（10 种）
- [x] 每种浆果按原版 `TemperatureRainfallCondition` 参数（种子 1000-1009）
- [x] 生物群系集合按原版温湿度公式重新生成（此前为近似映射）

### 7.4 地牢战利品（已完成）
- [x] `GTFODungeonLoot`（原版 `GTFODungeonLootLoader`）：
  - [x] 41 种食物 + 权重/数量逐项一致（7 种宝箱表：废弃矿井/丛林神庙/沙漠神殿/
        地牢/要塞走廊/要塞交叉口/林地府邸）
  - [x] 掺加（氰化物）变体（权重按原版 /3 /2 规则，`addLacedDungeonFoods` 配置门控）
  - [x] 稀有物品：矿泉水 / Smogus 之心（丛林神庙 + 林地府邸）
  - [x] 使用 GTCEu `ChestGenHooks`（1.20.1 等价实现）
  - [x] `addDungeonFoods` 配置门控

---

## 8. 实体（已完成 ✅）

- [x] `ItalianBuffaloEntity` 意大利水牛（对照原版 `EntityItalianBuffalo`）
  - [x] 纹理 `assets/gtmfo/textures/entity/italian_buffalo/italian_buffalo.png`（原资源已复制）
  - [x] 可挤奶 → `ItalianBuffaloMilk` 桶（原版 `UniversalBucket` 的 1.20 等价）
  - [x] 水生群系生成（3x3 区块内需有海洋/河流/海滩；生成群系修饰器 `add_italian_buffalo.json`）
  - [x] 刷怪蛋（0x3d352f / 0xf0ded1，与原版一致）
  - [x] 渲染器 `ItalianBuffaloRenderer`（`CowModel` + 自定义纹理，对应原版 `RenderItalianBuffalo`）
- [x] `StrongSnowmanEntity` 强力雪人（对照原版 `EntityStrongSnowman`）
  - [x] 强化雪球攻击（`performRangedAttack` 发射 `StrongSnowballEntity`，初速/散布 1.6F/4.0F 同原版）
  - [x] 10 血 / 0.3 移速（原版 `applyEntityAttributes`）
  - [x] 10000 tick 后自毁（原版 `onLivingUpdate` timer）
  - [x] 渲染器（复用原版 `SnowGolemRenderer`，对应原版 `RenderSnowMan`）
- [x] `StrongSnowballEntity` 强力雪球弹射物（对照原版 `EntityStrongSnowball`）
  - [x] 伤害 2-3，对烈焰人 +3；雪傀儡/玩家穿透不消失
  - [x] 渲染器（`ThrownItemRenderer`，原版 `RenderStrongSnowball` 的 1.20 等价）
- [x] 实体注册系统 `GTMFOEntities`（`DeferredRegister<EntityType<?>>` + 属性注册事件）

---

## 9. 药水效果（已完成 ✅）

### 9.1 骨架
- [x] `GTMFOEffects` 注册器（DeferredRegister）+ EN/CN lang
- [x] 10 个效果全部注册且有实际逻辑

### 9.2 各效果实现（对照原版 `potion/` 包）
- [x] `CreativeFlyEffect` 创造飞行（原版 `CreativityPotion` + `GTFOEventHandler` 的持久 NBT 管理）
- [x] `StepAssistEffect` 台阶辅助（潜行时 0.9 格；原版 `stepHeight` 修改）
- [x] `SnowGolemSpawnerEffect` 雪人生成（射线追踪生成强力雪人 + 力量 IV 1000t，同原版）
- [x] `CyanidePoisoningEffect` 氰化物中毒（分阶段：反胃/虚弱 → 失明 → 递增魔法伤害）
- [x] `VentingEffect` 排气（随机传送 + `amogus.vent` 音效，原版 `VentingPotion`）
- [x] PotionAmplifierEffect 药水增幅（ForgeCommonEventListener.onEffectApplicable：新增效果等级 + 增幅等级 + 1）
- [x] PotionLengthenerEffect 药水延长（同上；注意：原版时长公式用的是新效果的**等级**而非时长，疑似上游 bug，已忠实照搬）
- [x] `AntiSchizoEffect` 抗精神分裂（标记效果）
- [x] `LungCancerEffect` 肺癌（每 600 tick 最大生命 -1，不可治愈）
- [x] `EnhancedChorusEffect` 强化紫颂果（潜行朝视线方向传送 8 格）

### 9.3 掺加系统（Lacing）— 已完成 ✅（对照原版 `potion/LacingEntry` + `GTFOFoodStats.onFoodEaten`）
- [x] `GTMFOLacing`：3 条内置掺加（氰化钠→氰化物中毒 1300t / 碳酸锂→抗精神分裂 1000t /
      石棉粉→肺癌 99999999t），物品取自 `ChemicalHelper.get(TagPrefix.dust, ...)`
- [x] NBT 标记：食物物品 NBT 键 `gtmfo_lacing` 存效果索引（原版为图案字符串 `nbtKey`，
      这里用索引——`[简化]`，等价效果）
- [x] 食用触发：`GTMFOFoodStats.finishUsingItem` 读取标记并施加对应效果
      （原版 `onFoodEaten` 遍历 `LACING_REGISTRY` 检查布尔标记的等价实现）
- [x] 罐装配方 18 条（3 掺加物 × 6 种食物：面包片/吐司/小圆面包/披萨片/汉堡肉/意式烤猪肉片）
      ——原版通过 `RecipeMapFluidCannerMixin` 动态生成，这里改为显式配方（`[简化]`，效果一致）
- [ ] 与 JEI 的掺加信息页集成（P3）

---

## 10. 工具（已完成 ✅）

- [ ] `ROLLING_PIN` 擀面杖
  - [ ] GTCEu `ItemGTTool` 系统适配
  - [ ] 4 种材质配方（木/橡胶/PE/PTFE）
  - [ ] `craftingToolRollingPin` 工具类
  - [ ] 面包/纸张配方集成
- [ ] `BUTCHERY_KNIFE_HV` 电动屠刀
  - [ ] GTCEu `ItemGTSword` 系统适配
  - [ ] 电动工具（HV）+ Looting 5
  - [ ] 屠夫工具类

---

## 11. 覆盖板（已完成 ✅）

- [ ] `CoverSprinkler` 洒水器覆盖板
  - [ ] 9x9 工作区域
  - [ ] 作物催熟 + 耕地保湿
  - [ ] 灭火功能（高度 9）
  - [ ] 肥料属性支持（不同液体不同效果）
  - [ ] 清洁属性支持
  - [ ] 粒子效果 `GTFOSprinkleMaker`
  - [ ] 各电压等级
- [ ] `GTFOCoverBehaviors` 覆盖板注册

---

## 12. 配方链（原版 32 条，现代版 35 个链类，全覆盖 ✅）

### 12.1 已有（需核对完整性）
- [x] `AppleRecipes` 苹果链
- [x] `BerryRecipes` 浆果链
- [x] `BreadsRecipes` 面包链
- [x] `CapletRecipes` 胶囊链
- [x] `CoreChain` 核心链
- [x] `PotatoRecipes` 土豆链
- [x] `PizzaRecipes` 披萨链
- [x] `KebabRecipes` 烤肉链
- [x] `ItalianRecipes` 意大利菜链
- [x] `SmoreRecipes` S'more 链
- [x] `CornRecipes` 玉米链（部分）

### 12.2 缺失配方链
- [x] `AlcoholChain` 酒精链（伏特加/啤酒/白红葡萄酒/硅岩乳）→ `AlcoholRecipes`
- [x] `BananaProcessingChain` 香蕉加工链（钾提取/高氯酸盐）→ `BananaRecipes`
- [x] `BritishChain` 英式料理链（炸鱼薯条/全英早餐/香肠卷/牧羊人派）→ `BritishRecipes`
- [x] `CheeseChain` 奶酪链（切达/马苏里拉/戈贡佐拉/帕马森）→ `CheeseRecipes`
- [x] `ChorusChain` 紫颂果链 → `ChorusRecipes`
- [ ] `CoffeeChain` 咖啡链（14 阶段豆处理，现代物品已重构需重新设计）
- [x] `DyeChain` 染料链（温室玻璃组装部分跳过）→ `DyeRecipes`
- [ ] `FatChain` 脂肪链
- [ ] `GreenhouseChain` 温室链（需温室多方块）
- [x] `IceCreamChain` 冰淇淋链 → `IceCreamRecipes`
- [ ] `IVBagChain` 输液袋链
- [x] `LithiumChain` 锂链 → `LithiumRecipes`
- [x] `MicrowaveChain` 微波炉链（炖菜）→ `MicrowaveRecipes`
- [ ] `MineralWaterChain` 矿泉水链（0.x）
- [ ] `MobExtractionChain` 生物提取链（需生物提取机）
- [x] `PastaChain` 意面链 → `ItalianRecipes`
- [x] `PlateChain` 盘子链 → `PlateRecipes`
- [ ] `PopcornChain` 爆米花链（原版即注释 WIP）
- [ ] `PotatoProcessingChain` 土豆加工链（现代 `PotatoRecipes` 已覆盖大部分）
- [x] `PurpleDrinkChain` 紫色饮料链 → `PurpleDrinkRecipes`
- [x] `RussianChain` 俄式料理链 → `RussianRecipes`
- [x] `SeedsChain` 种子链（种子提取/大豆油）→ `SeedsRecipes`
- [x] `SmogusChain` Smogus 链（可可/巧克力）→ `ChocolateRecipes`
- [x] `SorbetChain` 冰糕链 → `SorbetRecipes`
- [ ] `VanillaOverrideChain` 原版覆写链
- [x] `VanillinChain` 香草醛链 → `VanillinRecipes`
- [x] `ItalianChain` 意大利菜链 → `ItalianRecipes`
- [x] `KebabChain` 烤肉链 → `KebabRecipes`

**已移植 18 条新链，剩余 8 条**（Coffee/Fat/Greenhouse/IVBag/MineralWater/MobExtraction/Popcorn/VanillaOverride）

### 12.3 配方移除
- [ ] `GTFORecipeRemoval` 原版配方移除系统
- [ ] `GTFOOreDictRegistration` 矿辞注册系统
- [ ] `GTFORecipeAddition` 配方追加系统

---

## 13. 兼容 / 集成（按用户决定暂不做，N/A）

- [ ] AppleCore 兼容（`GTFOAppleCoreCompat`）
  - [ ] 食物数值注册
  - [ ] 食物除数网络同步
- [ ] AppleSkin 兼容（4 个类）
  - [ ] `GTFOMetaFoodHelper`
  - [ ] `GTFOMetaHUDOverlay` HUD 显示
  - [ ] `GTFOMetaTooltipOverlay` 提示框显示
  - [ ] `GTFOAppleCoreIsolation`
- [ ] The One Probe (TOP) 兼容
  - [ ] `GTFOTOPCompatibility`
  - [ ] `GTFORootCropProvider` 根茎作物信息
- [ ] TFC (TerraFirmaCraft) 兼容
  - [ ] `GTFOTFCCompatibility`
- [ ] AgriCraft 兼容
  - [ ] `GTFOAgriCraftPlugin`
  - [ ] `GTFOAgriPlant`
  - [ ] `GTFOAgriCraftFarmerMode`
- [ ] EnderIO 兼容
  - [ ] `GTFOBerryFarmer`
  - [ ] `GTFORootCropFarmer`
  - [ ] `GTFOEIORecipeHandler`
- [ ] NuclearCraft 兼容（S'more 链）
  - [ ] `GTFONCRecipeHandler`
- [ ] ActuallyAdditions 兼容
  - [ ] 咖啡机配方修改
- [ ] Nutrition 兼容
  - [ ] `GTFONutritionCompatibility`
- [ ] Serene Seasons 兼容
  - [ ] `GTFOSSTooltipHandler`
- [ ] JEI 集成
  - [ ] `JEIGTFOPlugin`
  - [ ] `EatingRecipeCategory` 进食信息页
    - ✅ LacingCategory（掺加信息页）：掺加物 + 食物 → 带效果的掺加食物
- [ ] KubeJS 集成（原版有 ZenScript 注解，1.20.1 用 KubeJS）

---

## 14. 资源文件（已完成 ✅）
> 本章已于 2026-09-13 用脚本全面审计（blockstate→模型→纹理引用链）。

### 14.1 纹理
- [x] 作物纹理（各生长阶段）→ `gtmfo:textures/crop/`
- [x] 树木纹理（树苗/木板/原木/树叶）→ `gtmfo:textures/block/`
- [x] 实体纹理 → `gtmfo:textures/entity/`
- [x] GUI 纹理（药水图标 `gui/potions.png`、GTFO logo 系列、按钮）→ `gtceu:textures/gui/`
- [x] 机器 GUI 覆盖层（SEED/CROP 槽位覆盖、机器 overlay）
- [x] 覆盖板纹理（洒水器）→ `gtceu:textures/block/cover/`
- [x] 机器覆盖纹理（农场机/生物机器/微波炉等）→ `gtceu:textures/block/machines/`
- [x] CTM 连接纹理已复制（1.20.1 无 sussypatches，未接线，纯视觉差异）
- [x] **纹理引用接线**：脚本审计 806 个模型 → 0 个缺失父级；
      本轮修复 **19 个手写物品模型的错误纹理路径**（animal_fat/scrap_meat/香肠系列/
      热汤系列/结构纤维等缺少 `item/` 前缀，此前游戏内显示为紫黑格）

### 14.2 模型
- [x] 作物模型（1.20.1 `crop_cross` 父级，20 作物 × 6 阶段全部生成/手写）
- [x] 树苗/木板/原木/树叶模型（10 树，全部 datagen 生成）
- [x] 浆果丛模型（small/large/ripe，10 种浆果）
- [x] 披萨盒（3 种方块 + 右键变披萨 + 打包机配方）
- [x] 多方块结构模型（GTCEu workable casing 系统处理）
- [x] 机器模型（GTCEu workable 系统处理）
- 说明：未引用的遗留模型（hops/popcorn 作物、artichoke stage6-7、`gtfo_sapling_*`）为
  原版 WIP/未注册内容，无害

### 14.3 语言文件
- [x] 英文/中文 lang 由 datagen 生成（物品/方块/机器/效果/实体/创造页）
- [x] 新物品/方块/机器 EN/CN lang（本轮补 472 条缺失 + 修复陈旧条目）
- [x] 机器工具提示 lang（温室/厨房/烤炉 + 每级风味提示）
- [x] 药水效果 lang — 本轮修复：补全 10 个效果（此前生成文件仅 4 个且数值陈旧，
      如 fly 显示 "Fly" 而非 "Creativity"）
- [x] 覆盖板 lang（洒水器）
- [x] **其他 10 种语言**（本轮生成）：de_de / en_gb / es_es / fr_fr / it_it / ja_jp /
      ko_kr / pt_br / ru_ru / zh_tw — 按英文名映射 legacy 翻译，各 641/784 条
      （其余为移植版新增内容，legacy 无对应翻译）

### 14.4 音效
- [x] `microwave.finish` 微波炉完成音效
- [x] `farmer.laser` 农场激光音效
- [x] `amogus.vent` Smogus 排气音效
- [x] `sounds.json` 命名空间为 `gtmfo:`
- [x] SoundEvent 已注册（`GTMFOSounds`）并在机器/效果中调用
      （微波炉/农夫/排气效果）

### 14.5 资源完整性审计（2026-09-13）
- [x] 修复缺失方块状态/模型：土坯砖系列、磁砖系列、温室玻璃
- [x] 修复缺失树叶模型：原版仅有 banana/rainbowwood 纹理，其余 8 种树用 plain 纹理
- [x] 修复错误父级：minecraft:block/block/leaves → minecraft:block/leaves
- [x] 生成 59 个缺失物品模型 + 19 个错误纹理路径修复
- [x] 审计结果：0 个 blockstate 引用缺失模型，0 个模型引用缺失纹理（除未注册 WIP）

## 15. 数据生成（已完成 ✅）

- [x] `GTMFODataGen` provider 类型：Registrate LANG + 自定义 CNLANG（中文本地化）、
      Registrate 默认的 blockstate/item model/tag/loot 生成器
- [x] 方块状态生成（作物/树/机器，62 个生成 + 82 个手写）
- [x] 物品模型生成（419 个生成 + 86 个手写）
- [x] 战利品表：披萨/Smogus/外壳等 8 个方块用生成表；作物/树叶掉落由代码
      `getDrops` 实现（1.20.1 等价，含时运/果实掉落）
- [x] 方块标签生成（`gtceu:mineable/pickaxe_or_wrench` 等）
- [x] 世界生成 JSON：放置特征/生物群系标签手写于 `src/main/resources`
      （数据驱动，无需 datagen；生物群系修饰器为自定义 `gtmfo:config_gated_features`）

---

## 16. 移植优先级建议

### P0 — 核心体验（已完成 ✅）
> 本节已于 2026-09-13 对照代码逐项复核。

1. **配方链补全**（原版 32 条链）— ✅ **全部已移植 / 已确认 N/A**
   - ✅ 已移植：Italian / Kebab / Cheese / IceCream / PurpleDrink / Sorbet / Chorus / Microwave /
     Plate / Russian / Vanillin / Dye / Lithium / Chocolate / Alcohol / Seeds / Banana / British /
     Apple / Berry / Breads / Caplet / Coffee（本轮补咖啡作物+种子链）/ Fat / Greenhouse / IVBag /
     MobExtraction / VanillaOverride / Smore / Core / Potato / Pizza / AdobeBrick / Tree / Tool
   - N/A（原版即为整段注释的死代码）：`PopcornChain` / `MineralWaterChain`
   - ✅ 修复 `ONION_SLICE` 重复 ID、`mozzarella` 重复配方、咖啡种子缺失
   - ✅ 新增物品：`SEASONED_PORK`/`ANIMAL_FAT`/`KUBIDE_MEAT`/`BARG_MEAT`/热炖菜×3/`MUSHY_PEAS`/香肠×4/`SHEPHERDS_PIE`
   - ✅ 新增材料：凝乳×10 / 骨瓷×5 / 药物化学系列 / `LaminatedDough`
2. **材料/流体补全** — ✅ 已完成（脚本对比原版 131 个材料：全部在位；2 个"缺失"为原版死代码/GTCEu 自带）
3. **微波炉特殊行为 + GTFO GUI 标志** — ✅ 已完成
   - ✅ 微波炉爆炸（等级×4）+ 完成音效（`MicrowaveMachine`）
   - ✅ 音效注册系统（`GTMFOSounds`，3 个音效均已调用）
   - ✅ GTFO GUI 标志（`GTMFOGuiUtils.withLogo`）+ **XMAS 变体**（`GTValues.XMAS` 门控，本轮补）
4. **烤炉多方块 + 温度系统** — ✅ 已完成（本轮补温度系统）
   - ✅ `PrimitiveBakingOvenMachine` / `ElectricBakingOvenMachine` / `SteamBakingOvenMachine`
   - ✅ 注册 `PRIMITIVE_BAKING_OVEN`（3x3x2 土坯砖 + 铁/青铜框架）
   - ✅ 土坯砖链：泥砖×4 配方 → 熔炼土坯砖 → 2x2 外壳 → 加固外壳
   - ✅ 注册 `ADOBE_BRICKS` / `REINFORCED_ADOBE_BRICKS` / `PORCELAIN_TILE` / `DARK_PORCELAIN_TILE` 方块
   - ✅ **温度系统**（本轮实现）：`ELECTRIC_BAKING_OVEN_RECIPES` + 调温/指数耗电/温度精确匹配/
     并行=长度；蒸汽烤炉（蒸汽=温度/100、时长×4）；燃料配方（木板/煤/木炭）
5. **作物系统**（20 作物 + 10 浆果）— ✅ 已完成
   - ✅ `GTFOCropBlock`（age 0-5 + 右键收获）/ `GTFORootCropBlock`（age 0-7 中期收获）/
     `GTFOWaterCropBlock`（水稻）/ `GTFOBerryBushBlock`（efficiency 育种 + 荆棘）
   - ✅ `GTMFOCrops` 注册 **20 作物**（含本轮补的咖啡）+ 10 浆果丛
   - ✅ 右键种植事件（`ForgeCommonEventListener`）
   - ✅ 农场机集成 — 11 种 FarmerMode（见 P1）
   - N/A：作物自然生成（原版世界生成只有树木与浆果丛，没有作物）
6. **树木系统**（10 树种）— ✅ 已完成
   - ✅ `GTFOBlockLog` / `GTFOBlockPlanks` / `GTFOBlockLeaves` / `GTFOBlockSapling`（每树独立）
   - ✅ `GTMFOTrees` 40 个方块 + 生长逻辑；`TreeRecipes`（原木→木板 4 种：合成+切割机；木板→木棍）
   - ✅ 世界生成（数据驱动 + **本轮补原版 Perlin 聚簇**，见 P1）
   - ✅ 树叶颜色着色（10 树独立颜色 + 彩虹木坐标彩虹色）
   - `[简化]` 自定义树形（原版香蕉/椰子树等特有形状）：移植版使用原版树形 placer（fancy/straight），
     未复刻原版手写生成器

### P1 — 重要功能（已完成 ✅）
7. **农场机** — ✅ 已完成（**完整 FarmerMode 系统**，本轮补全）
   - ✅ `FarmerMachine`（LV-EV，动作间隔 20/10/5/2 tick）
   - ✅ **11 种 FarmerMode**（原版 `FarmerModeRegistry` 全部）：原版作物/可可/地狱疣/茎秆（棋盘格）/
     高杆作物（整列）/地面清理/紫颂/GTFO 作物/根茎作物/浆果丛
   - ✅ 9x9 工作区域 + 原版两阶段算法（收集作物 → 种植种子，输出满时跳过模式）
   - ✅ 假玩家收获 + 种子输入/作物输出 9+9 槽 + 充电槽 + 激光音效
   - ✅ **自动输出**（本轮补：GUI 开关，输出到机器背面；原版 `autoOutputItems`）
   - `[简化]` 激光束粒子（原版客户端 `GTFOFarmingLaserBeamParticle`）未移植（仅音效）
   - `[简化]` 输出面配置（原版可选六面）简化为固定背面
8. **生物机器 3 件套** — ✅ 已完成（年龄分拣/灭绝/提取 + 自定义伤害源）
9. **温室多方块** — ✅ 已完成（阳光判定/可配置土壤/配方链）
10. **洒水器覆盖板** — ✅ 已完成（9x9/催熟/保湿/灭火/肥料/粒子）
11. **世界生成**（树/浆果分布）— ✅ 已完成（**本轮升级为原版 Perlin 聚簇**）
    - ✅ 10 树种 + 10 浆果丛：configured/placed feature + biome modifier + biome tag
    - ✅ **原版条件系统**（本轮）：`GTFOFeaturePlacement` 自定义 placement modifier —
      首个满足条件 + simplex 噪声 cutoff + `ceil(max−cutoff×max)` 数量；`GTFOSimplexNoise`
      为 1.12 `NoiseGeneratorSimplex` 忠实移植；生物群系集合按原版温湿度公式重算
    - ✅ 地牢战利品注入（`GTFODungeonLoot` + GTCEu `ChestGenHooks`，41 食物 + 掺加变体）
    - ✅ `enableGTFOTrees/Berries` 配置实际生效
12. **工具**（擀面杖 4 材质 / HV 屠刀）— ✅ 已完成

### P2 — 内容补全（已完成 ✅）
13. **药水效果 10 种** + 掺加系统 — ✅ 已完成
    - ✅ 10 个效果全部有实际逻辑（创造飞行/台阶/雪人/氰化物/排气/增幅/延长/抗精神分裂/肺癌/紫颂）
    - ✅ **掺加系统**：`GTMFOLacing` + `GTMFOFoodStats` 食用触发；
      配方为**动态罐装逻辑**（`LacingCannerLogic`，任意 GTFO 食物可掺加，本轮替换硬编码 6 种）
    - ✅ 18 个意大利菜品食物属性接线；食物效果接线（BRUSCHETTA/CAPONATA/PASTA_AL_POMODORO 等）
14. **实体 3 种** — ✅ 已完成（水牛/强雪人/强雪球，含生成/渲染/刷怪蛋/生物群系修饰器）
15. **厨房多方块** — ✅ 主体已完成（订单系统/脏污/清洁/能量/状态显示）
    - `[简化]` 结构尺寸固定 5×2×6（原版动态半径扫描）
    - `[简化]` 目标设定用幻影槽 + 订单按钮（原版"配方卡"物品 + 自绘 GUI）
    - `[简化]` 配方来源为全局 RecipeManager（原版需配方卡录制）
    - [ ] 配方卡物品 + 录制 GUI（如需完全还原）
    - [ ] 动态尺寸结构（如需完全还原）
16. **创造标签页拆分** — ✅ 已完成（8 个标签页与原版一一对应）
17. **配置系统补全** — ✅ 已完成（9 组字段逐一对应；本轮补世界生成开关 + deleteBreadRecipe 接线）
    - 说明：NC/AA/AppleCore/Nutrition 相关开关保留但无效果（**用户已确认联动兼容暂不做**）

### JEI 集成（本轮补全）
- [x] `FoodInfoCategory`（饥饿/饱和/效果/营养素信息页，移植版增强内容）
- [x] `LacingCategory`（原版样式：每种掺加物一条，列出全部 GTFO 食物 + 效果/持续文本；
      罐装机为配方催化剂）
- [x] `EatingRecipeCategory`（原版 `eating.output`：食物 → 容器输出，本轮补）
- [x] 语言键 `eating.output` / `lacing.item_list` / `gtmfo.lacing.info.1/2`（EN/CN + 10 语言）
- N/A：JEI 配方传输到厨房 GUI（原版经 GTCEu 1.12 `ModularUIGuiHandler`；GTCEu 7.5.2 无对应 API）

### P2.5 — 配方链完整性审计（对照原版 32 条链，2026-09-12）

> 方法：脚本对比原版 `recipe/chain/` 与现代 `common/data/recipe/chain/` 的配方 ID / 产出 / 方法体，
> 逐条核对参数（EU/t、时长、数量、概率），发现并修复以下**断链**（物品被下游消耗但无生产配方）：

- [x] **Chum 链**（原版 `CoreChain.chum()`）：现代完全缺失 → 已补
  - 搅拌：污泥 + 臭鱼/臭肉 + 红蘑菇 + 毒马铃薯 + 发酵蛛眼 → 海霸糊 3（+紫饮料 → 6）
  - 发酵：鳕鱼/鲑鱼/热带鱼 → 臭鱼；5 种肉 + 腐肉/蛛眼 → 臭肉（水 100 进/出，8 EU，100t）
  - 搅拌：动物产物 + 水/硫酸 → 污泥（500t/16EU 与 250t/16EU）
  - 海霸糊棒：无序合成 + 组装机（4 EU，5t）
  - 补回 `ROTTEN_MEAT`/`ROTTEN_FISH` 物品（此前被注释，有 Foods 常量）
- [x] **提取液链**（原版 `CoreChain.zest()` + `liquidFoodExtracts()`）：现代全部被注释 → 已补
  - 榨汁：柠檬/酸橙/橙子 → 果皮粉 + 提取液 100（5 EU，100t）+ 研钵手搓
  - 橄榄油（27 EU，60t）、西瓜提取液（2 EU，10t）、蔓越莓/葡萄/杏子提取液（原版电路号）
  - 苹果汁/橙汁 ↔ 提取液 罐装（100mB）；发酵苹果提取液 → 苹果酒
  - 蒸馏：苹果提取液 / 苹果酒 / 橙·酸橙·柠檬提取液（柠檬酸）
  - 化学：氰化氢 + 氢氧化钠 → 氰化钠
- [x] **紫饮料链**（原版 `PurpleDrinkChain`）：紫饮料流体无生产 → 已补
  - 搅拌：碧雪 1000 + 止咳糖浆 500 + 碎硬糖 → 紫饮料 1000（480 EU，40t）
  - 罐装：紫饮料 500 + 玻璃瓶 → GTCEu 紫饮料瓶（30 EU，20t）
- [x] **种子链**（原版 `SeedsChain`）：缺大量内容 → 已补
  - 23 种作物的离心（甲烷 34，配置开关）/酿造（生物质 100）/压缩（8 个 → 植物球）
  - 16 种种子 → 种子油 8mB（电路 3）
  - 19 条"不明种子"定向合成（合成栏位置决定产物，原版机制）
- [x] **咖啡链**（原版 `CoffeeChain`）：现代有全部物品但零配方 → 已补
  - 杯子（压模/高炉/滤纸）、咖啡/提神咖啡灌装、5 阶段豆处理（离心/化学浴/脱水/车床/热解）
- [x] **脂肪链**（原版 `FatChain`）：硬脂/硬脂酸钠/动物脂肪无生产 → 已补
  - 5 种肉研磨 → 肉粉 + 骨粉 + 动物脂肪（含概率产出）
  - 肉粉离心/发酵；植物油 + 氢 + 纯碱 → 硬脂；动物脂肪 ↔ 硬脂
  - 硬脂 + 水 + NaOH → 硬脂酸钠（厨房清洁液）+ 甘油；硬脂 + 甲醇/乙醇 → 生物柴油
  - 污泥 + 细菌 + 方解石 + 碳网 → 生物质/水/二氧化硫/甲烷
- [x] **CoreChain.misc()**：肉锭/肉末/纸袋/肥料溶液/应急口粮等 → 已补
- [x] **三明治**（`BreadsRecipes.sandwich()` 是空方法）→ 已补 5 手搓 + 6 组装 + 4 大号
- [x] **温室链**：原版树木（6 种 × 3 电路）+ 橡胶树割胶（4 电路）+ 橡胶液 → 粘性树脂 → 已补
- [x] **生物提取机**：意大利水牛奶配方（电路 1）+ EU 修正（村民 64 / 马 24）→ 已补
- [x] **浆果链**：越橘果酱（原版有，现代缺失）→ 已补
- [x] **原版覆写链**（`VanillaOverrideChain`）→ `VanillaOverrideRecipes`：烤土豆/7 种烤肉（配置）、
      蘑菇/甜菜/兔肉汤、擀面杖造纸+粘性活塞、金胡萝卜/金苹果重构（纤维骨架 + 氯金酸浴）、
      钨钢苹果；配方移除（原版熔炼/合成）通过 GTCEu recipe filter，配置门控
- [x] **Caplet 链缺口**：对乙酰氨基酚完整化学链（硝基苯酚→氨基苯酚→乙酸酐→对乙酰氨基酚）、
      钚-241/紫颂/活力胶囊 → 已补
- [x] **makeChainsHarder 配置接线**：奶酪 4 条简单路径 / 白酒简单发酵 / 意面简单和面+热水煮
      （原版 3 条链共 9 处条件），配置现在真实生效
- [x] **生产审计（物品被消耗但无生产）**：修复 培根链（切片+烤制）、黄瓜/洋葱/茄子/胡萝卜切片、
      吐司（烤炉）、意式馄饨、玉米粒（离心+手搓）、披萨刀片（现代新增物品补配方）、温室玻璃
- [x] 披萨盒（GTFOPizzaBox）：3 种方块 + 右键变披萨 + 打包机配方（原版 id）—— 已完成
- [ ] **爆米花链**：原版 `POPCORN_BAG`/`FLAVORED_POPCORN_FLAKE` 已有物品但原版配方整段注释（WIP）
- 说明：`MineralWaterChain`（512 行）、`PopcornChain`（179 行）、`Dewar Flask` 在原版**整段注释**（WIP），未移植属正常
- 说明：`SmogusChain` 由 `SmoreRecipes`（smogus 组装）+ `ChocolateRecipes`（可可）覆盖，恒星熔炉部分用成型压床替代；
      巧克力精炼（liquor/dutching/press cake）在原版有更多阶段，现代版简化为可可→熔融巧克力
- 说明：生产审计剩余告警均为误报（作物来自种植、脏容器来自进食返还、面包/干面来自熔炉配方、循环变量输出）

### P3 — 兼容与打磨
18. **JEI 集成**（进食/掺加信息页）— ✅ 已完成
    - ✅ GT 配方 JEI 分类由 GTCEu 自动生成（切片机/微波炉/多功锅/菜肴组装机等）
    - ✅ `FoodInfoCategory`（原版 `EatingRecipeCategory`）：饥饿值/饱和度/效果/营养素，自动收集所有已注册可食用物品，EN/CN 本地化
    - ✅ LacingCategory（掺加信息页）：掺加物 + 食物 → 带效果的掺加食物
19. **AppleSkin / TOP 兼容** — [ ] 未移植（对应模组未安装；原版 AppleSkin 4 个类）
20. **地牢战利品** — ✅ 已完成（对照原版 `GTFODungeonLootLoader`）
    - ✅ 41 种食物注入 7 个战利品表（废弃矿井/丛林神庙/沙漠神殿/地牢/要塞走廊·交叉/林地府邸）
    - ✅ 掺加变体（氰化物标记 `gtmfo_lacing=0`），权重按原版（部分除以 2/3）
    - ✅ 稀有：矿泉水 / Smogus 之心（丛林神庙+林地府邸，掺加变体额外进地牢 ×4）
    - ✅ 用 GTCEu `ChestGenHooks`（GTCEu 战利品开关关闭时自行注册监听）
    - ✅ 配置开关 `addDungeonFoods` / `addLacedDungeonFoods` 生效
21. **音效/粒子效果** — 🔄 部分
    - ✅ 音效注册（微波炉完成/农场激光/排气）+ 代码调用
    - ✅ 洒水器粒子（`GTFOSprinkleOptions` + `GTFOSprinkleParticle`，飞向目标 + 流体颜色）
    - ✅ GTFO GUI 标志（`GTMFOGuiUtils.withLogo`，6 张原版 logo 纹理）
    - ✅ 药水图标：从原版 `potions.png` 图集提取 8 个（氰化物/抗精神分裂无图标，同原版）
    - [ ] 农场机激光束（GTCEu 7.5.2 无对应粒子系统，跳过）
22. **其他模组兼容**（TFC/AgriCraft/EnderIO/Nutrition 等）— [ ] 未移植（对应模组未安装，优先级低）

---

## 16.5 最近修复记录（2026-09-13）
- **洒水器肥力**：水肥力从 0% 修正为原版的 5%（血 30% / 肥料溶液 15% 已正确）。
- **果实掉落**：按原版 getAppleDrop 每树配率（香蕉 3-6 个、橄榄 1-4 个、杏 100% 等）。
- **时运加成**：作物/根茎作物掉落次数为 3+fortune（原版逻辑）。

- **温室结构**：补全钢框柶柱（原版 'F' 位置：前后墙中柱/中间层侧边/顶部）。

- **IV 袋 + 屠刀配方**：IV 袋物品+组装机配方（原版 IVBagChain）；
  HV 电动屠刀形状能量转移配方（充电 HV 电源单元 + 不锈钢板 + HV 电机 + 金线）。

- **机器合成配方**：补齐生物三件套/农场机/多功锅/原始烤炉
  （原版 10 条中此前仅 3 条）+ 厨房/温室/洒水器覆盖板配方。
- **农场机种植**：修复无法种 GTFO 种子（直接 useOn 绕过了种植事件），
  现直接放置作物方块（农田/水面规则同事件）。
- **饮料线接**：9 种饮品（啤酒/碧雪/柠檬水/伏特加等）此前用 Foods.EMPTY
  导致无任何饱食/效果，已接入各自的 Foods 常量；矿泉水补回配置数值 + 创造飞行 5000t。


- **mixin 系统重启**：此前完全未生效（空列表 + plugin false + refmap 不匹配），
  导致自定义进食时长（如炖菜 100 tick）全部失效。已修正配置、插件、refmap 名，
  并修正 ItemMixin 对非食物返回 0 的 bug（会弄坏弓/盾牌等）。
- **配方表容量**：酿酒机输出槽 0→1、提取机输入 1→2、发酵/压缩流体槽补齐
  （原版 CommonProxy.preLoad）——此前蚌豆泥/炸鱼/葡萄汁压榨配方无法运行。
- **杂草种子掉落**：1.20 全局战利品修饰器，概率 0.125×weight/(weight+1)，对应原版权重语义。
- **温室土壤配置**：greenhouseDirts 现实际生效（支持方块状态字符串）。
- **树叶着色**：10 种树各自颜色 + 彩虹木坐标彩虹色。
- **资源完整性**：修复 8 个缺失树叶模型、错误父级、59 个缺失物品模型、
  砖块/温室玻璃 blockstate；审计确认 0 个模型/纹理引用缺失。
- **接口线**：药水增幅/延长实际生效；新增掺加 JEI 信息页；食物信息页；
  GTFO GUI 标志；洒水器粒子；披萨盒；抗精神分裂/肺癌图标。

## 16.6 最近修复记录（2026-09-13 第二轮）
- **烤炉温度系统**（原版 `MetaTileEntityElectricBakingOven` + `GTFOUtils.addBakingOvenRecipes`）：
  - `BAKING_OVEN_RECIPES` 恢复 2 个输入槽（食物 + 燃料），新增 `ELECTRIC_BAKING_OVEN_RECIPES`。
  - `GTMFOBakingOvenRecipes.add(...)`：木板燃料配方 + 煤/木炭 x4 配方 + 电动四分之一时长配方（温度写入 recipe data）。
  - `ElectricBakingOvenMachine`：300K 起步、±5K/20t 调温、维持温度耗电 `exp((temp-100+size*5)/100)`、
    配方温度精确匹配、并行=多方块长度、无超频、长度指示器 `bakingOvenLength`。
  - `SteamBakingOvenMachine`（原版蒸汽烤炉）：时长 x4、蒸汽=温度/100、无并行。
  - 原始烤炉 GUI 补燃料槽；`bakingOvenReplacement` 配置门控熔炉/烟熏/营火替代配方（默认 false，与原版一致）。
  - 所有烘焙配方按原版参数（时长/温度/燃料量）重写：面包类、披萨、千层、烤肉、香肠卷、豆子、烤肉串、全麦饼干、原版肉类。
- **农夫模式系统**（原版 `FarmerModeRegistry`，11 种模式全部移植）：原版作物、可可、地狱疣、
  茎秆（棋盘格规则）、高杆作物（甘蔗/仙人掌整列）、地面清理（草/花/蘑菇等）、紫颂、
  GTFO 作物/根茎作物（有种子收作物、无种子收种子）/浆果丛（重置为 maxAge-1）。
  `FarmerMachine` 改为原版两阶段算法（收集作物→种植种子，输出满时跳过模式，播放激光音效）。
- **掺加（Lacing）动态配方**：新增 `LacingCannerLogic`（`GTRecipeType.ICustomRecipeLogic`），
  任意 GTFO 食物 + 任意掺加物在流体罐装机中掺加，取代此前硬编码的 6 种食物列表
  （原版为 `RecipeMapFluidCannerMixin`）。
- **食物数值全面审计**（对照原版 162 条 `GTFOFoodStats`）：
  - 修复 BRUSCHETTA/VITELLO_TONNATO/RED_WINE/SORBET_CHORUS/VIBRANT/FERMENTED_CHORUS/PIE/
    SANDWICH_VIBRANT 的缺失效果与概率；ELDERBERRY 中毒概率 4%/1%。
  - alwaysEdible 修正：培根、吐司、全部 5 种胶囊；咖啡为饮品（drink）；冰糕仅原味可随时食用。
  - 进食时长修正：PASTA_AL_POMODORO 16、FERMENTED_CHORUS 60、BAKED_CAKE_BOTTOM 32。
  - 玉米袋（Popcorn Bag）接入原版数值 + 组装机配方；补缺失的玉米袋配方。
  - 名称恢复为原版：Aubergine（茄子/片/种子）、Chumburger、Meat Burger、Meat Sandwich。
- **lang 补全**：472 条缺失的物品/方块语言（树木各部位用原版名称如 Banana Pseudostem、
  作物/浆果丛、新物品、砖块）；修复温室玻璃/安塔夫/活力系列 CN；106 条原版物品提示
  （`ItemTooltipEvent` + `item.gtmfo.<id>.tooltip`）；机器风味提示（拍拍刀/狗狗币/厨师机器人等）。
- **机器提示**：温室 3 条、厨房 5 条、电力烤炉 3 条原版提示；每级风味提示（切片机/微波炉/多功锅/菜肴组装机）。
- **抗精神分裂**：本地玩家拥有该效果时隐藏其他玩家渲染（原版 `handlePlayerRender`）。
- **氰化物**：掺加施加时隐藏粒子（原版 `PotionColorCalculationEvent`）。
- **扁平面团**：补回原版擀面杖手搓配方（`gtfo_flat_dough`，4 种擀面杖各一条）。

### 16.6.1 本轮审计结论（与原版逐项比对）
- **配方链**：原版 `GTFORecipeAddition` 的 32 条链中，`PopcornChain` 与 `MineralWaterChain`
  在原版即为整段注释（死代码），无需移植；其余链均已在移植版注册（部分合并，如 Pasta→Italian）。
- **食物数值**：162 条原版食物逐项比对完成（饥饿/饱和/营养素/效果概率/alwaysEdible/进食时长），
  唯一已知偏差：BUN 饥饿值移植版为 1，原版为 `baguetteHunger / 3`（默认配置整数除法=0，
  疑似原版 bug，保留 1 并在此记录）。
- **世界生成**：原版为 Perlin 噪声 + 生物群系/温湿度条件；移植版为数据驱动 JSON
  （生物群系标签 + rarity_filter 近似），已在此前章节记录为 [简化]。
- **未移植的兼容集成**（1.20.1 无对应模组或非必需）：TOP 根茎作物提示（改用 Jade 可做，未实现）、
  AppleCore/AppleSkin 插件（移植版使用标准 FoodProperties，AppleSkin 自动显示数值）、
  NuclearCraft/ActuallyAdditions/AgriCraft/EnderIO/TFC/SereneSeasons 兼容、Kitchen Recipe 物品
  （厨房多方块已用幻影槽替代其设定目标的功能，见下）。

### 16.6.2 已知 [简化] 清单（待后续可选补全）
1. **厨房食谱物品**（原版 `GTFOKitchenRecipeBehaviour` + `KitchenRecipeWidget`）：原版可编程
   食谱卡（NBT 存多条配方、JEI 拖拽写入、放入厨房配方槽生效）；移植版厨房使用控制器上的
   幻影目标槽 + 订单数量按钮实现同等目标设定功能，物品与 GUI 未移植。
2. **世界生成分布**：Perlin 聚簇分布未复刻，改用生物群系标签 + 稀有度。
3. **洒水器耗液**：原版 `drain(1, true)` 仅模拟不实际消耗（疑似 bug），移植版实际消耗 1mB。
4. **咖啡/矿泉水的 Creativity 效果**：移植版以原版 FLY 效果近似（1.20.1 无「允许飞行」药水属性）。

## 16.7 实机测试修复记录（2026-09-13 客户端实测）
> 首次成功启动客户端后进行世界创建测试，修复以下崩溃/加载问题：

1. **mixin 修复后首次实机启动**：发现食物效果在 `Foods` 静态初始化时急切调用
   `GTMFOEffects.X.get()`（RegistryObject 尚未填充）→ 全部改为惰性 `Supplier` 形式；
   `GTMFOLacing.init()` 移至 `FMLCommonSetupEvent`（效果注册完成后）。
2. **GTCEu 注册表时序**（并行 mod 构造竞态）：
   - `GTMFOTools` 触发 `GTToolType.<clinit>` → `GTSoundEntries.<clinit>`，
     但非 GTCEu 模组构造期间 `GTRegistries.SOUNDS.unfreeze()` 被门控跳过 → 注册表冻结崩溃。
     修复：工具初始化移入 `IGTAddon.initializeAddon()`（GTCEu 构造末尾，声音已就绪）。
   - `GTMFOCovers` 同理移入 `IGTAddon.registerCovers()`（`GTCovers.init()` 内、冻结之前）。
3. **材料 ID 冲突**：GTCEu 自带 `gtceu:paracetamol`（粉）与 `gtceu:aminophenol`（流体）；
   移植版原样注册同名材料导致 "contains key ... already"。
   修复：改为引用 `GTMaterials.Paracetamol` / `GTMaterials.AminoPhenol`，
   并在 `GTMFOMaterials.init()` 按原版调整（颜色 0x0045A0/0xFFFFFF、SHINY 图标、
   给氨基苯酚补 DUST 属性）。
4. **树木 configured_feature 缺 `decorators` 字段**：1.20.1 `minecraft:tree` 编解码器
   要求该字段（原移植版 JSON 未写）→ 全部 10 种树解析失败导致
   "Failed to load registries"（创建世界崩溃）。修复：补 `"decorators": []`
   （果实掉落由树叶 `getDrops` 实现，无需装饰器）。
5. 附带：`GTMFODataGen` 注册伤害类型语言；资源/语言审计见 14 章。

> 世界生成 JSON 已用脚本校验：30 configured + 30 placed + 38 生物群系标签引用一致（0 问题）。

## 16.8 客户端 WARN/ERROR 全量清理（2026-09-13 第二轮实测）
> 对照 `runclient5.log` 逐条修复所有 gtmfo 相关警告/错误：

1. **机器贴图紫黑块（缺失模型）**：farmer/生物三件套/温室/厨房/原始烤炉的 blockstate、
   方块模型与物品模型从未 datagen 生成（`MachineBuilder.exBlockstate` 为 datagen 路径）。
   修复：`runData` 成功跑通并生成全部机器资源（167 blockstates / 853 models）。
2. **datagen 前置修复**（否则 runData 无法完成）：
   - 作物/树木/浆果丛方块补 `blockstate(noop)`（资源为手写于 `src/main/resources`）
   - 洒水器覆盖板物品模型补 `model(noop)`（手写模型引用覆盖板纹理）
   - 删除 13 个与 datagen 输出重复的手写 blockstate/模型
3. **作物纹理未进方块图集**（"Missing textures" × 数百）：新增
   `assets/gtmfo/atlases/blocks.json`，把自定义 `textures/crop/` 目录纳入方块图集。
4. **无效路径错误**：`pasta_all'amogus.png` / `spaghetti_all'assassina.png` 含非法撇号 →
   重命名去掉撇号。
5. **自定义流体贴图缺失**：`rainbow_sap` 使用 `customStill()`，贴图名需与材料名一致
   （原为 `fluid.gtfo_rainbow_sap.png`）→ 复制为 `fluid.rainbow_sap.png`。
6. **配方警告**：
   - 切片机 IO 恢复 3 输入（移植版配方使用 `circuitMeta` 区分同输入配方）
   - `graham_cracker` 仅保留概率产出（原版行为，此前多了一个保底产出）
   - `molten_unsweetened_chocolate` 拆分为两条单流体产出配方（提取器只有 1 个流体输出槽）
7. **refmap 警告**：新增 gradle `copyRefmap` 任务，把 mixin 注解处理器生成的 refmap
   复制到 `build/resources/main`（开发环境运行时可见）。
8. **语言文件**：物品提示移入语言 provider（`GTMFOTooltips`，datagen 不再清除）；
   10 种附加语言移至 `src/main/resources`（datagen 会清理 generated 中的非生成文件）。
9. 审计确认：0 个 blockstate 引用缺失模型；剩余 14 个缺失纹理均为未注册 WIP
   （hops/popcorn 作物、artichoke stage6-7、旧 sapling 模型）。

## 16.9 客户端警告清理第三轮：贴图 + 配方冲突 + 温室预览（2026-09-13 下午）
> 对照 `runclient7.log`（用户实测会话）逐条修复：

1. **擀面杖紫黑块**：GT 工具的物品模型由 `ToolItemRenderer` 动态生成，
   parent 指向 `toolType.modelLocation`（默认 `gtceu:item/tools/<name>`）。
   原版 1.12 把贴图写进 `gregtech` 命名空间，移植版照做：
   - `assets/gtceu/models/item/tools/rolling_pin.json`（layer0=void，layer1=rolling_pin）
   - `assets/gtceu/textures/item/tools/rolling_pin.png`（16x16 灰度，材质染色）
   （`butchery_knife` 模型 GTCEu 自带，无需处理）
2. **作物贴图缺失 ×118**：图集配置必须放 `assets/minecraft/atlases/blocks.json`
   （游戏只查找 `minecraft:blocks` 配置位置；`assets/gtmfo/atlases/` 不会被读取）。
3. **配方冲突（GTCEu RecipeDB 只保留首个同输入配方）**：
   - 肉末/脂肪：原版 `FatChain` 在添加动物脂肪配方前会**删除**冲突的基础研磨配方，
     移植版补上（删除 `gtceu:macerator/macerate_steak/chicken/mutton/pork_chop/rabbit`）；
     同时删除自创的 `MINCE_MEAT` 物品与 `mince_meat_*` 研磨配方（原版没有该物品，
     "生肉末"就是 GT 的 Meat dust）：烘烤炉 `Meat dust → Cooked Mince Meat`、
     肉末披萨 `Meat dust x10`，`pizza_meat_raw` 补上原版提示
   - 苹果汁：删除 `AppleRecipes` 里 250mB 的重复版本，保留 `CoreChain` 的原版 100mB 版本
   - 可可脂：删除自创的 `nibs → 熔融巧克力`（保留原版的 `可可粉 → 熔融巧克力`），
     `nibs → 可可脂` 作为简化链保留（原版为压榨巧克力液；简化已记录）
   - `distill_creosote`：GTCEu 生成配方的 id 带配方类型前缀，删除 id 修正为
     `gtceu:distillation_tower/distill_creosote`
4. **温室 JEI 预览报 "Pattern formed checking failed"**：`soilPredicate()` 的候选方块
   列表为空，预览无法放置土壤层 → 现提供 dirt/grass + 配置中解析出的方块状态。
5. 剩余唯一警告：`Mod 'gtmfo' took 2.5s to run a deferred task` —— Registrate 的全局
   一次性监听器清理任务（`OneTimeEventReceiver`，静态列表包含 GTCEu 的数百个物品颜色
   监听器），归因取决于模组构造顺序，无法从本模组侧消除，无功能影响。

## 16.10 收尾修复 + JEI 全配方导出（2026-09-13 晚）
> 对照客户端实测日志逐条清理，并新增开发工具：

1. **对乙酰氨基酚/扑热息痛链冲突**：GTCEu 自带简化链（苯酚+硝化混合物→氨基苯酚流体）与
   原版 GTFO 链（硝基苯酚→分离→4-硝基苯酚加氢→氨基苯酚）抢同一输入，导致 GTCEu 配方注册失败。
   已按"原版优先"删除 GTCEu 的 `aminophenol`/`paracetamol`（化工反应器 + LCR 自动副本），
   恢复原版 CapletChain；下游（Paracetamol 粉尘相关）不受影响。
2. **创造模式标签为空**：
   - 工具物品 id 是 `<material>_rolling_pin`（GT 的 `idFormat = "%s_" + name`），旧判断按
     `rolling_pin` 开头匹配 → 工具从未进入 Tools 标签；
   - 方块物品是按 **BLOCK 条目**进标签的（GTCEu 生成器先遍历方块条目），旧逻辑只改 ITEM 条目
     → Blocks 标签为空。两处均已修复。
3. **JEI 全配方导出工具**（配置门控 `devConfigs.exportJeiRecipes`，客户端）：
   - 进存档、配方加载完成后（防抖 3 秒）后台线程流式导出两个 JSON：
     `jei_recipes.json`（55k+ 配方 / 95 分类）与 `jei_names.json`（机器/材料/方块/物品/流体
     的 id + 翻译 key + 中英文名）；
   - GT 配方附需求块（`RecipeHelper.getRealEUtWithIO` / `getRecipeEUtTier`）：EU/t、电流、
     耗电/发电、最低电压等级 + 下标 + 标称电压、总耗电、耗时、并行、超频；
   - 名称来自 `en_us`/`zh_cn` 语言文件（与客户端语言无关），GT 材料物品/工具按模板
     （`%s Dust`、`%s Wrench`）用材料 API 合成；支持绝对路径；
   - 格式说明：`docs/JEI_EXPORT_FORMAT.md`。

## 16.11 跨模组兼容层（2026-09-14）：季节系统 + 标准标签 + 条件兼容配方

> 目标：不添加任何新物品，把 GTMFO 内容接入通用生态（SereneSeasons / Farmer's Delight /
> Farm & Charm 等），全部"当 xx 模组启用时"才生效。注册表级审计见 `docs/REGISTRY_AUDIT.md`。

1. **SereneSeasons 季节系统**（机制 + 提示，无硬依赖）：
   - 数据包标签（47 个 JSON 中的一部分）：`data/sereneseasons/tags/{items,blocks}/{spring,summer,autumn,winter}_crops.json`
     —— 19 种作物方块 + 对应种子/产物按真实农时分配季节；`greenhouse_glass.json` 加入
     `gtmfo:greenhouse_glass`（使本模组温室玻璃被 SereneSeasons 识别为温室玻璃，全年可种）；
   - `compat/SereneSeasonsCompat`（客户端，FORGE 总线，`ModList.isLoaded` 守卫）：
     移植原版 `GTFOSSTooltipHandler`，为 GTMFO 种子/产物显示"适宜季节"提示
     （Spring 绿 / Summer 黄 / Autumn 金 / Winter 青 / 全年 淡紫）。
2. **forge 标准标签**（跨模组配方自动兼容的基础）：
   `forge:seeds`、`forge:crops(+子标签 ×19)`、`forge:vegetables(+子标签 ×8)`、`forge:fruits`、
   `forge:berries`、`forge:raw_meat`、`forge:cooked_meat`、`forge:dough`、`forge:cheeses`、
   `c:cheeses`、`farmersdelight:sweets` —— 使 FD/Create/F&C 等使用这些标签的配方自动接受 GTMFO 物品。
3. **条件兼容配方**（`data/gtmfo/recipes/compat/`，带 `forge:mod_loaded` 条件，16 条）：
   - Farmer's Delight 切菜板 ×10：番茄/洋葱/黄瓜/茄子/苹果/胡萝卜/意式烤猪肉 → 对应切片；
     3 种披萨方块 → 4 片披萨切片（与包内 KubeJS 配方不重复）；
   - Farm & Charm 绞肉机 ×6：GTMFO 生肉（beef_slice/seasoned_pork/bacon_raw/sausage_raw/
     scrap_meat/barg_meat）→ `gtceu:meat_dust`（MEAT 类型）。
4. **构建**：版本 0.0.5 → **0.0.6**；`compileJava` 通过（仅 JEI 导出工具既有弃用告警）。

## 16.12 农业平衡与季节联动（2026-09-14，版本 0.0.7）

> 详细取证与数值对照见 `docs/AGRICULTURE_BALANCE.md`。整合包实测反馈"香蕉树约 2 分钟一轮，破坏平衡"。

1. **修复果实掉率 10 倍超标（BUG）**：`GTFOBlockLeaves` 的果实掉落误用了树苗掉率常量 20；
   原版 1.12.2 `BlockLeaves#getDrops` 传给 `dropApple` 的是 **200**（苹果 0.5%），
   GTFO 的 `getAppleDrop` 再除以每树 divisor。已改为 200：
   香蕉单叶从 50%×3-6 降到 4%×3-6（整树期望 ~101 → ~8 个），其余树同比例（约 10~12.5 倍削弱）。
2. **温室限速配置**：新增 `gtfoMiscConfig.greenhouseDurationMultiplier`（1.0~100.0，默认 1.0 保持原速）；
   `GreenhouseRecipeLogic.setupRecipe` 对本机 `duration` 应用倍率（不改共享 GTRecipe）。
   整合包设为 **6.0**：树木电路 100 s → 600 s（10 min）、果实电路 150 s → 900 s（15 min），
   EV 超频后分别 ~75 s / ~112 s。
3. **树苗季节联动**（SereneSeasons 1.20.1，机制取自反编译 `SeasonalCropGrowthHandler`/`ModFertility`）：
   10 种树苗加入 `sereneseasons:{spring,summer,autumn,winter,year_round}_crops` 方块+物品标签
   （春：杏/柠檬/青柠；夏：香蕉/芒果/橙/椰子；秋：橄榄/肉豆蔻；全年：彩虹木），
   非当季树苗不 randomTick 生长、骨粉无效；上方盖玻璃（`sereneseasons:greenhouse_glass`，
   含本模组温室玻璃）可全年生长。
4. 已知未做：温室"电路 4"（肥料加速版，原版存在）未移植；树叶果实掉落未做当季限制。

## 16.13 畜牧业兼容：义大利水牛产奶 + 好感度（2026-09-16，版本 0.0.8）

> 目标：让本模组的农业生物（义大利水牛 `gtmfo:italian_buffalo`）接入整合包 "Sunlit Valley" 的
> husbandry 系统（`kubejs/server_scripts/entities/animalBase.js`：好感度/心情/喂食/挤奶/繁殖/抚摸）。

1. **模组侧自带兼容标签**（本轮新增，资源，无代码）：
   - `data/society/tags/entity_types/husbandry_animal.json` → `gtmfo:italian_buffalo`
   - `data/society/tags/entity_types/milkable_animal.json` → `gtmfo:italian_buffalo`
   - 该体系通过 `entity.entityType.tags` 判定动物（`global.checkEntityTag`），标签与包内
     `handleEntityTags.js` 的写入是**并集**关系，因此模组在任何使用该体系的包中都会被自动识别；
     society 模组不存在时标签为惰性数据，无副作用。
2. **整合包侧配合**（KubeJS，已提交到包仓库）：产奶定义（`society:buffalo_milk`/`large_buffalo_milk`，
   售价 64，接入奶酪压制机/意式咖啡机）+ 三个动物列表（husbandry/milkable/tier2 ×1.25）。
3. **保留原版行为**：模组自带桶挤奶（`gtceu:italian_buffalo_milk` 流体桶）不受影响；包内使用
   `society:milk_pail` 走通用挤奶流程（大小奶/品质按好感度与心情判定）。
4. **同类参考**：`meadow:water_buffalo`（同包同机制）——本水牛的全部配置与其对齐。

## 17. 技术难点与注意事项

### 17.1 GTCEu 版本差异
- **1.12.2**: `MetaTileEntity` + `TieredMetaTileEntity` 类体系
- **1.20.1**: `MachineDefinition` + `Machine` 注册体系（Registrate）
- 参考 `H:\MinecraftMods\GregTech-Modern` 中：
  - `MachineDefinition` 注册模式
  - `MultiblockMachineDefinition` 多方块模式
  - `PartAbility` 能力系统
  - `GTRecipeType` 配方类型
  - `Material` / `TagPrefix` 材料系统

### 17.2 世界生成 API 差异
- **1.12.2**: `IWorldGenerator` + 自定义 Feature 系统
- **1.20.1**: `ConfiguredFeature` / `PlacedFeature` / `BiomeModifier` + 数据驱动 JSON
- 建议：保留原版 Perlin 噪声逻辑，包装为 1.20.1 Feature

### 17.3 实体 API 差异
- **1.12.2**: `EntityRegistry.registerModEntity` + `RenderLiving`
- **1.20.1**: `DeferredRegister<EntityType<?>>` + `EntityRendererProvider`

### 17.4 药水 API 差异
- **1.12.2**: `Potion` + `PotionEffect` + 自定义图标渲染
- **1.20.1**: `MobEffect` + `MobEffectInstance` + `MobEffectTextureManager`
- 自定义图标需用 1.20.1 的纹理图集方式

### 17.5 覆盖板 API 差异
- **1.12.2**: `CoverBase` + `CoverableView`
- **1.20.1**: `CoverDefinition` + `CoverBehavior`（参考 GTCEu Modern）

### 17.6 数据生成差异
- **1.12.2**: 运行时注册（`GameRegistry` / `OreDictUnifier`）
- **1.20.1**: DataGen（`Registrate` provider）+ 标签系统

### 17.7 网络同步
- **1.12.2**: `writeCustomData` / `receiveCustomData` + `PacketBuffer`
- **1.20.1**: 使用 GTCEu 的同步系统或 Forge `SimpleChannel`

---

## 18. 里程碑规划

| 里程碑 | 内容 | 验收标准 |
|--------|------|----------|
| M1 | 材料/流体补全 + 配方链补全 | 所有原版流体可合成，配方链完整 |
| M2 | 作物 + 树木 + 世界生成 | 世界中可找到树木/浆果/作物 |
| M3 | 机器补全（Farmer/Mob 机器） | 所有单方块机器可用 |
| M4 | 多方块补全（原始烤炉/温室/厨房） | 所有多方块可搭建 |
| M5 | 药水 + 工具 + 覆盖板 | 药水效果生效，工具可用 |
| M6 | 实体 + 兼容集成 | 实体生成，JEI/AppleSkin 可用 |
| M7 | 打磨（音效/粒子/配置/本地化） | 完整发布版 |

---

*最后更新：基于 1.12.2 原版（v1.6.2+）与当前 1.20.1 移植版对比生成*
