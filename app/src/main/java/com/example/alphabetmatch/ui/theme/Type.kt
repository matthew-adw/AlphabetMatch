package com.example.alphabetmatch.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Kid-readable sizes: UI text >= 24sp, letters >= 32sp.
val KidTypography = Typography(
    displayLarge = TextStyle(fontSize = 64.sp, fontWeight = FontWeight.ExtraBold, color = InkNavy),
    headlineMedium = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = InkNavy),
    titleLarge = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = InkNavy),
    bodyLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium, color = InkNavy),
    bodyMedium = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Normal, color = InkNavy),
    labelLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = InkNavy),
)