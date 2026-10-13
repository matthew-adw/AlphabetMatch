package com.example.alphabetmatch.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

/**
 * Gentle left-right shake for a wrongly tapped card.
 * Restarts every time [shakeKey] changes; 700ms, decaying amplitude (300ms+ per spec).
 */
@Composable
fun Modifier.shake(shakeKey: Int): Modifier {
    val offset = remember { Animatable(0f) }
    LaunchedEffect(shakeKey) {
        if (shakeKey > 0) {
            offset.snapTo(0f)
            offset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 700
                    0f at 0
                    22f at 80
                    -20f at 170
                    16f at 260
                    -12f at 350
                    8f at 440
                    -5f at 530
                    0f at 700
                },
            )
        }
    }
    return graphicsLayer { translationX = offset.value.dp.toPx() }
}
