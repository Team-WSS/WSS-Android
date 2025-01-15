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

    return WebsosoTypography(
        headline = TextStyle(
            fontFamily = PretendardBold,
            fontWeight = FontWeight.Bold,
            fontSize = with(density) { 24.dp.toSp() },
            lineHeight = with(density) { 32.dp.toSp() },
        ),
        title1 = TextStyle(
            fontFamily = PretendardSemiBold,
            fontWeight = FontWeight.SemiBold,
            fontSize = with(density) { 20.dp.toSp() },
            lineHeight = with(density) { 28.dp.toSp() },
        ),
        title2 = TextStyle(
            fontFamily = PretendardMedium,
            fontWeight = FontWeight.Medium,
            fontSize = with(density) { 18.dp.toSp() },
            lineHeight = with(density) { 25.dp.toSp() },
        ),
        body1 = TextStyle(
            fontFamily = PretendardRegular,
            fontWeight = FontWeight.Normal,
            fontSize = with(density) { 16.dp.toSp() },
            lineHeight = with(density) { 24.dp.toSp() },
        ),
        body2 = TextStyle(
            fontFamily = PretendardRegular,
            fontWeight = FontWeight.Normal,
            fontSize = with(density) { 14.dp.toSp() },
            lineHeight = with(density) { 21.dp.toSp() },
        ),
        body3 = TextStyle(
            fontFamily = PretendardRegular,
            fontWeight = FontWeight.Normal,
            fontSize = with(density) { 12.dp.toSp() },
            lineHeight = with(density) { 18.dp.toSp() },
        ),
        caption = TextStyle(
            fontFamily = PretendardRegular,
            fontWeight = FontWeight.Normal,
            fontSize = with(density) { 10.dp.toSp() },
            lineHeight = with(density) { 15.dp.toSp() },
        ),
        button = TextStyle(
            fontFamily = PretendardBold,
            fontWeight = FontWeight.Bold,
            fontSize = with(density) { 14.dp.toSp() },
            lineHeight = with(density) { 20.dp.toSp() },
        ),
        label = TextStyle(
            fontFamily = PretendardSemiBold,
            fontWeight = FontWeight.SemiBold,
            fontSize = with(density) { 13.dp.toSp() },
            lineHeight = with(density) { 19.dp.toSp() },
        ),
    )
}
