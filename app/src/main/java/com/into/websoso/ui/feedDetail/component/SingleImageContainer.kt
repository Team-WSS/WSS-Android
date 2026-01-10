package com.into.websoso.ui.feedDetail.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.ui.component.AdaptationImage
import com.into.websoso.core.common.util.clickableWithoutRipple

@Composable
fun SingleImageContainer(
    imageUrl: String,
    onImageClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .size(100.dp),
    ) {
        AdaptationImage(
            imageUrl = imageUrl,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(8.dp))
                .clickableWithoutRipple {
                    onImageClick()
                },
        )
    }
}
