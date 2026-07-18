# Cobblemon Chromatics

Cobblemon Chromatics 是一个独立的 Fabric 附加模组，为 Cobblemon 提供可持久化、可配置的宝可梦异色（Chromatic）外观。

Chromatic 与 Cobblemon 原生闪光（shiny）是两个独立状态，可以分别存在，也可以同时存在。本工程不会修改、Fork 或重新分发 Cobblemon 本体。

## 版本基线

- Minecraft `1.21.1`
- Cobblemon `1.7.3`
- Fabric Loader `>=0.17.2`
- Fabric API `0.116.13+1.21.1`
- Java `21`
- Mod ID：`cobblemon_chromatics`

## 当前功能

- 服务端为 Pokemon 创建并持久化 Chromatic profile。
- profile 由 Pokemon UUID 确定性生成，不会因实体重载、出球或客户端渲染而重复抽取。
- 默认自然生成概率为 `1/4096`。
- Chromatic 与 shiny 相互独立，支持普通、仅 shiny、仅 chromatic 和 shiny + chromatic 四种状态。
- chromatic 个体的最终主贴图使用 `baseHsv`，所有最终 resolver layers 使用差异明显的 `layerHsv`。
- 即使当前形态没有 layer，profile 仍会保存 `layerHsv`，供以后形态变化或进化后使用。
- 地区形态默认排除：Alola、Galar、Hisui 和 Paldea。
- 服务端向追踪实体的客户端同步权威 profile。
- 摘要界面、PC 预览面板和 PC 槽位显示对应的 shiny、chromatic 或 shiny + chromatic 状态图标。
- PC 搜索支持 `chromatic`、`chromatics`、`!chromatic` 和 `!chromatics`，并可与 Cobblemon 原有条件组合。
- PC 右侧预览模型支持异色渲染，PC 槽位缩略模型保持原有渲染。
- 肩上动作宝可梦根据肩部 NBT 中的 Pokemon UUID 读取客户端 profile，并复用异色主贴图和 layer 渲染。
- 异色与闪光+异色拥有独立的双彩虹环/紧凑环粒子变体，并接入 Cobblemon 的闪光音效与粒子触发路径。
- 提供管理员生成、发放、设置和检查指令。

## 渲染流程

Cobblemon Chromatics 不替代 Cobblemon 的 resolver。Cobblemon 先根据物种、形态、aspects 和 shiny 状态选出最终主贴图及 layers，本模组再处理该结果：

```text
Pokemon 状态
  -> Cobblemon resolver 选择普通或闪光主贴图及 layers
  -> 客户端读取服务端同步的 Chromatic profile
  -> 主贴图应用 baseHsv
  -> 所有最终 layers 应用 layerHsv
  -> 缓存并注册动态纹理
  -> 按 Cobblemon 原有属性正常渲染
```

因此 shiny + chromatic 不需要单独的贴图分支：Cobblemon 选中的闪光贴图会继续接受 `baseHsv` 处理，最终 layers 则接受 `layerHsv` 处理。

复制 layer 时会保留原有的 `enabled`、`tint`、`emissive`、`translucent` 和 `translucentCull` 属性，只替换其纹理来源。像素处理会保留透明区域，并尽量保护近黑轮廓、低饱和区域和近白高光。

动态纹理缓存键包含原贴图、渲染 pass 和 profile hash，避免每帧重新读取和生成纹理。

PC 预览和肩上渲染通过临时的线程上下文传递当前 Pokemon，渲染结束后保证清理；这避免把异色材质错误应用到 PC 槽位或其他 GUI。异色光环则按普通、shiny、chromatic 和 shiny + chromatic 四态选择效果资源，并保留 Cobblemon 原有的触发时机。

## 安装

客户端和服务端均应安装相同版本的以下模组：

- Fabric Loader
- Fabric API
- Cobblemon `1.7.3`
- Cobblemon Chromatics

单人游戏同样需要在客户端实例中安装完整依赖。仅服务端安装时可以持久化 profile，但未安装本模组的客户端无法显示动态异色纹理和状态图标。

请使用普通发布 JAR：

```text
cobblemon-chromatics-0.1.0-SNAPSHOT.jar
```

不要将 `-sources.jar` 放入游戏的 `mods` 目录。Cobblemon 本体和 Fabric Language Kotlin 不包含在 Chromatics JAR 中，必须由游戏实例单独提供。

## 配置

首次运行后，配置文件位于：

```text
config/cobblemon_chromatics.json
```

当前有效字段：

- `schemaVersion`：配置结构版本，当前为 `1`。
- `developmentLogging`：开发日志开关，正式环境建议保持 `false`。
- `chromaticChanceDenominator`：自然生成概率分母，默认 `4096`。
- `chromaticParentBonusRolls`：异色亲本额外抽取次数，默认 `0`；当前孵蛋亲本接入尚未完成。

当前不支持配置热重载，修改后应重启游戏或服务端。

## Profile 数据

当前 profile schema 为 `2`，包含：

- `schemaVersion`
- `chromatic`
- `seed`
- `baseHsv`
- `layerHsv`
- `variant`

本项目仍处于早期开发阶段，当前 schema 不提供旧版 profile 自动迁移。非法或未知 profile 会被保留并报告，不会被静默覆盖或重新随机。

## 管理员指令

所有指令要求 Minecraft 权限等级 `2`：

```text
/chromatics spawn <Pokemon属性>
/chromatics give <玩家> <Pokemon属性>
/chromatics set <实体选择器> <true|false>
/chromatics inspect <实体选择器>
```

示例：

```text
/chromatics spawn charizard level=50
/chromatics spawn charizard shiny level=50
/chromatics give Steve charizard level=50
/chromatics set @e[type=cobblemon:pokemon,sort=nearest,limit=1] true
/chromatics set @e[type=cobblemon:pokemon,sort=nearest,limit=1] false
/chromatics inspect @e[type=cobblemon:pokemon,sort=nearest,limit=1]
```

`spawn` 和 `give` 会直接写入 chromatic profile，不经过自然概率抽取。地区形态仍会被拒绝。`set` 只操作已经生成在世界中的 Pokemon 实体，不能直接选择队伍或 PC 中的 Pokemon。

## 已知限制

- `chromaticParentBonusRolls` 已预留，但孵蛋亲本概率加成尚未完整接入。
- 动态纹理缓存尚未接入资源重载时的完整释放和失效流程。
- 礼物、化石、NPC 奖励和迁移等特殊 Pokemon 创建入口仍需逐项验证。
- 肩上渲染和光环覆盖已完成代码接入，但仍需要在实际客户端中验证不同 Pokemon、形态和加载路径。
- 单元测试覆盖 profile、地区形态策略和状态图标判定，但完整纹理效果仍需要游戏内 QA。

## 构建

确保 `JAVA_HOME` 指向 Java 21，然后执行：

```powershell
.\scripts\sync-dependencies.ps1
.\gradlew.bat clean test build
```

构建产物位于：

```text
build/libs/
```

Cobblemon JAR 仅作为本地编译依赖存放在 `libs/`，不会打入发布 JAR。

开发运行：

```powershell
.\gradlew.bat runClient
.\gradlew.bat runServer
```

首次运行专用服务器会因 EULA 停止。请阅读 Minecraft EULA 后再修改 `run/server/eula.txt`。

## 文档

- `docs/user-guide.md`：安装、配置与管理员指令。
- `docs/chromatic-system.md`：Chromatic profile 与生成规则。
- `docs/compatibility.md`：Cobblemon、shiny 和地区形态兼容边界。
- `docs/architecture.md`：共享、客户端与服务端职责。
- `docs/version-matrix.md`：锁定版本矩阵。

## License

All Rights Reserved.
