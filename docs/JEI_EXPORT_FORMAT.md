# GTFO JEI 导出格式说明（v1）

两个文件，均由模组在客户端进存档、配方加载完成后自动导出（后台线程、原子替换写入）：

| 文件 | 内容 | 规模 |
|---|---|---|
| `jei_recipes.json` | JEI 能显示的全部配方 | 55,582 条 / 95 分类 / ~60 MB |
| `jei_names.json` | 机器/材料/方块/物品/流体的 id → 中英文名 | 1,303 机器 / 825 材料 / 5,487 方块 / 11,312 物品 / 1,143 流体 |

数据来源：JEI 运行时 `IRecipeManager`（即"JEI 里查得到的配方"），GT 配方需求来自 GTCEu API
（`RecipeHelper.getRealEUtWithIO` / `getRecipeEUtTier`）。分类按 `type` 排序、配方按 `id` 排序，方便 diff。

---

## 1. jei_recipes.json

### 1.1 顶层

```json
{
  "format": "gtmfo_jei_recipes",
  "version": 2,
  "minecraft_version": "1.20.1",
  "exported_at": "2026-09-13T22:11:03Z",
  "include_hidden": false,
  "categories": [ ... ],
  "summary": { "category_count": 95, "recipe_count": 55582 }
}
```

| 字段 | 类型 | 说明 |
|---|---|---|
| `format` | string | 固定 `gtmfo_jei_recipes` |
| `version` | int | 结构版本，当前 `1` |
| `exported_at` | string | ISO-8601 UTC |
| `include_hidden` | bool | 是否包含 JEI 默认隐藏的配方 |
| `categories` | array | 见 1.2；**只包含至少 1 条配方的分类** |
| `summary` | object | 计数（写在文件末尾） |

### 1.2 分类对象（category）

```json
{
  "type": "gtceu:macerator",
  "title": "Macerator",
  "recipe_class": "com.gregtechceu.gtceu.api.recipe.GTRecipe",
  "kind": "recipe",
  "catalysts": [ { "type": "item", "id": "gtceu:lv_macerator", "count": 1 } ],
  "recipes": [ ... ]
}
```

| 字段 | 说明 |
|---|---|
| `type` | JEI RecipeType uid，全局唯一；建图时作为"配方类型/机器组"节点 |
| `title` | 分类显示名（本地化） |
| `recipe_class` | 配方对象的 Java 类名（可用于区分 GT 配方与其它模组配方） |
| `kind` | `recipe` = 真实加工配方；`information` = 纯信息页（`jei:information`、`*_info`），规划时应排除 |
| `catalysts` | 能执行该分类的机器（催化剂），格式同 ingredient（1.5）；**分类级**，不在每条配方里重复 |
| `recipes` | 配方数组（1.3） |

> **v2 说明**：GT 的分类（`gtceu:*`）不再取自 JEI —— GT 的 JEI 分类用 LDLib 控件渲染，
> 不经过 `IRecipeLayoutBuilder`，且在某些联机会话中整个分类为空。现在**直接从 GTCEu API
> （`GTRegistries.RECIPE_CATEGORIES` + `GTRecipeType.getRecipesInCategory`）导出**，
> 因此输入/输出槽位来自配方内容（item/fluid capability），`gt` 块完整，催化剂来自机器注册表。
> GT 分类排在 JEI 分类之后（两段内部各自按 uid 排序）。

注意：`type` 不一定等于 `gt.recipe_type`。GT 子分类（如 `gtceu:large_chemical_reactor`）的
JEI 分类与 GT 配方类型可能不同，见 1.4。

### 1.3 配方对象（recipe）

```json
{
  "id": "gtceu:macerator/macerate_steak",
  "inputs":  [ 槽对象 ... ],
  "outputs": [ 槽对象 ... ],
  "gt": { ... }
}
```

| 字段 | 说明 |
|---|---|
| `id` | 配方注册名。**绝大多数是全局唯一**；无法解析、路径为空（如 `"minecraft:"`）或与同分类内其它配方重复时，改用 `"<type>#<序号>"` 兜底（v2 起保证同分类内唯一） |
| `inputs` | 输入槽（角色 INPUT），只含非空槽 |
| `outputs` | 输出槽（角色 OUTPUT），只含非空槽 |
| `gt` | **仅 GT 配方存在**，见 1.4；非 GT 配方（原版合成等）无此字段 |

> v2 起 `id` **在每个分类内保证唯一**；下游建图请始终用 `type + "|" + id` 作为唯一键。

### 1.4 GT 需求块（`gt`）

```json
"gt": {
  "recipe_type": "gtceu:alloy_blast_smelter",
  "duration": 3750,
  "parallels": 1,
  "oc_level": 0,
  "eut": 120,
  "amperage": 1,
  "energy_io": "in",
  "tier": "MV",
  "tier_index": 2,
  "voltage": 128,
  "total_eu_t": 120,
  "total_eu": 450000
}
```

| 字段 | 类型 | 说明 |
|---|---|---|
| `recipe_type` | string | GT 配方类型注册名（`gtceu:xxx`） |
| `duration` | int | 耗时（tick，20 = 1 秒） |
| `parallels` | int | 并行数 |
| `oc_level` | int | 超频等级 |
| `eut` | long | **EU/t**（耗电或发电，正值） |
| `amperage` | long | 电流（A），通常 1 |
| `energy_io` | string | `"in"` = 耗电配方，`"out"` = 发电配方 |
| `tier` | string | **最低电压等级名**：ULV/LV/MV/HV/EV/IV/LuV/ZPM/UV/UHV/UEV/UIV/UXV/OpV/MAX |
| `tier_index` | int | 等级下标（ULV=0，LV=1，…），便于数值比较 |
| `voltage` | long | 该等级标称电压（LV=32、MV=128、HV=512…） |
| `total_eu_t` | long | `eut × amperage` |
| `total_eu` | long | `total_eu_t × duration`（一次配方的总耗电） |

边界：**燃料类配方（如烘焙炉）没有能量字段**（`eut` 等字段整体缺省，269 条），此时只剩
`recipe_type / duration / parallels / oc_level`。

### 1.5 槽对象（slot）

```json
{ "name": "slot_4", "ingredients": [ 原料对象 ... ] }
```

| 字段 | 说明 |
|---|---|
| `name` | JEI 槽位名（可能缺省）。GT 分类里通常是 `slot_<序号>`，**仅用于调试，不要依赖其语义** |
| `ingredients` | **多选一（OR）组**：长度 1 = 确定材料；长度 > 1 = 标签/替代品，任一满足即可（全库共 16,954 个多选槽） |

建图建议：`槽 → OR 节点 → 各候选`。例如：

```json
{"name":"slot_4","ingredients":[
  {"type":"item","id":"gtceu:nano_processor_mainframe","count":2},
  {"type":"item","id":"gtceu:quantum_processor_computer","count":2},
  {"type":"item","id":"gtceu:cutting_processor_assembly","count":2}]}
```

### 1.6 原料对象（ingredient）

只有两种 `type`（全库统计：item 508,488 个、fluid 17,900 个）：

```json
{ "type": "item",  "id": "minecraft:beef", "count": 1 }
{ "type": "item",  "id": "gtceu:iron_pickaxe", "count": 1, "nbt": "{Damage:79,GT.Tool:{},...}" }
{ "type": "fluid", "id": "gtceu:oxygen", "amount": 2000 }
{ "type": "fluid", "id": "gtceu:oxygen", "amount": 2000, "nbt": "{...}" }
```

| 字段 | 说明 |
|---|---|
| `type` | `"item"` 或 `"fluid"` |
| `id` | 注册名，**与 jei_names.json 的 id 一一对应**（可直接 join 中英文名） |
| `count` | 物品数量（item 专用） |
| `amount` | 流体数量，单位 mB（fluid 专用） |
| `nbt` | 可选，SNBT 字符串；**同 id 不同 nbt 视为不同材料**（如带材质的 GT 工具/转子） |

> v2 起 GT 分类的槽位来自 GTCEu 配方内容 API，因此：
> - 槽位 `name` 一律存在：能力名（`item` / `fluid` / `item_tick` / `fluid_tick`，以及极少见的其他能力）
>   或 JEI 分类给出的名称；JEI 分类未命名时自动生成 `input_N` / `output_N`（v2 前该字段可能缺失）；
> - **GT 概率产出**带 `chance` / `max_chance`（0~10000 与 10000 基准，取自 `Content`），例如：
>   ```json
>   {"name":"item","chance":8000,"max_chance":10000,
>    "ingredients":[{"type":"item","id":"gtceu:raw_rubber","count":1}]}
>   ```
> - 标签型原料（`Ingredient` / `FluidIngredient`）会展开为同一槽内的多个候选（上限 64 条/槽）。

---

## 2. jei_names.json

### 2.1 顶层

```json
{
  "format": "gtmfo_jei_names",
  "version": 1,
  "minecraft_version": "1.20.1",
  "exported_at": "2026-09-13T22:11:03Z",
  "machines": [ ... ],
  "materials": [ ... ],
  "blocks": [ ... ],
  "items": [ ... ],
  "fluids": [ ... ]
}
```

所有 section 均为数组、按 id 排序。每个条目统一包含 `id` / `key` / `en` / `zh`：
`key` = 翻译 key，`en` = 英文名，`zh` = 中文名（zh_cn 缺失时回退英文）。
中英文直接从 `en_us` / `zh_cn` 语言文件解析，**与客户端当前语言无关**。

### 2.2 各 section

```json
"machines":  [{"id":"gtceu:lv_macerator","key":"block.gtceu.lv_macerator","en":"Basic Macerator","zh":"基础研磨机","tier":"LV","tier_index":1}]
"materials": [{"id":"gtceu:iron","key":"material.gtceu.iron","en":"Iron","zh":"铁"}]
"blocks":    [{"id":"gtmfo:adobe_bricks","key":"block.gtmfo.adobe_bricks","en":"Adobe Bricks","zh":"土坯砖块"}]
"items":     [{"id":"gtceu:iron_dust","key":"tagprefix.dust","en":"Iron Dust","zh":"铁粉"}]
"fluids":    [{"id":"gtceu:oxygen","key":"material.gtceu.oxygen","en":"Oxygen","zh":"氧"}]
```

| section | 额外字段 | 说明 |
|---|---|---|
| `machines` | `tier`、`tier_index` | GTCEu 机器定义（`GTRegistries.MACHINES`） |
| `materials` | — | GTCEu 材料（`gtceu:iron` 这类"材料 id"，与物品 id 不同） |
| `blocks` | — | 全部方块 |
| `items` | — | 全部物品（含 BlockItem） |
| `fluids` | — | 全部流体 |

注意：
- **材料 id ≠ 物品 id**。配方里出现的是物品 id（`gtceu:iron_dust`、`gtceu:iron_ingot`）；
  `materials` 里的 `gtceu:iron` 是材料本体，建图时如果按材料聚合可以用"前缀剥离"或材料表关联。
- GT 材料物品/工具的名字是模板合成的（`tagprefix.dust = "%s Dust"`、`item.gtceu.tool.wrench = "%s Wrench"`），
  导出时已用 GT 材料 API 合成，直接读 `en`/`zh` 即可。
- `key` 可重复（同前缀不同材料共享 `tagprefix.dust`），**join 请用 `id`**。

---

## 3. 建图建议（给图渲染/查询侧）

1. **节点**
   - 物品/流体节点：用 ingredient 的 `id`（+`nbt` 如有）作为键，显示名查 `jei_names.json`
   - 配方节点：`type + "|" + id` 唯一键
   - 配方类型/机器组节点：category 的 `type`，机器图标可用 `catalysts`
2. **边**
   - `inputs` 的每个槽 → 配方：槽内是 OR 组，展开为"任一候选 → 配方"
   - 配方 → `outputs` 的每个槽（同理 OR）
3. **能量/等级过滤**：用 `gt.tier_index`（数值比较）与 `gt.eut`、`gt.total_eu`；发电配方看 `energy_io == "out"`
4. **排序稳定性**：分类与配方均已排序；`summary` 在文件尾部
5. **忽略项**：`include_hidden=false` 时隐藏配方不导出；信息类分类（无输入输出）可按 `inputs`/`outputs` 为空过滤
6. **原版配方**也在其中（`minecraft:crafting` 8,452 条、`minecraft:anvil` 6,046 条等），
   它们的 `type` 以 `minecraft:` 开头，无 `gt` 字段

## 4. 版本与重新导出

- 配置（`config/gtmfo.yaml`）：
  ```yaml
  devConfigs:
    exportJeiRecipes: false                          # 默认关闭；需要导出时改为 true
    jeiRecipeExportPath: H:/tools/jei_recipes.json   # 绝对路径直接用；相对路径相对 run/
    jeiNameExportPath: H:/tools/jei_names.json
    jeiExportHiddenRecipes: false
  ```
- **默认关闭**（模组默认值即 `false`）；启用后需重启客户端/重进存档才会触发（配置在启动时读取）
- 触发时机：进存档后配方加载完成（防抖 3 秒）自动覆盖导出；每次进存档/配方重载都会重导
- `version` 字段用于兼容：结构变更时会 +1

### v2 变更（2026-09-16，gtmfo-0.0.8）

1. **GT 配方改由 GTCEu API 直出**（修复"GT 分类整体缺失"）：GT 的 JEI 分类走 LDLib 控件渲染，
   不经过 `IRecipeLayoutBuilder`，某些联机会话中分类甚至完全不在 JEI 里；现在遍历
   `GTRegistries.RECIPE_CATEGORIES` + `GTRecipeType.getRecipesInCategory`，
   槽位取自 `GTRecipe.inputs/outputs/tickInputs/tickOutputs`（item / fluid 能力），
   催化剂取自有该配方类型的机器。
2. **分类新增 `kind` 字段**（`recipe` / `information`），便于建图时排除信息页
   （`jei:information`、`*_info`）。
3. **配方 `id` 同分类内保证唯一**：无法解析、空路径（JEI 偶尔返回 `"minecraft:"`）或重复时，
   改写为 `"<type>#<序号>"`。
4. **槽位 `name` 一律存在**（JEI 未命名时生成 `input_N` / `output_N`；GT 槽位用能力名）。
5. **GT 概率产出带 `chance` / `max_chance`**（JEI API 不暴露概率，此前无法导出）。
6. 标签型原料展开为同槽多候选（上限 64 条/槽）。
7. 文件顶层 `version` 升为 **2**；`jei_names.json` 结构不变（仍为 v1）。
