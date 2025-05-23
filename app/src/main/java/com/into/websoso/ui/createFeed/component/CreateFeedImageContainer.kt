package com.into.websoso.ui.createFeed.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.util.clickableWithoutRipple
import com.into.websoso.core.designsystem.component.NetworkImage
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_feed_remove_image

@Composable
fun CreateFeedImageContainer(
    imageUrls: List<String>,
    onRemoveClick: (index: Int) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        items(imageUrls.size) { index ->
            CreateFeedImageBox(
                imageUrl = imageUrls[index],
                onRemoveClick = {
                    onRemoveClick(index)
                },
            )
        }
    }
}

@Composable
private fun CreateFeedImageBox(
    imageUrl: String,
    onRemoveClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp)),
    ) {
        NetworkImage(
            imageUrl = imageUrl,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        ImageRemoveButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onRemoveClick = onRemoveClick,
        )
    }
}

@Composable
private fun ImageRemoveButton(
    modifier: Modifier,
    onRemoveClick: () -> Unit,
) {
    Image(
        painter = painterResource(ic_feed_remove_image),
        contentDescription = null,
        modifier = modifier
            .size(38.dp)
            .padding(10.dp)
            .clickableWithoutRipple { onRemoveClick() },
    )
}

@Preview
@Composable
private fun CreateFeedImageContainerPreview() {
    WebsosoTheme {
        CreateFeedImageContainer(
            imageUrls = listOf(
                "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/00fb05f8-cb19-4d21-84b1-5cf6b9988749.jpg",
                "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/00fb05f8-cb19-4d21-84b1-5cf6b9988749.jpg",
                "https://product-image.kurly.com/hdims/resize/%5E%3E360x%3E468/cropcenter/360x468/quality/85/src/product/image/00fb05f8-cb19-4d21-84b1-5cf6b9988749.jpg",
            ),
            onRemoveClick = {},
        )
    }
}
