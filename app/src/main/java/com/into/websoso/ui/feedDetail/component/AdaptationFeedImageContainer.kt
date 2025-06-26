package com.into.websoso.ui.feedDetail.component

import androidx.compose.runtime.Composable
import com.into.websoso.ui.feedDetail.model.ImageContainerType
import com.into.websoso.ui.feedDetail.model.ImageContainerType.DOUBLE
import com.into.websoso.ui.feedDetail.model.ImageContainerType.EMPTY
import com.into.websoso.ui.feedDetail.model.ImageContainerType.MULTIPLE
import com.into.websoso.ui.feedDetail.model.ImageContainerType.SINGLE
import com.into.websoso.ui.feedDetail.model.ImageContainerType.TRIPLE

@Composable
fun AdaptationFeedImageContainer(
    imageUrls: List<String>,
    onImageClick: (index: Int) -> Unit,
) {
    when (ImageContainerType.from(imageUrls.size)) {
        EMPTY -> Unit
        SINGLE -> SingleImageContainer(imageUrls.first()) { onImageClick(0) }
        DOUBLE -> DoubleImageContainer(imageUrls, onImageClick)
        TRIPLE -> TripleImageContainer(imageUrls, onImageClick)
        MULTIPLE -> MultipleImageContainer(imageUrls, onImageClick)
    }
}
