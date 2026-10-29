package io.github.angbang852.manjiao.ui.miuix

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.angbang852.manjiao.data.Prefs
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

object InjectedMiuixPanel {
    private var current: Dialog? = null

    fun show(ctx: Context): Boolean {
        val act = ctx as? Activity ?: return false
        if (current?.isShowing == true) return true
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Handler(Looper.getMainLooper()).post { show(act) }
            return true
        }
        return try {
            val env = HostComposeEnvironment(act, Runnable { current?.dismiss() })
            val view = ComposeView(env.context())
            env.attach(view)
            val dialog = Dialog(env.context())
            dialog.setContentView(view)
            dialog.setCanceledOnTouchOutside(true)
            dialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
                addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                attributes = attributes.apply {
                    dimAmount = 0.35f
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                }
            }
            dialog.setOnDismissListener {
                env.close()
                if (current === dialog) current = null
            }
            view.setContent {
                InjectedTheme {
                    InjectedScreen(dialog)
                }
            }
            current = dialog
            dialog.show()
            env.bindBackNavigation(dialog)
            true
        } catch (_: Throwable) {
            false
        }
    }
}

private data class ToggleItem(
    val title: String,
    val summary: String,
    val key: String,
    val default: Boolean,
)

private data class MenuCategory(
    val title: String,
    val summary: String,
    val icon: ImageVector,
    val items: List<ToggleItem>,
)

private val menuCategories = listOf(
    MenuCategory(
        "续火花", "续火花与消息增强", Icons.Filled.AutoAwesome,
        listOf(
            ToggleItem("启用续火花", "每天自动发送", Prefs.K_SPARK_ENABLED, false),
            ToggleItem("静默发送", "不显示发送状态", Prefs.K_SPARK_SILENT, false),
            ToggleItem("消息防撤回", "保留撤回前内容", Prefs.K_SPARK_ANTI_RECALL, true),
            ToggleItem("免已读", "打开会话不标记已读", Prefs.K_SPARK_NO_READ, false),
        )
    ),
    MenuCategory(
        "视频增强", "去水印、时间与计数", Icons.Filled.Movie,
        listOf(
            ToggleItem("去水印", "保存与下载去水印", Prefs.K_SPARK_NO_WATERMARK, true),
            ToggleItem("去开屏广告", "跳过启动广告", Prefs.K_SPARK_NO_SPLASH, true),
            ToggleItem("精确时间", "显示到秒", Prefs.K_SPARK_EXACT_TIME, false),
            ToggleItem("真实计数", "显示真实消息计数", Prefs.K_SPARK_REAL_COUNT, false),
            ToggleItem("隐身访问", "阻止访问轨迹上报", Prefs.K_SPARK_STEALTH_VISITOR, false),
        )
    ),
    MenuCategory(
        "美化", "底栏、右侧与文案", Icons.Filled.Palette,
        listOf(
            ToggleItem("隐藏底栏", "移除底栏 Tab", Prefs.K_BEAUTY_BOTTOM_HIDE, false),
            ToggleItem("底栏透明", "降低底栏不透明度", Prefs.K_BEAUTY_BOTTOM_TRANSPARENT, false),
            ToggleItem("隐藏右侧组件", "隐藏右侧操作栏", Prefs.K_BEAUTY_RIGHT_HIDE, false),
            ToggleItem("去除全屏按钮", "隐藏全屏按钮", Prefs.K_BEAUTY_NO_FULLSCREEN, false),
            ToggleItem("去除金币红包", "移除金币浮窗", Prefs.K_BEAUTY_NO_COIN, false),
            ToggleItem("强制显示时间", "所有视频显示时间", Prefs.K_BEAUTY_FORCE_TIME, false),
            ToggleItem("强制显示 IP", "所有视频显示 IP", Prefs.K_BEAUTY_FORCE_IP, false),
        )
    ),
    MenuCategory(
        "内容过滤", "广告、类型与净化", Icons.Filled.FilterAlt,
        listOf(
            ToggleItem("过滤广告", "移除广告内容", Prefs.K_FLT_ADS, false),
            ToggleItem("过滤广告视频", "移除广告视频", Prefs.K_FLT_ADVIDEO, false),
            ToggleItem("过滤短剧", "移除短剧", Prefs.K_FLT_DRAMA, false),
            ToggleItem("过滤图片", "移除图片", Prefs.K_FLT_IMAGE, false),
            ToggleItem("过滤直播", "移除直播", Prefs.K_FLT_LIVE, false),
            ToggleItem("过滤 AI", "移除 AI 内容", Prefs.K_FLT_AI, false),
            ToggleItem("过滤电商", "移除电商", Prefs.K_FLT_EC, false),
        )
    ),
    MenuCategory(
        "操作确认", "点赞、收藏、关注确认", Icons.Filled.TouchApp,
        listOf(
            ToggleItem("点赞确认", "点赞前确认", Prefs.K_CONFIRM_LIKE, false),
            ToggleItem("收藏确认", "收藏前确认", Prefs.K_CONFIRM_COLLECT, false),
            ToggleItem("关注确认", "关注前确认", Prefs.K_CONFIRM_FOLLOW, false),
        )
    ),
    MenuCategory(
        "性能与调试", "日志、主进程与诊断", Icons.Filled.Speed,
        listOf(
            ToggleItem("静默日志", "关闭刷屏日志", Prefs.K_PERF_QUIET, false),
            ToggleItem("仅主进程注入", "减少子进程 Hook", Prefs.K_PERF_MAINPROC, false),
            ToggleItem("诊断日志", "打开 diag 日志", Prefs.K_DIAG, false),
        )
    ),
)

@Composable
private fun InjectedTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MiuixTheme(colors = colors) {
        CompositionLocalProvider(LocalContentColor provides colors.onBackground) {
            content()
        }
    }
}

@Composable
private fun InjectedScreen(dialog: Dialog) {
    var category by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        Modifier.fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 18.dp, bottom = 20.dp)
    ) {
        item {
            Text("KiraTools", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(
                "注入菜单 · Miuix",
                fontSize = 12.sp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp),
            )
        }

        if (category == null) {
            item {
                Column {
                    menuCategories.forEachIndexed { index, cat ->
                        BasicComponent(
                            title = cat.title,
                            summary = cat.summary,
                            startAction = { IconBadge(cat.icon) },
                            endActions = {
                                Icon(
                                    MiuixIcons.Basic.ArrowRight,
                                    contentDescription = "进入",
                                    modifier = Modifier.size(16.dp),
                                )
                            },
                            onClick = { category = index },
                        )
                    }
                }
            }
            item { Spacer(Modifier.height(10.dp)) }
            item {
                BasicComponent(
                    title = "关闭菜单",
                    summary = "返回快手",
                    startAction = { IconBadge(Icons.Filled.Close) },
                    endActions = {
                        Icon(
                            MiuixIcons.Basic.ArrowRight,
                            contentDescription = "关闭",
                            modifier = Modifier.size(16.dp),
                        )
                    },
                    onClick = { dialog.dismiss() },
                )
            }
        } else {
            val cat = menuCategories[category!!]
            item {
                BasicComponent(
                    title = "返回",
                    summary = cat.title,
                    startAction = { IconBadge(Icons.Filled.ArrowBack) },
                    onClick = { category = null },
                )
            }
            item { Spacer(Modifier.height(10.dp)) }
            item {
                SectionCard {
                    SmallTitle(cat.title)
                    cat.items.forEach { item ->
                        SwitchRow(item.title, item.summary, item.key, item.default)
                    }
                }
            }
            item { Spacer(Modifier.height(10.dp)) }
            item {
                BasicComponent(
                    title = "关闭菜单",
                    summary = "返回快手",
                    startAction = { IconBadge(Icons.Filled.Close) },
                    endActions = {
                        Icon(
                            MiuixIcons.Basic.ArrowRight,
                            contentDescription = "关闭",
                            modifier = Modifier.size(16.dp),
                        )
                    },
                    onClick = { dialog.dismiss() },
                )
            }
        }
    }
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (dark) Color(0xFF1C1C1E) else Color.White)
            .padding(vertical = 4.dp)
    ) {
        content()
    }
}

@Composable
private fun SwitchRow(title: String, summary: String, key: String, default: Boolean) {
    val ctx = LocalContext.current
    var checked by remember(key) { mutableStateOf(Prefs.bool(key, default)) }
    SwitchPreference(
        title = title,
        summary = summary,
        checked = checked,
        onCheckedChange = {
            checked = it
            Prefs.setBoolSync(ctx, key, it)
        },
    )
}

@Composable
private fun IconBadge(icon: ImageVector) {
    Box(
        Modifier.size(34.dp)
            .clip(CircleShape)
            .background(Color(0xFF0A84FF).copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = rememberVectorPainter(icon),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            colorFilter = ColorFilter.tint(Color(0xFF0A84FF)),
        )
    }
}
