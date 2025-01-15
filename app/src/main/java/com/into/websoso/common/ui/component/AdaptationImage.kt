package com.into.websoso.common.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import com.into.websoso.common.util.getS3ImageUrl

@Composable
fun AdaptationImage(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    imageUrl: String,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
) {
    val urlRegex = Regex("^(https?://).*")
    val formattedUrl = when (urlRegex.matches(imageUrl)) {
        true -> imageUrl
        false -> LocalView.current.getS3ImageUrl(imageUrl)
    }

    ExternalImage(
        modifier = modifier,
        contentDescription = contentDescription,
        imageUrl = formattedUrl,
        contentScale = contentScale,
        alignment = alignment,
    )
}
