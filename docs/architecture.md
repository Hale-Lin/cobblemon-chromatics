# 架构说明

Cobblemon Chromatics 是独立 Cobblemon addon。它不 patch、不 fork、也不重新分发修改后的 Cobblemon 二进制文件。

## 运行时职责

| 范围 | 负责人 | 安装位置 |
| --- | --- | --- |
| Pokemon profile 生成、持久化、同步、权限和配置校验 | common/server Java 代码 | 服务端，必要时客户端也加载共享类型 |
| 动态纹理、缓存、渲染注入 | client Java 代码 | 客户端 |
| 配置模板和数据资源 | `pack/shared` 与 resources | 按整合包分发策略放置 |
| 运营和管理 | Fabric 服务端环境 | 服务端 |

服务端是 chromatic 状态的唯一权威。客户端只读取同步数据并渲染，不决定某只 Pokemon 是否 chromatic。

## 包边界

- `dev.cobblemonproject.chromatics.common`: logic shared by integrated and dedicated servers.
- `dev.cobblemonproject.chromatics.server`: dedicated-server-only bootstrapping and integrations.
- `dev.cobblemonproject.chromatics.client`: rendering and input only.
- `dev.cobblemonproject.chromatics.config`: versioned addon configuration.

功能包应按职责拆分，优先使用 Cobblemon 公开 API 和事件。Mixin 只在缺少稳定接入点时使用，并且需要明确记录目标版本和验证范围。

## Chromatic profile 生命周期

Chromatic profile 归属于 Pokemon 个体，而不是世界实体、species、form 或客户端模型。

生命周期原则：

- Pokemon 对象首次创建且 profile 缺失时，由服务端生成 profile。
- 已有 profile 的 Pokemon 不会因为 spawn、出球、进球、区块加载或客户端渲染而重新掷骰。
- 进化必须保留 profile。
- 地区形态也会写入 profile，但默认 `chromatic=false`。
- 非法 profile 不应被静默覆盖成新的随机结果。

## 网络同步

服务端在合适的追踪时机向客户端同步 profile。客户端以 Pokemon UUID 缓存 profile，并在断线时清空缓存。

客户端缓存只是显示层数据。真正的 profile 仍以服务端持久化数据为准。

## 渲染边界

客户端按 Cobblemon resolver 的最终结果处理纹理 pass，不按物种或骨骼硬编码。

当前支持的渲染模式：

- `STATIC_ONLY`: 处理主贴图。
- `LAYERS_ONLY`: 只处理允许的具名 layer。
- `FULL`: 同时处理主贴图和允许的具名 layer。

Layer 使用名称作为契约，不使用数字序号。处理 layer 时必须保留 Cobblemon 原有的 emissive、translucent、tint 等渲染属性。
