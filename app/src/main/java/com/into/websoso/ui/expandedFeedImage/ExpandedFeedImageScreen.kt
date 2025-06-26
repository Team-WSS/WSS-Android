package com.into.websoso.ui.expandedFeedImage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.util.clickableWithoutRipple
import com.into.websoso.core.designsystem.theme.ExpandedFeedImageBackground
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_expanded_feed_image_close

@Composable
fun ExpandedFeedImageScreen(
    index: Int = 0,
    imageUrls: List<String> = emptyList(),
    onBackButtonClick: () -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = index, pageCount = { imageUrls.size })

    BackHandler {
        onBackButtonClick()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ExpandedFeedImageBackground),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            ExpandedFeedImage(imageUrl = imageUrls[page])
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .align(Alignment.TopCenter),
        ) {
            ExpandedFeedImageCloseButton(
                modifier = Modifier
                    .align(Alignment.CenterStart),
                onBackButtonClick = onBackButtonClick,
            )
            ExpandedFeedImagePageNumber(
                pagerState = pagerState,
                imageUrls = imageUrls,
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Composable
private fun ExpandedFeedImageCloseButton(
    modifier: Modifier = Modifier,
    onBackButtonClick: () -> Unit,
) {
    Icon(
        painter = painterResource(id = ic_expanded_feed_image_close),
        contentDescription = null,
        tint = White,
        modifier = modifier
            .padding(start = 6.dp)
            .size(44.dp)
            .clickableWithoutRipple {
                onBackButtonClick()
            },
    )
}

@Composable
private fun ExpandedFeedImagePageNumber(
    pagerState: PagerState,
    imageUrls: List<String>,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "${pagerState.currentPage + 1}/${imageUrls.size}",
        style = WebsosoTheme.typography.title2,
        color = White,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth(),
    )
}

@Preview
@Composable
private fun ExpandedFeedImageScreenPreview() {
    WebsosoTheme {
        ExpandedFeedImageScreen(
            imageUrls = listOf(
                "https://example.com/image1.jpg",
                "https://example.com/image2.jpg",
                "https://example.com/image3.jpg",
            ),
            onBackButtonClick = {},
        )
    }
}
