package io.github.angbang852.manjiao.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.angbang852.manjiao.util.IconData
import io.github.angbang852.manjiao.util.Logger

/** 在快手设置页 RecyclerView 第一项注入 KiraTools 入口。 */
object SparkSettingsEntry {
    private const val TAG_RV = "kira_settings_rv"
    private const val RV_ID = 0x7f09181c
    private const val FRAGMENT_TAG = "settingListFragment"

    fun inject(activity: Activity) {
        try {
            val fm = (activity as? FragmentActivity)?.supportFragmentManager ?: return
            val fragment = fm.findFragmentByTag(FRAGMENT_TAG) ?: return
            fragment.view?.let { inject(it, activity) }
        } catch (t: Throwable) {
            Logger.d("spark settings entry activity miss: ${t.message}")
        }
    }

    fun inject(view: View, activity: Activity? = null) {
        try {
            val rv = view.findViewById<RecyclerView>(RV_ID) ?: return
            if (rv.tag == TAG_RV) return
            val original = rv.adapter ?: return
            val ctx = view.context
            val header = HeaderAdapter(ctx) {
                (activity ?: findActivity(ctx))?.let { MainMenuDialog.show(it) }
            }
            rv.adapter = ConcatAdapter(header, original)
            rv.tag = TAG_RV
            Logger.d("spark settings header injected")
        } catch (t: Throwable) {
            Logger.d("spark settings entry inject miss: ${t.message}")
        }
    }

    private fun findActivity(ctx: Context): Activity? {
        var c: Context? = ctx
        while (c is ContextWrapper) {
            if (c is Activity) return c
            c = c.baseContext
        }
        return c as? Activity
    }

    private class HeaderAdapter(
        private val ctx: Context,
        private val onClick: () -> Unit,
    ) : RecyclerView.Adapter<HeaderAdapter.Holder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
            Holder(buildRow(ctx, onClick))

        override fun onBindViewHolder(holder: Holder, position: Int) = Unit

        override fun getItemCount(): Int = 1

        class Holder(view: View) : RecyclerView.ViewHolder(view)
    }

    private fun buildRow(ctx: Context, onClick: () -> Unit): View {
        val row = LinearLayout(ctx).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(ctx, 16), dp(ctx, 14), dp(ctx, 16), dp(ctx, 14))
            isClickable = true
            isFocusable = true
            setOnClickListener { onClick() }
        }

        val avatar = ImageView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(dp(ctx, 40), dp(ctx, 40))
            scaleType = ImageView.ScaleType.CENTER_CROP
            try {
                setImageDrawable(IconData.get(ctx))
            } catch (_: Throwable) {
            }
        }
        row.addView(avatar)

        val texts = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(ctx, 12), 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        }
        texts.addView(TextView(ctx).apply {
            text = "KiraTools"
            textSize = 16f
            setTextColor(Color.DKGRAY)
        })
        texts.addView(TextView(ctx).apply {
            text = "模块设置"
            textSize = 12f
            setTextColor(Color.GRAY)
        })
        row.addView(texts)
        row.addView(ChevronView(ctx).apply {
            layoutParams = LinearLayout.LayoutParams(dp(ctx, 18), dp(ctx, 18))
        })
        return row
    }

    private fun dp(ctx: Context, v: Int): Int =
        (v * ctx.resources.displayMetrics.density).toInt()

    private class ChevronView(ctx: Context) : View(ctx) {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f * resources.displayMetrics.density
            color = Color.GRAY
            strokeCap = Paint.Cap.ROUND
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val w = width.toFloat()
            val h = height.toFloat()
            canvas.drawLine(w * 0.35f, h * 0.25f, w * 0.65f, h * 0.5f, paint)
            canvas.drawLine(w * 0.65f, h * 0.5f, w * 0.35f, h * 0.75f, paint)
        }
    }
}
