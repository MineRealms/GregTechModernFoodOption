# GTMFO 营养系统 × Sunlit Valley 联动分析报告

> 状态：**调研阶段（未改动任何代码）**　日期：2026-09-24
> 取证范围：GTMFO 仓库源码 + 整合包 "Society: Sunlit Valley"（`G:\MinecraftGames\Sunlit Valley(BaopuEdition)\.minecraft\versions\Society Sunlit Valley`，git HEAD）
> 原则：所有结论附**文件路径/行号或数值**；不确定处标注"待确认"。

---

## 0. 摘要（先看这里）

1. **GTMFO 营养系统目前是"半成品"**：只记录、不衰减、无效果、无同步、无外部接口（详见 §1）。
2. **整合包已有两套"吃"的元系统**：`SoLOnion`（吃不同食物 → 生命/力量/速度/护甲韧性）与 `Quality Food`（品质 0-3 → 售价 ×1.25/1.5/2）。营养系统**不能与它们抢同一个奖励生态位**（详见 §3.2）。
3. **推荐联动形态**：营养系统定位为**"类别均衡"第三轴**，主要驱动 **① 经济（售价/属性）** 与 **② 技能/任务（Puffish Skills + FTB 任务）**，只在低阈值给极小属性加成（详见 §4）。
4. **必须先修的模态问题 3 个**：`gain(5 floats)` 覆盖而非累加（数值失效）、无客户端同步（无法做 HUD/tooltip）、无 KubeJS/外部读取接口（包侧拿不到营养值）（详见 §1.3）。
5. 营养值覆盖：GTMFO 共 **156 种食物**带营养（均值 ~0.9，单件最高 3.0）；**非 GTMFO 食物默认没有营养值**——是否扩展是核心决策点（§3.4、§6）。

---

## 1. GTMFO 营养系统现状（代码取证）

### 1.1 数据结构与数据流

```
吃东西 (GTMFOFoodStats.finishUsingItem)
  └─ if (devConfigs.nutrientMode)                     ← GTMFOConfigHolder.java:69（默认 false）
       └─ GTMFOCapability.getNutrientsTracker(player) ← forge/GTMFOCapability.java:15
            └─ NutrientsTracker.gain(dairy, fruit, grain, protein, vegetable)   ← NutrientsTracker.java:24
                 └─ nutrients.put(...)   ← 玩家 NBT: {nutrients:{dairy:..,...}}  ← serializeNBT():57
查看/调试: /nutrient query|clear|gain     ← common/command/NutrientCommands.java
```

| 文件 | 作用 |
|---|---|
| `api/capability/Nutrients.java` | 五类固定列表 `dairy/fruit/grain/protein/vegetable` |
| `api/capability/NutrientsTracker.java` | 玩家级 Capability，NBT 持久化；`tick()` **空实现**；`gain(5参)`/`gain(name,amount)`/`remove(name)` |
| `api/capability/forge/GTMFOCapability.java` | Capability 注册与获取 |
| `api/item/component/GTMFOFoodStats.java:59-69` | 吃食物时写入营养（受 `nutrientMode` 门控） |
| `common/data/Foods.java` | **156 种食物的营养数值**（构建时传入 5 个 float） |
| `GTMFOConfigHolder.java:66-86` | `devConfigs.nutrientMode`（默认 `false`） |
| `common/command/NutrientCommands.java` | `/nutrient query` / `clear` / `gain`（管理员） |
| `api/mixin/INutrients.java` | 定义了 `addNutrients/getNutrients` 接口，但**全仓库没有被 mixin 使用**（死代码） |

### 1.2 数值分布（解析 `Foods.java` 共 156 条）

| 营养 | 出现食物数 | 总和 | 非零均值 | 单件最大 |
|---|---|---|---|---|
| dairy 乳 | 41 | 38.75 | 0.95 | 3.00 |
| fruit 果 | 48 | 40.75 | 0.85 | 2.00 |
| grain 谷 | 80 | 72.75 | 0.91 | 2.00 |
| protein 蛋白 | 69 | 58.50 | 0.85 | 2.50 |
| vegetable 菜 | 57 | 55.73 | 0.98 | 2.00 |

食物含营养的类别数：0 类 10 种、1 类 58 种、2 类 42 种、3 类 33 种、**4 类 11 种、5 类 2 种**。

→ **量级结论**：单件食物 ~0.9，一顿主菜 ~1~2.5。设计阈值时应对应"吃 N 件不同食物累计到 X"，而不是大数字。

### 1.3 已确认的缺口 / 缺陷

| # | 问题 | 证据 | 影响 |
|---|---|---|---|
| 1 | **`gain(5参)` 用 `put` 覆盖而非累加** | `NutrientsTracker.java:24-40`（对比 `gain(name,amount)` 是 `getFloat+amount`） | 营养值永远是"最近一次吃的东西"的值，累计逻辑失效 |
| 2 | 无**衰减/每日结算** | `tick()` 空（`:20`） | 营养只增不减，一次吃满永久生效 |
| 3 | 无**客户端同步** | Capability 仅注册 NBT（`ForgeCommonEventListener`），无网络包 | 无法做 HUD/物品 tooltip，玩家看不到营养 |
| 4 | 无 **KubeJS / 命令式读写接口** | 只有管理员命令 | 包侧（KubeJS/任务/技能）拿不到营养值，无法联动 |
| 5 | 无**死亡处理** | 未发现 `PlayerEvent.Clone` 复制逻辑 | 待确认：死亡后营养是否丢失（重生于新实体） |
| 6 | `INutrients` 接口未被使用 | `api/mixin/INutrients.java` | 死代码（清洁度问题） |
| 7 | `nutrientMode` 放在 `devConfigs` | `GTMFOConfigHolder.java:68` | 语义是"功能开关"却标为开发者选项 |

---

## 2. Sunlit Valley 现有"吃"相关系统盘点

### 2.1 SoLOnion（Spice of Life: Onion）——"食物多样性"轴 ✅ 已在用
- 模组：`mods/SoLOnion_FORGE_v1.4.5_mc1.20.1.jar`；配置 `config/solonion.json`
- 机制：记录最近 **32** 次进食的不同食物；`resetOnDeath: true`
- 阈值奖励（节选）：5→+2 生命；10→力量+4 生命；15→+6 生命；20→速度+8 生命；23→+2 护甲韧性；25→+10 生命；28→力量 I；30→+12 生命；35→+16；40→+20
- 另有 `foodDiversity` 加成（金胡萝卜 +2、金苹果 +2、附魔金苹果 +5、无尽果实 -2 等）
- 包内对它的改动：**移除** `solonion:lunchbox/lunchbag/golden_lunchbox`（`removeRecipes.js:547-549`、`handleBinBans.js:182-184`）——保留机制，去掉容器

### 2.2 Quality Food——"单件品质"轴 ✅ 已在用（且**直接影响经济**）
- 模组：`mods/quality_food-1.20.1-2.4.3-all.jar`
- 数据：物品 NBT `quality_food:{quality:0..3, effects:[]}`；作物/畜牧产出会带品质（`havestHandling.js:189`、`farmingLoot.js:34`、`animalBase.js:493`、`globalAnimalHandlers.js:145/387`）
- **价格倍率**（`kubejs/client_scripts/tooltips/addPriceTooltips.js:1-7`）：
  | 品质 | 常规 | 有 `the_quality_of_the_earth` 技能时 |
  |---|---|---|
  | 1 | ×1.25 | ×1.5 |
  | 2 | ×1.5 | ×2 |
  | 3 | ×2 | ×3 |
- 包内还有 `qualityWasher` 机器、`quality_food:material_whitelist/blacklist` 标签等

### 2.3 Puffish Skills——技能树（**天然的营养载体**）
- 包内分类：`adventuring / books / farming / fishing / husbandry / mastery / mining`
  （`kubejs/data/society/puffish_skills/categories/*/`，每个分类含 `category/experience/skills/definitions/connections.json`）
- **关键证据**：`farming/experience.json` 已使用 **`puffish_skills:eat_food`** 事件源（吃 `#society:dish` → 20 XP）✅
- `husbandry/experience.json` 用 `puffish_skills:increase_stat`（`animals_bred` → 40 XP）
- `farming/skills.json` 节点示例：`axe_efficiency+1`、`tiller`、`artisan`、`aged_prize`、`rancher`…

### 2.4 经济与"属性"体系
- 价格表：`global.trades`（`globalRegistry.js:2021+`），分类换算 `global.getConfiguredValue(value, kind)`（`crop/gem/wood/...`）
- 售价倍率来自**玩家属性**：`shippingbin:{crop,gem,meat,wood,sell}_sell_multiplier`（`config/attributefix.json:1257/1928/2698/3402/3457`；`kubejs/assets/shippingbin/lang/ko_kr.json`）
- 属性由**技能 stage** 授予：`global.addAttributesFromStages`（`globalServer.js:330`）、`checkAttributes.js` 读取 `player.nbt.Attributes`
- 价格 tooltip 链路：`addPriceTooltips.js` → `formatNumber(value, quality, doubled)`

### 2.5 任务（FTB Quests）
- 相关章节：`crops`（189 quest）、`drinks`（216）、`pantry`（48）、`abandoned_farm`（23）、`ii__building_up_the_farm`（308）、`iii__advanced_farming`（187）
- 任务标题走本地化键（`ftbquestlocalizer`），即已做过汉化流水线
- 存在 `society:dish` 标签（餐食集合，`handleItemBlockFluidTags.js:530`）

### 2.6 其它相关
- **SereneSeasons**：季节标签已接入（作物/树苗/温室玻璃，本仓库 `data/sereneseasons/tags/...`）
- **society** 模组：村民好感/贸易/畜牧（`animalBase.js` 好感度、`husbandryLoot.js`）；畜牧产物带品质
- 本仓库已给水牛接入畜牧（`data/society/tags/entity_types/*.json`）

---

## 3. 数值与平衡分析

### 3.1 三条"吃"轴的分工（避免重复奖励）

| 轴 | 系统 | 奖励维度 | 已有奖励 |
|---|---|---|---|
| 多样性 | SoLOnion | 吃**不同物品**的数量 | 生命（最高 +20）、力量、速度、护甲韧性 |
| 品质 | Quality Food | **单件**物品质量 | 售价倍率（×1.25~×3）、品质特效 |
| **均衡** | **GTMFO 营养（拟）** | **5 类别的覆盖/均衡** | **建议：经济 + 技能/任务（弱属性）** |

> 原则：营养**不要再给"生命/力量"**（与 SoLOnion 重叠）；它的独特性是"**类别**均衡"而非"**物品**多样性"。

### 3.2 阈值 / 衰减建议（基于 §1.2 数值）

设"每日衰减"与"阈值"两参数（建议做成 datapack/config，便于包侧调）：

| 项 | 建议初值 | 依据 |
|---|---|---|
| 单日衰减 | 每类 -1.0（每天结算一次） | 单件食物 ~0.9 → 需要持续吃才维持 |
| 均衡阈值 | 5 / 10 / 15 / 20（每类独立） | 20 大致对应"约 20 件对应类别食物"，与 SoLOnion 32 件窗口同量级 |
| 均衡度评分 | `min(5 类当前值)` 或 `Σmin(类别,阈值)` | 鼓励"补齐短板"而非堆单一类别 |
| 上限 | 每类 30 | 防止无限囤积 |

### 3.3 覆盖问题（核心决策）

- GTMFO 营养值只覆盖**本模组 156 种食物**；包里 ~400 个模组的大量食物**没有营养值**。
- 三个选项：
  - **(c) 只做 GTMFO 食物**（最低成本）：营养成为"用 GTMFO 内容"的激励——与"Gregification"包定位一致 ✅ 推荐起步
  - **(b) 标签自动映射**：按 `forge:milk→dairy`、`forge:vegetables→vegetable`、`forge:crops/*` 等**标签**推断营养（mod 侧或包侧），覆盖面广但对"复合菜"不精确
  - **(a) 数据驱动定义**：新增 JSON/datapack 层，包侧可给任意物品指定营养（最灵活，需 mod 侧开发）

### 3.4 与 SoLOnion 的叠加风险
- 若营养也给属性，玩家可同时吃满 SoLOnion（+20 血）+ 营养（属性）→ 数值膨胀。
- 建议营养的属性类奖励**只在小阈值**（如每类 ≥5 给 +1 心，封顶 +5），大头放经济与技能。

---

## 4. 推荐联动方案（B+C 混合）

### 方案 A：营养 → 属性（不单独推荐）
- 实现最容易（mod 侧即可），但与 SoLOnion 抢生态位。
- 仅作为**低阈值小加成**纳入。

### 方案 B：营养 → 经济（推荐之一）
- 思路：把"营养总值/均衡度"映射为售价加成，接入现有 **属性**体系（`shippingbin:sell_multiplier`），或对"营养丰富"的食物在 `global.trades` 定价上浮。
- 实现：包侧 KubeJS 读营养（需 mod 提供接口）→ 通过 `global.addAttributesFromStages` 同类机制写属性/或直接改价格表。
- 好处：与品质/多样性不冲突，符合"农场经营"主题。

### 方案 C：营养 → 技能与任务（推荐之一）
- **Puffish Skills 新分类 `nutrition`**：XP 源用 `puffish_skills:eat_food`（包内已验证可用），技能节点给"营养相关"perk（例：乳制品营养 +25%、均衡阈值 -2、每日衰减减半）。
- **FTB 任务线**：在 `pantry`/`crops` 章加"均衡饮食"任务（如"五类营养各 ≥10"），奖励可用 stage/属性与营养 perk 联动。
- 好处：完全复用包内既有框架，可玩性与引导性最好。

### 推荐组合
> **C 为主（技能+任务）+ B 为辅（经济加成）+ A 极小（每类 ≥5 各 +1 心，封顶 +5）**；
> 营养系统定位＝"**类别均衡**"，与 SoLOnion（物品多样性）、Quality Food（单件品质）形成三轴互补。

---

## 5. 技术实施路径（分阶段、每阶段可独立验证）

### 阶段 0：mod 侧必要修复（前置，约 1 个小轮次）
1. **修 `gain(5参)` 覆盖 → 累加**（`NutrientsTracker.java:24`）；保持 NBT 结构不变（旧存档兼容）。
2. **加衰减/日结算**：`tick()` 里按游戏日结算（参考包内 husbandry 的"日"计数模式），衰减值/上限走 config。
3. **营养值数据驱动（决策相关）**：新增 `data/gtmfo/nutrients/*.json`（或 `gtmfo:nutrient/*` 标签），**保留 `Foods.java` 硬编码为默认**，JSON 可覆盖/新增（这样包侧能覆盖非 GTMFO 食物）。
4. **外部接口**（关键）：至少提供只读访问，三选一或全给：
   - KubeJS 绑定：`GTMFO.nutrients(player)` 返回 `{dairy,fruit,...}`；
   - **Scoreboard 镜像**：`gtmfo_nutrient_dairy` 等只读 objective（FTB 任务/其它脚本可直接读）；
   - 命令增强：`/nutrient query <player>` 支持他人 + 机器可读输出。
5. *（可选）* 物品 tooltip 显示营养；需先有客户端同步。

### 阶段 1：包侧最小联动（KubeJS）
- 读营养值 → 写属性（经济，方案 B）与 `nutrition` 技能 XP（方案 C）。
- 用数据驱动营养（阶段 0.3）给若干**包内常用非 GTMFO 食物**补营养（如 `#forge:crops→vegetable` 批量）。

### 阶段 2：内容建设
- Puffish Skills `nutrition` 分类（category/experience/skills/definitions/connections 五个 json）。
- FTB 任务：`pantry` 章新增"均衡饮食"支线（依赖阶段 0 的 scoreboard/接口）。
- 平衡回测：模拟"日常饮食"→ 达到各阈值的所需天数。

### 阶段 3（可选）：可视化
- 客户端同步 + HUD（五格小图标）或食物 tooltip 显示"提供营养/当前均衡度"。

---

## 6. 风险与兼容

| 风险 | 说明 | 缓解 |
|---|---|---|
| 与 SoLOnion 奖励重叠 | 造成数值膨胀 | 营养不给大属性；只给经济/技能/小加成（§3.1） |
| 存档兼容 | `gain` 行为改变、NBT 结构 | 保持键名 `nutrients`；只改"累加语义"（旧值仍可读） |
| 死亡丢营养 | 目前未见 Clone 复制 | 阶段 0 明确：对齐 SoLOnion `resetOnDeath`（要么都重置，要么都保留） |
| 多人/客户端 | 无同步 → HUD 不可能 | 阶段 0.4 先给服务端接口；HUD 放阶段 3 |
| 性能 | 每日结算 × 在线玩家 | 结算放 `player.tick` 低频（每 X tick 检查日切），或 `SleepFinishedTimeEvent`/日切事件 |
| KubeJS 顺序 | 属性/技能写入需在 `globalRegistry` 之后 | 沿用包内 `priority` 规律（参考 R2 的 `-30` 教训） |
| 任务书改动 | 包内任务书已汉化、ID 有约束（历史事故：ID 溢出） | 新任务用现成流水线生成；不改既有章节 key |
| 覆盖非 GTMFO 食物 | 可能与包内平衡/其它模组冲突 | 先只在包内显式清单内加，不做全量自动 |

---

## 7. 测试清单

1. **静态**：`node --check` 所有新 KubeJS；JSON/tag 语法校验；`gradlew compileJava`（mod 侧）。
2. **单机**：`/nutrient gain` 与吃食物 → 值**累加**正确；跨日衰减；死亡/重生行为；`nutrition` 技能 XP 增长；任务可完成。
3. **服务器**：多人并发吃食物/结算无异常；scoreboard 镜像数值与命令一致；无客户端不同步报错。
4. **回归**：SoLOnion 阈值奖励与营养奖励**可叠加但不叠加同一属性**；品质价格计算不受影响。

---

## 8. 待用户决策（影响工作量）

1. **奖励形态**：接受"经济 + 技能/任务为主、属性为辅"（推荐），还是要更强/更弱的属性向？
2. **覆盖范围**：(c) 只 GTMFO 食物 / (b) 标签映射 / (a) 数据驱动自定义——选哪个起步？
3. **阈值与衰减**：接受 §3.2 初值（衰减 1.0/日、阈值 5/10/15/20、上限 30）？还是由 datapack 交给包侧调？
4. **死亡处理**：营养**重置**（对齐 SoLOnion）还是**保留**？
5. **是否需要 HUD**（若需要 → 阶段 3 必做，需同步包）。

> 决策确定后即可按 §5 阶段 0 开工（一个小轮次、可单独验证）。
