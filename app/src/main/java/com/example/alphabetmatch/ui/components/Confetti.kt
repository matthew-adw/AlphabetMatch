package com.example.alphabetmatch.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.alphabetmatch.ui.theme.CoralRed
import com.example.alphabetmatch.ui.theme.FreshGreen
import com.example.alphabetmatch.ui.theme.SkyBlue
import com.example.alphabetmatch.ui.theme.SoftPurple
import com.example.alphabetmatch.ui.theme.SunnyYellow
import kotlin.math.sin
import kotlin.random.Random

private data class ConfettiPiece(
    val startX: Float,       // 0..1 of screen width
    val startY: Float,       // -0.15..0 of screen height
    val widthPx: Float,
    val heightPx: Float,
    val color: Color,
    val delay: Float,        // 0..0.35 of total duration
    val swayAmpPx: Float,
    val swayFreq: Float,
    val phase: Float,
    val spinDeg: Float,
    val startRotationDeg: Float,
)

/**
 * Slow, smooth confetti rain shown while [active]. Toddler pace: ~2.4s, fades out at the end.
 */
@Composable
fun ConfettiOverlay(active: Boolean, modifier: Modifier = Modifier) {
    if (!active) return

    val progress = remember { Animatable(0f) }
    LaunchedEffect(active) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMillis = 2_400, easing = LinearEasing))
    }

    val pieces = remember {
        val palette = listOf(SunnyYellow, CoralRed, FreshGreen, SkyBlue, SoftPurple)
        val random = Random(System.nanoTime())
        List(80) {
            ConfettiPiece(
                startX = random.nextFloat(),
                startY = -0.15f - random.nextFloat() * 0.15f,
                widthPx = 14f + random.nextFloat() * 14f,
                heightPx = 10f + random.nextFloat() * 10f,
                color = palette[random.nextInt(palette.size)],
                delay = random.nextFloat() * 0.35f,
                swayAmpPx = 30f + random.nextFloat() * 50f,
                swayFreq = 2f + random.nextFloat() * 3f,
                phase = random.nextFloat() * 6.28f,
                spinDeg = 360f + random.nextFloat() * 720f,
                startRotationDeg = random.nextFloat() * 360f,
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val overall = progress.value
        pieces.forEach { piece ->
            val t = ((overall - piece.delay) / (1f - piece.delay)).coerceIn(0f, 1f)
            if (t <= 0f) return@forEach
            val x = piece.startX * size.width + sin((t * piece.swayFreq + piece.phase).toDouble()).toFloat() * piece.swayAmpPx
            val y = piece.startY * size.height + t * size.height * 1.2f
            val alpha = if (t > 0.75f) (1f - (t - 0.75f) / 0.25f) else 1f
            rotate(degrees = piece.startRotationDeg + t * piece.spinDeg, pivot = Offset(x, y)) {
                drawRoundRect(
                    color = piece.color,
                    topLeft = Offset(x - piece.widthPx / 2f, y - piece.heightPx / 2f),
                    size = Size(piece.widthPx, piece.heightPx),
                    cornerRadius = CornerRadius(piece.widthPx / 3f),
                    alpha = alpha.coerceIn(0f, 1f),
                )
            }
        }
    }
}
