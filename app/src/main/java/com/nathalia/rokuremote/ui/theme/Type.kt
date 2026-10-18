package com.nathalia.rokuremote.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nathalia.rokuremote.R

val Sora = FontFamily(Font(R.font.sora))

val AppTypography = Typography(
    headlineMedium = TextStyle(
        fontFamily = Sora, fontWeight = FontWeight.SemiBold, fontSize = 26.sp, letterSpacing = (-0.5).sp
    ),
    titleLarge = TextStyle(
        fontFamily = Sora, fontWeight = FontWeight.SemiBold, fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Sora, fontWeight = FontWeight.Medium, fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Sora, fontWeight = FontWeight.Normal, fontSize = 15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Sora, fontWeight = FontWeight.Normal, fontSize = 13.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Sora, fontWeight = FontWeight.Medium, fontSize = 13.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Sora, fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.2.sp
    )
)
