# GTMFO × Society: Sunlit Valley（宝铺版）整合分析

> 分析对象：`G:\MinecraftGames\Sunlit Valley(BaopuEdition)\.minecraft\versions\Society Sunlit Valley`
> 方法：解包 mod jar + 读取整合包 KubeJS（tags/recipes/datagen）+ 类级 API diff + 配置检查。
> 目标：**不加新物品**，只做「标签合并 + 配方添加 + 经济数据」的联动。

---

## 1. 兼容性实测结论

| 依赖 | 整合包 | 我们的开发环境 | 结论 |
|---|---|---|---|
| GTCEu | **7.5.3** | 7.5.2 | 类列表 diff：**0 删除 / 1 新增**（仅 datagen mixin）→ 二进制兼容 ✅ |
| JEI | **15.56.0.205** | 15.20.0.115 | 我们使用的 API 类全部存在；`IRecipeSlotBuilder` 新增 `setFluidRenderer(..., TilingDirection)` 重载（LDLib/GTCEu 均未调用，风险低） |
| LDLib | 1.0.52.a | 1.0.50 | 小版本差异，待实机验证 |
| KubeJS | 2001.6.5-build.16 | 同版本 | GTCEu 7.5.3 自带 KubeJS 集成（`GregTechKubeJSPlugin`）→ **可写 GT 配方** ✅ |
| Society / Quality Food / Serene Seasons / ShippingBin / FD / Create | 见报告 | — | 全部数据/标签驱动，可对接 ✅ |

**⚠️ 模组元数据问题（顺带发现）**
- `mods.toml` **未声明 `gtceu` 依赖**（只声明 forge/minecraft）→ 建议加 `modId="gtceu" versionRange="[7.5.2,)"`。
- `gradle.properties` 的 `mod_license=All Rights Reserved` 与 README 的 **LGPL-3.0** 不一致（合规问题，需统一）。

---

## 2. 整合包的食物体系接口（逆向结果）

### 2.1 标签层（KubeJS 管理，`server_scripts/tags/`）

**`forge:` 标准食物标签**（FD/Create/Quality Food 都在用）：
`forge:crops`、`forge:crops/<name>`、`forge:vegetables(/<name>)`、`forge:fruits`、`forge:berries`、
`forge:seeds(/<name>)`、`forge:raw_meat`、`forge:cooked_meat`、`forge:eggs`、`forge:milk(s)`、
`forge:mushrooms`、`forge:dough`、`forge:flour`、`forge:grain`、`forge:raw_fishes`、`forge:cooked_fishes`、
`forge:cheeses` / `c:cheeses`、`forge:sweets`、`forge:drinks`、`forge:water_bottles`、`forge:salt` 等。

**品质系统（Quality Food）**：`quality_food:material_whitelist` 内含
`#forge:crops`、`#forge:seeds`、`#forge:eggs`、`#forge:mushrooms`、`#forge:dough`、`#forge:flour`
→ **只要物品进了对应 forge 标签，品质系统自动生效，无需单独写条目** ✅

**季节系统（Serene Seasons）**：`sereneseasons:spring_crops / summer_crops / autumn_crops / winter_crops`
（**item + block 双标签**），整合包在 `handleSeasonTags.js` 用数组维护；`sereneseasons:greenhouse_glass` 是温室玻璃。
→ 我们的作物（`gtmfo:crop_*` 继承原版 `CropBlock`，兼容季节逻辑）只需加入季节标签。

**Society 标签**：`society:sellable`、`society:farmer_product`、`society:geologist_product`、
`society:artisan_product`、`society:adventurer_product` —— 全部由 `global.trades` 自动生成（见 2.2）；
`society:villager_gift` 引用 `#society:sellable`；另有 `society:need_seeds`、`society:infertile` 等。

**SoLOnion（饮食多样性）**：配置 `foodItems.entries=[]` 且 `isWhitelist=false`
→ **所有食物自动参与多样性系统**，我们的 200+ 食物可直接为玩家提供多样性收益，无需配置 ✅

### 2.2 经济层（Shipping Bin + Society）

`kubejs/startup_scripts/globalRegistry.js` 定义：

```js
global.trades = new Map();               // item -> { value, multiplier }
global.crops / animalProducts / cooking / wines / brews / ore / pristine / geodeList  // {item, value} 列表
```

- `society:sellable`、`society:farmer_product` 等标签由 `handleItemBlockFluidTags.js` **遍历 `global.trades` 自动生成**
- 价格 tooltip（`addPriceTooltips.js`）、村民礼物、Shipping Bin 售价（乘数 `crop/gem/wood/meat`）全部走这套数据

### 2.3 加工层（可挂配方的地方）

| 机器 | KubeJS 配方类型 | 备注 |
|---|---|---|
| Create | `create:milling` / `mixing` / `compacting` / `crushing` / `pressing` | 整合包已有同类脚本可照抄 |
| Farmer's Delight | `farmersdelight:cutting`（`tool: #forge:tools/knives`） | 切菜板 |
| Farm & Charm | `farm_and_charm:mincer` 等 | |
| GTCEu | `event.recipes.gtceu.<type>` | 7.5.3 带 KubeJS 集成 |

---

## 3. 可做的联动（零新物品）

### A. 标签合并

| GTMFO 内容 | 目标标签 |
|---|---|
| 18 种种子（`seed_*`） | `forge:seeds`、`sereneseasons:*_crops`（item+block，按作物季节分配） |
| 27 种作物/水果（onion/cucumber/eggplant/tomato/artichoke/garlic…） | `forge:crops`、`forge:vegetables(/<name>)` |
| 水果（banana/mango/orange/lemon/lime/apricot/grapes…） | `forge:fruits` |
| 浆果（blackberry/blueberry/raspberry/currant/lingonberry/elderberry/cranberry） | `forge:berries`（我们已有 `forge:berry/sweet|tart`） |
| 生肉/熟肉（bacon_raw/bacon/barg_meat…） | `forge:raw_meat` / `forge:cooked_meat` |
| 奶制品（mozzarella_ball/slice、cheese 类） | `forge:cheeses` / `c:cheeses` |
| 面团/面粉（dough、flour 类） | `forge:dough` / `forge:flour` |
| 甜点（apple_candy、ice_cream…） | `farmersdelight:sweets`（可选） |
| 作物方块（`gtmfo:crop_*`） | `sereneseasons:*_crops`（block 标签） |

> 实施位置：**整合包侧 KubeJS 最灵活**（季节归类是整合包决策）；模组侧只建议补通用 `forge:` 标签
> （让任何整合包都能识别，属于通用改进）。

### B. 经济数据（让我们的东西能卖）

在整合包 startup 脚本里把我们的物品 push 进 `global.crops` / `global.cooking` /
`global.animalProducts` / `global.wines` / `global.brews`（带 `value`，参考同类物品定价）
→ 自动获得：`society:sellable` 标签、价格 tooltip、村民礼物、Shipping Bin 售价 ✅

### C. 配方（两条方向）

1. **整合包机器加工我们的物品**（推荐，避开 GT 电力门槛）
   - Create 研磨：我们的可可豆 → 可可粉、小麦 → 面粉；压块：奶酪；混合：果汁/面团
   - FD 切菜板：我们的奶酪轮、面包、披萨
   - Farm & Charm mincer：我们的肉 → 肉末
2. **我们的机器加工整合包物品**（需要 GT 电力，门槛高）
   - `event.recipes.gtceu.slicer(...)` 切整合包蔬菜、`extractor` 榨整合包水果、烘焙炉/微波炉等
   - ⚠️ 我们的机器是 GTCEu 电力机器（LV+）；整合包科技线是 Create，没有 GT 电力产线
     → 要么玩家自建 GT 发电，要么只给"无电"内容（烘焙炉用燃料、手搓/工作台配方）

---

## 4. 风险与决策点

1. **GTCEu 内容解锁**：整合包已加载 GTCEu 7.5.3 且世界生成配置是默认值
   （`removeVanillaOreGen: true`、矿脉网格 3、`addLoot: true`），玩家侧已有探矿缓存
   → GT 矿石/材料其实**已经在世界里**；加 GTMFO 会让 GT 食物机器真正可用，等于首次给 GT 内容
   设计玩法。需确认这是想要的（否则整合包的"Create 科技线"会被 GT 电力线分流）。
2. **两套能源体系**：GT 电力（EU）vs Create 动力。建议整合包侧用 Create/FD 配方给 GTMFO
   内容提供"非 GT 路径"，避免强迫玩家进 GT 电力。
3. **我们的原版覆盖配置**：`useBakingOvenForMeats` / `useRollingPinForPaper` / `deleteBreadRecipe`
   会移除原版配方（如 paper、烤肉），整合包任务书若引用这些配方会断
   → 建议在整合包里关闭这些覆盖（或核对任务书）。
4. **JEI 15.56**：我们的 JEI 分类用稳定 API ✅；JEI 导出工具理论上可能遇到别的模组分类调用新重载
   （低概率，导出是可选工具）。
5. **配方表容量调整**：我们模组会调整 GTCEu 的 BREWING/EXTRACTOR/FERMENTING/COMPRESSOR 槽位
   （全局生效），对整合包是新增内容，无破坏性。

---

## 5. 建议实施顺序

1. **启动验证**：把 GTMFO 放进测试整合包，确认 GTCEu 7.5.3 + JEI 15.56 + LDLib 1.0.52 下能启动、
   无缺失依赖（顺带验证 JEI 分类正常显示）。
2. **模组侧**：补 `gtceu` 依赖声明 + 统一 license；给物品补通用 `forge:` 标签（crops/vegetables/
   fruits/berries/seeds/raw_meat/cooked_meat/cheeses/dough/flour）。
3. **整合包侧 KubeJS**：
   - `tags`：`sereneseasons:*_crops`（item+block，按季节分配）+ 补充标签
   - `startup`：`global.crops` / `global.cooking` / `global.animalProducts` 定价
   - `recipes`：Create/FD 加工我们的物品（非 GT 路径）+ 可选 GT 配方
4. **平衡与任务书**：确认是否给 GTMFO 内容加任务章节（否则玩家不知道有这些内容）。
