# 兼容性说明

本文记录 Cobblemon Chromatics 与 Cobblemon 资源、shiny、地区形态和整合包分发之间的边界。

## Cobblemon 版本

当前目标版本是 Cobblemon `1.7.3` + Minecraft `1.21.1`。

Cobblemon 的 resolver、模型、layer 和 API 都可能随版本变化。升级 Cobblemon 前必须重新验证：

- Pokemon profile 生命周期。
- resolver 主贴图和 layer 解析。
- shiny variation 的实际贴图选择。
- 客户端渲染注入点。
- 网络同步和持久化数据。

## 不重新分发 Cobblemon

Cobblemon Chromatics 是独立 addon。

构建时 Cobblemon JAR 仅作为本地编译依赖使用，不应被打入发布 JAR，也不应作为修改版随本模组重新分发。

## 与 shiny 的兼容

Chromatic 与 Cobblemon 原生 shiny 独立保存，但组合渲染受 layer 范围限制。

Cobblemon 先根据自己的 resolver 和 aspects 选择最终主贴图与 layer，本模组再处理这些结果。shiny 不与 chromatic 互斥，但只有 resolver 最终提供配置允许的具名 layer 时才能生成组合状态；组合状态强制使用 `LAYERS_ONLY`，不会修改 shiny 主贴图。

服务端会从已安装模组的 resolver JSON 建立轻量 layer 索引，不会加载 Cobblemon 客户端渲染类，也不会把 Cobblemon 的 resolver、贴图或 class 复制进本模组 JAR。纯客户端资源包新增的 layer 不属于服务端权威资源，不能自行扩大 shiny + chromatic 的生成资格。

## 与地区形态的兼容

当前策略要求同一种宝可梦的地区形态不生成 chromatic。

排除目标包括：

- 阿罗拉形态
- 伽勒尔形态
- 洗翠形态
- 帕底亚形态

如果 Cobblemon 后续加入新的地区形态标签，需要同步扩展排除策略和测试。

## Resolver Layer

Cobblemon resolver layer 使用名称作为关键数据，例如 `"flame"`、`"glow"`、`"color"`。

本模组也使用 layer 名称作为配置和存档契约。原因是 Cobblemon 会按 layer 名称合并最终结果，同名且后匹配的 variation 可能覆盖前层，最终只保留启用的 layer。

## 资源属性

处理 layer 时必须保留 Cobblemon 原有渲染属性，包括但不限于：

- `enabled`
- `tint`
- `emissive`
- `translucent`
- `translucent_cull`

Chromatic 只改变允许范围内的纹理颜色，不应改变这些属性。

## 整合包建议

- 同时声明 Cobblemon `1.7.3` 和 Cobblemon Chromatics。
- 不要混用不匹配的 Minecraft 或 Cobblemon 版本。
- 服务端和客户端安装同版本 Cobblemon Chromatics。
- 在升级资源包、Cobblemon 或本模组后，优先测试多层宝可梦，例如喷火龙的 `"flame"` layer。
