# GTMFO 营养系统 × Sunlit Valley 联动分析

> 文档性质：**Sunlit Valley 包侧联动分析与实施建议**
>
> 状态日期：2026-09-24
>
> 当前结论：GTMFO 模组侧基础系统已实现；Sunlit Valley 的技能、任务、经济和其他饮食系统联动尚未接线。
>
> 权威功能说明见 [`NUTRIENT_SYSTEM.md`](NUTRIENT_SYSTEM.md)；其他 AI 接手时先读 [`NUTRIENT_AI_HANDOFF.md`](NUTRIENT_AI_HANDOFF.md)。

---

## 1. 结论摘要

1. GTMFO 已实现五类营养的累计、衰减、死亡处理、奖励、HUD、tooltip、外部读取以及 KubeJS 逐物品精确定义。
2. Sunlit Valley 已有两条成熟的饮食轴：SoLOnion 负责“吃过多少种食物”，Quality Food 负责“单件食物品质”。
3. GTMFO 应作为第三条“类别均衡”轴，不应复制 SoLOnion 的大额生命、力量和速度奖励。
4. 当前只有**接口层兼容**，没有直接联动代码：SoLOnion、Quality Food、Puffish Skills、FTB Quests 和 ShippingBin 都未读取 GTMFO 营养值。
5. 推荐包侧先完成常用食物营养表和任务引导，再考虑技能与经济奖励；不要一开始同时引入多套高额属性奖励。

---

## 2. 调研范围与时效

### GTMFO 仓库

```text
H:\MinecraftMods\GregTechModernFoodOption
```

当前实现以提交 `651aba1` 及其之前的营养提交为依据。

### Sunlit Valley 调研快照

```text
G:\MinecraftGames\Sunlit Valley(BaopuEdition)\.minecraft\versions\Society Sunlit Valley
```

本报告中的整合包文件名、配置值和脚本位置来自 2026-09-24 的本地工作区快照。另一个 AI 在实际修改整合包前，应重新确认路径、模组版本和脚本结构没有变化。

---

## 3. GTMFO 模组侧当前能力

### 3.1 玩家状态

固定五类营养：

```text
dairy / fruit / grain / protein / vegetable
```

默认规则：

- 单类上限 `30`；
- 每游戏日每类衰减 `1`；
- 死亡重置；
- 每类达到 `5` 增加一颗心；
- 总生命加成最多五颗心；
- 五类全部达标时可配置一个均衡效果，但默认关闭；
- 没有营养不足 debuff。

系统默认关闭，需要整合包显式开启。准确配置见 `NUTRIENT_SYSTEM.md`。

### 3.2 食物定义

目前有三种来源：

1. GTMFO 食物内置值；
2. `gtmfo:nutrient/<name>` 标签提供统一 `tagValue`；
3. KubeJS `GTMFO.nutrients.add/addMany/addAll` 提供逐物品、逐类别精确值。

KubeJS 显式类别覆盖该类别的内置值和标签值；显式 `0` 可以关闭该类别。普通标准可食用物品会在完成进食时实际累计，而不是只显示 tooltip。

### 3.3 包侧可读取的数据

精确小数值：

```js
player.persistentData.getFloat("gtmfo_nutrient_dairy")
player.persistentData.getFloat("gtmfo_nutrient_fruit")
player.persistentData.getFloat("gtmfo_nutrient_grain")
player.persistentData.getFloat("gtmfo_nutrient_protein")
player.persistentData.getFloat("gtmfo_nutrient_vegetable")
```

整数记分板镜像：

```text
gtmfo_dairy
gtmfo_fruit
gtmfo_grain
gtmfo_protein
gtmfo_vegetable
```

这两类镜像都依赖 `scoreboardMirror: true`。

### 3.4 已验证和未验证

编译、资源处理、打包和 JAR 内容检查已经通过。尚未进行完整游戏内联调，尤其是 KubeJS 重载、多人后加入、显式 `0` 和普通外部食物累计场景。详见 `NUTRIENT_SYSTEM.md` §12。

---

## 4. Sunlit Valley 现有饮食系统

## 4.1 SoLOnion：食物多样性

调研快照：

- 模组文件：`mods/SoLOnion_FORGE_v1.4.5_mc1.20.1.jar`；
- 配置：`config/solonion.json`；
- 记录最近进食中的不同食物；
- `resetOnDeath: true`；
- 阈值奖励包含最大生命、力量、速度和护甲韧性；
- 包内移除了午餐盒类容器，但保留多样性机制。

**与 GTMFO 当前关系：没有直接代码联动。**

二者计算维度不同：

- SoLOnion 关心物品种类数量；
- GTMFO 关心五类营养是否覆盖和均衡。

死亡都默认重置只是配置设计一致，不代表存在 API 联动。

## 4.2 Quality Food：单件品质

调研快照：

- 模组文件：`mods/quality_food-1.20.1-2.4.3-all.jar`；
- 品质存储于物品 NBT；
- 作物和畜牧产出可携带品质；
- 品质会影响售价倍率和品质效果；
- 包内有品质清洗机器和材料白名单/黑名单。

**与 GTMFO 当前关系：没有直接代码联动。**

当前：

- 品质不会放大营养摄入；
- 营养不会改变物品品质；
- 营养均衡不会自动影响 Quality Food 的价格计算。

## 4.3 Puffish Skills

包内已有多个技能分类，并已在 farming 经验配置中使用 `puffish_skills:eat_food` 事件源。这说明它适合作为营养玩法的任务与成长载体。

**当前尚未实现：**

- `nutrition` 分类；
- 读取 GTMFO 五类营养的技能条件；
- 营养吸收、阈值或衰减相关 perk；
- 吃特定营养类别食物获得的专属 XP。

## 4.4 FTB Quests

包内已有 crops、drinks、pantry、farming 等相关章节，也有本地化流水线。

**当前尚未实现：**

- 五类营养各达到某阈值的观察任务；
- 均衡饮食教程；
- 与 Puffish Skills 或 stage 联动的营养任务奖励。

## 4.5 ShippingBin 与经济

包内已有 `shippingbin:*_sell_multiplier` 属性和技能 stage 授予属性的机制，Quality Food 也已经参与售价计算。

**当前尚未实现：**

- 根据玩家营养均衡度调整售价；
- 根据食物营养密度调整基础售价；
- GTMFO 营养值与现有品质倍率的组合公式。

---

## 5. 三条饮食轴的推荐分工

| 轴 | 系统 | 回答的问题 | 推荐奖励 |
|---|---|---|---|
| 多样性 | SoLOnion | 吃过多少种不同食物 | 现有属性奖励 |
| 品质 | Quality Food | 这一件食物品质如何 | 品质效果与售价倍率 |
| 类别均衡 | GTMFO | 乳、果、谷、蛋白、蔬菜是否均衡 | 小额内置生命 + 任务/技能/经济 |

设计原则：

- 保留 GTMFO 默认的小额生命奖励即可；
- 不再给营养系统叠加大额力量、速度或最大生命；
- 让包侧奖励重点落在教程、任务、技能成长和适度经济反馈；
- 用五类最低值体现“短板”，避免只堆一种营养。

---

## 6. 推荐的包侧实施顺序

### 阶段 A：最小可玩闭环

1. 在整合包配置中开启 GTMFO 营养；
2. 用 `GTMFO.nutrients.addMany()` 给常用主食、作物、肉类、乳制品和复合菜配置精确值；
3. 标签只用于大量同质原料的快速兜底，复合菜优先写精确值；
4. 增加一条简单任务或说明，引导玩家观察 HUD 与 tooltip；
5. 实测一天饮食后五类增长和衰减速度。

### 阶段 B：任务联动

1. 在 pantry 或 crops 章节加入均衡饮食支线；
2. 使用记分板观察任务检查整数阈值，或使用 KubeJS 读取 `persistentData` 棏查小数；
3. 建议里程碑从五类各 `5` 开始，不要直接要求 `20` 或 `30`；
4. 奖励优先用物品、stage、技能 XP 或配方解锁，避免再次大量加属性。

### 阶段 C：技能联动

可选方案：

- 新增 `nutrition` 分类；
- 吃 `#society:dish` 获得基础经验；
- 五类全部达到阈值时周期性授予少量 XP；
- perk 可减少衰减或调整包侧奖励，但当前 GTMFO 没有直接提供“修改单类吸收倍率”的公开脚本 API，若要做此类 perk 需额外开发。

### 阶段 D：经济联动

建议先选一种，避免双重奖励：

- 玩家均衡度给很小的全局售价倍率；或
- 营养丰富的特定商品提高基础价格。

不要同时把 Quality Food 高品质倍率、技能倍率和营养倍率都设计得很高。

---

## 7. 包侧数值建议

GTMFO 内置食物的历史统计约为每个非零类别 `0.9`/件，单件最高约 `3`。可先使用以下量级：

| 食物类型 | 建议值 |
|---|---|
| 单一原料 | 主要类别 `0.5–1.0` |
| 加工原料 | 主要类别 `0.75–1.5` |
| 普通复合菜 | 两到三类各 `0.5–1.5` |
| 丰盛主菜 | 三到五类，总值约 `3–6` |
| 特殊高级食物 | 可略高，但单类通常不超过 `3` |

平衡原则：

- 不要因为食物恢复饥饿值高，就机械地给所有类别都很高；
- 蔬菜汤等复合菜可覆盖多个类别，但每类应低于纯对应原料的极端值；
- 用实际配方组成推导类别，而不是仅按物品名称猜测；
- 显式 `0` 用于修正错误标签或屏蔽 GTMFO 内置类别；
- 完成第一批定义后，用正常玩家一到三个游戏日的菜单回测。

---

## 8. 建议的均衡度公式

包侧若需要把五类压缩成一个指标，推荐优先使用最低值：

```js
const names = ["dairy", "fruit", "grain", "protein", "vegetable"];
const values = names.map(name =>
  player.persistentData.getFloat("gtmfo_nutrient_" + name)
);
const balance = Math.min(...values);
```

理由：

- 鼓励补齐短板；
- 不会因单一类别堆到上限而掩盖缺失类别；
- 与“五类全部达标”的内置奖励逻辑一致。

若需要 `0–1` 评分，可按目标阈值归一化：

```js
const target = 10.0;
const score = Math.min(1.0, balance / target);
```

这只是包侧建议，不是模组当前提供的内置字段。

---

## 9. 风险和限制

| 风险 | 当前情况 | 建议 |
|---|---|---|
| 与 SoLOnion 属性叠加 | GTMFO 默认最多再加五颗心 | 保持小额，不新增同类大属性奖励 |
| Quality Food 价格膨胀 | 已有最高倍率和技能增强 | 营养经济倍率保持很小或只做固定任务奖励 |
| 普通外部食物覆盖不足 | 模组提供 API，但包侧尚未建表 | 先做高频食物清单，不要盲目全标签映射 |
| 特殊食用机制 | 不一定触发标准完成事件 | 对蛋糕、方块食物和特殊模组逐项测试 |
| KubeJS 重载时机 | 在线时通常一秒内提交 | 重载后等待并重新查看 tooltip，再测试进食 |
| JEI 展示 | 外部 KubeJS 食物不会全部进入动态 JEI 信息页 | 以物品 tooltip 为准 |
| 多人协议 | GTMFO 网络协议严格匹配 | 客户端和服务端安装同版本 |
| 运行时验证不足 | 当前只完成自动构建验证 | 实际整合包接入前执行 §10 清单 |

---

## 10. 下一轮实际联调清单

### 模组功能

1. 开启 `gtfoNutrientConfig.enabled`；
2. 给两种普通外部食物设置不同小数值；
3. 验证 tooltip 与实际累计一致；
4. 验证显式 `0` 屏蔽内置值或标签值；
5. 验证未覆盖类别仍使用内置值与标签值；
6. 验证 GTMFO 自带食物只累计一次；
7. `/reload` 后删除、修改、增加定义并再次检查；
8. 第二个客户端后加入，检查 HUD 和 tooltip；
9. 跨日检查衰减，死亡检查重置；
10. 测试蛋糕和包内特殊食物。

### Sunlit Valley 回归

1. SoLOnion 多样性仍正常记录；
2. Quality Food 品质和价格倍率不变；
3. ShippingBin 原价格链不受影响；
4. Puffish Skills 原 `eat_food` 经验正常；
5. FTB Quests 原章节与本地化正常；
6. 多人服务器无网络协议或脚本错误。

---

## 11. 历史调查结论与当前状态对照

最初调查曾发现以下问题；它们是开发前状态，现已处理：

| 历史问题 | 当前状态 |
|---|---|
| 五参数 `gain` 覆盖而非累加 | 已修复为正确累计 |
| 没有每日衰减 | 已实现按游戏日衰减 |
| 没有死亡处理 | 已实现可配置重置或保留 |
| 没有客户端同步/HUD | 已实现玩家数值同步和 HUD |
| 没有食物营养 tooltip | 已实现 |
| 包侧无法读取玩家营养 | 已提供 `persistentData` 和记分板镜像 |
| 外部普通食物标签只显示、不实际累计 | 已通过普通进食完成事件接入 |
| 不同食物只能共享一个标签值 | 已提供 KubeJS 逐物品、逐类别精确值 |
| KubeJS 定义客户端不可见 | 已提供定义快照同步 |

因此，不应再引用“GTMFO 营养系统仍是只记录、无衰减、无同步的半成品”作为当前结论。

---

## 12. 相关实现提交

| 提交 | 内容 |
|---|---|
| `def246f` | 累计、衰减、死亡、配置、生命收益、标签、记分板和 JEI 接线 |
| `d17a03d` | 食物 tooltip 和中英文营养名称 |
| `77c2055` | SimpleChannel 同步、客户端缓存和 HUD |
| `159237e` | 玩家 `persistentData` 镜像 |
| `a93f098` | 初版系统与联动文档 |
| `651aba1` | KubeJS 逐物品精确值、普通食物应用、定义同步和 tooltip 统一解析 |
