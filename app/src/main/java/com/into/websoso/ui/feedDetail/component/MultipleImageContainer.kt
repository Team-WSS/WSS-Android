package com.into.websoso.ui.feedDetail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.ui.component.AdaptationImage
import com.into.websoso.core.common.util.clickableWithoutRipple

@Composable
fun MultipleImageContainer(
    imageUrls: List<String>,
    onImageClick: (index: Int) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        items(imageUrls.size) { index ->
            AdaptationImage(
                imageUrl = imageUrls[index],
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickableWithoutRipple {
                        onImageClick(index)
                    },
            )
        }
    }
}
