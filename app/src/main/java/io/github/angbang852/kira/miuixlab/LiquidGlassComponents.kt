package io.github.angbang852.kira.miuixlab

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyant.backdrop.Backdrop
import com.kyant.backdrop.drawBackdrop
import com.kyant.backdrop.effects.blur
import com.kyant.backdrop.effects.colorControls
import com.kyant.backdrop.effects.lens
import com.kyant.backdrop.effects.vibrancy
import com.kyant.backdrop.highlight.Highlight
import com.kyant.backdrop.shadow.Shadow
import com.kyant.shapes.Capsule
import com.kyant.shapes.RoundedRectangle
import kotlin.math.cos
import kotlin.math.sin

enum class KiraIcon {
    HOME, SETTINGS, CLOCK, PACKAGE, SPARKLE, DOWNLOAD, UPLOAD, HEART, CHAT, LINK, CHEVRON
}

data class LiquidTab(val label: String, val icon: KiraIcon)

@Composable
fun CanvasIcon(
    icon: KiraIcon,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        val s = size.minDimension
        val w = (s * 0.11f).coerceAtLeast(1f)
        val stroke = Stroke(width = w, cap = StrokeCap.Round)
        val c = center
        when (icon) {
            KiraIcon.HOME -> {
                val p = Path().apply {
                    moveTo(s * 0.18f, s * 0.48f)
                    lineTo(s * 0.50f, s * 0.20f)
                    lineTo(s * 0.82f, s * 0.48f)
                    lineTo(s * 0.82f, s * 0.82f)
                    lineTo(s * 0.18f, s * 0.82f)
                    close()
                }
                drawPath(p, tint, style = stroke)
            }
            KiraIcon.SETTINGS -> {
                drawCircle(tint, radius = s * 0.17f, center = c, style = stroke)
                repeat(6) { i ->
                    val a = i * Math.PI / 3.0
                    drawLine(
                        tint,
                        Offset(c.x + cos(a).toFloat() * s * 0.27f, c.y + sin(a).toFloat() * s * 0.27f),
                        Offset(c.x + cos(a).toFloat() * s * 0.38f, c.y + sin(a).toFloat() * s * 0.38f),
                        strokeWidth = w,
                        cap = StrokeCap.Round,
                    )
                }
            }
            KiraIcon.CLOCK -> {
                drawCircle(tint, radius = s * 0.34f, center = c, style = stroke)
                drawLine(tint, c, Offset(c.x, c.y - s * 0.20f), strokeWidth = w, cap = StrokeCap.Round)
                drawLine(tint, c, Offset(c.x + s * 0.16f, c.y + s * 0.10f), strokeWidth = w, cap = StrokeCap.Round)
            }
            KiraIcon.PACKAGE -> {
                drawRoundRect(
                    tint,
                    topLeft = Offset(s * 0.20f, s * 0.28f),
                    size = androidx.compose.ui.geometry.Size(s * 0.60f, s * 0.52f),
                    cornerRadius = CornerRadius(s * 0.08f),
                    style = stroke,
                )
                drawLine(tint, Offset(s * 0.20f, s * 0.44f), Offset(s * 0.80f, s * 0.44f), strokeWidth = w, cap = StrokeCap.Round)
                drawLine(tint, Offset(s * 0.44f, s * 0.44f), Offset(s * 0.44f, s * 0.80f), strokeWidth = w, cap = StrokeCap.Round)
            }
            KiraIcon.SPARKLE -> {
                drawLine(tint, Offset(c.x, s * 0.18f), Offset(c.x, s * 0.82f), strokeWidth = w, cap = StrokeCap.Round)
                drawLine(tint, Offset(s * 0.18f, c.y), Offset(s * 0.82f, c.y), strokeWidth = w, cap = StrokeCap.Round)
                drawLine(tint, Offset(s * 0.28f, s * 0.28f), Offset(s * 0.72f, s * 0.72f), strokeWidth = w * 0.8f, cap = StrokeCap.Round)
                drawLine(tint, Offset(s * 0.72f, s * 0.28f), Offset(s * 0.28f, s * 0.72f), strokeWidth = w * 0.8f, cap = StrokeCap.Round)
            }
            KiraIcon.DOWNLOAD -> {
                drawLine(tint, Offset(c.x, s * 0.20f), Offset(c.x, s * 0.66f), strokeWidth = w, cap = StrokeCap.Round)
                drawLine(tint, Offset(c.x, s * 0.66f), Offset(c.x - s * 0.16f, s * 0.50f), strokeWidth = w, cap = StrokeCap.Round)
                drawLine(tint, Offset(c.x, s * 0.66f), Offset(c.x + s * 0.16f, s * 0.50f), strokeWidth = w, cap = StrokeCap.Round)
                drawLine(tint, Offset(s * 0.28f, s * 0.80f), Offset(s * 0.72f, s * 0.80f), strokeWidth = w, cap = StrokeCap.Round)
            }
            KiraIcon.UPLOAD -> {
                drawLine(tint, Offset(c.x, s * 0.80f), Offset(c.x, s * 0.34f), strokeWidth = w, cap = StrokeCap.Round)
                drawLine(tint, Offset(c.x, s * 0.34f), Offset(c.x - s * 0.16f, s * 0.50f), strokeWidth = w, cap = StrokeCap.Round)
                drawLine(tint, Offset(c.x, s * 0.34f), Offset(c.x + s * 0.16f, s * 0.50f), strokeWidth = w, cap = StrokeCap.Round)
                drawLine(tint, Offset(s * 0.28f, s * 0.80f), Offset(s * 0.72f, s * 0.80f), strokeWidth = w, cap = StrokeCap.Round)
            }
            KiraIcon.HEART -> {
                val p = Path().apply {
                    moveTo(c.x, s * 0.78f)
                    cubicTo(s * 0.12f, s * 0.56f, s * 0.18f, s * 0.26f, c.x, s * 0.38f)
                    cubicTo(s * 0.82f, s * 0.26f, s * 0.88f, s * 0.56f, c.x, s * 0.78f)
                    close()
                }
                drawPath(p, tint, style = stroke)
            }
            KiraIcon.CHAT -> {
                drawRoundRect(
                    tint,
                    topLeft = Offset(s * 0.18f, s * 0.22f),
                    size = androidx.compose.ui.geometry.Size(s * 0.64f, s * 0.44f),
                    cornerRadius = CornerRadius(s * 0.10f),
                    style = stroke,
                )
                drawLine(tint, Offset(s * 0.34f, s * 0.66f), Offset(s * 0.30f, s * 0.82f), strokeWidth = w, cap = StrokeCap.Round)
                drawLine(tint, Offset(s * 0.34f, s * 0.66f), Offset(s * 0.48f, s * 0.66f), strokeWidth = w, cap = StrokeCap.Round)
            }
            KiraIcon.LINK -> {
                drawLine(tint, Offset(s * 0.30f, s * 0.42f), Offset(s * 0.54f, s * 0.66f), strokeWidth = w, cap = StrokeCap.Round)
                drawCircle(tint, radius = s * 0.12f, center = Offset(s * 0.30f, s * 0.34f), style = stroke)
                drawCircle(tint, radius = s * 0.12f, center = Offset(s * 0.70f, s * 0.66f), style = stroke)
            }
            KiraIcon.CHEVRON -> {
                drawLine(tint, Offset(s * 0.36f, s * 0.28f), Offset(s * 0.64f, s * 0.50f), strokeWidth = w, cap = StrokeCap.Round)
                drawLine(tint, Offset(s * 0.64f, s * 0.50f), Offset(s * 0.36f, s * 0.72f), strokeWidth = w, cap = StrokeCap.Round)
            }
        }
    }
}

@Composable
fun LiquidGlassBottomBar(
    backdrop: Backdrop,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    tabs: List<LiquidTab>,
    modifier: Modifier = Modifier,
) {
    val light = !isSystemInDarkTheme()
    val accent = if (light) Color(0xFF0088FF) else Color(0xFF0091FF)
    val container = if (light) Color(0xFFFAFAFA).copy(0.4f) else Color(0xFF121212).copy(0.4f)
    val contentColor = if (light) Color.Black else Color.White

    Row(
        modifier.fillMaxWidth()
            .height(64.dp)
            .drawBackdrop(
                backdrop = backdrop,
                shape = { Capsule() },
                effects = {
                    vibrancy()
                    blur(8.dp.toPx())
                    lens(24.dp.toPx(), 24.dp.toPx())
                },
                shadow = { Shadow(alpha = 0.35f) },
                onDrawSurface = { drawRect(container) },
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEachIndexed { index, tab ->
            val active = index == selectedIndex
            val color = if (active) accent else contentColor.copy(0.7f)
            Column(
                Modifier.weight(1f)
                    .fillMaxHeight()
                    .clip(Capsule())
                    .clickable { onSelected(index) }
                    .background(if (active) accent.copy(0.18f) else Color.Transparent),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CanvasIcon(tab.icon, tint = color, modifier = Modifier.size(18.dp))
                BasicText(
                    tab.label,
                    style = TextStyle(color = color, fontSize = 11.sp),
                )
            }
        }
    }
}

@Composable
fun LiquidGlassDialog(
    backdrop: Backdrop,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val light = !isSystemInDarkTheme()
    val contentColor = if (light) Color.Black else Color.White
    val accent = if (light) Color(0xFF0088FF) else Color(0xFF0091FF)
    val container = if (light) Color(0xFFFAFAFA).copy(0.72f) else Color(0xFF121212).copy(0.62f)
    val dim = if (light) Color(0xFF29293A).copy(0.23f) else Color(0xFF121212).copy(0.56f)

    Box(
        Modifier.fillMaxSize()
            .background(dim)
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.padding(32.dp)
                .fillMaxWidth()
                .drawBackdrop(
                    backdrop = backdrop,
                    shape = { RoundedRectangle(28.dp) },
                    effects = {
                        colorControls(
                            brightness = if (light) 0.2f else 0f,
                            saturation = 1.5f,
                        )
                        blur(if (light) 16.dp.toPx() else 8.dp.toPx())
                        lens(24.dp.toPx(), 48.dp.toPx(), depthEffect = true)
                    },
                    highlight = { Highlight.Plain },
                    onDrawSurface = { drawRect(container) },
                )
                .padding(24.dp),
        ) {
            BasicText(
                title,
                style = TextStyle(contentColor, 22.sp, FontWeight.Medium),
            )
            Spacer(Modifier.height(10.dp))
            BasicText(
                message,
                style = TextStyle(contentColor.copy(0.7f), 14.sp),
            )
            Spacer(Modifier.height(20.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    Modifier.weight(1f)
                        .height(44.dp)
                        .clip(Capsule())
                        .background(container.copy(0.25f))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText("取消", style = TextStyle(contentColor, 15.sp))
                }
                Box(
                    Modifier.weight(1f)
                        .height(44.dp)
                        .clip(Capsule())
                        .background(accent)
                        .clickable(onClick = onConfirm),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText("确定", style = TextStyle(Color.White, 15.sp))
                }
            }
        }
    }
}
