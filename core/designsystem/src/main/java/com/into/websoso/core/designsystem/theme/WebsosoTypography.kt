package com.into.websoso.core.designsystem.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.into.websoso.core.resource.R.font.pretendard_bold
import com.into.websoso.core.resource.R.font.pretendard_medium
import com.into.websoso.core.resource.R.font.pretendard_regular
import com.into.websoso.core.resource.R.font.pretendard_semibold

data class WebsosoTypography(
    val headline1: TextStyle,
    val title1: TextStyle,
    val title2: TextStyle,
    val title3: TextStyle,
    val title4:TextStyle,
    val body1: TextStyle,
    val body2: TextStyle,
    val body3: TextStyle,
    val body4: TextStyle,
    val body5: TextStyle,
    val body4Secondary: TextStyle,
    val body5Secondary: TextStyle,
    val label1: TextStyle,
    val label2: TextStyle,
)

internal fun WebsosoTypography(density: Density): WebsosoTypography {
    fun textStyle(
        fontFamily: FontFamily,
        fontWeight: FontWeight,
        fontSizeDp: Dp,
        lineHeightDp: Dp,
        letterSpacingEm: Float = 0f,
    ) = TextStyle(
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        fontSize = with(density) { fontSizeDp.toSp() },
        lineHeight = with(density) { lineHeightDp.toSp() },
        letterSpacing = letterSpacingEm.em,
    )

    return WebsosoTypography(
        headline1 = textStyle(PretendardBold, Bold, 20.dp, 28.dp, -0.06f),
        title1 = textStyle(PretendardBold, Bold, 18.dp, 25.dp, -0.033f),
        title2 = textStyle(PretendardSemiBold, SemiBold, 16.dp, 22.dp, -0.038f),
        title3 = textStyle(PretendardMedium, Medium, 14.dp, 14.dp, -0.043f),
        title4 = textStyle(PretendardBold, Bold, 13.dp, 23.dp),
        body1 = textStyle(PretendardRegular, Normal, 17.dp, 24.dp, -0.035f),
        body2 = textStyle(PretendardRegular, Normal, 15.dp, 22.5.dp, -0.04f),
        body3 = textStyle(PretendardRegular, Normal, 14.dp, 21.dp, -0.029f),
        body4 = textStyle(PretendardMedium, Medium, 13.dp, 19.dp, -0.031f),
        body5 = textStyle(PretendardRegular, Normal, 12.dp, 17.4.dp),
        body4Secondary = textStyle(PretendardRegular, Normal, 13.dp, 19.dp, -0.031f),
        body5Secondary = textStyle(PretendardMedium, Medium, 12.dp, 17.4.dp),
        label1 = textStyle(PretendardMedium, Medium, 13.dp, 19.dp, -0.031f),
        label2 = textStyle(PretendardMedium, Medium, 10.dp, 10.dp),
    )
}

private val PretendardBold = FontFamily(Font(pretendard_bold, Bold))
private val PretendardSemiBold = FontFamily(Font(pretendard_semibold, SemiBold))
private val PretendardMedium = FontFamily(Font(pretendard_medium, Medium))
private val PretendardRegular = FontFamily(Font(pretendard_regular, Normal))
