package io.github.angbang852.kira.miuixlab.liquid

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.highlight.Highlight
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle

@Composable
fun LiquidDialogContent(
    backdrop: Backdrop,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val light = !isSystemInDarkTheme()
    val contentColor = if (light) Color.Black else Color.White
    val accentColor = if (light) Color(0xFF0088FF) else Color(0xFF0091FF)
    val containerColor = if (light) Color.White else Color(0xFF1C1C1E)
    val dimColor = if (light) Color(0xFF29293A).copy(0.23f) else Color(0xFF121212).copy(0.56f)

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(180)) + scaleIn(tween(180), initialScale = 0.92f),
        exit = fadeOut(tween(120)) + scaleOut(tween(120), targetScale = 0.96f),
    ) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier.fillMaxSize()
                    .background(dimColor)
                    .clickable(onClick = onDismiss)
            )

            Column(
                Modifier
                    .padding(40.dp)
                    .drawBackdrop(
                        backdrop = backdrop,
                        shape = { RoundedRectangle(48.dp) },
                        effects = {
                            blur(if (light) 16.dp.toPx() else 8.dp.toPx())
                            lens(24.dp.toPx(), 48.dp.toPx(), depthEffect = true)
                        },
                        highlight = { Highlight.Plain },
                        onDrawSurface = { drawRect(containerColor.copy(alpha = 0.94f)) },
                    )
                    .fillMaxWidth(),
            ) {
                BasicText(
                    title,
                    Modifier.padding(28.dp, 24.dp, 28.dp, 12.dp),
                    style = TextStyle(contentColor, 24.sp, FontWeight.Medium),
                )
                BasicText(
                    message,
                    Modifier.padding(24.dp, 12.dp, 24.dp, 12.dp),
                    style = TextStyle(contentColor.copy(0.68f), 15.sp),
                    maxLines = 5,
                )
                Row(
                    Modifier
                        .padding(24.dp, 12.dp, 24.dp, 24.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        Modifier
                            .clip(Capsule())
                            .background(containerColor.copy(0.12f))
                            .clickable(onClick = onDismiss)
                            .height(48.dp)
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BasicText(
                            "取消",
                            style = TextStyle(contentColor, 16.sp),
                        )
                    }
                    Row(
                        Modifier
                            .clip(Capsule())
                            .background(accentColor)
                            .clickable(onClick = onConfirm)
                            .height(48.dp)
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BasicText(
                            "确定",
                            style = TextStyle(Color.White, 16.sp),
                        )
                    }
                }
            }
        }
    }
}
