package com.example.alphabetmatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alphabetmatch.ui.components.BackButton
import com.example.alphabetmatch.ui.components.BigButton
import com.example.alphabetmatch.ui.theme.CoralRed
import com.example.alphabetmatch.ui.theme.FreshGreen
import com.example.alphabetmatch.ui.theme.InkNavy
import com.example.alphabetmatch.ui.theme.PastelSky

/**
 * SettingsScreen - sound/music toggles and reset progress.
 * Big touch-friendly controls, no gating.
 */
@Composable
fun SettingsScreen(
    soundEnabled: Boolean,
    musicEnabled: Boolean,
    onBack: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleMusic: () -> Unit,
    onResetProgress: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PastelSky)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackButton(onClick = onBack, contentDescription = "Back to home")
            Spacer(modifier = Modifier.padding(start = 16.dp))
            Text(
                text = "Settings",
                color = InkNavy,
                fontSize = 32.sp,
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        BigButton(
            text = if (soundEnabled) "Sounds: ON" else "Sounds: OFF",
            onClick = onToggleSound,
            containerColor = if (soundEnabled) FreshGreen else Color.White,
            contentColor = if (soundEnabled) Color.White else InkNavy,
            modifier = Modifier.fillMaxWidth(0.9f),
            fontSize = 28,
        )
        Spacer(modifier = Modifier.height(16.dp))
        BigButton(
            text = if (musicEnabled) "Music: ON" else "Music: OFF",
            onClick = onToggleMusic,
            containerColor = if (musicEnabled) FreshGreen else Color.White,
            contentColor = if (musicEnabled) Color.White else InkNavy,
            modifier = Modifier.fillMaxWidth(0.9f),
            fontSize = 28,
        )

        Spacer(modifier = Modifier.height(48.dp))

        BigButton(
            text = "Reset progress",
            onClick = onResetProgress,
            containerColor = CoralRed,
            contentColor = Color.White,
            modifier = Modifier.fillMaxWidth(0.9f),
            fontSize = 28,
        )

        Spacer(modifier = Modifier.height(48.dp))
    }
}
