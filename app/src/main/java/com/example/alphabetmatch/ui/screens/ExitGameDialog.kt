package com.example.alphabetmatch.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.alphabetmatch.ui.components.BigButton
import com.example.alphabetmatch.ui.theme.InkNavy
import com.example.alphabetmatch.ui.theme.PastelCream
import com.example.alphabetmatch.ui.theme.SoftPurple

/**
 * Confirmation shown when leaving the game:
 * "Stay" is the big easy button, "Leave" quits.
 */
@Composable
fun ExitGameDialog(
    onConfirmExit: () -> Unit,
    onDismissExit: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissExit) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = PastelCream,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Leave the game?",
                    color = InkNavy,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.padding(top = 16.dp),
                )
                BigButton(
                    text = "Stay",
                    onClick = onDismissExit,
                    containerColor = Color(0xFF42A5F5),
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth(0.9f),
                    fontSize = 32,
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.padding(top = 12.dp),
                )
                BigButton(
                    text = "Leave",
                    onClick = onConfirmExit,
                    containerColor = SoftPurple,
                    contentColor = Color.White,
                    modifier = Modifier.fillMaxWidth(0.9f),
                    fontSize = 32,
                )
            }
        }
    }
}
