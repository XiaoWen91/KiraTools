# KiraTools Miuix Lab

独立实验工程，不参与主工程 `settings.gradle.kts`，不会影响 `com.KiraTools.Wen` 主 APK。

## 目的

验证 Miuix 0.9.4 + Compose 在独立 Android 工程中可用，再决定是否迁入 KiraTools。

## 版本

- Kotlin 2.4.20
- Compose 插件 2.4.20
- AGP 9.1.0
- Miuix UI/Preference 0.9.4
- compileSdk 37 / minSdk 28

## 溯源规则

- 主工程旧 View UI 标记：`legacy-view-ui`
- Miuix 新 UI 标记：`miuix-ui`
- 迁移开关：`ui_miuix_enabled`
- 旧 UI 文件在 Miuix 页面真机验证通过前不删除
- 每个迁移页面必须在本 README 增加映射：旧文件 -> Miuix 文件 -> 验证状态

## 当前映射

| 页面 | 旧实现 | Miuix 实验实现 | 状态 |
|---|---|---|---|
| 设置入口 | 主工程 MainMenuDialog/SettingsActivity | 待迁移 | 未开始 |
| 视图查找 | port/feature/ViewFinderOverlay.java | 待迁移 | 未开始 |
| 组件隐藏 | ScanHideHooks | 待迁移 | 未开始 |
| 日志查看 | port/diagnostics/DebugLogView.java | 待迁移 | 未开始 |

## 构建

在 `miuix-lab` 目录使用兼容 AGP 9.1.0 的 Gradle/JDK 构建：

```bash
./gradlew :app:assembleDebug
```

主工程仍使用原工具链，不受本实验工程影响。
