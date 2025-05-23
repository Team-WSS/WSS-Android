package com.into.websoso.ui.feedDetail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.ui.component.AdaptationImage
import com.into.websoso.core.common.util.clickableWithoutRipple

@Composable
fun TripleImageContainer(
    imageUrls: List<String>,
    onImageClick: (index: Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        imageUrls.take(3).forEach { imageUrl ->
            AdaptationImage(
                imageUrl = imageUrl,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickableWithoutRipple {
                        onImageClick(imageUrls.indexOf(imageUrl))
                    },
            )
        }
    }
}
