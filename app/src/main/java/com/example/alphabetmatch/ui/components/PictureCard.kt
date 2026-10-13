package com.example.alphabetmatch.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Render-only picture card (no click handling – taps/drags are handled by the
 * GameScreen container so a single gesture detector sees every touch).
 *
 * [borderColor] highlights the card (e.g. green when matched, blue when selected).
 *
 * TODO(ASSETS): [imageRes] currently points at a colored placeholder vector
 *  (res/drawable/ph_*.xml). Replace with real artwork and the Image() below picks it
 *  up automatically. When real art is in place, set [showLabel] = false.
 */
@Composable
fun PictureCard(
    imageRes: Int,
    label: String,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true, // TODO(ASSETS): set false once real images carry their own labels.
    borderColor: Color = Color.Transparent,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .border(4.dp, borderColor, RoundedCornerShape(20.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = label,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.matchParentSize(),
        )
        if (showLabel) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xCC1F3B73))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
            )
        }
    }
}
