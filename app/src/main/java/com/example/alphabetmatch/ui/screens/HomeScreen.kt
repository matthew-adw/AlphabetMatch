package com.example.alphabetmatch.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabetmatch.ui.components.BigButton
import com.example.alphabetmatch.ui.theme.CoralRed
import com.example.alphabetmatch.ui.theme.FreshGreen
import com.example.alphabetmatch.ui.theme.InkNavy
import com.example.alphabetmatch.ui.theme.PastelSky
import com.example.alphabetmatch.ui.theme.SkyBlue
import com.example.alphabetmatch.ui.theme.SoftPurple
import com.example.alphabetmatch.ui.theme.SunnyYellow
import kotlin.math.sin

/** Cute pastel background: slowly bobbing soft color blobs. */
@Composable
fun CuteBackground(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "bg")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(tween(6_000), RepeatMode.Restart),
        label = "phase",
    )
    val blobs = listOf(
        Triple(SunnyYellow.copy(alpha = 0.35f), 0.18f, 0.75f),
        Triple(SoftPurple.copy(alpha = 0.35f), 0.82f, 0.68f),
        Triple(FreshGreen.copy(alpha = 0.30f), 0.25f, 0.28f),
        Triple(CoralRed.copy(alpha = 0.25f), 0.75f, 0.22f),
        Triple(SkyBlue.copy(alpha = 0.30f), 0.50f, 0.88f),
    )
    Canvas(modifier = modifier.fillMaxSize().background(PastelSky)) {
        blobs.forEachIndexed { index, (color, fx, fy) ->
            val bob = sin(phase + index).toFloat() * 24f
            drawCircle(
                color = color,
                radius = size.minDimension * 0.16f,
                center = Offset(size.width * fx, size.height * fy + bob),
            )
        }
    }
}

/**
 * HomeScreen: title "ABC Match", big Play button, small settings gear in the corner.
 */
@Composable
fun HomeScreen(
    stars: Int,
    onPlay: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        CuteBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "ABC Match",
                color = InkNavy,
                fontSize = 64.sp,
                style = MaterialTheme.typography.displayLarge,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "\u2605 $stars",
                color = SunnyYellow,
                fontSize = 40.sp,
                style = MaterialTheme.typography.headlineMedium,
            )
            Spacer(modifier = Modifier.height(48.dp))
            BigButton(
                text = "PLAY \u25B6",
                onClick = onPlay,
                containerColor = FreshGreen,
                contentColor = Color.White,
                modifier = Modifier.fillMaxWidth(0.8f),
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
        // Small settings gear tucked in the corner; Play stays the focus.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(56.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable(onClick = onOpenSettings),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "\u2699", fontSize = 28.sp, color = InkNavy)
        }
    }
}
