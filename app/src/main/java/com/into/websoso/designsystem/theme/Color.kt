package com.into.websoso.designsystem.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Primary20 = Color(0xFFF5F7FF)
val Primary50 = Color(0xFFF1EFFF)
val Primary100 = Color(0xFF6A5DFD)
val Primary200 = Color(0xFF240991)

val Secondary50 = Color(0xFFFFAA8F)
val Secondary100 = Color(0xFFFF675D)

val White = Color(0xFFFFFFFF)
val Gray20 = Color(0xFFFAFAFA)
val Gray50 = Color(0xFFF4F5F8)
val Gray70 = Color(0xFFDFDFE3)
val Gray80 = Color(0xFFDDDDE3)
val Gray100 = Color(0xFFCBCBD1)
val Gray200 = Color(0xFFAEADB3)
val Gray300 = Color(0xFF52515F)
val Black = Color(0xFF111118)

val GrayToast = Color(0xCC394258)
val Black60 = Color(0x99000000)
val Warning = Color(0xFFFF675D)

val Transparent = Color(0x00000000)

val BgGradientGray = Brush.linearGradient(
    colors = listOf(
        Color(0xCC070A25),
        Color(0xFF000215),
    ),
    start = Offset(0f, 0f),
    end = Offset(0f, Float.POSITIVE_INFINITY),
)

val BgSelectedGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF6341F0),
        Color(0xFFAD00FF),
    ),
    start = Offset(0f, 0f),
    end = Offset(0f, Float.POSITIVE_INFINITY),
)
