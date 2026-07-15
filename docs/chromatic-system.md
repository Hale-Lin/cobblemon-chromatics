# Chromatic 系统说明

Chromatic 是 Cobblemon Chromatics 自己维护的外观状态。它不替换 Cobblemon 原生 shiny；两种状态可以同时存在，但同时存在时有额外的 layer 限制。

## Profile 数据

每只受管理的 Pokemon 会保存一个 Chromatic profile。profile 是权威数据，不只是一个 seed。

当前数据契约包含以下概念：

- `schemaVersion`: 存档结构版本。
- `chromatic`: 是否为 chromatic。
- `seed`: 初始生成和审计用 seed。
- `renderMode`: 渲染模式。
- `baseHsv`: 主贴图 HSV 参数。
- `layerHsv`: layer HSV 参数。
- `layers`: 允许处理的具名 layer。
- `variant`: 稳定语义 variant ID。

后续迁移应通过 `schemaVersion` 处理，不应直接丢弃旧 profile。

## 概率

默认基础概率为 `1/4096`，由配置字段 `chromaticChanceDenominator` 控制。

抽取必须满足：

- 服务端执行。
- 只在 profile 缺失时执行。
- 使用稳定输入派生结果，避免同一 Pokemon 被重复刷新。
- 不读取或修改 Cobblemon shiny 概率。
- 如果 Pokemon 已经是 shiny，只有最终 resolver 能提供配置允许的具名 layer 时才有资格抽取 chromatic。

因此 shiny 和 chromatic 可以同时出现，但 shiny + chromatic profile 会强制保存为 `LAYERS_ONLY`。其主 shiny 贴图不会被 HSV 处理，只有配置允许且实际存在的具名 layer 会发生异色变化。

## 地区形态

地区形态不参与 chromatic 生成。当前策略通过 Cobblemon form aspects 识别以下标签：

- `alolan_form`
- `galarian_form`
- `hisuian_form`
- `paldean_form`

这些形态会生成非异色 profile，而不是跳过 profile 写入。

## 进化

Chromatic profile 属于 Pokemon 个体，而不是 species、form 或实体渲染实例。

进化时应保留原 profile：

- 如果 Cobblemon 只是修改同一个 Pokemon 对象的 species 或 form，profile 应自然保留。
- 如果某个流程创建了替代 Pokemon 对象，必须复制 profile。

不要因为进化后的贴图、模型或 layer 改变而重新掷骰。

## 孵蛋

当前配置已包含 `chromaticParentBonusRolls`，用于未来控制亲本 chromatic 对子代概率的影响。

现阶段该字段默认 `0`，并且孵蛋亲本加成尚未完整接入。子代 chromatic 应由服务端在 Pokemon 对象创建时决定，不能由客户端推断。

## 渲染处理

客户端只读取服务端同步的 profile，并对 Cobblemon resolver 最终选中的纹理结果执行处理。

渲染原则：

- 处理最终 resolved texture pass。
- 保留 Cobblemon layer 的 `enabled`、`tint`、`emissive`、`translucent` 和 `translucent_cull` 等属性。
- 使用 layer 名称作为稳定契约。
- shiny 状态下无条件保护主贴图；即使 Pokemon 在 profile 创建后才被改成 shiny，也只允许处理具名 layer。
- 不处理透明像素。
- 默认保护近黑轮廓、瞳孔、近白或低饱和的牙齿、眼白、高光。
- 动态贴图应缓存，不能每帧重新生成。
