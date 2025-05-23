package com.into.websoso.ui.expandedFeedImage

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.component.NetworkImage

@Composable
fun ExpandedFeedImage(
    imageUrl: String,
    modifier: Modifier = Modifier,
) {
    NetworkImage(
        imageUrl = imageUrl,
        modifier = modifier
            .fillMaxSize()
            .heightIn(488.dp),
        contentScale = ContentScale.Fit,
    )
}
