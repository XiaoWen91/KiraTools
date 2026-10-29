# KiraTools

KiraTools 是一个面向快手的 LSPosed 模块，基于 libxposed API 102，使用 Miuix 与 Liquid Glass 构建模块界面。

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

- KsSpark：续火花与部分 Hook 思路
- Kiri：注入 UI 与交互参考
- DouAI：工程化、视图查找、日志与性能思路
- AndroidLiquidGlass：Liquid Glass 底栏与二级菜单样式
- Miuix：模块设置界面组件
- libxposed：模块 API
- DexKit：结构发现
- Material Icons Extended：图标

## 说明

本项目仅供学习与研究，请遵守相关服务条款与法律法规。使用本模块产生的任何后果由使用者自行承担。
