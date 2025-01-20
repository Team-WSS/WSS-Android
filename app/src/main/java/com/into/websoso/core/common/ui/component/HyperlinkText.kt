package com.into.websoso.core.common.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.HyperlinkBlue
import com.into.websoso.core.designsystem.theme.WebsosoTheme

private const val URL_REGEX = "(https?://[\\w./?=&%#-]+|www\\.[\\w./?=&%#-]+)"

@Composable
fun HyperlinkText(
    text: String,
    style: TextStyle = WebsosoTheme.typography.body2,
    textColor: Color = Black,
    hyperlinkColor: Color = HyperlinkBlue,
    hyperlinkDecoration: TextDecoration = TextDecoration.Underline,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val urlRegex = Regex(URL_REGEX)

    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        urlRegex.findAll(text).forEach { matchResult ->
            var url = matchResult.value
            val startIndex = matchResult.range.first
            val endIndex = matchResult.range.last + 1

            // `www.`로 시작하면 `https://` 추가
            if (url.startsWith("www.")) {
                url = "https://$url"
            }

            if (startIndex > lastIndex) {
                append(text.substring(lastIndex, startIndex))
            }

            val linkUrl = LinkAnnotation.Url(
                url,
                TextLinkStyles(
                    SpanStyle(
                        color = hyperlinkColor,
                        textDecoration = hyperlinkDecoration,
                    ),
                ),
            ) {
                uriHandler.openUri(url)
            }

            withLink(linkUrl) { append(matchResult.value) }
            lastIndex = endIndex
        }

        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }

        addStyle(
            style = SpanStyle(color = textColor),
            start = 0,
            end = text.length,
        )
    }

    Text(text = annotatedString, modifier = modifier, style = style)
}

@Preview(showBackground = true)
@Composable
fun HyperlinkTextPreview() {
    WebsosoTheme {
        HyperlinkText(
            text = "구글은 www.google.com \n네이버는 https://www.naver.com 입니다.\n웹소소는 websoso.kr 입니다.",
        )
    }
}
