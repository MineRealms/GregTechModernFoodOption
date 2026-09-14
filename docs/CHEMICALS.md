# GTMFO 化学品注册表（有明确化学式的纯物质/单质/中间体）

> **数据来源（零推测）**：本表所有分子式/结构式均直接读取自代码中的
> `.components(...)` 与 `.formula(...)` 参数，非人工推断：
> - `src/main/java/com/ironsword/gtmfo/common/data/material/GTMFOMaterials.java`（固体材料，`chemicalDust(...)`）
> - `src/main/java/com/ironsword/gtmfo/common/data/material/GTMFOFluids.java`（流体，`builder(...)`）
> - 以及配方链 `common/data/recipe/chain/*.java` 中引用的 GTCEu 自带材料（`GTMaterials.*`）。
>
> **ID 规则**：
> - 固体材料：材料 ID 为 `gtceu:<snake_case>`，其粉尘物品为 `gtceu:<snake_case>_dust`。
> - 流体：流体 ID 为 `gtceu:<snake_case>`。
> - GTCEu 自带材料（如水、硫酸、元素单质等）沿用 GTCEu 官方 ID。
>
> **CAS 号** 取自 PubChem / NIST / ECHA 权威数据库；标注 `—` 表示罕见/无公开 CAS 的条目。
> 查 3D 结构：PubChem（CID）/ ChemSpider / CCDC (CSD) / Materials Project（无机晶体）/ PDB+AlphaFold（蛋白/酶）。

---

## 1. 药物活性成分 / 生物碱（有机）

| ID | 英文名 | 中文名 | 分子式 | CAS | 备注 |
|---|---|---|---|---|---|
| `gtceu:paracetamol_dust` | Paracetamol (Acetaminophen) | 对乙酰氨基酚（扑热息痛） | C₈H₉NO₂ | 103-90-2 | 别名 GTCEu `Paracetamol`；CapletChain |
| `gtceu:aminophenol_dust` | 4-Aminophenol | 4-氨基苯酚 | C₆H₇NO | 123-30-8 | 别名 GTCEu `AminoPhenol`；扑热息痛前体 |
| `gtceu:codeine_dust` | Codeine | 可待因 | C₁₈H₂₁NO₃ | 76-57-3 | 止咳糖浆成分 |
| `gtceu:promethazine_dust` | Promethazine | 异丙嗪 | C₁₇H₂₀N₂S | 60-87-7 | 止咳糖浆成分 |
| `gtceu:phenothiazine_dust` | Phenothiazine | 吩噻嗪 | C₁₂H₉NS | 92-84-2 | 异丙嗪合成前体 |
| `gtceu:diphenylamine_dust` | Diphenylamine | 二苯胺 | C₁₂H₁₁N | 122-39-4 | 吩噻嗪合成前体 |

## 2. 芳香族有机中间体

| ID | 英文名 | 中文名 | 分子式 | CAS | 备注 |
|---|---|---|---|---|---|
| `gtceu:vanillin_dust` | Vanillin | 香兰素 | C₈H₈O₃ | 121-33-5 | VanillinChain 终产物 |
| `gtceu:vanillylmandelic_acid_dust` | Vanillylmandelic Acid | 香草扁桃酸 | C₉H₁₀O₅ | 55-10-7 | 香兰素代谢中间体 |
| `gtceu:vanilglycolic_acid_dust` | Vanilglycolic Acid | 香草乙醇酸 | C₉H₈O₅ | — | 香兰素氧化中间体 |
| `gtceu:guaiacol` | Guaiacol | 愈创木酚 | C₇H₈O₂ | 90-05-1 | 香兰素合成原料 |
| `gtceu:aniline` | Aniline | 苯胺 | C₆H₅NH₂ | 62-53-3 | 二苯胺/染料前体 |
| `gtceu:phenol_dust` | Phenol | 苯酚 | C₆H₆O | 108-95-2 | GTCEu 自带；硝基苯酚前体 |
| `gtceu:nitrobenzene` | Nitrobenzene | 硝基苯 | C₆H₅NO₂ | 98-95-3 | GTCEu；苯胺前体 |
| `gtceu:iv_nitrophenol_dust` | 4-Nitrophenol | 4-硝基苯酚 | C₆H₅NO₃ | 100-02-7 | 对硝基苯酚 |
| `gtceu:ii_nitrophenol_dust` | 2-Nitrophenol | 2-硝基苯酚 | C₆H₅NO₃ | 88-75-5 | 邻硝基苯酚 |
| `gtceu:nitrophenols` | Nitrophenols | 硝基苯酚（混合异构体） | (C₆H₅NO₃)₂ | 88-75-5 / 100-02-7 | 2-硝基 + 4-硝基混合物 |
| `gtceu:x_phenothiazine_ii_propyl_chloride` | X-Phenothiazine II Propyl Chloride | 异丙嗪中间体 | C₁₅H₁₄NSCl | — | 异丙嗪合成关键中间体 |
| `gtceu:isopropyl_chloride` | Isopropyl Chloride (2-Chloropropane) | 异丙基氯 | (CH₃)₂CHCl | 75-29-6 | 异丙嗪合成原料 |

## 3. 脂肪族有机中间体

| ID | 英文名 | 中文名 | 分子式 | CAS | 备注 |
|---|---|---|---|---|---|
| `gtceu:acetaldehyde` | Acetaldehyde | 乙醛 | C₂H₄O (CH₃CHO) | 75-07-0 | 香兰素链 |
| `gtceu:glyoxal` | Glyoxal | 乙二醛 | C₂H₂O₂ | 107-22-2 | 香兰素链 |
| `gtceu:glyoxylic_acid` | Glyoxylic Acid | 乙醛酸 | C₂H₂O₃ | 298-12-4 | 香兰素链 |
| `gtceu:citric_acid` | Citric Acid | 柠檬酸 | C₆H₈O₇ (HOC(CH₂CO₂H)₂) | 77-92-9 | 柠檬酸 |
| `gtceu:acetic_acid` | Acetic Acid | 乙酸 | CH₃COOH | 64-19-7 | GTCEu |
| `gtceu:acetic_anhydride` | Acetic Anhydride | 乙酸酐 | (CH₃CO)₂O | 108-24-7 | GTCEu；扑热息痛链 |
| `gtceu:dimethylamine` | Dimethylamine | 二甲胺 | (CH₃)₂NH | 124-40-3 | GTCEu；异丙嗪链 |
| `gtceu:chloroform` | Chloroform | 氯仿 | CHCl₃ | 67-66-3 | GTCEu |
| `gtceu:methanol` | Methanol | 甲醇 | CH₃OH | 67-56-1 | GTCEu |
| `gtceu:ethanol` | Ethanol | 乙醇 | C₂H₅OH | 64-17-5 | GTCEu；酒类链 |
| `gtceu:ethylene` | Ethylene | 乙烯 | C₂H₄ | 74-85-1 | GTCEu |
| `gtceu:propene` | Propene | 丙烯 | C₃H₆ | 115-07-1 | GTCEu |
| `gtceu:methane` | Methane | 甲烷 | CH₄ | 74-82-8 | GTCEu |
| `gtceu:methyl_acetate` | Methyl Acetate | 乙酸甲酯 | C₃H₆O₂ | 79-20-9 | GTCEu |
| `gtceu:glycerol` | Glycerol | 甘油 | C₃H₈O₃ | 56-81-5 | GTCEu；脂肪链 |

## 4. 脂类 / 脂肪酸（有机）

| ID | 英文名 | 中文名 | 分子式 | CAS | 备注 |
|---|---|---|---|---|---|
| `gtceu:stearin` | Stearin (Tristearin) | 硬脂酸甘油三酯 | C₅₇H₁₁₀O₆ | 555-43-1 | 脂肪链产物 |
| `gtceu:sodium_stearate` | Sodium Stearate | 硬脂酸钠 | C₁₇H₃₅COONa | 822-16-2 | 肥皂；厨房清洁剂 |
| `gtceu:soy_lecithin` | Soy Lecithin | 大豆卵磷脂 | 混合磷脂 | 8002-43-5 | 乳化剂（混合物，无单一式） |

## 5. 无机化合物（酸 / 碱 / 盐 / 氧化物）

| ID | 英文名 | 中文名 | 分子式 | CAS | 备注 |
|---|---|---|---|---|---|
| `gtceu:hydrogen_cyanide` | Hydrogen Cyanide | 氰化氢 | HCN | 74-90-8 | GTCEu；剧毒 |
| `gtceu:sodium_cyanide_dust` | Sodium Cyanide | 氰化钠 | NaCN | 143-33-9 | 剧毒 |
| `gtceu:hydrogen_sulfide` | Hydrogen Sulfide | 硫化氢 | H₂S | 7783-06-4 | GTCEu |
| `gtceu:ammonia` | Ammonia | 氨 | NH₃ | 7664-41-7 | GTCEu |
| `gtceu:ammonium_chloride` | Ammonium Chloride | 氯化铵 | NH₄Cl | 12125-02-9 | GTCEu |
| `gtceu:ammonium_perchlorate_dust` | Ammonium Perchlorate | 高氯酸铵 | NH₄ClO₄ | 7790-98-9 | 火箭推进剂成分 |
| `gtceu:hydrochloric_acid` | Hydrochloric Acid | 盐酸 | HCl | 7647-01-0 | GTCEu |
| `gtceu:nitric_acid` | Nitric Acid | 硝酸 | HNO₃ | 7697-37-2 | GTCEu |
| `gtceu:sulfuric_acid` | Sulfuric Acid | 硫酸 | H₂SO₄ | 7664-93-9 | GTCEu |
| `gtceu:perchloric_acid` | Perchloric Acid | 高氯酸 | HClO₄ | 7601-90-3 | 强氧化性酸 |
| `gtceu:sodium_perchlorate_dust` | Sodium Perchlorate | 高氯酸钠 | NaClO₄ | 7601-89-0 | 氧化剂 |
| `gtceu:potassium_perchlorate_dust` | Potassium Perchlorate | 高氯酸钾 | KClO₄ | 7778-74-7 | 氧化剂 |
| `gtceu:sodium_chlorate_dust` | Sodium Chlorate | 氯酸钠 | NaClO₃ | 7775-09-9 | 氧化剂 |
| `gtceu:sodium_hydroxide` | Sodium Hydroxide | 氢氧化钠 | NaOH | 1310-73-2 | GTCEu；强碱 |
| `gtceu:sodium_bicarbonate` | Sodium Bicarbonate | 碳酸氢钠 | NaHCO₃ | 144-55-8 | GTCEu；小苏打 |
| `gtceu:baking_soda_solution` | Baking Soda Solution | 小苏打溶液 | NaHCO₃·H₂O | 144-55-8 | GTMFO |
| `gtceu:sodium_carbonate_solution` | Sodium Carbonate Solution | 碳酸钠溶液 | Na₂CO₃·H₂O | 497-19-8 | GTMFO |
| `gtceu:sodium_sulfate_dust` | Sodium Sulfate | 硫酸钠 | Na₂SO₄ | 7757-82-6 | 芒硝 |
| `gtceu:sodium_chloride` | Sodium Chloride | 氯化钠 | NaCl | 7647-14-5 | GTCEu；食盐 |
| `gtceu:sodium_arsenite_solution` | Sodium Arsenite Solution | 亚砷酸钠溶液 | NaAsO₂ | 7784-46-5 | 剧毒 |
| `gtceu:cupric_hydrogen_arsenite_dust` | Cupric Hydrogen Arsenite | 亚砷酸氢铜（舍勒绿） | CuHAsO₃ | 10290-12-7 | 绿色颜料；剧毒 |
| `gtceu:arsenic_trioxide` | Arsenic Trioxide | 三氧化二砷（砒霜） | As₂O₃ | 1327-53-3 | GTCEu；剧毒 |
| `gtceu:chloroauric_acid` | Chloroauric Acid | 氯金酸 | HAuCl₄ | 16903-35-8 | 金化合物 |
| `gtceu:blue_vitriol` | Blue Vitriol (Copper Sulfate) | 蓝矾（硫酸铜） | CuSO₄ | 7758-99-8 | 五水合物即胆矾 |
| `gtceu:tricalcium_phosphate` | Tricalcium Phosphate | 磷酸三钙 | Ca₃(PO₄)₂ | 7758-87-4 | GTCEu；骨灰成分 |
| `gtceu:lithium_carbonate_dust` | Lithium Carbonate | 碳酸锂 | Li₂CO₃ | 554-13-2 | 锂链 |
| `gtceu:lithium_oxide_dust` | Lithium Oxide | 氧化锂 | Li₂O | 12057-24-8 | 锂链 |
| `gtceu:carbon_dioxide` | Carbon Dioxide | 二氧化碳 | CO₂ | 124-38-9 | GTCEu |
| `gtceu:carbon_monoxide` | Carbon Monoxide | 一氧化碳 | CO | 630-08-0 | GTCEu |
| `gtceu:sulfur_dioxide` | Sulfur Dioxide | 二氧化硫 | SO₂ | 7446-09-5 | GTCEu |
| `gtceu:nitrogen_dioxide` | Nitrogen Dioxide | 二氧化氮 | NO₂ | 10102-44-0 | GTCEu |
| `gtceu:nitrous_oxide` | Nitrous Oxide | 一氧化二氮 | N₂O | 10024-97-2 | GTCEu |
| `gtceu:water` | Water | 水 | H₂O | 7732-18-5 | GTCEu |

## 6. 单质元素

| ID | 英文名 | 中文名 | 分子式 | CAS | 备注 |
|---|---|---|---|---|---|
| `gtceu:hydrogen` | Hydrogen | 氢 | H₂ | 1333-74-0 | GTCEu |
| `gtceu:oxygen` | Oxygen | 氧 | O₂ | 7782-44-7 | GTCEu |
| `gtceu:nitrogen` | Nitrogen | 氮 | N₂ | 7727-37-9 | GTCEu |
| `gtceu:chlorine` | Chlorine | 氯 | Cl₂ | 7782-50-5 | GTCEu |
| `gtceu:sulfur` | Sulfur | 硫 | S₈ | 7704-34-9 | GTCEu |
| `gtceu:arsenic` | Arsenic | 砷 | As | 7440-38-2 | GTCEu |
| `gtceu:lithium` | Lithium | 锂 | Li | 7439-93-2 | GTCEu |
| `gtceu:sodium` | Sodium | 钠 | Na | 7440-23-5 | GTCEu |
| `gtceu:potassium` | Potassium | 钾 | K | 7440-09-7 | GTCEu |
| `gtceu:copper` | Copper | 铜 | Cu | 7440-50-8 | GTCEu |
| `gtceu:aluminium` | Aluminium | 铝 | Al | 7429-90-5 | GTCEu |
| `gtceu:iron` | Iron | 铁 | Fe | 7439-89-6 | GTCEu |
| `gtceu:titanium` | Titanium | 钛 | Ti | 7440-32-6 | GTCEu |
| `gtceu:tin` | Tin | 锡 | Sn | 7440-31-5 | GTCEu |
| `gtceu:lead` | Lead | 铅 | Pb | 7439-92-1 | GTCEu |
| `gtceu:nickel` | Nickel | 镍 | Ni | 7440-02-0 | GTCEu |
| `gtceu:zinc` | Zinc | 锌 | Zn | 7440-66-6 | GTCEu |
| `gtceu:gold` | Gold | 金 | Au | 7440-57-5 | GTCEu |
| `gtceu:palladium` | Palladium | 钯 | Pd | 7440-05-3 | GTCEu |
| `gtceu:bismuth` | Bismuth | 铋 | Bi | 7440-69-9 | GTCEu（铋青铜外壳） |
| `gtceu:plutonium` | Plutonium | 钚 | Pu | 7440-07-5 | GTCEu（钚胶囊） |

---

## 7. 虚构 / 无明确化学式的条目（**不提供化学式**）

以下条目在配方链中出现，但为**混合物、生物大分子、菌种或模组虚构梗物质**，无单一明确分子式，仅列出 ID 供查配方图用：

| 类别 | ID 示例 |
|---|---|
| 生物菌种/酶 | `gtceu:penicillium_roqueforti_dust`（罗克福青霉）、`gtceu:lactic_acid_bacteria`（乳酸菌）、`crude_rennet_solution`/`fungal_rennet_solution`（凝乳酶）、`gtceu:bacteria`（GTCEu 菌种） |
| 奶酪/奶凝乳中间体 | `coagulated_milk_curd`、`cut_curd`、`cooked_curd`、`salted_curd`、`large/small/dried/solidified_mozzarella_curd`、`gorgonzola_curd`、`shredded_parmesan`（均为复杂蛋白/脂肪混合物） |
| 陶瓷/骨瓷原料 | `bone_china_clay`、`bone_ash`（骨灰）、`unfired/biscuit/glazed/black_glazed_porcelain_tile`、`bentonite`、`talc` |
| 面团/淀粉 | `laminated_dough`（起酥面团）、`starch_filled_water`（淀粉水）、`potato_juice` |
| 植物/食品提取物 | `zest`（果皮屑）、`crushed_poppy`（碾碎罂粟）、`rainbow_sap`（彩虹树液）、`rubber_sap`、`sludge`（污泥）、`alkaline_extract`、`vibrant_extract`（活力提取液）、`ender_pearl_solution`（末影珍珠溶液）、`ender_sugar_solution`（末影糖溶液，虚构 BeN₂K·H₂O） |
| 油脂/混合油 | `frying_oil`、`frying_oil_hot`、`olive_oil`、`raw/hydrated/soybean_oil`、`seed_oil`、`fish_oil`（均为混合脂肪酸甘油酯） |
| 饮料/糖浆/酱料 | `purple_drink`（紫色饮料）、`etirps`、`cough_syrup`（止咳糖浆）、`carbonated_water`、`leninade`、`vodka`、`white/red_wine`、`coffee`、`molten_*_chocolate`、`cane_syrup`、`hfcs_solution`、各种 `*_sauce`、`tomato_sauce`、`berry_jam` 等 |

> 查 3D 结构时，本表第 7 节条目**无对应纯物质结构**（混合物/菌种/虚构）；第 1–6 节条目均可在 PubChem / Materials Project / CCDC 检索到明确 3D 结构。

---

*生成依据：GTMFOMaterials.java / GTMFOFluids.java 的 `components()`、`formula()` 参数；GTMaterials 引用于 recipe chain 文件。*  
*版本：gtmfo-0.0.6*
