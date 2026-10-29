package io.github.angbang852.kira.miuixlab

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import io.github.angbang852.kira.miuixlab.liquid.LiquidBottomTab
import io.github.angbang852.kira.miuixlab.liquid.LiquidBottomTabs
import io.github.angbang852.kira.miuixlab.liquid.LiquidDialogContent
import io.github.angbang852.manjiao.data.Prefs
import io.github.angbang852.manjiao.data.SyncService
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.LocalContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

private data class AppTab(val label: String, val icon: ImageVector)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LabTheme {
                KiraToolsApp()
            }
        }
    }
}

@Composable
private fun LabTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MiuixTheme(colors = colors) {
        CompositionLocalProvider(LocalContentColor provides colors.onBackground) {
            content()
        }
    }
}

@Composable
private fun KiraToolsApp() {
    val backdrop = rememberLayerBackdrop()
    var page by remember { mutableStateOf(0) }
    var dialogTitle by remember { mutableStateOf<String?>(null) }
    var dialogMessage by remember { mutableStateOf("") }
    val tabs = listOf(
        AppTab("首页", Icons.Filled.Home),
        AppTab("增强", Icons.Filled.AutoAwesome),
        AppTab("美化", Icons.Filled.Palette),
        AppTab("过滤", Icons.Filled.FilterAlt),
        AppTab("设置", Icons.Filled.Settings),
    )
    val light = !isSystemInDarkTheme()
    val accent = if (light) Color(0xFF0088FF) else Color(0xFF0091FF)
    val contentColor = if (light) Color.Black else Color.White
    val pageBackground = if (light) Color(0xFFF5F5F7) else Color(0xFF0F0F10)

    Box(Modifier.fillMaxSize().background(pageBackground)) {
        Box(
            Modifier.fillMaxSize()
                .background(pageBackground)
                .layerBackdrop(backdrop)
        ) {
            when (page) {
                0 -> HomePage()
                1 -> EnhancedPage()
                2 -> BeautifyPage { title, message ->
                    dialogTitle = title
                    dialogMessage = message
                }
                3 -> FilterPage()
                else -> SettingsPage { title, message ->
                    dialogTitle = title
                    dialogMessage = message
                }
            }
        }

        LiquidBottomTabs(
            selectedTabIndex = { page },
            onTabSelected = { page = it },
            backdrop = backdrop,
            tabsCount = tabs.size,
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(horizontal = 18.dp, vertical = 22.dp),
        ) {
            tabs.forEachIndexed { index, tab ->
                val color = if (page == index) accent else contentColor.copy(0.7f)
                LiquidBottomTab(onClick = { page = index }) {
                    MaterialIcon(tab.icon, color, Modifier.size(22.dp))
                    BasicText(tab.label, style = TextStyle(color = color, fontSize = 10.sp))
                }
            }
        }

        dialogTitle?.let { title ->
            LiquidDialogContent(
                backdrop = backdrop,
                title = title,
                message = dialogMessage,
                onDismiss = { dialogTitle = null },
                onConfirm = { dialogTitle = null },
            )
        }
    }
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val background = if (dark) Color(0xFF1C1C1E) else Color.White
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(background)
            .padding(vertical = 6.dp)
    ) { content() }
}

@Composable
private fun PageHeader(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 16.dp)) {
        Text(title, fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text(subtitle, fontSize = 13.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary)
    }
}

@Composable
private fun HomePage() {
    val context = LocalContext.current
    val active = try { SyncService.isActive() } catch (_: Throwable) { false }
    val heartbeat = try { SyncService.isHeartbeatFresh() } catch (_: Throwable) { false }
    val framework = try { SyncService.frameworkInfo() } catch (_: Throwable) { null }
    val targetVersion = try {
        context.packageManager.getPackageInfo("com.smile.gifmaker", 0).versionName ?: "未知"
    } catch (_: Throwable) { "未安装" }
    val device = "${Build.MANUFACTURER} ${Build.MODEL} · Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"

    LazyColumn(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 24.dp)) {
        item { PageHeader("KiraTools", "模块状态") }
        item {
            SectionCard {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(
                        if (active) "注入成功" else "未检测到注入",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) Color(0xFF34C759) else Color(0xFFFF3B30),
                    )
                    Text(
                        if (active) "快手进程已加载模块" else "请检查 LSPosed 作用域与模块启用状态",
                        fontSize = 13.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(top = 6.dp),
                    )
                    Text(
                        "心跳：${if (heartbeat) "正常" else "未就绪"}",
                        fontSize = 12.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    framework?.let {
                        Text(
                            "框架：${it.first} ${it.second}",
                            fontSize = 12.sp,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("目标应用")
                InfoRow("应用", "快手", Icons.Filled.Android)
                InfoRow("包名", "com.smile.gifmaker", Icons.Filled.Inventory2)
                InfoRow("版本", targetVersion, Icons.Filled.Info)
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("适配信息")
                InfoRow("适配版本", "14.8.40.50567", Icons.Filled.Build)
                InfoRow("设备", device, Icons.Filled.PhoneAndroid)
            }
        }
        item { Spacer(Modifier.height(96.dp)) }
    }
}

@Composable
private fun EnhancedPage() {
    LazyColumn(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 24.dp)) {
        item { PageHeader("增强功能", "续火花、防撤回、消息与手势") }
        item {
            SectionCard {
                SmallTitle("续火花")
                SwitchRow("启用续火花", "每天自动给勾选好友发送消息", Prefs.K_SPARK_ENABLED, false)
                SwitchRow("静默发送", "不显示发送状态", Prefs.K_SPARK_SILENT, false)
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("消息增强")
                SwitchRow("消息防撤回", "保留撤回前内容", Prefs.K_SPARK_ANTI_RECALL, true)
                SwitchRow("免已读", "打开会话不标记已读", Prefs.K_SPARK_NO_READ, false)
                SwitchRow("真实计数", "显示真实消息计数", Prefs.K_SPARK_REAL_COUNT, false)
                SwitchRow("隐身访问（实验性）", "阻止访问轨迹上报", Prefs.K_SPARK_STEALTH_VISITOR, false)
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("视频增强")
                SwitchRow("去水印", "下载与保存去水印", Prefs.K_SPARK_NO_WATERMARK, true)
                SwitchRow("去开屏广告", "跳过启动广告", Prefs.K_SPARK_NO_SPLASH, true)
                SwitchRow("精确时间", "显示精确到秒的时间", Prefs.K_SPARK_EXACT_TIME, false)
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("操作确认")
                SwitchRow("点赞确认", "点赞前弹出确认框", Prefs.K_CONFIRM_LIKE, false)
                SwitchRow("收藏确认", "收藏前弹出确认框", Prefs.K_CONFIRM_COLLECT, false)
                SwitchRow("关注确认", "关注前弹出确认框", Prefs.K_CONFIRM_FOLLOW, false)
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("手势与播放")
                SwitchRow("禁止双击点赞", "防止误触", Prefs.K_GS_NO_DBL_LIKE, false)
                SwitchRow("双击打开评论区", "双击视频打开评论", Prefs.K_GS_OPEN_COMMENT, false)
                SwitchRow("双击打开模块菜单", "双击视频打开菜单", Prefs.K_GS_OPEN_MENU, false)
                SwitchRow("停止循环播放", "播放结束不循环", Prefs.K_PB_NO_LOOP, false)
                SwitchRow("后台暂停播放", "切后台自动暂停", Prefs.K_PB_BG_PAUSE, false)
                SwitchRow("禁止自动进入直播间", "阻止自动跳转直播", Prefs.K_PB_NO_AUTO_LIVE, false)
            }
        }
        item { Spacer(Modifier.height(96.dp)) }
    }
}

@Composable
private fun BeautifyPage(onDialog: (String, String) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 24.dp)) {
        item { PageHeader("美化", "底栏、右侧、文案与沉浸式") }
        item {
            SectionCard {
                SmallTitle("底栏")
                SwitchRow("隐藏底栏", "移除底栏 Tab", Prefs.K_BEAUTY_BOTTOM_HIDE, false)
                SwitchRow("底栏透明", "降低底栏不透明度", Prefs.K_BEAUTY_BOTTOM_TRANSPARENT, false)
                SwitchRow("底栏自适应", "加号与 Tab 自动补位", Prefs.K_BEAUTY_BOTTOM_AUTO, true)
                ActionRow("替换底栏文字", "自定义底栏文案", Icons.Filled.Edit) {
                    onDialog("替换底栏文字", "底栏文字编辑入口待接入 Miuix 版本。")
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("右侧与全屏")
                SwitchRow("隐藏右侧组件", "隐藏右侧操作栏", Prefs.K_BEAUTY_RIGHT_HIDE, false)
                SwitchRow("去除全屏按钮", "隐藏全屏观看按钮", Prefs.K_BEAUTY_NO_FULLSCREEN, false)
                SwitchRow("去除金币红包", "移除金币红包浮窗", Prefs.K_BEAUTY_NO_COIN, false)
                SwitchRow("全屏隐藏状态栏", "全屏时隐藏状态栏", Prefs.K_BEAUTY_HIDE_STATUS_BAR, false)
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("文案与时间")
                SwitchRow("强制显示时间", "所有视频显示时间", Prefs.K_BEAUTY_FORCE_TIME, false)
                SwitchRow("时间精确到秒", "时间格式带秒", Prefs.K_BEAUTY_TIME_SECONDS, false)
                SwitchRow("强制显示 IP", "所有视频显示 IP", Prefs.K_BEAUTY_FORCE_IP, false)
                SwitchRow("官方时间格式", "使用官方日期格式", Prefs.K_BEAUTY_OFFICIAL_DATE, false)
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("分享与透明度")
                SwitchRow("分享面板隐藏项", "隐藏指定分享项", Prefs.K_BEAUTY_SHARE_HIDE, false)
                SwitchRow("暂停恢复透明度", "暂停时恢复默认透明度", Prefs.K_BEAUTY_ALPHA_PAUSE_RESET, false)
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("沉浸式")
                SwitchRow("一键沉浸", "隐藏顶栏与底栏", Prefs.K_IMM_ON, false)
                SwitchRow("隐藏顶栏项", "自定义顶栏隐藏", Prefs.K_IMM_TOPBAR_ON, false)
                SwitchRow("隐藏右侧项", "自定义右侧隐藏", Prefs.K_IMM_RIGHT_ON, false)
                SwitchRow("隐藏昵称", "隐藏视频昵称", Prefs.K_IMM_NICKNAME, false)
                SwitchRow("隐藏收藏", "隐藏收藏按钮", Prefs.K_IMM_COLLECTION, false)
                SwitchRow("隐藏底栏", "沉浸式隐藏底栏", Prefs.K_IMM_BOTTOM_BAR, false)
                SwitchRow("隐藏金币", "沉浸式隐藏金币", Prefs.K_IMM_GOLD, false)
            }
        }
        item { Spacer(Modifier.height(96.dp)) }
    }
}

@Composable
private fun FilterPage() {
    LazyColumn(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 24.dp)) {
        item { PageHeader("内容过滤", "广告、类型、关键词与性能") }
        item {
            SectionCard {
                SmallTitle("内容类型")
                SwitchRow("过滤广告", "移除广告内容", Prefs.K_FLT_ADS, false)
                SwitchRow("过滤广告视频", "移除广告视频", Prefs.K_FLT_ADVIDEO, false)
                SwitchRow("过滤短剧", "移除短剧内容", Prefs.K_FLT_DRAMA, false)
                SwitchRow("过滤图片", "移除图片内容", Prefs.K_FLT_IMAGE, false)
                SwitchRow("过滤直播", "移除直播内容", Prefs.K_FLT_LIVE, false)
                SwitchRow("过滤 AI", "移除 AI 内容", Prefs.K_FLT_AI, false)
                SwitchRow("过滤电商", "移除电商内容", Prefs.K_FLT_EC, false)
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("规则")
                SwitchRow("点赞过滤", "按点赞数过滤", Prefs.K_FLT_LIKE_ON, false)
                SwitchRow("关键词过滤", "按关键词过滤", Prefs.K_FLT_KW_ON, false)
                SwitchRow("AI 疑似过滤", "过滤疑似 AI 内容", Prefs.K_FLT_AI_SUSPECT, false)
                SwitchRow("启动清理", "启动时清理缓存", Prefs.K_FLT_BOOTFLUSH, false)
                SwitchRow("首页补池", "首页空响应时补池", Prefs.K_FLT_HOMEREFILL, false)
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("净化")
                SwitchRow("净化推送", "过滤推送内容", Prefs.K_PURIFY_PUSH, false)
                SwitchRow("净化日志", "减少日志刷屏", Prefs.K_PURIFY_LOG, false)
                SwitchRow("净化 WebView", "过滤 WebView 内容", Prefs.K_PURIFY_WEBVIEW, false)
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("性能与调试")
                SwitchRow("静默日志", "关闭刷屏日志", Prefs.K_PERF_QUIET, false)
                SwitchRow("仅主进程注入", "减少子进程 Hook", Prefs.K_PERF_MAINPROC, false)
                SwitchRow("诊断日志", "打开 diag 调试日志", Prefs.K_DIAG, false)
            }
        }
        item { Spacer(Modifier.height(96.dp)) }
    }
}

@Composable
private fun SettingsPage(onDialog: (String, String) -> Unit) {
    val context = LocalContext.current
    LazyColumn(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 24.dp)) {
        item { PageHeader("设置", "配置、支持与工具") }
        item {
            SectionCard {
                SmallTitle("配置")
                ActionRow("导出配置", "保存当前设置到文件", Icons.Filled.Download) {
                    onDialog("导出配置", "配置导出功能待接入文件保存。当前 Prefs 已迁移，后续可接 JSON 导出。")
                }
                ActionRow("导入配置", "从文件恢复设置", Icons.Filled.Upload) {
                    onDialog("导入配置", "配置导入功能待接入文件选择。当前 Prefs 已迁移，后续可接 JSON 导入。")
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("支持")
                ActionRow("赞助开发", "支持持续维护和版本适配", Icons.Filled.Favorite) {
                    onDialog("赞助开发", "赞助入口待接入。感谢支持。")
                }
                ActionRow("交流群", "加入交流群获取更新", Icons.Filled.Group) {
                    onDialog("交流群", "交流群入口待接入。")
                }
                ActionRow("仓库地址", "打开 GitHub 仓库", Icons.Filled.Link) {
                    openUrl(context, "https://github.com/XiaoWen91/XiaoWenmiux")
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("工具")
                ActionRow("模块日志", "查看 slowkick.log", Icons.Filled.Article) {
                    onDialog("模块日志", "日志查看页待接入 Miuix 版本。")
                }
                ActionRow("视图查找", "选择控件并复制定位", Icons.Filled.Search) {
                    onDialog("视图查找", "视图查找页待接入 Miuix 版本。")
                }
                ActionRow("模块状态", "查看 Hook 安装与运行状态", Icons.Filled.BugReport) {
                    onDialog("模块状态", "模块状态页待接入 Miuix 版本。")
                }
            }
        }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            SectionCard {
                SmallTitle("审计")
                SwitchRow("审计开关", "记录过滤与命中", Prefs.K_AUDIT_ON, false)
                SwitchRow("审计反馈", "显示审计反馈", Prefs.K_AUDIT_FEEDBACK, false)
            }
        }
        item { Spacer(Modifier.height(96.dp)) }
    }
}

@Composable
private fun SwitchRow(title: String, summary: String, key: String, default: Boolean) {
    val context = LocalContext.current
    var checked by remember(key) { mutableStateOf(Prefs.bool(key, default)) }
    SwitchPreference(
        title = title,
        summary = summary,
        checked = checked,
        onCheckedChange = {
            checked = it
            Prefs.setBoolSync(context, key, it)
        },
    )
}

@Composable
private fun InfoRow(title: String, summary: String, icon: ImageVector) {
    BasicComponent(
        title = title,
        summary = summary,
        startAction = { IconBadge(icon) },
    )
}

@Composable
private fun ActionRow(title: String, summary: String, icon: ImageVector, onClick: () -> Unit) {
    BasicComponent(
        title = title,
        summary = summary,
        startAction = { IconBadge(icon) },
        endActions = {
            Icon(
                MiuixIcons.Basic.ArrowRight,
                contentDescription = "进入",
                modifier = Modifier.size(16.dp),
            )
        },
        onClick = onClick,
    )
}

@Composable
private fun IconBadge(icon: ImageVector) {
    Box(
        Modifier.size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFF0A84FF).copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center,
    ) {
        MaterialIcon(icon, Color(0xFF0A84FF), Modifier.size(18.dp))
    }
}

@Composable
private fun MaterialIcon(icon: ImageVector, tint: Color, modifier: Modifier = Modifier) {
    Image(
        painter = rememberVectorPainter(icon),
        contentDescription = null,
        modifier = modifier,
        colorFilter = ColorFilter.tint(tint),
    )
}

private fun openUrl(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (_: Throwable) {
        Toast.makeText(context, "无法打开链接", Toast.LENGTH_SHORT).show()
    }
}
