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
> 5. **验证**：每个里程碑跑 `.\gradlew compileJava --console=plain`（用户约束：不跑 datagen）。

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

## 0. 当前进度总览

| 指标 | 1.12.2 原版 | 1.20.1 移植版 | 进度 |
|------|-------------|---------------|------|
| Java 文件数 | 200 | 63 | ~35% |
| 资源文件数 | 1120 | 485 | ~43% |
| 机器（单方块） | 7 类（含 Farmer/Mob 机器） | 4 类（全电压，缺特殊行为） | ~50% |
| 多方块 | 5 种 | 2 种（烤炉） | ~40% |
| 配方链 | 32 条 | 11 条 | ~34% |
| 世界生成 | 完整 | 无 | 0% |
| 实体 | 3 种 | 无 | 0% |
| 药水效果 | 10 种（+2 已注释） | 4 个占位（无逻辑） | ~10% |
| 工具 | 2 种 | 无 | 0% |
| 兼容集成 | 10+ | 无 | 0% |

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

## 10. 工具

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

## 11. 覆盖板

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

## 12. 配方链（原版 32 条，现代版 11 条）

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

## 13. 兼容 / 集成（全部缺失）

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

## 14. 资源文件（原始资源已复制完成 ✅）

### 14.1 纹理（已完成复制，剩余为引用接线）
- [x] 作物纹理（各生长阶段）已复制 → `gtmfo:textures/crop/`
- [x] 树木纹理（树苗/木板/原木/树叶）已复制 → `gtmfo:textures/block/`
- [x] 实体纹理已复制 → `gtmfo:textures/entity/`
- [x] GUI 纹理已复制（药水图标 `gui/potions.png`、GTFO logo 系列、按钮）→ `gtceu:textures/gui/`
- [x] 机器 GUI 覆盖层已复制（SEED/CROP 槽位覆盖、机器 overlay）
- [x] 覆盖板纹理（洒水器）已复制 → `gtceu:textures/block/cover/`
- [x] 机器覆盖纹理（农场机/生物机器/微波炉等）已复制 → `gtceu:textures/block/machines/`
- [x] CTM 连接纹理（温室玻璃/铋青铜外壳）已复制
- [ ] 实现功能时接线纹理引用（datagen 或模型 JSON）

### 14.2 模型（旧格式已存至 legacy-resources，需转换/重新生成）
- [ ] 作物模型（`crop_cross.json` 需转换：forge_marker → 1.20.1 格式）
- [ ] 树苗/木板/原木模型（`legacy-resources/assets/gtmfo/models/`）
- [ ] 浆果丛模型（`small_berry_bush.json` / `large_berry_bush.json`）
- [x] 披萨盒（GTFOPizzaBox）：3 种方块 + 右键变披萨 + 打包机配方（原版 id）—— 已完成
- [ ] 多方块结构模型（由 GTCEu 系统处理）
- [ ] 机器模型（Farmer/Mob 机器，由 GTCEu workable 系统处理）

### 14.3 语言文件（旧 .lang 已存至 legacy，需转换 + 映射）
- [x] 英文/中文 lang 由 datagen 生成（现有物品）
- [ ] 新物品/方块/机器的 EN/CN lang 补全（随功能实现）
- [ ] 机器工具提示 lang
- [ ] 药水效果 lang（4 个已有占位）
- [ ] 覆盖板 lang
- [ ] 参考 `legacy-resources/assets/gtmfo/lang/` 的 12 个语言文件补充其他语言翻译

### 14.4 音效（已复制 ✅）
- [x] `microwave.finish` 微波炉完成音效
- [x] `farmer.laser` 农场激光音效
- [x] `amogus.vent` Smogus 排气音效
- [x] `sounds.json` 已修正命名空间为 `gtmfo:`
- [ ] 代码中注册 SoundEvent 并在机器中调用

---

### 14.5 资源完整性审计（脚本扫描，2026-09-13）
- 修复缺失方块状态/模型：土坯砖系列、磁砖系列、温室玻璃（datagen 外手写）
- 修复缺失树叶模型：原版仅有 banana/rainbowwood 纹理，其余 8 种树用 plain 纹理
- 修复错误父级：minecraft:block/block/leaves → minecraft:block/leaves
- 生成 59 个缺失物品模型（10 种树×4 + 新增物品 + 洒水器覆盖板）
- 审计结果：0 个 blockstate 引用缺失模型，0 个物品模型引用缺失纹理
- 剩余未引用模型（hops/popcorn 作物、artichoke stage6-7）为原版 WIP/未注册内容，无害

## 15. 数据生成

- [ ] 核对 `GTMFODataGen` 覆盖的 provider 类型
- [ ] 方块状态生成（作物/树/机器）
- [ ] 物品模型生成
- [ ] 战利品表生成（作物掉落/树掉落）
- [ ] 方块标签生成（可挖掘工具等）
- [ ] 世界生成 JSON 生成（生物群系修改器）

---

## 16. 移植优先级建议

### P0 — 核心体验（进行中 🔄）
1. **配方链补全**（26 条目标）— ✅ **18 条新链已移植**（剩余 8 条依赖未移植系统）
   - ✅ `ItalianRecipes` / `KebabRecipes` / `CheeseRecipes` / `IceCreamRecipes`
   - ✅ `PurpleDrinkRecipes` / `SorbetRecipes` / `ChorusRecipes` / `MicrowaveRecipes`
   - ✅ `PlateRecipes` / `RussianRecipes` / `VanillinRecipes` / `DyeRecipes`
   - ✅ `LithiumRecipes` / `ChocolateRecipes` / `AlcoholRecipes` / `SeedsRecipes`
   - ✅ `BananaRecipes` / `BritishRecipes`
   - ✅ 修复 `ONION_SLICE` 重复 ID、`mozzarella` 重复配方
   - ✅ 新增物品：`SEASONED_PORK`/`ANIMAL_FAT`/`KUBIDE_MEAT`/`BARG_MEAT`/热炖菜×3/`MUSHY_PEAS`/香肠×4/`SHEPHERDS_PIE`
   - ✅ 新增材料：凝乳×10 / 骨瓷×5 / 药物化学系列 / `LaminatedDough`
   - ⏳ 剩余 8 条：Coffee（需重构）/ Fat / Greenhouse（需多方块）/ IVBag / MineralWater / MobExtraction（需机器）/ Popcorn（原版 WIP）/ VanillaOverride
2. **材料/流体补全** — ✅ 已完成（~85 流体 + ~55 粉末材料）
3. **微波炉特殊行为 + GTFO GUI 标志** — 🔄 部分完成
   - ✅ 微波炉爆炸 + 完成音效（`MicrowaveMachine`）
   - ✅ 音效注册系统（`GTMFOSounds`）
   - [ ] GTFO GUI 标志（含 XMAS 变体）
4. **原始烤炉多方块** + 烤炉配方温度系统 — ✅ 已完成（原始烤炉）
   - ✅ `PrimitiveBakingOvenMachine`（原始多方块 + 原始 GUI）
   - ✅ 注册 `PRIMITIVE_BAKING_OVEN`（3x3x2 土坯砖 + 铁/青铜框架）
   - ✅ 土坯砖链：泥砖×4 配方 → 熔炼土坯砖 → 2x2 外壳 → 加固外壳
   - ✅ 注册 `ADOBE_BRICKS` / `REINFORCED_ADOBE_BRICKS` / `PORCELAIN_TILE` / `DARK_PORCELAIN_TILE` 方块
   - [ ] 烤炉配方温度系统（现代版已简化为无温度，可选恢复）
5. **作物系统**（19 作物 + 10 浆果）— ✅ 方块与种植已完成
   - ✅ `GTFOCropBlock`（基础作物 age 0-5 + 右键收获）
   - ✅ `GTFORootCropBlock`（根茎作物 age 0-7 + 中期收获/末期收种）
   - ✅ `GTFOWaterCropBlock`（水稻，水上种植）
   - ✅ `GTFOBerryBushBlock`（浆果丛 age 0-2 + efficiency 0-4 育种 + 荆棘伤害 + 减速）
   - ✅ `GTMFOCrops` 注册 19 作物 + 10 浆果丛（延迟解析种子→方块映射）
   - ✅ 右键种植事件（`ForgeCommonEventListener`）
   - ✅ 修复转换资源：grape/cotton 纹理键、artichoke 阶段数、浆果丛 efficiency 范围
   - [ ] 世界生成（作物/浆果自然分布）→ P1
   - [ ] 农场机集成 → P1
6. **树木系统**（10 树种 + 木板/原木/树苗）— ✅ 方块与生长已完成
   - ✅ `GTFOBlockLog`（每树独立原木，旋转柱）
   - ✅ `GTFOBlockPlanks`（每树独立木板）
   - ✅ `GTFOBlockLeaves`（每树独立树叶，掉落树苗/木棍/果实）
   - ✅ `GTFOBlockSapling`（每树独立树苗，骨粉/随机刻生长）
   - ✅ `GTMFOTrees` 注册 40 个方块（10 树 × 原木/木板/树苗/树叶）+ 生长逻辑（标准树冠 + 椰子树棕榈冠）
   - ✅ `TreeRecipes`（原木→木板 4 个：合成+切割机；木板→木棍）
   - ✅ 资源重构：变体 blockstate → 每树独立 blockstate（1.20.1 无 metadata 物品）
   - [ ] 世界生成（树木自然分布）→ P1
   - [ ] 自定义树形（原版香蕉/椰子树等特有形状）→ 打磨阶段
   - [ ] 树叶颜色着色（原版每树独立色调）→ 打磨阶段

### P1 — 重要功能（进行中 🔄）
7. **农场机** — ✅ 已完成（简化版）
   - ✅ `FarmerMachine`（LV-EV，动作间隔 20/10/5/2 tick）
   - ✅ 9x9 工作区域（循环扫描）
   - ✅ 假玩家收获（成熟作物右键/破坏 + 掉落收集）
   - ✅ 自动补种（从输入槽取种子）
   - ✅ 种子输入/作物输出 9+9 槽 + 充电槽
   - [ ] 完整 FarmerMode 系统（13 种模式：可可/下界疣/茎类/高杆/根茎/浆果/原版作物等）
   - [ ] 激光束粒子效果
   - [ ] 输出面配置 + 自动输出
8. **生物机器 3 件套** — ✅ 已完成
9. **温室多方块** — ✅ 已完成
10. **洒水器覆盖板** — ✅ 已完成
11. **世界生成**（树/浆果分布）— ✅ 已完成（数据驱动 JSON）
    - ✅ 10 树种：configured feature（`minecraft:tree`）+ placed feature（稀有度/高度图）+ biome modifier + biome tag
    - ✅ 10 浆果丛：random_patch + simple_block + would_survive 过滤
    - ✅ 生物群系映射（原版条件近似）：香蕉→丛林 / 橙子→热带草原 / 柠檬→丛林边缘+森林 / 橄榄→白桦林 / 彩虹木→平原 / 椰子→海滩 等
    - ✅ 共 100 个 JSON 文件（`data/gtmfo/worldgen` + `forge/biome_modifier` + `tags`）
    - [ ] 地牢战利品注入（LootModifier）→ P3
    - [ ] 原版 Perlin 噪声分布系统（当前用标准 rarity 过滤）→ 打磨阶段
12. **工具**（擀面杖/屠刀）— ✅ 已完成

### P2 — 内容补全
13. **药水效果 10 种** + 掺加系统 — ✅ 已完成
    - ✅ `CreativeFlyEffect`（创造飞行 + 落地免伤）
    - ✅ `StepAssistEffect`（台阶辅助，潜行时 0.9 格）
    - ✅ `SnowGolemSpawnerEffect`（雪人生成）
    - ✅ `CyanidePoisoningEffect`（分阶段中毒：反胃/虚弱→失明→致死伤害）
    - ✅ `VentingEffect`（随机传送 + amogus.vent 音效）
    - ✅ PotionAmplifierEffect / PotionLengthenerEffect（实际生效，非标记）
    - ✅ `AntiSchizoEffect`（标记）
    - ✅ `LungCancerEffect`（每 600 tick 降低 1 点最大生命，不可治愈）
    - ✅ `EnhancedChorusEffect`（潜行看向方向传送 8 格）
    - ✅ **掺加系统**：`GTMFOLacing`（NBT 标记 + 食用时触发效果）+ 18 条罐装配方（氰化物/碳酸锂/石棉 × 6 种食物）
    - ✅ **修复**：18 个意大利菜品从普通物品转为食物物品（此前缺食物属性）
    - ✅ 食物效果接线：BRUSCHETTA/CAPONATA/PASTA_AL_POMODORO/PIZZA_VEGGIE/SANDWICH_BACON
14. **实体 3 种** — ✅ 已完成（对照原版 `entity/` 包）
    - ✅ `ItalianBuffaloEntity`（extends Cow）：挤奶 → `GTMFOFluids.ItalianBuffaloMilk` 桶；生成需 3x3 区块内有
      海洋/河流/海滩群系（`checkSpawnRules` + `BiomeTags.IS_OCEAN/IS_RIVER/IS_BEACH`），对应原版
      `NEARBY_BIOME_SPAWNS`；刷怪蛋颜色 0x3d352f / 0xf0ded1（与原版一致）
    - ✅ `StrongSnowmanEntity`（extends SnowGolem）：10 血 / 0.3 速度（原版 `applyEntityAttributes`）、
      投掷强力雪球、`tickCount > 10000` 自毁；AI 沿用原版（远程攻击+游荡+看玩家+打怪）
    - ✅ `StrongSnowballEntity`（extends Snowball）：伤害 2-3（对烈焰人 +3），穿过雪傀儡/玩家不消失
      （原版 `onImpact` 提前 return 的等价实现）
    - ✅ 渲染器（牛用自定义纹理 `textures/entity/italian_buffalo/italian_buffalo.png`；雪人复用原版；
      雪球 `ThrownItemRenderer`）、实体属性注册、EN/CN lang、生成生物群系修饰器
    - ✅ `SnowGolemSpawnerEffect` 修正为原版行为：射线追踪命中方块上方生成**强力雪人** + 力量 IV 1000t
      （原版 `GTFOEventHandler` 的 `Potion.getPotionById(5)`=Strength 4 级）
    - 与原版差异：原版 `spawn.addPotionEffect` 用的 id 5 已确认为力量（1.12 id 表），非抗火
15. **厨房多方块**（订单系统）— ✅ 主体已完成（对照原版 `multiblock/kitchen/` 4 个类）
    - ✅ `KitchenMachine`（控制器）+ `KitchenCraftNode`（合成步骤）+ `KitchenState`（8 状态枚举，
      与原版 `KitchenLogicState` 同名同义）
    - ✅ 结构内机器扫描：`onStructureFormed` 遍历内部 12 格（3 宽 × 4 深 × 1 高），识别
      `WorkableTieredMachine`（排除 `SimpleGeneratorMachine`，对应原版排除 `SimpleGeneratorMetaTileEntity`）
    - ✅ 输入/输出总线+流体仓识别（原版 `initializeAbilities` 的等价实现）
    - ✅ 合成树：从目标物品沿机器配方类型反查（原版是查控制器 NBT 里的"配方书"），BFS + 深度排序 +
      环保护；基础材料不在机器配方中的视为需玩家提供
    - ✅ 执行：投料（从输入总线搬入机器 `importItems/importFluids`）→ `RecipeLogic.setupRecipe` →
      收菜（中间产物回输入总线，最终产物进输出总线，对应原版 `slurpInventory/slurpFluids` 的
      `getNodes(stack)==null || resultItem.isItemEqual(stack)` 判定）
    - ✅ 能量：自身 `VA[tier]/2` 维持消耗（原版 `drainEnergy`），并给受控机器补足整道配方所需 EU
      （原版机器各自供电，这里改为厨房统一供电——`[简化]`）
    - ✅ 脏污系统：每次启动节点 `dirtiness += 1`，概率卡顿 `random * dirtiness < 10`
      （原版 `dirtinessChance`）；清洁液 `DistilledWater=2` / `SodiumStearate=16`
      （原版 `GTFOMaterialHandler` 数值一致），通过 `CleanerProperty`（新建，对应原版 `materials/CleanerProperty`）
    - ✅ 状态显示（`addDisplayText`）：目标/订单数/状态/机器数/脏污
    - `[简化]` 结构尺寸：原版动态半径（`sDist`/`bDist` 扫描）+ 自动更新，这里固定 5×2×6
      （钢管道外壳 + 瓷/暗瓷地板 + 控制器前墙中央），12 个机器位
    - `[简化]` GUI：原版有"配方卡"物品 + 自绘 `KitchenRecipeWidget`（记录配方书、32 电路定订单数）；
      这里改为控制器上的**幻影槽**设目标物品 + 按钮循环订单数 1/2/4/8/16/32/64，无配方卡物品
    - `[简化]` 配方来源：原版需先用配方卡录制配方；这里直接查全局 `RecipeManager`（更省事，但玩家
      无法限制用哪条配方）
    - [ ] 配方卡物品 + 录制 GUI（如需完全还原）
    - [ ] 动态尺寸结构（如需完全还原）
16. **创造标签页拆分** — ✅ 已完成（对照原版 `GTFOValues` 的 8 个 `BaseCreativeTab`）
    - ✅ 8 个标签页：主 / 食物 / 作物 / 工具 / 方块 / 药品与酒精 / 饮品 / 水果与蔬菜
    - ✅ `assignTabs()` 在 `FMLCommonSetupEvent` 重分类（原版 `getSubItems` 过滤规则逐条对应）：
      种子→作物；擀面杖/屠刀→工具；伏特加/列宁檬汁/啤酒/红酒白酒/胶囊→药品酒精；
      矿泉水/气泡水/果汁/碧雪/咖啡→饮品；香蕉/橙子/葡萄/芒果/杏/柠檬/酸橙/浆果/橄榄/番茄/洋葱/黄瓜/茄子→果蔬；
      非机器 BlockItem→方块；含 `GTMFOFoodStats` 组件→食物；其余留主标签
    - ✅ 图标与原版一致（面团/香蕉/洋葱种子/擀面杖/土坯砖/啤酒/橙汁/芒果）
    - ✅ 标题本地化（EN 走 Registrate，CN 走自定义 provider + 生成 lang）
17. **配置系统补全** — ✅ 已完成（对照原版 `GTFOConfig` 9 大配置组）
    - ✅ `GTFOChainsConfig` / `GTFOVanillaOverridesConfig` / `GTFOOtherFoodModConfig` /
      `GTFONCConfig` / `GTFOAAConfig` / `GTFOFoodConfig` / `GTFOPotionConfig` / `GTFOMiscConfig` /
      `GTFOWorldGenConfig`，字段名与默认值与原版逐一对应
    - ✅ 药水开关接线：`creativity`/`stepAssist`/`snowGolemSpawner` 控制对应效果逻辑
    - ✅ 食物数值接线：硬糖/法棍/薯片/薯条/气泡水/柠檬/酸橙/碧雪 从配置读取（原版即如此）
    - 说明：NC/AA/AppleCore/Nutrition 相关开关保留但无效果（对应模组未移植），已在注释标注

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
