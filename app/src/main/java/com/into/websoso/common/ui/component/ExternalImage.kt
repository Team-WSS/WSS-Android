package com.into.websoso.common.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@Composable
fun ExternalImage(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    imageUrl: String,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
) {
    AsyncImage(
        modifier = modifier,
        contentDescription = contentDescription,
        model = imageUrl,
        contentScale = contentScale,
        alignment = alignment,
    )
}
