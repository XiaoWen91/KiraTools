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
import io.github.angbang852.manjiao.hook.ScanHideHooks
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.preference.SwitchPreference
import top.yukonga.miuix.kmp.theme.LocalContentColor
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

object InjectedScanPanel {
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
                setWindowAnimations(android.R.style.Animation_Dialog)
                statusBarColor = AndroidColor.TRANSPARENT
                navigationBarColor = AndroidColor.TRANSPARENT
                if (android.os.Build.VERSION.SDK_INT >= 29) {
                    isStatusBarContrastEnforced = false
                    isNavigationBarContrastEnforced = false
                }
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
                ScanTheme {
                    ScanScreen(act, dialog)
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
private fun ScanTheme(content: @Composable () -> Unit) {
    val colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    MiuixTheme(colors = colors) {
        CompositionLocalProvider(LocalContentColor provides colors.onBackground) {
            content()
        }
    }
}

@Composable
private fun ScanScreen(act: Activity, dialog: Dialog) {
    val ctx = LocalContext.current
    val dark = isSystemInDarkTheme()
    Column(
        Modifier.fillMaxWidth()
            .padding(20.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (dark) Color(0xFF1C1C1E) else Color.White)
            .padding(20.dp)
    ) {
        Text("隐藏 Tab / 视频控件", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(
            "扫描当前页面并选择要隐藏的控件",
            fontSize = 12.sp,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )

        BasicComponent(
            title = "扫描当前页面",
            summary = "打开扫描结果面板",
            onClick = {
                dialog.dismiss()
                ScanHideHooks.showScanResultPanel(act)
            },
        )
        BasicComponent(
            title = "恢复全部隐藏",
            summary = "清除已保存的隐藏 token",
            onClick = {
                Prefs.setStrSync(ctx, Prefs.K_BEAUTY_HIDDEN_ELEMS, "")
                ScanHideHooks.apply(act.window?.decorView)
                dialog.dismiss()
            },
        )

        var hidden by remember { mutableStateOf(Prefs.str(Prefs.K_BEAUTY_HIDDEN_ELEMS, "")) }
        val hiddenSet = hidden.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
        fun toggle(token: String, on: Boolean) {
            val next = hiddenSet.toMutableSet()
            if (on) next.add(token) else next.remove(token)
            hidden = next.joinToString(",")
            Prefs.setStrSync(ctx, Prefs.K_BEAUTY_HIDDEN_ELEMS, hidden)
            ScanHideHooks.apply(act.window?.decorView)
        }

        SmallTitle("顶栏")
        listOf("分类", "侧边栏", "游戏").forEach { label ->
            val token = "text:$label"
            SwitchPreference(title = label, checked = token in hiddenSet, onCheckedChange = { toggle(token, it) })
        }
        SmallTitle("底栏")
        listOf("首页", "发现", "消息", "我").forEach { label ->
            val token = "text:$label"
            SwitchPreference(title = label, checked = token in hiddenSet, onCheckedChange = { toggle(token, it) })
        }

        Spacer(Modifier.height(12.dp))
        TextButton(
            text = "关闭",
            onClick = { dialog.dismiss() },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
