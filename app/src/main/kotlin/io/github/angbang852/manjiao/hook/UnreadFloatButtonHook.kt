package io.github.angbang852.manjiao.hook

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import io.github.angbang852.manjiao.data.Prefs
import io.github.angbang852.manjiao.util.Logger
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

object UnreadFloatButtonHook {
    private const val TAG = "kira_clear_unread_btn"

    fun hook(module: XposedModule, cl: ClassLoader) {
        try {
            val c = Class.forName("qbj.b", false, cl)
            val m = c.getDeclaredMethod("a")
            module.hook(m)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .setId("kira.clearunread.float")
                .intercept { chain ->
                    val r = chain.proceed()
                    try {
                        inject(chain.thisObject)
                    } catch (_: Throwable) {
                    }
                    r
                }
            Logger.d("UnreadFloatButtonHook installed")
        } catch (t: Throwable) {
            Logger.d("UnreadFloatButtonHook miss: ${t.message}")
        }
    }

    private fun inject(obj: Any?) {
        if (obj == null) return
        if (!Prefs.bool(Prefs.K_CLEAR_UNREAD_FLOAT, false)) return
        val root = findRoot(obj) ?: return
        if (root.findViewWithTag<View>(TAG) != null) return
        val ctx = root.context
        val act = findActivity(ctx) ?: return
        val btn = TextView(ctx).apply {
            tag = TAG
            text = "清除未读"
            textSize = 13f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(dp(ctx, 14), dp(ctx, 8), dp(ctx, 14), dp(ctx, 8))
            background = android.graphics.drawable.GradientDrawable().apply {
                cornerRadius = dp(ctx, 14).toFloat()
                setColor(0xCC0A84FF.toInt())
            }
            setOnClickListener {
                ClearUnreadBridge.clearAll(act)
            }
        }
        root.addView(
            btn,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
    }

    private fun findRoot(obj: Any): ViewGroup? {
        for (f in obj.javaClass.declaredFields) {
            if (!View::class.java.isAssignableFrom(f.type)) continue
            try {
                f.isAccessible = true
                val v = f.get(obj) as? View ?: continue
                if (v is ViewGroup) return v
            } catch (_: Throwable) {
            }
        }
        return null
    }

    private fun findActivity(ctx: Context): Activity? {
        var c: Context? = ctx
        while (c is ContextWrapper) {
            if (c is Activity) return c
            c = c.baseContext
        }
        return c as? Activity
    }

    private fun dp(ctx: Context, v: Int): Int =
        (v * ctx.resources.displayMetrics.density).toInt()
}

object ClearUnreadBridge {
    fun clearAll(act: Activity) {
        Toast.makeText(act, "清除未读：测试按钮已触发", Toast.LENGTH_SHORT).show()
        Logger.d("kira clear unread button clicked")
    }
}
