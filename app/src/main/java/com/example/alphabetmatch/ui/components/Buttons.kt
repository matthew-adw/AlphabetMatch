package com.example.alphabetmatch.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Big rounded text button: min 96dp tall (>= 64dp touch target), 40sp label. */
@Composable
fun BigButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    fontSize: Int = 40,
) {
    Box(
        modifier = modifier
            .heightIn(min = 96.dp)
            .widthIn(min = 200.dp)
            .clip(RoundedCornerShape(48.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 40.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
    }
}

/** Round back button, 64dp minimum target with a large "arrow" glyph. */
@Composable
fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier, contentDescription: String = "Back") {
    Box(
        modifier = modifier
            .widthIn(min = 64.dp)
            .heightIn(min = 64.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "\u2190", // left arrow
            color = Color(0xFF1F3B73),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp),
        )
    }
}
