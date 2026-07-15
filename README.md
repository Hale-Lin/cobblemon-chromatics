# Cobblemon Chromatics

Cobblemon Chromatics 是一个独立的 Fabric 附加模组，用于为 Cobblemon 提供可维护的异色外观扩展。

它面向 Minecraft `1.21.1`、Cobblemon `1.7.3`、Java `21`，mod ID 为 `cobblemon_chromatics`。本工程不修改、不 fork、也不重新分发 Cobblemon 本体。

## 使用前须知

- 服务端必须安装 Cobblemon `1.7.3` 和 Cobblemon Chromatics。
- 客户端需要安装 Cobblemon Chromatics 才能看到动态异色渲染效果。
- Chromatic 是独立于 Cobblemon 原生 shiny aspect 的外观状态；两者可同时存在，但闪光状态只允许异色影响配置认可的具名 layer。
- 异色判定由服务端负责，并在 Pokemon 对象首次创建且缺少 profile 时写入持久化数据。
- 世界实体刷新、出球、区块加载、客户端渲染不会重新抽取异色。
- 同一种宝可梦的地区形态默认不生成异色，但仍会写入 `chromatic=false` 的 profile，避免以后被误判为未初始化。
- 当前没有加入指令、保底机制、配置热重载或完整孵蛋遗传逻辑。

## 安装

将以下模组放入同一个 Fabric `mods` 目录：

- Fabric Loader `0.17.2` 或更高兼容版本
- Fabric API
- Cobblemon `1.7.3`
- Cobblemon Chromatics

服务端和客户端版本应保持一致。只有服务端安装时，服务端可以保存 profile，但普通客户端无法看到本模组提供的动态渲染。

## 配置

默认配置文件位于：

```text
config/cobblemon_chromatics.json
```

工程内的默认配置模板位于：

```text
pack/shared/config/cobblemon_chromatics.json
```

主要字段：

- `chromaticChanceDenominator`: 基础概率分母，默认 `4096`，表示 `1/4096`。
- `chromaticParentBonusRolls`: 亲本异色加成掷骰次数，默认 `0`。字段已预留，但当前孵蛋逻辑尚未完整接入亲本影响。
- `defaultRenderMode`: 默认渲染模式，当前默认为 `STATIC_ONLY`。
- `enabledLayerNames`: 允许参与异色处理的 resolver layer 名称，默认包含 `"flame"`。
- `developmentLogging`: 开发调试日志开关，正式服建议保持 `false`。

修改配置后建议重启游戏或服务端。当前不承诺热重载。

## 当前功能边界

已实现或已建立的基础能力：

- Pokemon 持久化 profile 的创建、读取和写入。
- 基于 Pokemon UUID 的确定性异色抽取。
- Chromatic 与原生 shiny 独立保存；叠加时保护 shiny 主贴图，只处理允许的具名 layer。
- 地区形态排除策略。
- 服务端到客户端的 profile 同步。
- 客户端 `STATIC_ONLY`、`LAYERS_ONLY`、`FULL` 三种渲染模式的基础路径。
- 动态 HSV 纹理缓存。

仍需继续验证或开发的内容：

- 所有 Cobblemon 创建入口的完整覆盖，例如礼物、化石、NPC 奖励、迁移等。
- 孵蛋遗传规则与亲本概率加成的实际接入。
- 指令、权限、保底机制和管理工具。
- 资源重载时动态纹理缓存的释放与失效。
- 更完整的客户端游戏内 QA。

## 构建

首次构建前同步 Cobblemon 依赖：

```powershell
.\scripts\sync-dependencies.ps1
.\gradlew.bat build
```

构建产物位于：

```text
build/libs/
```

Cobblemon JAR 仅作为本地编译依赖放在 `libs/` 下，不会被打入 Cobblemon Chromatics 的发布 JAR。

## 开发运行

```powershell
.\gradlew.bat runClient
.\gradlew.bat runServer
```

首次运行服务端会因 EULA 停止。请阅读 Minecraft EULA 后再修改 `run/server/eula.txt`。

## 文档索引

- `docs/user-guide.md`: 使用须知与服主配置说明。
- `docs/chromatic-system.md`: 异色系统规则。
- `docs/compatibility.md`: 与 Cobblemon 资源、shiny、地区形态的兼容说明。
- `docs/architecture.md`: 工程边界与服务端/客户端职责。
- `docs/version-matrix.md`: 锁定版本矩阵。
