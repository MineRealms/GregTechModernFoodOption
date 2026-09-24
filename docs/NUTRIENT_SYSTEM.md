# 营养系统说明（Nutrient System）

> 文档性质：**当前实现的权威使用说明**
>
> 适用版本：GTMFO `0.0.9`（含提交 `651aba1`）
>
> 整理日期：2026-09-24
>
> Sunlit Valley 联动分析见 [`NUTRIENT_INTEGRATION_ANALYSIS.md`](NUTRIENT_INTEGRATION_ANALYSIS.md)；交给其他 AI 时先阅读 [`NUTRIENT_AI_HANDOFF.md`](NUTRIENT_AI_HANDOFF.md)。

---

## 1. 当前状态

模组侧营养系统已经具备完整的基础闭环：

- 玩家进食后累积五类营养；
- 每日衰减、单类上限、死亡重置；
- 最大生命收益和可选的均衡效果；
- HUD、食物 tooltip、命令、记分板和 `persistentData`；
- GTMFO 内置食物、物品标签和 KubeJS 逐物品精确值；
- 服务端权威计算，玩家数值和 KubeJS 定义同步到客户端；
- KubeJS 脚本重载后重建定义，后加入玩家会收到当前快照。

**系统默认关闭**。需要在 `config/gtmfo.yaml` 中启用：

```yaml
gtfoNutrientConfig:
  enabled: true
```

旧配置键 `devConfigs.nutrientMode` 仍兼容；新旧开关任意一个为 `true` 即启用。

---

## 2. 五类营养

| 脚本名称 | 中文含义 |
|---|---|
| `dairy` | 乳制品 |
| `fruit` | 水果 |
| `grain` | 谷物 |
| `protein` | 蛋白质 |
| `vegetable` | 蔬菜 |

名称来源为 `Nutrients.LIST`，KubeJS 注册时不支持额外的自定义营养类别。

---

## 3. 食物营养值来源与优先级

营养值有三种来源：

| 来源 | 用途 |
|---|---|
| GTMFO 内置值 | `Foods.java` 中 GTMFO 自带食物的默认数值 |
| KubeJS 逐物品值 | 为任意已注册物品按类别指定精确非负浮点数 |
| `gtmfo:nutrient/<name>` 物品标签 | 以全局 `tagValue` 为普通食物快速补类别值 |

优先级按**物品 + 单个类别**计算：

1. KubeJS 显式定义该类别时，使用脚本值，并替换该类别的内置值和标签值；
2. 没有脚本定义时，使用内置值与标签值之和；
3. 显式值 `0` 有意义：它会关闭该物品对应类别的内置值和标签值。

示例：某食物内置 `protein: 1`，同时命中蛋白质标签（默认 `+1`）：

- 未写 KubeJS 定义：最终 `protein: 2`；
- KubeJS 写 `protein: 2.5`：最终 `protein: 2.5`；
- KubeJS 写 `protein: 0`：最终 `protein: 0`。

玩家实际存储值仍受 `cap` 限制，默认每类最高 `30`。

---

## 4. KubeJS 逐物品 API

API 只注入服务端脚本环境，应放在 `kubejs/server_scripts/`。

### 4.1 单个注册

```js
GTMFO.nutrients.add("farmersdelight:beef_patty", {
  protein: 2.5,
  grain: 0.5
});
```

### 4.2 批量注册

```js
GTMFO.nutrients.addMany({
  "minecraft:bread": {
    grain: 1.5
  },
  "minecraft:apple": {
    fruit: 1.0
  },
  "farmersdelight:mixed_salad": {
    vegetable: 1.5,
    fruit: 0.5,
    protein: 0.25
  }
});
```

`addAll()` 是 `addMany()` 的别名：

```js
GTMFO.nutrients.addAll({
  "minecraft:baked_potato": { vegetable: 1.0 }
});
```

### 4.3 合并与覆盖规则

- 同一物品可多次注册；不同类别会合并；
- 同一物品、同一类别重复注册时，最后一次值获胜；
- 只写一个类别不会清除其他类别的内置值、标签值或较早脚本定义；
- 每次 KubeJS 服务端脚本重载会先清空旧 generation，再由当前脚本重新建立定义。

### 4.4 输入校验

注册要求：

- 物品 ID 必须是合法 `ResourceLocation`，并且物品已存在于注册表；
- 营养名称必须是五种固定名称之一；
- 数值必须是有限、非负的数字；允许小数和 `0`；
- `add()` 的非法定义不会写入；
- `addMany()` / `addAll()` 会先验证整批数据，其中一项非法时，该次批量调用整体不写入；
- 非法输入会写入服务端日志警告。

### 4.5 重载生效时间

- KubeJS 重载开始时清空 staging 定义；
- 当前实现由在线玩家的每秒 tick 提交新快照，因此有在线玩家时通常在一秒内生效；
- 没有在线玩家时，提交可能延迟到玩家开始 tick；
- 提交后会向在线客户端广播定义；玩家登录及 datapack 同步时也会收到快照。

---

## 5. 标签方式

物品标签名称：

```text
gtmfo:nutrient/dairy
gtmfo:nutrient/fruit
gtmfo:nutrient/grain
gtmfo:nutrient/protein
gtmfo:nutrient/vegetable
```

KubeJS 示例：

```js
ServerEvents.tags("item", event => {
  event.add("gtmfo:nutrient/dairy", [
    "farmersdelight:milk_bottle",
    "farm_and_charm:butter"
  ]);

  event.add("gtmfo:nutrient/vegetable", [
    "farmersdelight:tomato",
    "farm_and_charm:lettuce"
  ]);
});
```

每命中一个类别标签会增加该类别的 `tagValue`（默认 `1.0`），但只在 KubeJS 没有显式覆盖该类别时生效。

---

## 6. 哪些食物会实际生效

### 已覆盖

- GTMFO 自带食物；
- 使用标准 `ItemStack` 食物属性、完成普通进食流程的其他模组食物；
- 上述食物的 KubeJS 精确值和营养标签；
- GTMFO 组件食物使用自身完成逻辑，普通食物使用 Forge `LivingEntityUseItemEvent.Finish`，两条路径已排除重复累计。

### 不保证自动覆盖

- 蛋糕方块一类通过方块交互食用的内容；
- 完全绕过标准物品使用完成事件的自定义消费机制；
- 不是可食用物品、只是在脚本中注册了营养值的普通物品。

这类特殊消费路径需要对应模组或整合包显式调用营养逻辑。

---

## 7. 配置项

配置位置：`config/gtmfo.yaml` → `gtfoNutrientConfig`。

| 键 | 默认值 | 说明 |
|---|---:|---|
| `enabled` | `false` | 营养系统总开关 |
| `cap` | `30.0` | 单类存储上限 |
| `decayPerDay` | `1.0` | 每游戏日衰减；`0` 表示不衰减 |
| `tagValue` | `1.0` | 每个营养类别标签提供的值 |
| `resetOnDeath` | `true` | 死亡时是否清空营养 |
| `benefitThreshold` | `5.0` | 单类收益阈值；`0` 关闭内置属性收益 |
| `healthPerNutrient` | `2.0` | 每个达标类别增加的最大生命；`2.0` 为一颗心 |
| `healthBonusCap` | `10.0` | 营养生命加成总上限；`10.0` 为五颗心 |
| `balancedEffect` | `""` | 五类全部达标时维持的效果 ID；空字符串关闭 |
| `balancedEffectAmplifier` | `0` | 均衡效果等级；`0` 为 I 级 |
| `scoreboardMirror` | `true` | 镜像到记分板和玩家 `persistentData` |
| `hud` | `true` | 是否显示客户端 HUD |
| `foodTooltips` | `true` | 是否显示食物营养 tooltip |

---

## 8. 不均衡时会发生什么

当前实现采用“达到阈值获得奖励”，**没有营养不足 debuff**。

默认配置下：

- 每个达到 `5` 的类别增加 `2` 最大生命，即一颗心；
- 五类都达标时最多增加 `10` 最大生命，即五颗心；
- 某类因衰减跌破阈值，会失去该类别对应的最大生命加成；
- 五类全部达标时可以维持 `balancedEffect`；默认值为空，所以默认没有额外效果；
- 每个游戏日每类默认减少 `1`；
- 死亡默认重置全部营养。

因此“饮食不均衡”当前只表示拿不到缺失类别对应的正向收益，不会自动获得虚弱、饥饿、中毒等负面状态。

---

## 9. 对外读取接口

### 9.1 玩家 persistentData

当 `scoreboardMirror` 开启时，每秒镜像：

```js
const dairy = player.persistentData.getFloat("gtmfo_nutrient_dairy");
const fruit = player.persistentData.getFloat("gtmfo_nutrient_fruit");
const grain = player.persistentData.getFloat("gtmfo_nutrient_grain");
const protein = player.persistentData.getFloat("gtmfo_nutrient_protein");
const vegetable = player.persistentData.getFloat("gtmfo_nutrient_vegetable");
```

### 9.2 记分板

只读镜像目标：

```text
gtmfo_dairy
gtmfo_fruit
gtmfo_grain
gtmfo_protein
gtmfo_vegetable
```

记分板是整数镜像；需要精确小数时应读取 `persistentData`。

### 9.3 命令

```text
/nutrient query
/nutrient clear [name]
/nutrient gain <name> <amount>
```

`clear` 和 `gain` 用于管理员调试或剧情发放。

### 9.4 显示接口

- HUD：显示玩家当前五类营养；
- Tooltip：显示该食物实际解析后的营养值，包括客户端同步的 KubeJS 定义；
- JEI：现有 GTMFO 食物信息分类可显示内置 `INutrients` 数据，但不会动态为所有外部 KubeJS 食物创建 JEI 条目；外部食物以普通 tooltip 为准。

---

## 10. 多人和客户端兼容

- 营养累计、衰减和奖励由服务端权威计算；
- 玩家当前值和逐物品定义通过 Forge `SimpleChannel` 同步；
- 当前网络协议版本为 `2`，客户端和服务端校验使用严格的版本相等规则；
- 因此应按**客户端与服务端安装同版本 GTMFO**进行部署，不应将当前版本当成纯服务端可选模组；
- 客户端退出世界时会清空玩家营养缓存和物品定义缓存，避免跨服务器残留。

---

## 11. 与 Sunlit Valley 的当前关系

GTMFO 已提供联动所需的输入与读取接口，但 Sunlit Valley 包侧尚未接线：

- SoLOnion：没有读取或修改其多样性数据；
- Quality Food：品质不会自动放大营养，营养也不会自动影响品质；
- Puffish Skills：尚未新增正式 `nutrition` 分类或营养技能；
- FTB Quests：尚未添加营养任务线；
- ShippingBin：尚未根据营养均衡度调整售价。

当前定位是三条互补轴：

1. SoLOnion：吃过多少种不同食物；
2. Quality Food：单件食物的品质；
3. GTMFO：乳、果、谷、蛋白、蔬菜是否均衡。

具体包侧方案和风险见 [`NUTRIENT_INTEGRATION_ANALYSIS.md`](NUTRIENT_INTEGRATION_ANALYSIS.md)。

---

## 12. 验证状态与已知边界

### 已完成

- `./gradlew compileJava`：成功；
- `./gradlew compileJava jar`：成功；
- `./gradlew processResources jar`：成功；
- JAR 已确认包含 KubeJS 插件入口、逐物品注册表和定义同步包；
- `git diff --check`：通过；
- 实现提交：`651aba1 feat(nutrient): add KubeJS per-item food values`。

### 尚未完成的游戏内联调

- 实际 KubeJS `server_scripts` 的单个/批量注册；
- 两个普通模组食物获得不同小数值；
- 显式 `0` 与标签、内置值的覆盖关系；
- `/reload` 后进食结果和 tooltip 同步变化；
- 第二客户端后加入时收到当前定义；
- GTMFO 自带食物只累计一次；
- 特殊消费路径的兼容性。

编译与打包成功不等于上述运行时场景已经完成验证。
