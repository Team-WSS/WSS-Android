package com.into.websoso.ui.notificationDetail.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.HyperlinkBlue
import com.into.websoso.core.designsystem.theme.WebsosoTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HyperlinkText(
    text: String,
    style: TextStyle =
        WebsosoTheme.typography.body2,
    textColor: Color = Black,
    hyperlinkColor: Color = HyperlinkBlue,
    hyperlinkDecoration: TextDecoration = TextDecoration.Underline,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    val annotatedString = buildAnnotatedString {
        val regex = Regex("(https?://[\\w./?=&%]+|www\\.[\\w./?=&%]+)")
        var lastIndex = 0
        regex.findAll(text).forEach { matchResult ->
            val startIndex = matchResult.range.first
            val endIndex = matchResult.range.last + 1

            if (startIndex > lastIndex) {
                append(text.substring(lastIndex, startIndex))
                addStyle(
                    style = SpanStyle(
                        color = textColor,
                    ),
                    start = length - (startIndex - lastIndex),
                    end = length,
                )
            }

            val url = matchResult.value
            addStyle(
                style = SpanStyle(
                    color = hyperlinkColor,
                    textDecoration = hyperlinkDecoration,
                ),
                start = length,
                end = length + url.length,
            )
            addStringAnnotation(
                tag = "URL",
                annotation = url,
                start = length,
                end = length + url.length,
            )
            append(url)

            lastIndex = endIndex
        }
        // Add remaining text
        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
            addStyle(
                style = SpanStyle(
                    color = textColor,
                ),
                start = length - (text.length - lastIndex),
                end = length,
            )
        }
    }

    Text(
        text = annotatedString,
        modifier = modifier.pointerInteropFilter { event ->
            val offset = event.x.toInt()
            annotatedString
                .getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()
                ?.let { annotation ->
                    uriHandler.openUri(annotation.item)
                }
            false
        },
        style = style,
    )
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
