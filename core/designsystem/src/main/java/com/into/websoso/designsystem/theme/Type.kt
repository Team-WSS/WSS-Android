package com.into.websoso.designsystem.theme

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
import com.into.websoso.core.resource.R.font.pretendard_bold
import com.into.websoso.core.resource.R.font.pretendard_medium
import com.into.websoso.core.resource.R.font.pretendard_regular
import com.into.websoso.core.resource.R.font.pretendard_semibold

data class WebsosoTypography(
    val headline1: TextStyle,

    val title1: TextStyle,
    val title2: TextStyle,
    val title3: TextStyle,

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
    val textStyle =
        { fontFamily: FontFamily, fontWeight: FontWeight, fontSizeDp: Dp, lineHeightDp: Dp ->
            TextStyle(
                fontFamily = fontFamily,
                fontWeight = fontWeight,
                fontSize = with(density) { fontSizeDp.toSp() },
                lineHeight = with(density) { lineHeightDp.toSp() },
            )
        }

    return WebsosoTypography(
        headline1 = textStyle(PretendardBold, Bold, 20.dp, 28.dp),

        title1 = textStyle(PretendardBold, Bold, 18.dp, 25.dp),
        title2 = textStyle(PretendardSemiBold, SemiBold, 16.dp, 22.dp),
        title3 = textStyle(PretendardMedium, Medium, 14.dp, 14.dp),

        body1 = textStyle(PretendardRegular, Normal, 17.dp, 24.dp),
        body2 = textStyle(PretendardRegular, Normal, 15.dp, 23.dp),
        body3 = textStyle(PretendardRegular, Normal, 14.dp, 21.dp),
        body4 = textStyle(PretendardMedium, Medium, 13.dp, 19.dp),
        body5 = textStyle(PretendardRegular, Normal, 12.dp, 17.dp),

        body4Secondary = textStyle(PretendardRegular, Normal, 13.dp, 19.dp),
        body5Secondary = textStyle(PretendardMedium, Medium, 12.dp, 17.dp),

        label1 = textStyle(PretendardMedium, Medium, 13.dp, 19.dp),
        label2 = textStyle(PretendardRegular, Normal, 10.dp, 10.dp),
    )
}

private val PretendardBold = FontFamily(Font(pretendard_bold, Bold))
private val PretendardSemiBold = FontFamily(Font(pretendard_semibold, SemiBold))
private val PretendardMedium = FontFamily(Font(pretendard_medium, Medium))
private val PretendardRegular = FontFamily(Font(pretendard_regular, Normal))
