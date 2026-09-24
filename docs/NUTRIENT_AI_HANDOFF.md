# GTMFO 营养系统：AI 分析交接入口

> 用途：把本文件连同下面列出的文档路径交给另一个 AI，作为后续分析或 Sunlit Valley 包侧联动的起点。
>
> 状态日期：2026-09-24

---

## 1. 建议阅读顺序

### 第一份：当前功能事实

```text
H:\MinecraftMods\GregTechModernFoodOption\docs\NUTRIENT_SYSTEM.md
```

这份是当前实现的权威说明，包含：

- 五类营养与默认配置；
- KubeJS `GTMFO.nutrients.add/addMany/addAll`；
- 内置值、标签值和脚本值的优先级；
- 玩家奖励、不均衡时的实际结果；
- `persistentData`、记分板、命令、HUD、tooltip、JEI；
- 多人同步、兼容边界和验证状态。

分析当前能力时，以这份文档和源码为准。

### 第二份：Sunlit Valley 联动分析

```text
H:\MinecraftMods\GregTechModernFoodOption\docs\NUTRIENT_INTEGRATION_ANALYSIS.md
```

这份包含：

- SoLOnion、Quality Food、Puffish Skills、FTB Quests 和 ShippingBin 的关系；
- 当前已联动与未联动边界；
- 三条饮食轴的职责划分；
- 包侧实施顺序、数值建议和联调清单；
- 开发前历史问题与当前状态对照。

### 第三份：项目整体移植记录

```text
H:\MinecraftMods\GregTechModernFoodOption\PORTING_TODO.md
```

只需重点查看“营养系统”相关章节，用于理解项目历史和已完成提交。它不是营养系统 API 的权威文档。

---

## 2. 当前准确结论

- GTMFO 模组侧营养基础系统已经实现，不再是“只记录、无衰减、无同步”的半成品；
- 固定五类为 `dairy / fruit / grain / protein / vegetable`；
- KubeJS 可以为任意已注册物品按类别提供非负精确值；
- 显式 `0` 可以屏蔽某类别的内置值和标签值；
- 普通标准可食用物品会在完成进食时实际累计；
- 服务端定义会同步给客户端，用于一致的 tooltip；
- 当前没有营养不足 debuff，不均衡只会拿不到缺失类别的正向收益；
- 系统默认关闭，需要整合包配置启用；
- Sunlit Valley 包侧联动尚未实现，目前只有可供读取和定义的接口。

---

## 3. Sunlit Valley 当前没有实现的内容

不要把以下内容误认为已经存在：

- SoLOnion 多样性与 GTMFO 营养值互相转换；
- Quality Food 品质影响营养吸收；
- GTMFO 营养影响 Quality Food 品质或现有售价计算；
- Puffish Skills 的 `nutrition` 分类；
- FTB Quests 的营养任务线；
- ShippingBin 根据营养均衡度调整售价；
- 全量 Sunlit Valley 食物营养定义表。

---

## 4. 关键源码入口

### 固定营养类别与玩家状态

```text
H:\MinecraftMods\GregTechModernFoodOption\src\main\java\com\ironsword\gtmfo\api\capability\Nutrients.java
H:\MinecraftMods\GregTechModernFoodOption\src\main\java\com\ironsword\gtmfo\api\capability\NutrientsTracker.java
```

### 配置与奖励

```text
H:\MinecraftMods\GregTechModernFoodOption\src\main\java\com\ironsword\gtmfo\GTMFOConfigHolder.java
H:\MinecraftMods\GregTechModernFoodOption\src\main\java\com\ironsword\gtmfo\common\nutrient\NutrientEffects.java
```

### 标签、KubeJS 定义与解析规则

```text
H:\MinecraftMods\GregTechModernFoodOption\src\main\java\com\ironsword\gtmfo\common\nutrient\NutrientTags.java
H:\MinecraftMods\GregTechModernFoodOption\src\main\java\com\ironsword\gtmfo\common\nutrient\NutrientDefinitionRegistry.java
H:\MinecraftMods\GregTechModernFoodOption\src\main\java\com\ironsword\gtmfo\integration\kubejs\GTMFOKubeJSPlugin.java
H:\MinecraftMods\GregTechModernFoodOption\src\main\resources\kubejs.plugins.txt
```

### 进食事件、玩家 tick 和 tooltip

```text
H:\MinecraftMods\GregTechModernFoodOption\src\main\java\com\ironsword\gtmfo\forge\ForgeCommonEventListener.java
H:\MinecraftMods\GregTechModernFoodOption\src\main\java\com\ironsword\gtmfo\api\item\component\GTMFOFoodStats.java
```

### 网络同步与客户端缓存

```text
H:\MinecraftMods\GregTechModernFoodOption\src\main\java\com\ironsword\gtmfo\network\NutrientsNetwork.java
H:\MinecraftMods\GregTechModernFoodOption\src\main\java\com\ironsword\gtmfo\network\NutrientSyncPacket.java
H:\MinecraftMods\GregTechModernFoodOption\src\main\java\com\ironsword\gtmfo\network\NutrientDefinitionSyncPacket.java
H:\MinecraftMods\GregTechModernFoodOption\src\main\java\com\ironsword\gtmfo\client\nutrient\ClientNutrientCache.java
H:\MinecraftMods\GregTechModernFoodOption\src\main\java\com\ironsword\gtmfo\client\nutrient\NutrientHudOverlay.java
```

---

## 5. 已完成的提交

```text
def246f  营养累计、衰减、死亡、配置、奖励、标签和镜像
d17a03d  食物 tooltip 与语言条目
77c2055  客户端同步、缓存和 HUD
159237e  玩家 persistentData 镜像
a93f098  初版营养系统文档
651aba1  KubeJS 逐物品值、普通食物累计、定义同步与统一解析
```

---

## 6. 已知边界

- 只自动覆盖标准物品进食完成路径；蛋糕、方块食物和特殊自定义消费需要单独测试；
- 外部 KubeJS 食物主要通过普通 tooltip 展示，JEI 不会动态创建所有外部定义；
- KubeJS 重载后的定义由在线玩家每秒 tick 提交，通常存在不超过约一秒的延迟；
- 没有在线玩家时，提交可能延迟到玩家开始 tick；
- 玩家登录和 datapack 同步可能重复发送定义快照，但当前不影响结果；
- 网络协议严格匹配，客户端和服务端应安装同版本 GTMFO；
- 版本号仍是 `0.0.9`；
- 尚未新增自动化 GameTest/JUnit。

---

## 7. 验证状态

已通过：

```text
./gradlew compileJava
./gradlew compileJava jar
./gradlew processResources jar
git diff --check
```

尚未完成：

- 实际 Minecraft 客户端/专用服务器启动联调；
- 实际 KubeJS 单个和批量注册；
- 显式 `0`、标签回退与内置值覆盖；
- `/reload` 后定义、tooltip 和实际累计同步；
- 第二客户端后加入；
- GTMFO 自带食物只累计一次；
- Sunlit Valley 整合包回归。

---

## 8. 推荐给下一个 AI 的任务描述

可以直接使用下面这段：

> 先阅读 `NUTRIENT_AI_HANDOFF.md`、`NUTRIENT_SYSTEM.md` 和 `NUTRIENT_INTEGRATION_ANALYSIS.md`。不要重新把模组侧营养系统判断为半成品。请分析 Sunlit Valley 当前食物、任务、技能和经济脚本，提出或实施包侧联动；明确区分已实现功能、建议方案和实际验证结果。修改前重新核对整合包当前路径与文件版本。优先完成高频食物的营养定义表和游戏内联调，不要同时叠加过高的 SoLOnion、Quality Food 与营养属性/经济奖励。
