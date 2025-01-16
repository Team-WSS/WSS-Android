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
    title3: TextStyle,
    body1: TextStyle,
    body2: TextStyle,
    body3: TextStyle,
    body4: TextStyle,
    body4Secondary: TextStyle,
    body5: TextStyle,
    body5Secondary: TextStyle,
    label1: TextStyle,
    label2: TextStyle,
) {
    var headline: TextStyle by mutableStateOf(headline)
        private set
    var title1: TextStyle by mutableStateOf(title1)
        private set
    var title2: TextStyle by mutableStateOf(title2)
        private set
    var title3: TextStyle by mutableStateOf(title3)
        private set
    var body1: TextStyle by mutableStateOf(body1)
        private set
    var body2: TextStyle by mutableStateOf(body2)
        private set
    var body3: TextStyle by mutableStateOf(body3)
        private set
    var body4: TextStyle by mutableStateOf(body4)
        private set
    var body4Secondary: TextStyle by mutableStateOf(body4Secondary)
        private set
    var body5: TextStyle by mutableStateOf(body5)
        private set
    var body5Secondary: TextStyle by mutableStateOf(body5Secondary)
        private set
    var label1: TextStyle by mutableStateOf(label1)
        private set
    var label2: TextStyle by mutableStateOf(label2)
        private set

    fun copy(
        headline: TextStyle = this.headline,
        title1: TextStyle = this.title1,
        title2: TextStyle = this.title2,
        title3: TextStyle = this.title3,
        body1: TextStyle = this.body1,
        body2: TextStyle = this.body2,
        body3: TextStyle = this.body3,
        body4: TextStyle = this.body4,
        body4Secondary: TextStyle = this.body4Secondary,
        body5: TextStyle = this.body5,
        body5Secondary: TextStyle = this.body5Secondary,
        label1: TextStyle = this.label1,
        label2: TextStyle = this.label2,
    ): WebsosoTypography =
        WebsosoTypography(
            headline,
            title1,
            title2,
            title3,
            body1,
            body2,
            body3,
            body4,
            body4Secondary,
            body5,
            body5Secondary,
            label1,
            label2,
        )

    fun update(other: WebsosoTypography) {
        headline = other.headline
        title1 = other.title1
        title2 = other.title2
        title3 = other.title3
        body1 = other.body1
        body2 = other.body2
        body3 = other.body3
        body4 = other.body4
        body4Secondary = other.body4Secondary
        body5 = other.body5
        body5Secondary = other.body5Secondary
        label1 = other.label1
        label2 = other.label2
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
    ): TextStyle =
        TextStyle(
            fontFamily = fontFamily,
            fontWeight = fontWeight,
            fontSize = with(density) { fontSizeDp.toSp() },
            lineHeight = with(density) { lineHeightDp.toSp() },
        )

    return WebsosoTypography(
        headline = textStyle(PretendardBold, FontWeight.Bold, 20.dp, 28.dp),
        title1 = textStyle(PretendardBold, FontWeight.Bold, 18.dp, 25.dp),
        title2 = textStyle(PretendardSemiBold, FontWeight.SemiBold, 16.dp, 22.dp),
        title3 = textStyle(PretendardMedium, FontWeight.Medium, 14.dp, 14.dp),
        body1 = textStyle(PretendardRegular, FontWeight.Normal, 17.dp, 24.dp),
        body2 = textStyle(PretendardRegular, FontWeight.Normal, 15.dp, 23.dp),
        body3 = textStyle(PretendardRegular, FontWeight.Normal, 14.dp, 21.dp),
        body4 = textStyle(PretendardMedium, FontWeight.Medium, 13.dp, 19.dp),
        body4Secondary = textStyle(PretendardRegular, FontWeight.Normal, 13.dp, 19.dp),
        body5 = textStyle(PretendardRegular, FontWeight.Normal, 12.dp, 17.dp),
        body5Secondary = textStyle(PretendardMedium, FontWeight.Medium, 12.dp, 17.dp),
        label1 = textStyle(PretendardMedium, FontWeight.Medium, 13.dp, 19.dp),
        label2 = textStyle(PretendardRegular, FontWeight.Normal, 10.dp, 10.dp),
    )
}
