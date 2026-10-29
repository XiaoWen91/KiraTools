# KiraTools

## 说明与免责声明

KiraTools 是一个第三方、非官方的 LSPosed 实验性模块，仅用于个人学习、研究与技术交流。

本项目与快手官方及其关联公司没有任何关系，不属于快手官方项目，也未获得快手的授权、认可、赞助、支持或背书。项目中出现的“快手”名称仅用于说明适配目标，不代表任何官方关联。开发者、贡献者、维护者、分发者均不代表快手，也不参与快手的产品设计、运营、审核、风控或客服。

使用本项目即表示你理解并同意以下全部内容：

1. 本项目按“现状”和“可用现状”提供，不提供任何明示或暗示的保证，包括但不限于可用性、稳定性、安全性、准确性、完整性、兼容性、合法性或不侵权保证。
2. 本项目可能修改宿主应用行为，可能导致账号异常、功能受限、数据丢失、应用崩溃、设备异常、系统不稳定或其他不可预期后果。
3. 你可能因使用本项目而违反快手用户协议、社区规则、平台政策或相关服务条款，由此产生的限制、封禁、处罚、申诉失败或其他后果由使用者自行承担。
4. 开发者、贡献者、维护者、分发者不对因使用或无法使用本项目造成的任何直接、间接、偶然、特殊、惩罚性、附带性或后果性损失承担责任，包括但不限于账号损失、数据损失、设备损坏、利润损失、商誉损失、隐私泄露、法律纠纷或第三方索赔。
5. 你应自行评估所在地区的法律法规、平台规则、账号风险和设备环境；不得将本项目用于任何违法、侵权、破坏、绕过安全机制、干扰平台运营或其他不当用途。
6. 本项目不收集、不上传你的账号密码；但模块运行、日志、配置同步、本地缓存和宿主应用行为仍可能产生本地数据，请自行评估并妥善保管。
7. 本项目可能包含实验性功能，可能随时修改、失效、删除或停止维护，不承诺长期兼容任何快手版本，也不承诺提供技术支持、问题修复或版本更新。
8. 任何第三方对本项目的修改、二次分发、打包、售卖、宣传、改名或再发布均与原作者无关，原作者不对第三方版本负责，也不对第三方版本造成的任何后果负责。
9. 本项目不是快手官方产品，不得以 KiraTools 名义冒充快手官方、授权方、合作方或服务提供方。
10. 在适用法律允许的最大范围内，本免责声明构成你与本项目开发者之间的全部风险分配约定；即使本声明中的某一条被认定无效，其余条款仍应继续有效。
11. 本免责声明可能随项目更新而调整，继续使用即视为接受更新后的内容。

如果你不同意上述任何内容，请立即停止使用并卸载本项目。

- 仓库：https://github.com/XiaoWen91/KiraTools
- 适配快手：14.8.40.50567
- 包名：com.KiraTools.MiuixLab
- 最低系统：Android 9 / API 28
- 构建方式：GitHub Actions

## 功能

- 续火花：自动发送、静默发送、固定消息、测试续火花
- 消息增强：防撤回、免已读、真实计数
- 视频增强：去水印、去开屏广告、精确时间
- 美化：底栏隐藏/透明/自适应、右侧组件隐藏、全屏按钮、金币红包、IP/时间强制显示
- 区域透明度：顶栏、右侧、左下角、底栏滑杆
- 隐藏 Tab / 视频控件：扫描结果与预设隐藏项
- 操作确认：点赞、收藏、关注二次确认
- 内容过滤：广告、短剧、图片、直播、AI、电商、关键词、点赞阈值
- 性能与调试：静默日志、仅主进程注入、诊断日志
- 实验功能：隐身访问

## 界面

- 桌面只保留首页与设置
- 首页显示注入状态、目标应用、适配版本、设备信息
- 快手内模块菜单使用 Miuix 聚合卡片与分类二级页
- 二级菜单与操作确认使用 Liquid Glass
- 图标使用 Material Icons Extended 与 Miuix Icons

## 构建

推送 main 后进入 GitHub Actions：

1. 打开 Actions
2. 选择 Build Miuix Lab
3. Run workflow
4. 下载 KiraMiuixLab-debug

构建时间由 BuildConfig.BUILD_TIME 在构建时生成。

## 安装

1. 安装 APK
2. 打开 LSPosed
3. 启用 KiraTools
4. 作用域只勾选快手
5. 强制停止快手或重启手机

## 项目结构

- app/src/main/kotlin/io/github/angbang852/manjiao：模块核心、Hook、数据、兼容 UI
- app/src/main/kotlin/io/github/angbang852/manjiao/ui/miuix：注入 Miuix 面板
- app/src/main/java/io/github/angbang852/kira/miuixlab：桌面 Compose UI
- app/src/main/java/io/github/angbang852/kira/miuixlab/liquid：Liquid Glass 组件
- app/src/main/resources/META-INF/xposed：libxposed 静态作用域与入口

## 致谢

- AndroidLiquidGlass：Liquid Glass 底栏与二级菜单样式，仓库 https://github.com/Kyant0/AndroidLiquidGlass
- Miuix：模块设置界面组件，仓库 https://github.com/compose-miuix-ui/miuix
- libxposed：模块 API，仓库 https://github.com/libxposed
- DexKit：结构发现，仓库 https://github.com/LuckyPray/DexKit
- Material Icons Extended：图标，文档 https://developer.android.com/jetpack/androidx/releases/compose-material

## 责任排除

本项目不是快手官方产品，也不属于快手。开发者不代表快手，不参与快手的产品设计、运营、审核或风控。任何因账号、内容、数据、设备、网络或第三方服务产生的问题，均与开发者无关。

使用者应自行备份重要数据，并自行承担安装、启用、配置、调试、卸载、二次分发和传播本项目的全部风险。

本项目仅用于学习与研究，请遵守相关法律法规及服务条款。
