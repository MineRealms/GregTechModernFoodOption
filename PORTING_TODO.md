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

### 1.1 注册系统
- [x] GTRegistrate 注册器 (`GTMFORegistries`)
- [x] 创造模式标签页 (`GTMFOCreativeModeTabs`) — 原版有 8 个分类页，现代版需拆分
- [ ] 拆分创造标签页：主分类 / 食物 / 作物 / 工具 / 方块 / 饮品 / 果蔬（原版 `GTFOValues` 中定义 8 个 `BaseCreativeTab`）
- [ ] 网络包系统（原版 `PacketAppleCoreFoodDivisorUpdate` 等自定义包）
- [ ] 数据码/同步 ID 系统（原版 `assignId()` 机制）

### 1.2 配置系统
- [x] Toma Configuration 基础框架 (`GTMFOConfigHolder`)
- [ ] 补全配置项（原版 `GTFOConfig` 有 9 大配置组、80+ 配置项）：
  - [ ] `GTFOChainsConfig` — 删面包配方 / 硬核模式
  - [ ] `GTFOVanillaOverridesConfig` — 原版食物链覆写 / 烤炉烤肉 / 擀面杖造纸
  - [ ] `GTFOOtherFoodModConfig` — AppleCore 兼容 / 外来食物数值削减
  - [ ] `GTFONCConfig` — NuclearCraft 兼容 / S'more 链 / Smogus
  - [ ] `GTFOAAConfig` — ActuallyAdditions 兼容 / 禁用咖啡机
  - [ ] `GTFOFoodConfig` — 各食物饥饿值/饱和度覆写
  - [ ] `GTFOPotionConfig` — 药水功能开关
  - [ ] `GTFOWorldGenConfig` — 世界生成开关
  - [ ] `GTFOMiscConfig` — 温室土壤、烤炉替换熔炉等杂项

### 1.3 核心 API / Mixin
- [x] `GTMFOFoodStats` 食物组件系统（6 维营养）
- [x] `INutrients` / `Nutrients` / `NutrientsTracker` 营养能力
- [x] `FoodPropertiesMixin` / `ItemMixin` / `PlayerMixin`
- [ ] `IEatingDuration` 进食时长接口补全
- [ ] `IContainerItem` 容器返还接口补全
- [ ] `RecipeMapFluidCannerMixin`（原版 late mixin，修改流体罐装器配方）
- [ ] AppleSkin 联动 Mixin（原版有 4 个 AppleSkin 集成类）

### 1.4 已有但未使用的脚手架（可复用）
- [ ] `WoodBlock`（去皮原木逻辑，未注册）
- [ ] `LogBlock`（自然原木状态，未注册）
- [ ] `PlacedFoodBlock`（放置食物方块基类，未使用）
- [ ] `PlaceableFoodItem`（可放置食物物品，未使用）
- [ ] `CreativeFlyEffect`（创造飞行药水逻辑，未注册）
- [ ] `BoxBuilder`（形状构建工具，仅 11 处引用）
- [ ] `NutrientCommands`（营养指令，需确认接线）

### 1.5 事件处理器（原版 `GTFOEventHandler` + `GTFODropsEventHandler`，共 300+ 行）
- [ ] `onMaterialsInit` 材料初始化事件
- [ ] `onLivingUpdate` 实体更新事件：
  - [ ] 创造飞行药水的持久化 NBT 管理（`PERSISTED_NBT_TAG`）
  - [ ] 雪人生成药水逻辑
  - [ ] 台阶辅助药水（跳跃提升集合 `jumpBoostSet`）
  - [ ] 药水颜色计算事件 `PotionColorCalculationEvent`
  - [ ] 成瘾/戒断药水（已注释，可选）
- [ ] 进度事件 `AdvancementEvent`
- [ ] 方块事件 `BlockEvent`
- [ ] 世界事件 `WorldEvent`
- [ ] 玩家事件 `PlayerEvent`（tick/重生等）
- [ ] `GTFODropsEventHandler` 掉落事件处理：
  - [ ] 猪/牛/鸡/羊/兔死亡时 1/3 概率掉落 `SCRAP_MEAT` 碎肉
  - [ ] 碎肉数量随掠夺等级增加（`rand.nextInt(lootingLevel) + 1`）
- [ ] 假玩家处理 `GregFakePlayer`

### 1.6 配方表修改（原版 `CommonProxy.preLoad`）
- [ ] `RecipeMaps.BREWING_RECIPES.setMaxOutputs(1)` 酿造配方表输出扩容
- [ ] `RecipeMaps.EXTRACTOR_RECIPES.setMaxInputs(2)` 提取器输入扩容
- [ ] `RecipeMaps.FERMENTING_RECIPES.setMaxInputs(1) / setMaxOutputs(1)` 发酵配方表
- [ ] `RecipeMaps.COMPRESSOR_RECIPES.setMaxFluidInputs(1) / setMaxFluidOutputs(1)` 压缩机流体扩容
- [ ] 杂草种子掉落注册（`addGrassSeed`，配置权重 `unknownSeedsWeight`）
- [ ] Lacing 掺加注册表（3 种：氰化物/抗精神分裂/肺癌 + 物品图案字符串）

---

## 2. 材料与流体（原版 ~100+ 个，现代版 ~20 个）

### 2.1 缺失的流体材料（按原版 ID 21500-21628 补全）

**水果提取液类：**
- [ ] `ApricotExtract` 杏子提取液
- [ ] `CranberryExtract` 蔓越莓提取液
- [ ] `AppleCider` 苹果酒
- [ ] `AppleSyrup` 苹果糖浆
- [ ] `AppleCandySyrup` 苹果糖浆（糖果用）
- [ ] `GrapeExtract` 葡萄汁（已有）

**糖浆/甜味类：**
- [ ] `UnheatedCaneSyrup` 未加热甘蔗糖浆（已有）
- [ ] `CaneSyrup` 甘蔗糖浆（已有）
- [ ] `HighFructoseCornSyrupSolution` 高果糖玉米糖浆溶液
- [ ] `SweetenedDilutedCaneSyrupMixture` 稀释加糖甘蔗糖浆混合物（已有）
- [ ] `MarshmallowSyrupMixture` 棉花糖糖浆混合物（已有）
- [ ] `MarshmallowFoam` 棉花糖泡沫（已有）

**油脂/煎炸类：**
- [ ] `FryingOil` / `HotFryingOil`（已有）
- [ ] `OliveOil`（已有）
- [ ] `RawSoybeanOil` 生大豆油
- [ ] `HydratedSoybeanOil` 水合大豆油
- [ ] `SoybeanOil` 大豆油
- [ ] `SoyLecithin` 大豆卵磷脂
- [ ] `Stearin` 硬脂
- [ ] `SodiumStearate` 硬脂酸钠

**乳制品类：**
- [ ] `ItalianBuffaloMilk` 意大利水牛奶
- [ ] `CrudeRennetSolution` 粗制凝乳酶溶液
- [ ] `Whey` 乳清
- [ ] `ActivatedBuffaloMilk` 活化水牛奶
- [ ] `WheySaltWaterMix` 乳清盐水混合物
- [ ] `HeatedRicottaStarter` 加热里科塔发酵剂
- [ ] `AcidicMilkSolution` 酸化牛奶溶液
- [ ] `CoagulatingRicottaSolution` 凝固里科塔溶液
- [ ] `PasteurizedMilk` 巴氏杀菌奶
- [ ] `SkimmedMilk` 脱脂奶
- [ ] `UnpasteurizedSkimmedMilk` 未杀菌脱脂奶
- [ ] `MilkColloid` 牛奶胶体
- [ ] `Cream` 奶油
- [ ] `SourCream` 酸奶油
- [ ] `LacticAcidBacteria` 乳酸菌
- [ ] `Butter`（已有）
- [ ] `IceCreamMixture` 冰淇淋混合物

**奶酪类：**
- [ ] `ParmigianoReggianoStarter` 帕马森发酵剂
- [ ] `CurdlingParmigianoReggiano` 凝固帕马森
- [ ] `FungalRennetSolution` 真菌凝乳酶溶液

**汤/酱汁类：**
- [ ] `MushroomSoup` 蘑菇汤
- [ ] `BeetrootSoup` 甜菜汤
- [ ] `RabbitStew` 兔肉煲
- [ ] `TomatoSauce`（已有）
- [ ] `BologneseSauce` 博洛尼亚酱
- [ ] `TomatoBologneseSauce` 番茄博洛尼亚酱
- [ ] `CarbonaraSauce` 培根蛋酱
- [ ] `Pesto` 青酱
- [ ] `BechamelSauce` 白酱
- [ ] `ChickenBroth` 鸡高汤
- [ ] `VitelloTonnatoSauce` 意式鱼香小牛肉酱
- [ ] `VitelloTonnatoFlavorant` 风味剂
- [ ] `Agrodolce` 意式酸甜酱
- [ ] `Polenta` 玉米糊
- [ ] `RafanataMixture` 辣根蛋饼混合物
- [ ] `PastaEFagioliBase` 意面豆汤底
- [ ] `MixedPastaEFagioli` 混合意面豆汤

**酒精/饮品：**
- [ ] `Vodka` 伏特加
- [ ] `Leninade` 列宁檬汁
- [ ] `WhiteWine` 白葡萄酒
- [ ] `RedWine` 红葡萄酒
- [ ] `MaceratedWhiteGrapes` 浸渍白葡萄
- [ ] `PressedWhiteWort` 压榨白麦芽汁
- [ ] `ClarifiedWhiteWort` 澄清白麦芽汁
- [ ] `RedGrapesMust` 红葡萄汁
- [ ] `FermentedRedGrapesMust` 发酵红葡萄汁
- [ ] `AlcoholicRedGrapeJuice` 含酒精红葡萄汁
- [ ] `WheatyJuice` 小麦汁
- [ ] `PoorQualityBeer` 劣质啤酒
- [ ] `BeerBatter` 啤酒面糊
- [ ] `Etirps` 碧雪
- [ ] `EtirpsCranberry` 蔓越莓碧雪
- [ ] `CranberrySodaSyrup` 蔓越莓苏打糖浆
- [ ] `CranberrySludge` 蔓越莓浆
- [ ] `LemonLimeSodaSyrup` 柠檬酸橙苏打糖浆
- [ ] `LemonLimeSolution` 柠檬酸橙溶液
- [ ] `LemonLimeSludge` 柠檬酸橙浆
- [ ] `CarbonatedWater`（已有）
- [ ] `PurpleDrink` 紫色饮料
- [ ] `CoughSyrup` 止咳糖浆
- [ ] `Nilk` 硅岩风味乳

**咖啡/可可类：**
- [ ] `Coffee` 咖啡
- [ ] `EnergizedCoffee` 提神咖啡
- [ ] `MoltenUnsweetenedChocolate` 熔融无糖巧克力
- [ ] `CocoaButter` 可可脂
- [ ] `MoltenDarkChocolate` 熔融黑巧克力
- [ ] `MoltenMilkChocolate` 熔融牛奶巧克力

**化学/加工类：**
- [ ] `IsopropylChloride` 异丙基氯
- [ ] `PerchloricAcid` 高氯酸
- [ ] `ChloroauricAcid` 氯金酸
- [ ] `MoistAir` / `ColdMoistAir` 湿空气/冷湿空气
- [ ] `Sludge` 污泥
- [ ] `AlkalineExtract` 碱性提取液
- [ ] `PotatoJuice` 马铃薯汁
- [ ] `StarchFilledWater` 淀粉水
- [ ] `CitricAcid` 柠檬酸
- [ ] `HydrogenCyanide` 氰化氢
- [ ] `Guaiacol` 愈创木酚
- [ ] `Acetaldehyde` 乙醛
- [ ] `Glyoxal` 乙二醛
- [ ] `GlyoxylicAcid` 乙醛酸
- [ ] `SodiumArseniteSolution` 亚砷酸钠溶液
- [ ] `RubberSap` / `RainbowSap` 橡胶树液/彩虹树液
- [ ] `BlueVitriol` 蓝矾
- [ ] `BakingSodaSolution` 小苏打溶液
- [ ] `SodiumSulfate` 硫酸钠（粉）
- [ ] `Blood` 血
- [ ] `FertilizerSolution` 肥料溶液
- [ ] `XPhenothiazineIiPropylChloride` 异丙嗪中间体
- [ ] `Aniline` 苯胺
- [ ] `HeatedWater` 加热水
- [ ] `GelatinSolution` 明胶溶液
- [ ] `AceticAnhydride` 乙酸酐
- [ ] `Nitrophenols` 硝基苯酚
- [ ] `Egg`（已有）
- [ ] `Albumen` / `Yolk`（已有）
- [ ] `EnderPearlSolution` 末影珍珠溶液
- [ ] `EnderSugarSolution` 末影糖溶液
- [ ] `ChorusJuice` 紫颂果汁
- [ ] `FermentedChorusJuice` 发酵紫颂果汁
- [ ] `Antaf` 安塔夫
- [ ] `VibrantExtract` 活力提取液
- [ ] `SodiumCarbonateSolution` 碳酸钠溶液
- [ ] `LingonberryJam` / `ElderberryJam` 越橘/接骨木莓果酱
- [ ] `CranberrySodaSyrup` 蔓越莓苏打糖浆

### 2.2 缺失的粉/物品材料
- [ ] `Paracetamol` 对乙酰氨基酚（粉）
- [ ] `SodiumCyanide`（已有）
- [ ] `Zest`（已有）
- [ ] `PotatoStarch` 马铃薯淀粉
- [ ] `CornStarch` 玉米淀粉
- [ ] `BoneAsh` 骨灰
- [ ] `BoneChinaClay` 骨瓷土
- [ ] `UnfiredPorcelainTile` / `BiscuitPorcelainTile` / `GlazedPorcelainTile` / `BlackGlazedPorcelainTile` 瓷砖系列
- [ ] `VanillylmandelicAcid` / `VanilglycolicAcid` / `Vanillin` 香草醛系列
- [ ] `Aminophenol` / `IVNitrophenol` / `IINitrophenol` 药物中间体
- [ ] `Promethazine` / `Codeine` / `Phenothiazine` / `Diphenylamine` 药物
- [ ] `CrushedPoppy` 碾碎罂粟
- [ ] `AmmoniumPerchlorate` / `PotassiumPerchlorate` / `SodiumPerchlorate` / `SodiumChlorate` / `ArsenicTrioxide` / `CupricHydrogenArsenite` / `LithiumOxide` / `LithiumCarbonate` 化学粉
- [ ] `LargeMozzarellaCurd` / `SmallMozzarellaCurd` / `DriedMozzarellaCurd` / `SolidifiedMozzarellaCurd` / `CoagulatedMilkCurd` / `CutCurd` / `CookedCurd` / `SaltedCurd` / `GorgonzolaCurd` / `PenicilliumRoqueforti` 奶酪凝乳系列
- [ ] `ToughMeat` / `KubideMeat` / `BargMeat` / `Fat` / `MeatIngot` / `CookedMinceMeat` 肉类中间品
- [ ] `ShreddedParmesan` / `BlackPepper` / `Nutmeg` / `GratedHorseradishRoot` 调味料
- [ ] `MashedPotato` 土豆泥（粉状）
- [ ] `BurntBananaPeel` 烧焦香蕉皮
- [ ] `HotAppleHardCandy` / `CrushedHardCandy` / `HardCandyPlate` / `HardCandyResin` 糖果系列
- [ ] `COFFEE_GROUNDS` 咖啡粉 + 咖啡豆 14 阶段中间品
- [ ] `CHOCOLATE_LIQUOR` 系列 6 种（refined/dutched/pressed）
- [ ] `PRESS_CAKE` 系列 3 种
- [ ] `MATTER_MARSHMALLOW` / `MATTER_GRAHAM` 系列 4 种
- [ ] `CRACKER_GRAHAM_UNGRADED` / `CHUNK_GRAHAM_HOT` / `WAFER_GRAHAM_HOT` 饼干系列
- [ ] `BareCornKernel` / `CornKernel` 玉米粒系列
- [ ] `COCOA_HULL` / `COCOA_NIB` 可可中间品

### 2.3 材料属性系统
- [ ] `FertilizerProperty` 肥料属性（水 5% / 血 30% / 肥料溶液 15%）
- [ ] `LacingProperty` 掺加属性（药水效果注入食物）
- [ ] `CleanerProperty` 清洁属性（蒸馏水 2 / 硬脂酸钠 16）
- [ ] `GTFOFireSuppressantProperty` 灭火属性（水）
- [ ] `Organic` MaterialIconSet 有机图标集
- [ ] 材料工具提示注册 `registerPropertyTooltips()`

### 2.4 原版材料标志修改
- [ ] `Iron` + `GENERATE_FRAME`
- [ ] `BismuthBronze` + `GENERATE_FRAME`（已有）
- [ ] `Aluminium` + `GENERATE_DENSE`
- [ ] `StainlessSteel` + `GENERATE_DENSE, GENERATE_SPRING_SMALL`
- [ ] `Titanium` + `GENERATE_DENSE`

---

## 3. 物品（原版 ~200 个）

### 3.1 容器类
- [x] 烤盘 / 易拉罐 / 瓷碗 / 脏碗 / 瓷盘 / 脏盘 / 空杯 / 纸袋 / 塑料袋
- [ ] 未烧制容器系列（碗/盘/杯）
- [ ] `Dewar Flask` 保温杯系列（原版已注释，可选）

### 3.2 缺失食物物品（对比 `GTMFOItems` vs 原版 `GTFOMetaItem`）
- [x] **食物属性接线审计（2026-09-12）**：用脚本逐项对比原版 `GTFOMetaItem` 的 163 个
      `GTFOFoodStats` 物品与 `GTMFOItems`，发现 **110 个原版食物在现代版是普通物品**
  - [x] 已修复 72 个（有现成 `Foods` 常量）：水果/蔬菜、切片、冰淇淋 10、三明治 9、千层面 3、
        烤肉 9、饺子 2、培根/焗豆/豆吐司/蛋糕底/全麦饼干/棉花糖/豌豆泥/炸鱼薯条/煎鱼/全套早餐/
        香肠×2/牧羊人派/牛奶巧克力/应急口粮/发酵紫颂果×2 等
  - [x] 补充接线：披萨片×3（`PIZZA_CHEESE/VEGGIE/MEAT`）、`PASTA_ALL_AMOGUS`（+VENTING 50%）、
        `ICE_CREAM` 原味（`ICE_CREAM_PLAIN`）、`CHUM_BUCKET`（`KEBAB_CHUM_BUCKET`）、
        `BRICK_MUD`（+中毒 50%）、`BRICK_ADOBE`（可食用）、`SORBET_CHORUS`/`SORBET_VIBRANT`
  - [x] 取消注释并接线 5 个 `Foods` 条目（SANDWICH_BACON_LARGE 台阶辅助 /
        ICE_CREAM_VANILLA 雪人生成 50% / FERMENTED_CHORUS / PIE / SANDWICH_VIBRANT）
  - [ ] **剩余真实缺失物品**：`ROTTEN_MEAT` 臭肉 / `ROTTEN_FISH` 臭鱼（现代版被注释掉，
        原版为 (1,0) alwaysEat + 中毒 500t 100%）；对应 `Foods.ROTTEN_MEAT`/`ROTTEN_FISH` 已存在
  - 说明：`ICE_CREAM_CHORUS` 为现代版新增（原版无），保持普通物品未加属性
- [ ] 逐项核对 `GTMFOItems` 与原版 `GTFOMetaItem` 的 ID 差异表（已完成食物部分）
- [ ] 补全 `POPCORN_BAG` 爆米花袋（原版即注释 WIP）
- [ ] 补全 `MINERAL_WATER` 矿泉水（原版 0.x 内容；现代版物品已存在，需核对是否食物）
- [x] 补全 Sorbet 系列（`SORBET`/`SORBET_APPLE`/`APRICOT`/`GRAPE`/`LIME`/`CHORUS`/`VIBRANT`）
- [x] 补全 Fermented Chorus 系列（`FERMENTED_CHORUS`/`FERMENTED_CHORUS_PIE`）
- [ ] 补全 IV Bag 输液袋（`IVBagChain`）
- [ ] 补全 `DEWAR_FLASK` 保温杯系列（原版已注释）
- [x] 补全 British 英式料理系列（`BritishRecipes`）

### 3.3 种子类
- [ ] 所有作物种子物品（番茄/黄瓜/葡萄/洋葱/大豆/咖啡/豆/豌豆/牛至/辣根/大蒜/罗勒/茄子/玉米/洋蓟/黑胡椒/大米/白葡萄/棉花）
- [ ] `Unidentified GTFO Seeds` 未鉴定种子（杂草掉落）

### 3.4 工具类
- [ ] `ROLLING_PIN` 擀面杖（GTCEu ToolItem 系统，支持木/橡胶/聚乙烯/聚四氟乙烯材质）
- [ ] `BUTCHERY_KNIFE_HV` 电动屠刀 HV（带 Looting 5 附魔）

---

## 4. 方块

### 4.1 作物方块（已完成 ✅）
- [x] `GTFOCropBlock` 基础作物方块系统（age 0-5）
- [x] `GTFORootCropBlock` 根茎类作物（洋葱/辣根，中期收获机制）
- [x] `GTFOWaterCropBlock` 水生作物（大米）
- [x] `GTFOBerryBushBlock` 浆果丛（含荆棘/效率育种机制）
- [x] 19 种作物：番茄/洋葱/黄瓜/葡萄/大豆/豆/豌豆/牛至/辣根/大蒜/罗勒/茄子/玉米/洋蓟/黑胡椒/大米/白葡萄/棉花/咖啡（咖啡待配方）
- [x] 10 种浆果丛：蓝莓/黑莓/树莓/草莓/红加仑/黑加仑/白加仑/越橘/接骨木莓/蔓越莓
- [x] 种子右键种植（事件拦截）
- [ ] 作物自然生成（世界生成）
- [ ] 农场机模式集成

### 4.2 树木方块（已完成 ✅）
- [x] `GTFOBlockSapling` 树苗系统（每树独立，含生长）
- [x] `GTFOBlockPlanks` 木板系统
- [x] `GTFOBlockLog` 原木系统
- [x] `GTFOBlockLeaves` 树叶系统（果实掉落）
- [x] 10 种树：香蕉/橙子/芒果/杏子/柠檬/酸橙/橄榄/彩虹木/肉桂/椰子
- [x] 树木配方（原木→木板/木棍）
- [ ] 树木世界生成（见第 7 章）
- [ ] 树叶着色（原版每树独立颜色）

### 4.3 建筑方块
- [x] `BISMUTH_BRONZE_CASING` 食品级铋青铜外壳
- [ ] `ADOBE_BRICKS` 土坯砖块
- [ ] `REINFORCED_ADOBE_BRICKS` 加固土坯砖块
- [ ] `PORCELAIN_TILE` 瓷砖
- [ ] `DARK_PORCELAIN_TILE` 暗色瓷砖
- [ ] `GTFOGlassCasing` 温室玻璃（CTM 连接纹理）
- [ ] `GTFOBlockCasing` 通用外壳系统
- [ ] `GTFOMetalCasing` 金属外壳系统

### 4.4 食物方块
- [x] 披萨方块（芝士/肉末/橄榄蘑菇，切片机制）
- [x] Smogus 方块（4 级）
- [ ] S'more 方块（1/64 等，当前注释）
- [x] 披萨盒（GTFOPizzaBox）：3 种方块 + 右键变披萨 + 打包机配方（原版 id）—— 已完成

---

## 5. 机器（单方块）

### 5.1 已有但需扩展
- [x] Slicer 切片机（已注册 LV-UV 全电压，`registerSimpleMachines` 默认 `ELECTRIC_TIERS`）
- [x] Cuisine Assembler 菜肴组装机（已注册 LV-UV 全电压）
- [x] Microwave 微波炉（已注册 LV-UV，但缺少特殊行为）
- [x] Multicooker 多功能烹饪机（已注册 LV-UV 全电压）
- [ ] **微波炉特殊行为缺失**（原版 `MetaTileEntityMicrowave`）：
  - [ ] 放入锭/易燃/易爆/熔炉燃料物品时爆炸（伤害 = 等级 × 4）
  - [ ] 完成时播放 `MICROWAVE_FINISH` 音效
- [ ] **GTFO GUI 标志缺失**（原版 `GTFOSimpleMachineMetaTileEntity` 在 GUI 加 GTFO logo，含 XMAS 变体）
- [ ] 核对配方映射 IO 数量（原版 vs 现代）：
  - Slicer: 原版 2/2/1/1，**现代 3/2/1/1（多 1 输入，需确认是否有意为之）**
  - Cuisine Assembler: 原版 6/2/3/1，现代 6/2/3/1 ✅
  - Microwave: 原版 1/1/0/0，现代 1/1/0/0 ✅
  - Multicooker: 原版 6/3/3/2，现代 6/3/3/2 ✅

### 5.2 缺失机器（全部需新建）
- [ ] `MetaTileEntityFarmer` 农场机（LV-EV 4 级）
  - [ ] `FarmerMode` 模式系统 + `FarmerModeRegistry`
  - [ ] 5 种模式：茎类/下界疣/高杆作物/根茎作物/普通作物
  - [ ] 9x9 工作区域逻辑
  - [ ] 假玩家收获系统
  - [ ] 种子输入/作物输出 9+9 槽
  - [ ] 激光束粒子效果
  - [ ] 自动输出 + 输出面配置
- [x] `MobAgeSorterMachine` 生物年龄分拣机（LV-EV 4 级）→ ✅ 已完成
  - [x] 红石控制
  - [x] 幼年/成年过滤切换
  - [x] 传送范围配置（1/3/5/9）
- [x] `MobExterminatorMachine` 生物灭绝机（LV-EV 4 级）→ ✅ 已完成
  - [x] 红石控制 + 9x9 范围
  - [x] 一氧化二氮加速机制
  - [x] 掠夺等级 = 机器等级 - 1（假玩家 + 附魔剑）
  - [x] 流体槽
- [x] `MobExtractorMachine` 生物提取机（LV-UV 8 级）→ ✅ 已完成
  - [x] 配方数据 `mob_on_top`（实体类型）
  - [x] 配方数据 `cause_damage`（伤害值）
  - [x] 实体检测/攻击逻辑
  - [ ] 自定义伤害源（暂用通用伤害）
- [ ] `MetaTileEntityBioReactor` 生物反应器（HV-IV，原版已注释，可选）

---

## 6. 多方块

### 6.1 已有
- [x] `ELECTRIC_BAKING_OVEN` 电力烤炉
- [x] `STEAM_BAKING_OVEN` 蒸汽烤炉

### 6.2 缺失
- [ ] `MetaTileEntityBakingOven` 原始烤炉（土坯砖结构，烧木炭）
- [ ] `MetaTileEntityGreenhouse` 温室
  - [ ] 7x7x9 玻璃结构
  - [ ] 可配置土壤方块（配置项 `greenhouseDirts`）
  - [ ] 作物催熟/收获逻辑
  - [ ] `GreenhouseChain` 温室配方链
- [ ] `MetaTileEntityKitchen` 厨房
  - [ ] 自定义大小结构（最小半径 2）
  - [ ] `KitchenLogic` 订单系统
  - [ ] 配方卡片槽 + 32 电路槽
  - [ ] 能量消耗 = VA[tier] / 2
  - [ ] 输入/输出物品+流体仓
- [ ] 烤炉配方系统重做：
  - [ ] `BakingOvenRecipeBuilder` 温度机制
  - [ ] `ElectricBakingOvenRecipeMap` 温度匹配
  - [ ] 原始烤炉 → 电力烤炉配方自动转换
  - [ ] 烤炉替换熔炉配方选项

---

## 7. 世界生成（全部缺失）

### 7.1 框架
- [ ] 移植 `GTFOFeature` 特征系统（Perlin 噪声 + 条件系统）
- [ ] 移植 `GTFOFeatureGen` 生成器
- [ ] 移植条件系统：
  - [ ] `FeatureCondition` 基础条件
  - [ ] `BiomeCondition` 生物群系条件
  - [ ] `TemperatureRainfallCondition` 温度/降雨条件
- [ ] 改用 1.20.1 的 `ConfiguredFeature` / `PlacedFeature` / `BiomeModifier` 系统

### 7.2 树木世界生成（10 种）
- [ ] 香蕉树、橙子树、芒果树、杏子树、柠檬树、酸橙树
- [ ] 橄榄树、彩虹木、肉桂树、椰子树
- [ ] 各自的生成条件（温度/降雨/群系）
- [ ] `GTFOTreeGen` 树木生成器

### 7.3 浆果/作物世界生成
- [ ] `GTFOBerryGen` 浆果丛生成
- [ ] 各浆果群系条件

### 7.4 地牢战利品
- [ ] `GTFODungeonLootLoader` 地牢战利品注入
- [ ] 改用 1.20.1 `LootModifier` 系统

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
