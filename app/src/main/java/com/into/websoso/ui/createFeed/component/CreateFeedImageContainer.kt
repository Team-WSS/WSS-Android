package com.into.websoso.ui.createFeed.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
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
import com.into.websoso.core.common.ui.component.AdaptationImage
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
        Image(
            painter = painterResource(ic_feed_remove_image),
            contentDescription = null,
            modifier = Modifier
                .size(38.dp)
                .padding(10.dp)
                .align(Alignment.TopEnd)
                .clickableWithoutRipple { onRemoveClick() },
        )
        NetworkImage(
            imageUrl = imageUrl,
            contentScale = ContentScale.Crop,
        )
    }
}

@Preview
@Composable
private fun CreateFeedImageContainerPreview() {
    WebsosoTheme {
        CreateFeedImageContainer(
            imageUrls = listOf(
                "https://github.com/user-attachments/assets/e89a02bb-549f-414d-809f-0ab1e8f72c5f",
                "https://github.com/user-attachments/assets/e89a02bb-549f-414d-809f-0ab1e8f72c5f",
                "https://github.com/user-attachments/assets/e89a02bb-549f-414d-809f-0ab1e8f72c5f",
            ),
            onRemoveClick = {},
        )
    }
}
