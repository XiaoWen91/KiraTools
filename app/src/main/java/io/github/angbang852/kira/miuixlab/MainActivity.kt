package io.github.angbang852.kira.miuixlab

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.theme.LocalContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

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
    var showDialog by remember { mutableStateOf(false) }
    val tabs = listOf(
        LiquidTab(label = "首页", icon = KiraIcon.HOME),
        LiquidTab(label = "设置", icon = KiraIcon.SETTINGS),
    )
    val light = !isSystemInDarkTheme()
    val accent = if (light) Color(0xFF0088FF) else Color(0xFF0091FF)
    val contentColor = if (light) Color.Black else Color.White

    Box(Modifier.fillMaxSize().background(Color.White)) {
        Box(
            Modifier.fillMaxSize()
                .background(Color.White)
                .layerBackdrop(backdrop)
        ) {
            if (page == 0) HomePage() else SettingsPage(onPreview = { showDialog = true })
        }

        LiquidBottomTabs(
            selectedTabIndex = { page },
            onTabSelected = { page = it },
            backdrop = backdrop,
            tabsCount = tabs.size,
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 24.dp),
        ) {
            tabs.forEachIndexed { index, tab ->
                val color = if (page == index) accent else contentColor.copy(0.7f)
                LiquidBottomTab(onClick = { page = index }) {
                    CanvasIcon(
                        icon = tab.icon,
                        tint = color,
                        modifier = Modifier.size(24.dp),
                    )
                    BasicText(
                        tab.label,
                        style = TextStyle(color = color, fontSize = 11.sp),
                    )
                }
            }
        }

        if (showDialog) {
            LiquidDialogContent(
                backdrop = backdrop,
                title = "二级菜单",
                message = "这是从 AndroidLiquidGlass 的 DialogContent 移植的二级菜单样式。",
                onDismiss = { showDialog = false },
                onConfirm = { showDialog = false },
            )
        }
    }
}

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val background = if (dark) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)
    Column(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(background)
            .padding(vertical = 6.dp)
    ) {
        content()
    }
}

@Composable
private fun HomePage() {
    Column(
        Modifier.fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
            .padding(bottom = 96.dp)
    ) {
        Text(
            "KiraTools",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            "版本 ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            fontSize = 13.sp,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        )
        Spacer(Modifier.height(20.dp))

        SectionCard {
            SmallTitle("构建信息")
            InfoRow("构建时间", BuildConfig.BUILD_TIME, KiraIcon.CLOCK)
            InfoRow("包名", "com.KiraTools.MiuixLab", KiraIcon.PACKAGE)
            InfoRow("界面", "Liquid Glass + Miuix", KiraIcon.SPARKLE)
        }

        Spacer(Modifier.height(16.dp))

        SectionCard {
            SmallTitle("说明")
            Text(
                "这是独立实验工程，用于验证 Miuix 与 Liquid Glass 底栏、二级菜单样式。",
                fontSize = 14.sp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun SettingsPage(onPreview: () -> Unit) {
    val context = LocalContext.current

    LazyColumn(
        Modifier.fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        item {
            Text(
                "设置",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 20.dp),
            )
        }

        item {
            SectionCard {
                SmallTitle("配置")
                SettingsRow("导出配置", "保存当前设置到文件", KiraIcon.DOWNLOAD) {
                    Toast.makeText(context, "导出配置：待接入文件保存", Toast.LENGTH_SHORT).show()
                }
                SettingsRow("导入配置", "从文件恢复设置", KiraIcon.UPLOAD) {
                    Toast.makeText(context, "导入配置：待接入文件选择", Toast.LENGTH_SHORT).show()
                }
            }
        }

        item { Spacer(Modifier.height(12.dp)) }

        item {
            SectionCard {
                SmallTitle("支持")
                SettingsRow("二级菜单预览", "打开 Liquid Glass 二级菜单", KiraIcon.SPARKLE) {
                    onPreview()
                }
                SettingsRow("赞助开发", "支持持续维护和版本适配", KiraIcon.HEART) {
                    Toast.makeText(context, "感谢支持，赞助入口待接入", Toast.LENGTH_SHORT).show()
                }
                SettingsRow("交流群", "加入 QQ 群获取更新与支持", KiraIcon.CHAT) {
                    Toast.makeText(context, "交流群入口待接入", Toast.LENGTH_SHORT).show()
                }
                SettingsRow("仓库地址", "打开 GitHub 仓库", KiraIcon.LINK) {
                    openUrl(context, "https://github.com/XiaoWen91/XiaoWenmiux")
                }
            }
        }

        item { Spacer(Modifier.height(96.dp)) }
    }
}

@Composable
private fun InfoRow(title: String, summary: String, icon: KiraIcon) {
    BasicComponent(
        title = title,
        summary = summary,
        startAction = { IconBadge(icon) },
    )
}

@Composable
private fun SettingsRow(
    title: String,
    summary: String,
    icon: KiraIcon,
    onClick: () -> Unit,
) {
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
private fun IconBadge(icon: KiraIcon) {
    Box(
        Modifier.size(36.dp)
            .clip(CircleShape)
            .background(Color(0xFF0A84FF).copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center,
    ) {
        CanvasIcon(
            icon = icon,
            tint = Color(0xFF0A84FF),
            modifier = Modifier.size(18.dp),
        )
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (_: Throwable) {
        Toast.makeText(context, "无法打开链接", Toast.LENGTH_SHORT).show()
    }
}
