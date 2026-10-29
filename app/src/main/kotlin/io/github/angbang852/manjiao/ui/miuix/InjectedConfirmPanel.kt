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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberLayerBackdrop
import io.github.angbang852.kira.miuixlab.liquid.LiquidDialogContent

object InjectedConfirmPanel {
    private var current: Dialog? = null

    fun show(
        ctx: Context,
        title: String,
        message: String,
        confirmText: String,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit = {},
    ): Boolean {
        val act = ctx as? Activity ?: return false
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Handler(Looper.getMainLooper()).post {
                show(act, title, message, confirmText, onConfirm, onDismiss)
            }
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
            var confirmed = false
            dialog.window?.apply {
                setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
                addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                attributes = attributes.apply {
                    dimAmount = 0.35f
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.MATCH_PARENT
                }
            }
            dialog.setOnDismissListener {
                env.close()
                if (current === dialog) current = null
                if (!confirmed) onDismiss()
            }
            view.setContent {
                ConfirmLiquidContent(
                    title = title,
                    message = message,
                    onDismiss = { dialog.dismiss() },
                    onConfirm = {
                        confirmed = true
                        dialog.dismiss()
                        onConfirm()
                    },
                )
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
private fun ConfirmLiquidContent(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    Box(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().layerBackdrop(backdrop))
        LiquidDialogContent(
            backdrop = backdrop,
            title = title,
            message = message,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
        )
    }
}
