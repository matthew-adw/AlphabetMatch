package com.example.alphabetmatch.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabetmatch.ui.components.BigButton
import com.example.alphabetmatch.ui.components.ConfettiOverlay
import com.example.alphabetmatch.ui.theme.FreshGreen
import com.example.alphabetmatch.ui.theme.InkNavy
import com.example.alphabetmatch.ui.theme.PastelSky
import com.example.alphabetmatch.ui.theme.SunnyYellow

/**
 * RewardScreen: shown after every 5 completed rounds. Celebrates the collected
 * stars + letter stickers with a slow pop-in animation and confetti.
 */
@Composable
fun RewardScreen(
    stars: Int,
    stickerLetters: List<Char>,
    onContinue: () -> Unit,
    onHome: () -> Unit,
) {
    val stickerColors = listOf(
        Color(0xFFE57373), Color(0xFF64B5F6), Color(0xFFFFB74D),
        Color(0xFF81C784), Color(0xFFBA68C8),
    )

    Box(modifier = Modifier.fillMaxSize().background(PastelSky)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "You did it!",
                color = InkNavy,
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$stars stars",
                color = SunnyYellow,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Sticker badges pop in one by one (500ms each, staggered).
            var started by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) { started = true }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                stickerLetters.forEachIndexed { index, letter ->
                    val visible by animateFloatAsState(
                        targetValue = if (started) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = 500,
                            delayMillis = index * 250,
                        ),
                        label = "sticker$index",
                    )
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .scale(visible)
                            .clip(RoundedCornerShape(20.dp))
                            .background(stickerColors[index % stickerColors.size]),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = letter.toString(),
                            color = Color.White,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "New stickers!",
                color = InkNavy,
                fontSize = 24.sp,
            )
            Spacer(modifier = Modifier.height(40.dp))

            BigButton(
                text = "Play on!",
                onClick = onContinue,
                containerColor = FreshGreen,
                contentColor = Color.White,
                fontSize = 32,
            )
            Spacer(modifier = Modifier.height(16.dp))
            BigButton(
                text = "\u2190 Home",
                onClick = onHome,
                containerColor = Color.White,
                contentColor = InkNavy,
                fontSize = 32,
            )
        }

        ConfettiOverlay(active = true, modifier = Modifier.fillMaxSize())
    }
}
