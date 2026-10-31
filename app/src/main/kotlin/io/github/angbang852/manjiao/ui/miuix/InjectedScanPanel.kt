package io.github.angbang852.manjiao.ui.miuix

import android.app.Activity
import android.app.Dialog
import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.angbang852.manjiao.data.Prefs
import io.github.angbang852.manjiao.hook.ScanHideHooks
import io.github.angbang852.manjiao.hook.BeautifyHooks
import io.github.angbang852.manjiao.hook.KiriQ2
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

    fun show(act: Activity, initialCategory: String? = null): Boolean {
        return showDialog(act) { a, d -> ScanScreen(a, d, initialCategory) }
    }

    fun showScanResults(act: Activity): Boolean {
        return showDialog(act) { a, d -> ScanResultsScreen(a, d) }
    }

    private fun showDialog(act: Activity, content: @Composable (Activity, Dialog) -> Unit): Boolean {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Handler(Looper.getMainLooper()).post { showDialog(act, content) }
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
                ScanTheme { content(act, dialog) }
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
private fun ScanScreen(act: Activity, dialog: Dialog, initialCategory: String?) {
    val ctx = LocalContext.current
    val dark = isSystemInDarkTheme()
    var hidden by remember { mutableStateOf(Prefs.str(Prefs.K_BEAUTY_HIDDEN_ELEMS, "")) }
    var bottomHidden by remember { mutableStateOf(Prefs.str(Prefs.K_BEAUTY_BOTTOM_HIDE, "")) }
    var order by remember { mutableStateOf(loadOrder(ctx)) }
    val hiddenSet = hidden.split(",", "，", "\n").map { it.trim() }.filter { it.isNotEmpty() }.toSet()

    fun toggle(token: String, on: Boolean) {
        val next = hiddenSet.toMutableSet()
        if (on) next.add(token) else next.remove(token)
        hidden = next.joinToString(",")
        Prefs.setStrSync(ctx, Prefs.K_BEAUTY_HIDDEN_ELEMS, hidden)
        ScanHideHooks.apply(act.window?.decorView)
        act.window?.decorView?.postDelayed({ ScanHideHooks.apply(act.window?.decorView) }, 120L)
    }

    fun saveOrder(next: List<String>) {
        order = next
        Prefs.setStrSync(ctx, Prefs.K_BEAUTY_TAB_ORDER, next.joinToString(","))
    }

    fun moveUp(index: Int) {
        if (index <= 0 || index >= order.size) return
        val next = order.toMutableList()
        val item = next.removeAt(index)
        next.add(index - 1, item)
        saveOrder(next)
    }

    LazyColumn(
        Modifier.fillMaxWidth().heightIn(max=420.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(if (dark) Color(0xFF1C1C1E) else Color.White)
            .padding(20.dp)
    ) {
        item {
            Text("隐藏 Tab / 视频控件", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(
                "选择要隐藏的控件，修改后立即应用",
                fontSize = 12.sp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
            )
        }
        item {
            BasicComponent(
                title = "扫描当前页面",
                summary = "打开扫描结果面板",
                onClick = {
                    dialog.dismiss()
                    ScanHideHooks.showScanResultPanel(act)
                },
            )
        }
        item {
            BasicComponent(
                title = "恢复全部隐藏",
                summary = "清除已保存的隐藏 token",
                onClick = {
                    Prefs.setStrSync(ctx, Prefs.K_BEAUTY_HIDDEN_ELEMS, "")
                    hidden = ""
                    ScanHideHooks.apply(act.window?.decorView)
                },
            )
        }

        val cats = if (initialCategory != null) listOf(initialCategory) else listOf("顶栏", "底栏", "视频控件", "分享面板")
        cats.forEach { cat ->
            item { SmallTitle(cat) }
            item {
                BasicComponent(
                    title = "隐藏本区全部",
                    summary = "勾选本区所有预设",
                    onClick = {
                        val labels = if (cat == "底栏") order else presetsFor(cat)
                        val next = hiddenSet.toMutableSet()
                        labels.forEach { next.add("text:$it") }
                        hidden = next.joinToString(",")
                        Prefs.setStrSync(ctx, Prefs.K_BEAUTY_HIDDEN_ELEMS, hidden)
                        ScanHideHooks.apply(act.window?.decorView)
                    },
                )
            }
            item {
                BasicComponent(
                    title = "恢复本区",
                    summary = "取消本区所有预设",
                    onClick = {
                        val labels = if (cat == "底栏") order else presetsFor(cat)
                        val next = hiddenSet.toMutableSet()
                        labels.forEach { next.remove("text:$it") }
                        hidden = next.joinToString(",")
                        Prefs.setStrSync(ctx, Prefs.K_BEAUTY_HIDDEN_ELEMS, hidden)
                        ScanHideHooks.apply(act.window?.decorView)
                    },
                )
            }
            if (cat == "底栏") {
                val bottomSet = bottomHidden.split(",", "，", "\n").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                item { SmallTitle("隐藏底栏项") }
                presetsFor("底栏").forEach { label ->
                    item {
                        SwitchPreference(
                            title = label,
                            checked = label in bottomSet,
                            onCheckedChange = { on ->
                                val next = bottomSet.toMutableSet()
                                if (on) next.add(label) else next.remove(label)
                                bottomHidden = next.joinToString(",")
                                Prefs.setStrSync(ctx, Prefs.K_BEAUTY_BOTTOM_HIDE, bottomHidden)
                                BeautifyHooks.reapply()
                                ScanHideHooks.apply(act.window?.decorView)
                                act.window?.decorView?.postDelayed({
                                    BeautifyHooks.reapply()
                                    ScanHideHooks.apply(act.window?.decorView)
                                }, 120L)
                            },
                        )
                    }
                }
            } else if (cat == "视频控件") {
                KiriQ2.controls.forEach { c ->
                    val tokens = c.idNames.map { "id:$it" }
                    val checked = tokens.any { it in hiddenSet }
                    item {
                        SwitchPreference(
                            title = c.label,
                            checked = checked,
                            onCheckedChange = { on ->
                                val next = hiddenSet.toMutableSet()
                                if (on) tokens.forEach { next.add(it) } else tokens.forEach { next.remove(it) }
                                hidden = next.joinToString(",")
                                Prefs.setStrSync(ctx, Prefs.K_BEAUTY_HIDDEN_ELEMS, hidden)
                                ScanHideHooks.apply(act.window?.decorView)
                                act.window?.decorView?.postDelayed({ ScanHideHooks.apply(act.window?.decorView) }, 120L)
                            },
                        )
                    }
                }
            } else {
                presetsFor(cat).forEach { label ->
                    val token = "text:$label"
                    item {
                        SwitchPreference(
                            title = label,
                            checked = token in hiddenSet,
                            onCheckedChange = { toggle(token, it) },
                        )
                    }
                }
            }
        }

        item {
            TextButton(
                text = "关闭",
                onClick = { dialog.dismiss() },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        }
    }
}

private fun loadOrder(ctx: android.content.Context): List<String> {
    val def = presetsFor("底栏")
    val raw = Prefs.str(Prefs.K_BEAUTY_TAB_ORDER, def.joinToString(","))
    val parsed = raw.split(",", "，", "\n").map { it.trim() }.filter { it.isNotEmpty() }
    val missing = def.filter { it !in parsed }
    return parsed + missing
}

@Composable
private fun ScanResultsScreen(act: Activity, dialog: Dialog) {
    val ctx = LocalContext.current
    val dark = isSystemInDarkTheme()
    var hidden by remember { mutableStateOf(Prefs.str(Prefs.K_BEAUTY_HIDDEN_ELEMS, "")) }
    val hiddenSet = hidden.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    val items = remember { ScanHideHooks.scanCurrent(act) }

    fun toggle(token: String, on: Boolean) {
        val next = hiddenSet.toMutableSet()
        if (on) next.add(token) else next.remove(token)
        hidden = next.joinToString(",")
        Prefs.setStrSync(ctx, Prefs.K_BEAUTY_HIDDEN_ELEMS, hidden)
        ScanHideHooks.apply(act.window?.decorView)
        act.window?.decorView?.postDelayed({ ScanHideHooks.apply(act.window?.decorView) }, 120L)
    }

    LazyColumn(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(if (dark) Color(0xFF1C1C1E) else Color.White)
            .padding(20.dp)
    ) {
        item {
            Text("扫描结果", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(
                "当前页面可隐藏元素 ${items.size} 个",
                fontSize = 12.sp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
            )
        }
        if (items.isEmpty()) {
            item { Text("没扫到可隐藏元素", fontSize = 13.sp, color = MiuixTheme.colorScheme.onSurfaceVariantSummary) }
        } else {
            items.forEach { e ->
                item {
                    SwitchPreference(
                        title = e.label,
                        summary = "${e.token} · ${e.w}x${e.h}",
                        checked = hiddenSet.contains(e.token),
                        onCheckedChange = { toggle(e.token, it) },
                    )
                }
            }
        }
        item {
            TextButton(
                text = "关闭",
                onClick = { dialog.dismiss() },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            )
        }
    }
}

private fun presetsFor(cat: String): List<String> = when (cat) {
    "顶栏" -> listOf("分类", "侧边栏", "王者送福利", "游戏", "玩游戏")
    "底栏" -> listOf("首页", "发现", "拍摄", "消息", "我", "同城", "精选")
    "视频控件" -> listOf("头像", "点赞", "评论", "收藏", "分享", "音乐")
    "分享面板" -> listOf("保存到相册", "下载", "分享到微信", "朋友圈", "QQ", "微博", "复制链接", "举报", "不感兴趣")
    else -> emptyList()
}
