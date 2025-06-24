package com.into.websoso.ui.createFeed.component

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.into.websoso.core.common.util.clickableWithoutRipple
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_feed_remove_image
import com.into.websoso.ui.createFeed.CreateFeedViewModel
import com.into.websoso.ui.createFeed.CreateFeedViewModel.Companion.MAX_IMAGE_COUNT

@Composable
fun CreateFeedImageContainer(
    viewModel: CreateFeedViewModel,
    onRemoveClick: (index: Int) -> Unit,
) {
    val images = viewModel.attachedImages.collectAsStateWithLifecycle()
    Column {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
        ) {
            items(images.value.size) { index ->
                CreateFeedImageBox(
                    image = images.value.elementAt(index),
                    onRemoveClick = {
                        onRemoveClick(index)
                    },
                )
            }
        }
        if (images.value.isNotEmpty()) {
            FeedImageCounter(
                current = images.value.size,
                max = MAX_IMAGE_COUNT,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun CreateFeedImageBox(
    image: Uri,
    onRemoveClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp)),
    ) {
        Image(
            painter = rememberAsyncImagePainter(image),
            contentDescription = null,
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

@Composable
private fun FeedImageCounter(
    current: Int,
    max: Int,
    modifier: Modifier = Modifier,
) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(color = Primary100)) {
                append("$current")
            }
            withStyle(style = SpanStyle(color = Gray200)) {
                append(" / $max")
            }
        },
        style = WebsosoTheme.typography.title2,
        modifier = modifier.padding(start = 20.dp, top = 8.dp),
    )
}

/*
@Preview
@Composable
private fun CreateFeedImageContainerPreview() {
    WebsosoTheme {
        CreateFeedImageContainer(
            viewModel = CreateFeedViewModel(),
            onRemoveClick = {},
        )
    }
}
*/
