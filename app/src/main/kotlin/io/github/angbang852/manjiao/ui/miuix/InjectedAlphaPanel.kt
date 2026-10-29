package io.github.angbang852.manjiao.ui.miuix

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.angbang852.manjiao.data.Prefs
import io.github.angbang852.manjiao.hook.RegionAlphaHooks
import java.util.Locale
import top.yukonga.miuix.kmp.basic.Slider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.LocalContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

object InjectedAlphaPanel {
    private var current: Dialog? = null

    fun show(act: Activity): Boolean {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Handler(Looper.getMainLooper()).post { show(act) }
            return true
        }
        if (current?.isShowing == true) return true
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
                AlphaTheme {
                    AlphaScreen(dialog)
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

@Composable
private fun AlphaTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MiuixTheme(colors = colors) {
        CompositionLocalProvider(LocalContentColor provides colors.onBackground) {
            content()
        }
    }
}

@Composable
private fun AlphaScreen(dialog: Dialog) {
    val ctx = LocalContext.current
    val dark = isSystemInDarkTheme()
    Column(
        Modifier.fillMaxWidth()
            .padding(20.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (dark) Color(0xFF1C1C1E) else Color.White)
            .padding(20.dp)
    ) {
        Text("区域透明度", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            "拖动滑杆调整对应区域",
            fontSize = 12.sp,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )

        AlphaSlider("顶栏", Prefs.K_BEAUTY_ALPHA_TOP)
        AlphaSlider("右侧信息", Prefs.K_BEAUTY_ALPHA_RIGHT)
        AlphaSlider("左下角信息", Prefs.K_BEAUTY_ALPHA_TEXT)
        AlphaSlider("底栏", Prefs.K_BEAUTY_ALPHA_BOTTOM)

        Spacer(Modifier.height(8.dp))
        var reset by remember { mutableStateOf(Prefs.bool(Prefs.K_BEAUTY_ALPHA_PAUSE_RESET, false)) }
        SwitchPreference(
            title = "暂停时恢复默认透明度",
            summary = "暂停播放时恢复 100%",
            checked = reset,
            onCheckedChange = {
                reset = it
                Prefs.setBoolSync(ctx, Prefs.K_BEAUTY_ALPHA_PAUSE_RESET, it)
                RegionAlphaHooks.reapplyLast()
            },
        )

        Spacer(Modifier.height(12.dp))
        TextButton(
            text = "关闭",
            onClick = { dialog.dismiss() },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AlphaSlider(title: String, key: String) {
    val ctx = LocalContext.current
    val initial = ((Prefs.str(key, "1.00").toFloatOrNull() ?: 1f) * 100f).coerceIn(0f, 100f)
    var value by remember(key) { mutableFloatStateOf(initial) }

    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text("$title ${value.toInt()}%", fontSize = 15.sp)
        Slider(
            value = value,
            onValueChange = {
                value = it
                val text = String.format(Locale.US, "%.2f", it / 100f)
                Prefs.setStrSync(ctx, key, text)
                RegionAlphaHooks.reapplyLast()
            },
            valueRange = 0f..100f,
            steps = 99,
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        )
    }
}
