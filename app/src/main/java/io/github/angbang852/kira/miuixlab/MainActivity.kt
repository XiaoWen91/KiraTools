package io.github.angbang852.kira.miuixlab

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.LocalContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LabTheme {
                LabScreen()
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
private fun LabScreen() {
    val context = LocalContext.current
    var miuixEnabled by remember { mutableStateOf(true) }
    var hideBottomBar by remember { mutableStateOf(false) }
    var alpha by remember { mutableStateOf(60f) }
    var applied by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(MiuixTheme.colorScheme.background, RoundedCornerShape(20.dp)),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp)) {
                Text(
                    "KiraTools Miuix Lab",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "独立实验工程 · Miuix 0.9.4",
                    fontSize = 13.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                )
            }

            LazyColumn(Modifier.weight(1f).fillMaxWidth()) {
                item { SmallTitle("界面") }
                item {
                    SwitchPreference(
                        title = "启用 Miuix 界面",
                        summary = "关闭后仅用于对比旧 View UI",
                        checked = miuixEnabled,
                        onCheckedChange = { miuixEnabled = it },
                    )
                }
                item {
                    SwitchPreference(
                        title = "隐藏底栏",
                        summary = "示例开关，不作用于快手",
                        checked = hideBottomBar,
                        onCheckedChange = { hideBottomBar = it },
                    )
                }

                item { SmallTitle("透明度") }
                item {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp)) {
                        Text("区域透明度 ${alpha.roundToInt()}%", fontSize = 16.sp)
                        Slider(
                            value = alpha,
                            onValueChange = { alpha = it },
                            valueRange = 0f..100f,
                            steps = 99,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        )
                    }
                }

                item { SmallTitle("工具") }
                item {
                    BasicComponent(
                        title = "视图查找",
                        summary = "点击选择控件并复制定位信息",
                        onClick = {
                            Toast.makeText(context, "Miuix 示例：视图查找入口", Toast.LENGTH_SHORT).show()
                        },
                    )
                }
                item {
                    BasicComponent(
                        title = "模块日志",
                        summary = "查看 slowkick.log 并脱敏",
                        onClick = {
                            Toast.makeText(context, "Miuix 示例：模块日志入口", Toast.LENGTH_SHORT).show()
                        },
                    )
                }

                item {
                    Column(Modifier.fillMaxWidth().padding(24.dp)) {
                        TextButton(
                            text = if (applied) "已应用" else "应用设置",
                            onClick = {
                                applied = true
                                Toast.makeText(context, "Miuix 页面状态已应用（示例）", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                        )
                    }
                }
            }
        }
    }
}
