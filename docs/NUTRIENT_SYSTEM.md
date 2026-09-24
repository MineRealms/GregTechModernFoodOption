# 营养系统说明（Nutrient System）

> 版本：gtmfo-0.0.9+　状态：可用（**默认关闭**，需在配置中开启）
> 相关：调研与联动分析见 `docs/NUTRIENT_INTEGRATION_ANALYSIS.md`

---

## 1. 概述

玩家通过进食累积五类营养：**dairy（乳）/ fruit（果）/ grain（谷）/ protein（蛋白）/ vegetable（菜）**。

- **累积**：吃食物 +（食物内置值或标签值），单类上限 `cap`（默认 30）
- **衰减**：每个游戏日 -`decayPerDay`（默认 1.0）；离线时间不叠加（只按观察到的日切结算一次）
- **死亡**：默认重置（`resetOnDeath: true`，与包内 SoLOnion 的 `resetOnDeath` 对齐）
- **收益**：弱属性 + 可选均衡效果（详见 §4）

系统 **默认关闭**：`gtfoNutrientConfig.enabled = false`（旧键 `devConfigs.nutrientMode` 仍兼容，二者任一为 true 即启用）。

---

## 2. 数值来源

| 来源 | 说明 |
|---|---|
| GTMFO 内置 | `Foods.java` 中 156 种食物带内置营养值（均值 ~0.9/件，单件最高 3.0） |
| **物品标签** | 任意食物加入 `gtmfo:nutrient/<name>` 标签 → 每命中一个标签 +`tagValue`（默认 1.0）。用于覆盖**非 GTMFO 食物**（FD、F&C、Pam…） |

标签示例（KubeJS，`server_scripts`）：

```js
ServerEvents.tags("item", (e) => {
  // 乳制品
  ["farmersdelight:milk_bottle", "farm_and_charm:butter"].forEach((id) => e.add("gtmfo:nutrient/dairy", id));
  // 蔬菜
  ["farmersdelight:tomato", "farm_and_charm:lettuce"].forEach((id) => e.add("gtmfo:nutrient/vegetable", id));
});
```

> 注意：标签只影响**进食时的写入**；已有存档的数值不受影响。

---

## 3. 配置（`config/gtmfo.yaml` → `gtfoNutrientConfig`）

| 键 | 默认 | 说明 |
|---|---|---|
| `enabled` | `false` | 总开关 |
| `cap` | `30.0` | 单类数值上限 |
| `decayPerDay` | `1.0` | 每日衰减；0 = 不衰减 |
| `tagValue` | `1.0` | 标签命中时每类加值 |
| `resetOnDeath` | `true` | 死亡是否清空 |
| `benefitThreshold` | `5.0` | 收益阈值；**0 = 关闭属性收益**（给包侧自定义奖励留空间） |
| `healthPerNutrient` | `2.0` | 每类达标加的生命（2.0 = 1 心） |
| `healthBonusCap` | `10.0` | 生命加成总上限（10.0 = 5 心） |
| `balancedEffect` | `""` | 五类全部达标时维持的效果 id（如 `minecraft:luck`）；空 = 关闭 |
| `balancedEffectAmplifier` | `0` | 均衡效果等级（0 = I 级） |
| `scoreboardMirror` | `true` | 镜像到记分板 + `persistentData`（供任务/脚本读取） |
| `hud` | `true` | 左上角营养面板（客户端） |
| `foodTooltips` | `true` | 食物 tooltip 显示营养 |

---

## 4. 收益机制（服务端，每秒刷新）

1. **最大生命**：每类 ≥ `benefitThreshold` 的营养 → +`healthPerNutrient`，总量封顶 `healthBonusCap`
   （以瞬时属性修饰符实现，UUID 固定，重登自动恢复；属性变化时自动修正当前血量）
2. **均衡饮食**（可选）：五类**全部** ≥ 阈值 → 持续维持 `balancedEffect`（环境、无粒子）
3. 关闭系统时会自动移除上述加成（不会残留）

---

## 5. 对外接口（供 KubeJS / FTB 任务 / 技能读取）

| 接口 | 形式 | 示例 |
|---|---|---|
| 玩家 persistentData | float 键 `gtmfo_nutrient_<name>` | KubeJS：`player.persistentData.getFloat("gtmfo_nutrient_dairy")` |
| 记分板 | 只读目标 `gtmfo_<name>`（整数） | `/scoreboard players get <player> gtmfo_dairy`；FTB 任务用记分板观察 |
| 命令 | `/nutrient query` / `clear [name]` / `gain <name> <amount>`（后两个管理员） | 调试与剧情发放 |
| JEI | 食物信息页显示营养（`GTMFO 食物信息` 分类） | 玩家查阅 |
| Tooltip | 食物提示显示"营养：乳 1 果 0.5" | 客户端 |
| HUD | 左上角面板，达标显示绿色 | 客户端 |

KubeJS 示例（均衡奖励由包侧自定义时，可把 `benefitThreshold` 设为 0 关闭内置属性收益）：

```js
// 每 5 秒检查一次，五类都 ≥ 10 时给一点粮食技能经验（示意）
PlayerEvents.tick((event) => {
  const p = event.player;
  if (p.age % 100 !== 0 || p.level.isClientSide()) return;
  const need = ["dairy", "fruit", "grain", "protein", "vegetable"];
  const ok = need.every((n) => p.persistentData.getFloat("gtmfo_nutrient_" + n) >= 10);
  if (ok) {
    // 这里接包内实际 API，例如 puffish_skills 的 XP 或属性授予
  }
});
```

---

## 6. 与 Sunlit Valley 的联动建议（包侧，未实现）

参考 `docs/NUTRIENT_INTEGRATION_ANALYSIS.md` §4：

1. **开启**：包 config `gtfoNutrientConfig.enabled: true`
2. **标签覆盖**：按类别给 FD / F&C / Pam 等食物打 `gtmfo:nutrient/*` 标签
3. **技能**：Puffish Skills 新增 `nutrition` 分类（`puffish_skills:eat_food` 经验源 + 读 `persistentData` 的均衡度）
4. **任务**：FTB Quests 在 `pantry` / `crops` 章加"均衡饮食"支线（用记分板观察任务）
5. **经济**：读营养值 → 通过包内属性体系（`shippingbin:*_sell_multiplier`）给售价加成
6. **平衡**：建议先跑一版"日常饮食模拟"，再定 `decayPerDay` 与阈值

---

## 7. 兼容性与注意事项

- **默认关闭**：不影响未开启的整合包
- **存档兼容**：NBT 结构保持 `{nutrients:{...}, lastDecayDay:N}`；旧存档可读
- **多人**：所有逻辑服务端；客户端只接收快照（HUD）；无客户端安装也可正常游戏
- **与 SoLOnion / Quality Food 的关系**：三轴互补——多样性（SoLOnion）/ 单件品质（Quality Food）/ 类别均衡（本系统）；
  营养的属性收益刻意做得很小（封顶 5 心），避免与 SoLOnion 叠加造成数值膨胀
- **性能**：服务端每秒一次结算（玩家级），记分板/`persistentData` 镜像同一节奏
- **修改 `benefitThreshold`/`healthBonusCap` 等**：每秒自动重算，无需重登
