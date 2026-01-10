<div align="center">

# AutoViaUpdater v10.0.0

让您的 Via 插件保持最新 —> 自动、安全、按计划更新。

[![SpigotMC](https://img.shields.io/badge/SpigotMC-下载-orange)](https://www.spigotmc.org/resources/autoviaupdater.109331/)
![支持平台](https://img.shields.io/badge/支持平台-Spigot%20%7C%20Paper%20%7C%20Folia%20%7C%20Velocity%20%7C%20BungeeCord-5A67D8)
![支持版本](https://img.shields.io/badge/支持版本-1.8%E2%86%92Latest-2EA043)
![可用Java](https://img.shields.io/badge/可用Java-8%2B-1F6FEB)
![许可证](https://img.shields.io/badge/许可证-MIT-0E8A16)

</div>

> 插件由mc506lw进行二改和翻译

> 简而言之
> 放入 jar 文件 ➜ 它会检查 Jenkins 并按计划更新 ViaVersion / ViaBackwards / ViaRewind（以及 Spigot 上的 ViaRewind-Legacy）。
> 可选快照版本，支持 DEV 和 Java8 构建任务。

---

## 目录

* [亮点](#亮点)
* [更新内容](#更新内容)
* [平台与要求](#平台与要求)
* [安装](#安装)
* [快速开始](#快速开始)
* [配置](#配置)
    * [Spigot/Bungee (`config.yml`)](#spigotbungee-configyml)
    * [Velocity (`config.toml`)](#velocity-configtoml)
    * [定时任务](#定时任务)
    * [语言设置](#语言设置)
* [命令与权限](#命令与权限)
* [工作原理](#工作原理)
* [故障排除与常见问题](#故障排除与常见问题)
* [从源码构建](#从源码构建)
* [更新日志亮点](#更新日志亮点)

---

## 亮点

* 从 Jenkins 自动更新 Via 生态系统插件。
* 可控制的快照处理：最新的整体版本 vs 最新的非快照版本。
* 每个插件支持 DEV 和 Java 8 构建任务。
* 简单的时间间隔或 UNIX cron 调度，加上启动延迟。
* 安全重启：可选的广播和延迟关机。

## 更新内容

* ViaVersion
* ViaBackwards
* ViaRewind
* ViaRewind Legacy Support（仅限 Spigot）

## 平台与要求

* 平台：Spigot、Paper、Folia；Velocity；BungeeCord
* Minecraft：1.8 → 最新版本
* Java：8+

## 安装

1. 从 Spigot 下载最新版本。
2. 将 jar 文件放入服务器的 `plugins/` 文件夹（在代理服务器上，使用代理服务器的 `plugins/`）。
3. 启动服务器以生成配置文件和 `versions.yml`。
4. 调整设置并重启。

## 快速开始

* 运行 `/updatevias` 立即检查更新。
* 保持 `snapshot: true` 以始终获取最新构建；设置为 `false` 以获取最新的非快照版本。
* 根据需要为每个插件启用 `dev` 或 `java8`。

## 配置

配置文件位于：

* Spigot/Bungee：`plugins/AutoViaUpdater/config.yml`
* Velocity：`plugins/AutoViaUpdater/config.toml`

### Spigot/Bungee (`config.yml`)

```yaml
ViaVersion:
  enabled: true
  snapshot: true   # 最新整体版本；false = 最新非快照版本
  dev: false       # 使用 Jenkins -DEV 构建任务
  java8: false     # 使用 Jenkins -Java8 构建任务
  fileName: ""     # 自定义下载文件名（留空则使用默认名称）

ViaBackwards:
  enabled: true
  snapshot: true
  dev: false
  java8: false
  fileName: ""     # 自定义下载文件名（留空则使用默认名称）

ViaRewind:
  enabled: true
  snapshot: true
  dev: false
  java8: false
  fileName: ""     # 自定义下载文件名（留空则使用默认名称）

ViaRewind-Legacy:
  enabled: true    # 仅限 Spigot
  snapshot: true
  dev: false       # DEV 路径使用 ViaRewind 视图下的 "...%20Support%20DEV"
  fileName: ""     # 自定义下载文件名（留空则使用默认名称）

# 调度
Check-Interval: 60        # 分钟；当 cron 为空时使用
Cron-Expression: ""       # UNIX cron（5 个字段）；设置后覆盖间隔
Delay: 5                  # 启动后首次检查前的延迟（秒）

# 成功更新后可选的安全重启
AutoRestart: false
AutoRestart-Delay: 60
AutoRestart-Message: '&c服务器将在 1 分钟后重启！'

# 语言设置 / Language Settings
Language: zh-CN          # 语言设置：zh-CN（中文）或 en-US（英文）
```

### Velocity (`config.toml`)

```toml
Check-Interval = 60
Cron-Expression = ""
Delay = 5

AutoRestart = false
AutoRestart-Delay = 60
AutoRestart-Message = '&c服务器将在 1 分钟后重启！'

# 语言设置 / Language Settings
Language = "zh-CN"        # 语言设置：zh-CN（中文）或 en-US（英文）

[ViaVersion]
enabled = true
snapshot = true
dev = false
java8 = false
fileName = ""             # 自定义下载文件名（留空则使用默认名称）

[ViaBackwards]
enabled = true
snapshot = true
dev = false
java8 = false
fileName = ""             # 自定义下载文件名（留空则使用默认名称）

[ViaRewind]
enabled = true
snapshot = true
dev = false
java8 = false
fileName = ""             # 自定义下载文件名（留空则使用默认名称）

# ViaRewind-Legacy 仅限 Spigot；Velocity 会忽略它
```

### 定时任务

* 每 2 小时：`0 */2 * * *`
* 每天 05:00：`0 5 * * *`
* 每 15 分钟：`*/15 * * * *`

如果 cron 为空，插件将使用 `Check-Interval`（分钟）和初始 `Delay`（秒）。

### 语言设置

插件支持多语言，可以在配置文件中设置语言：

* `zh-CN` - 简体中文（默认）
* `en-US` - 英语

语言文件位于 `plugins/AutoViaUpdater/lang/` 目录下，您可以自定义语言文件中的文本内容。

**首次启动自动检测：**

插件首次启动时会自动检测已安装的 Via 插件，并根据检测到的插件自动配置：
- 只启用已安装的插件
- 自动设置下载文件名为当前安装的插件文件名
- 禁用未安装的插件

**自定义文件名：**

您可以在配置文件中为每个插件设置自定义的下载文件名（`fileName` 参数）。如果留空，插件将使用默认名称。

## 命令与权限

* `/updatevias` — 立即触发检查
* `/autoviaupdater reload` — 重新加载配置文件和语言设置

权限：
* `autoviaupdater.admin` — 使用所有命令（在 Velocity/Bungee 上需要；在 Spigot 上默认为 OP）

## 工作原理

* 插件为每个选定的构建任务调用 Jenkins API。
* 选择：
    * 快照开启 → 最新整体构建（无论是否为 "-SNAPSHOT"）。
    * 快照关闭 → 最新非快照构建。
    * DEV/Java8 标志在可用时选择 `-DEV` / `-Java8` 构建任务。
* 下载：
    * 新的 jar 文件进入 `plugins/`。如果存在匹配的 jar 文件，它将被暂存到 `plugins/update/` 以便在重启时进行干净的替换。
* 跟踪：
    * 最后安装的构建号保存在 `plugins/AutoViaUpdater/versions.yml` 中。

Jenkins 快捷链接

* ViaVersion DEV：`https://ci.viaversion.com/job/ViaVersion-DEV/`
* ViaBackwards DEV：`https://ci.viaversion.com/view/ViaBackwards/job/ViaBackwards-DEV/`
* ViaRewind DEV：`https://ci.viaversion.com/view/ViaRewind/job/ViaRewind-DEV/`
* ViaRewind Legacy Support DEV：`https://ci.viaversion.com/view/ViaRewind/job/ViaRewind%20Legacy%20Support%20DEV/`

## 故障排除与常见问题

* 没有更新 — 确保目标 Via 插件已安装（更新器会替换现有的插件）。
* 错误的渠道 — 检查特定插件的 `snapshot/dev/java8` 标志。
* 已下载但未应用 — 启用 `AutoRestart` 或手动重启。如果 `plugins/update` 存在，jar 文件将在重启时移动。
* 未找到构建 — Jenkins 可能宕机或构建任务已移动。尝试再次运行 `/updatevias` 或验证构建任务 URL。

## 从源码构建

```bash
mvn -DskipTests package
```

从 `target/` 获取打包后的 jar 文件。

## 更新日志亮点

* **多语言支持** - 支持中文和英文，可自定义语言文件
* **首次启动自动检测** - 自动检测已安装的 Via 插件并配置
* **自定义文件名** - 支持为每个插件设置自定义下载文件名
* **重载命令** - 新增 `/autoviaupdater reload` 命令重新加载配置
* **改进的日志系统** - 使用标准日志系统替代 System.out/err
* **双语注释** - 配置文件支持中英文双语注释
* Folia 安全调度和 Spigot 上的严格 Bukkit 访问。
* 清晰的快照选择规则（最新整体版本 vs 仅非快照版本）。
* 正确的 ViaVersion 和 ViaRewind Legacy Support 的 DEV 构建任务。
* ViaRewind-Legacy-Support 的人性化文件名。
* HTTP 超时和改进的错误处理。
