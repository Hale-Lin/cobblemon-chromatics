# 配置说明

Cobblemon Chromatics 的默认配置模板位于：

```text
pack/shared/config/cobblemon_chromatics.json
```

实际运行时，配置应放在 Minecraft 或服务端实例的：

```text
config/cobblemon_chromatics.json
```

## 默认配置

```json
{
  "schemaVersion": 1,
  "developmentLogging": false,
  "chromaticChanceDenominator": 4096,
  "chromaticParentBonusRolls": 0,
  "defaultRenderMode": "STATIC_ONLY",
  "enabledLayerNames": [
    "flame"
  ]
}
```

## 字段说明

| 字段 | 默认值 | 说明 |
| --- | --- | --- |
| `schemaVersion` | `1` | 配置结构版本，用于未来迁移 |
| `developmentLogging` | `false` | 开发调试日志开关 |
| `chromaticChanceDenominator` | `4096` | 基础概率分母，`4096` 表示 `1/4096` |
| `chromaticParentBonusRolls` | `0` | 预留的亲本加成掷骰次数，当前孵蛋流程尚未完整接入 |
| `defaultRenderMode` | `STATIC_ONLY` | 新 profile 默认渲染模式 |
| `enabledLayerNames` | `["flame"]` | 允许处理的 resolver layer 名称 |

## 概率建议

`chromaticChanceDenominator` 必须是正整数。数值越小，异色越常见。

示例：

| 配置值 | 概率 |
| --- | --- |
| `4096` | `1/4096` |
| `2048` | `1/2048` |
| `512` | `1/512` |

请注意，这只影响新创建且尚未拥有 profile 的 Pokemon。已经保存 profile 的 Pokemon 不会因为修改配置而重掷。

## Layer 配置

`enabledLayerNames` 使用 Cobblemon resolver layer 的名称。例如喷火龙火焰层是 `"flame"`。

不要使用数字序号作为 layer 配置。Cobblemon resolver 最终 layer 是按名称合并和覆盖的，序号不是稳定契约。

## 生效范围

配置读取和实际生效以服务端为准。客户端只负责根据服务端同步的 profile 进行渲染。

当前不承诺配置热重载。修改后请重启实例。
