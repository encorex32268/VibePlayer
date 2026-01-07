package com.lihan.vibeplayer.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)



val ButtonPrimary = Color(0xFFDE84FF)
val ButtonPrimary30 = Color(0x4DDE84FF)
val ButtonHover28 = Color(0x471A2735)
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFA7BBD1)
val TextDisabled = Color(0xFF4C647C)
val SurfaceBG = Color(0xFF0A131D)
val SurfaceOutline = Color(0xFF1A2F47)
val Accent = Color(0xFFF1FF95)


val ScannerLineGradient = Brush.linearGradient(
    0.0f to Color(0xFFEBFE6F),
    0.15f to Color(0xFFF8FDD6),
    0.75f to Color(0xFFF8FDD6),
    1f to Color(0xFFEBFE6F)
)


// 0~1 => 0~360
val ScannerArcGradient = Brush.sweepGradient(
    colorStops = arrayOf(
        0.0f to Color.Transparent,
        0.37f to Color(0xFFEBFE6F).copy(alpha = 0.5f),
        0.375f to Color(0xFFEBFE6F).copy(alpha = 0.3f),
        0.376f to Color(0xFFEBFE6F).copy(alpha = 0.1f)
    )
)
