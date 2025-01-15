package com.into.websoso.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.into.websoso.R

val PretendardBold = FontFamily(Font(R.font.pretendard_bold, FontWeight.Bold))
val PretendardSemiBold = FontFamily(Font(R.font.pretendard_semibold, FontWeight.SemiBold))
val PretendardMedium = FontFamily(Font(R.font.pretendard_medium, FontWeight.Medium))
val PretendardRegular = FontFamily(Font(R.font.pretendard_regular, FontWeight.Normal))

@Stable
class WebsosoTypography internal constructor(
    headline: TextStyle,
    title1: TextStyle,
    title2: TextStyle,
    body1: TextStyle,
    body2: TextStyle,
    body3: TextStyle,
    caption: TextStyle,
    button: TextStyle,
    label: TextStyle,
) {
    var headline: TextStyle by mutableStateOf(headline)
        private set
    var title1: TextStyle by mutableStateOf(title1)
        private set
    var title2: TextStyle by mutableStateOf(title2)
        private set
    var body1: TextStyle by mutableStateOf(body1)
        private set
    var body2: TextStyle by mutableStateOf(body2)
        private set
    var body3: TextStyle by mutableStateOf(body3)
        private set
    var caption: TextStyle by mutableStateOf(caption)
        private set
    var button: TextStyle by mutableStateOf(button)
        private set
    var label: TextStyle by mutableStateOf(label)
        private set

    fun copy(
        headline: TextStyle = this.headline,
        title1: TextStyle = this.title1,
        title2: TextStyle = this.title2,
        body1: TextStyle = this.body1,
        body2: TextStyle = this.body2,
        body3: TextStyle = this.body3,
        caption: TextStyle = this.caption,
        button: TextStyle = this.button,
        label: TextStyle = this.label,
    ): WebsosoTypography = WebsosoTypography(
        headline,
        title1,
        title2,
        body1,
        body2,
        body3,
        caption,
        button,
        label,
    )

    fun update(other: WebsosoTypography) {
        headline = other.headline
        title1 = other.title1
        title2 = other.title2
        body1 = other.body1
        body2 = other.body2
        body3 = other.body3
        caption = other.caption
        button = other.button
        label = other.label
    }
}

@Composable
fun websosoTypography(): WebsosoTypography {
    val density = LocalDensity.current

    fun textStyle(
        fontFamily: FontFamily,
        fontWeight: FontWeight,
        fontSizeDp: Dp,
        lineHeightDp: Dp,
    ): TextStyle {
        return TextStyle(
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = with(density) { fontSizeDp.toSp() },
            lineHeight = with(density) { lineHeightDp.toSp() },
        )
    }

    return WebsosoTypography(
        headline = textStyle(PretendardBold, FontWeight.Bold, 24.dp, 32.dp),
        title1 = textStyle(PretendardSemiBold, FontWeight.SemiBold, 20.dp, 28.dp),
        title2 = textStyle(PretendardMedium, FontWeight.Medium, 18.dp, 25.dp),
        body1 = textStyle(PretendardRegular, FontWeight.Normal, 16.dp, 24.dp),
        body2 = textStyle(PretendardRegular, FontWeight.Normal, 14.dp, 21.dp),
        body3 = textStyle(PretendardRegular, FontWeight.Normal, 12.dp, 18.dp),
        caption = textStyle(PretendardRegular, FontWeight.Normal, 10.dp, 15.dp),
        button = textStyle(PretendardBold, FontWeight.Bold, 14.dp, 20.dp),
        label = textStyle(PretendardSemiBold, FontWeight.SemiBold, 13.dp, 19.dp),
    )
}
