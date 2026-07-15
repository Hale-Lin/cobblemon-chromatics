# 使用须知

本文面向服主、整合包作者和参与测试的玩家，说明 Cobblemon Chromatics 当前如何使用，以及哪些内容仍处于开发阶段。

## 适用版本

| 组件 | 版本 |
| --- | --- |
| Minecraft | `1.21.1` |
| Cobblemon | `1.7.3` |
| Fabric Loader | `>=0.17.2` |
| Java | `21` |

请不要把本模组用于其他 Cobblemon 大版本，除非已经做过启动和游戏内验证。

## 安装要求

- 服务端安装 Cobblemon、Fabric API 和 Cobblemon Chromatics。
- 客户端安装同版本 Cobblemon Chromatics 后才能看到异色渲染。
- 本模组不会替代 Cobblemon，也不会携带 Cobblemon 本体内容。

整合包分发时，请把 Cobblemon 作为独立依赖处理，不要把 Cobblemon JAR 合并进 Cobblemon Chromatics 的 JAR。

## 异色与闪光的关系

Cobblemon Chromatics 的 `chromatic` 不是 Cobblemon 原生 `shiny`。

两者可以同时存在，但组合时受以下限制：

- 一只宝可梦可以只是普通。
- 一只宝可梦可以只是 shiny。
- 一只宝可梦可以只是 chromatic。
- 一只有配置允许的具名 layer 的宝可梦可以同时 shiny 和 chromatic。

出现 shiny 时，Chromatic 不会处理 shiny 主贴图，只会处理 resolver 最终保留、配置允许的具名 layer。如果该宝可梦没有这样的 layer，它不会生成 shiny + chromatic 组合。以喷火龙为例，身体的 shiny 主贴图保持原样，允许的 `"flame"` layer 可以异色。

## 生成规则

Chromatic profile 在服务端权威生成，并持久化到 Pokemon 个体数据中。

关键原则：

- 只在 Pokemon 对象首次创建且没有 profile 时生成。
- 不在世界实体 spawn 时重新抽取。
- 不在出球、进球、区块加载或客户端渲染时重新抽取。
- 非法或未知 profile 不会被静默覆盖成新的随机结果。

这个规则是为了避免同一只宝可梦因为加载顺序或显示方式变化而不断刷新外观。

## 地区形态规则

同一种宝可梦的地区形态默认不生成异色。当前排除的地区形态标签包括：

- `alolan_form`
- `galarian_form`
- `hisuian_form`
- `paldean_form`

被排除的地区形态仍会保存 profile，只是 `chromatic=false`。这样可以避免后续系统把它误认为还没有初始化。

## 渲染模式

当前配置支持三种模式：

| 模式 | 效果 |
| --- | --- |
| `STATIC_ONLY` | 只处理主贴图 |
| `LAYERS_ONLY` | 只处理允许的具名 resolver layer |
| `FULL` | 同时处理主贴图和允许的具名 resolver layer |

Layer 通过名称识别，而不是通过数字序号识别。例如喷火龙火焰层使用 `"flame"`。

上述配置模式适用于非 shiny Pokemon。Pokemon 为 shiny 时，有效模式始终收窄为 `LAYERS_ONLY`，不能通过 `STATIC_ONLY` 或 `FULL` 绕过主贴图保护。

## 管理员指令

所有 Cobblemon Chromatics 指令都位于 `/chromatics` 下，要求 Minecraft 权限等级 `2`。单人游戏需要启用作弊；服务器中需要相应的 OP 或权限配置。

| 指令 | 用途 |
| --- | --- |
| `/chromatics spawn <Pokemon属性>` | 在指令源当前位置生成一只异色 Pokemon |
| `/chromatics give <玩家> <Pokemon属性>` | 向指定玩家队伍添加一只异色 Pokemon |
| `/chromatics set <实体选择器> <true\|false>` | 设置已经生成在世界中的 Pokemon 是否异色 |
| `/chromatics inspect <实体选择器>` | 查看已生成 Pokemon 的 shiny、chromatic、渲染模式和 variant |

`<Pokemon属性>` 使用 Cobblemon 自带的 Pokemon 属性参数和补全规则。例如：

```text
/chromatics spawn charizard level=50
/chromatics spawn charizard shiny level=50
/chromatics give Steve charizard level=50
```

对世界实体进行修改或检查时，可以使用 Minecraft 实体选择器：

```text
/chromatics set @e[type=cobblemon:pokemon,sort=nearest,limit=1] true
/chromatics set @e[type=cobblemon:pokemon,sort=nearest,limit=1] false
/chromatics inspect @e[type=cobblemon:pokemon,sort=nearest,limit=1]
```

指令行为遵守正常生成规则：

- `spawn` 和 `give` 会强制写入异色 profile，不进行随机概率抽取。
- 地区形态不能被强制设为异色。
- shiny Pokemon 只有在 resolver 最终提供配置允许的具名 layer 时才能被强制设为异色；异色仍只影响该 layer。
- `set` 仅操作当前已生成在世界中的 Pokemon 实体，不能直接选择玩家队伍或 PC 中的 Pokemon。
- `give` 需要玩家队伍存在可用位置；队伍无法接收时指令会失败并显示错误。
- `inspect` 不修改 Pokemon 数据。

## 已知限制

- 暂无保底机制。
- `chromaticParentBonusRolls` 配置已预留，但孵蛋亲本概率加成尚未完整接入。
- 动态纹理缓存已经存在，但资源重载后的缓存释放和失效仍需要继续完善。
- 礼物、化石、NPC 奖励和迁移等特殊创建入口仍需要继续逐项验证。

## 服主建议

- 正式服保持 `developmentLogging=false`。
- 修改配置后重启服务端。
- 升级 Cobblemon 或 Minecraft 前先在测试环境验证。
- 如果发现某只宝可梦在不同加载场景下异色状态变化，请保留日志和复现步骤，这通常意味着某个创建入口还需要补齐。
