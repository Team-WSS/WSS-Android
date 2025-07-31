package com.into.websoso.feature.library.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary20
import com.into.websoso.core.designsystem.theme.Primary50
import com.into.websoso.core.designsystem.theme.Secondary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_library_character
import com.into.websoso.core.resource.R.drawable.ic_library_interesting
import com.into.websoso.core.resource.R.drawable.ic_library_material
import com.into.websoso.core.resource.R.drawable.ic_library_quote_finished
import com.into.websoso.core.resource.R.drawable.ic_library_quote_started
import com.into.websoso.core.resource.R.drawable.ic_library_relationship
import com.into.websoso.core.resource.R.drawable.ic_library_vibe
import com.into.websoso.core.resource.R.drawable.ic_library_world_view
import com.into.websoso.core.resource.R.drawable.ic_storage_star
import com.into.websoso.domain.library.model.AttractivePoint
import com.into.websoso.feature.library.model.AttractivePointUiModel
import com.into.websoso.feature.library.model.LibraryListItemModel
import com.into.websoso.feature.library.model.ReadStatusUiModel

private const val THUMBNAIL_WIDTH_RATIO = 60f / 360f
private const val THUMBNAIL_HEIGHT_RATIO = 80f / 360f
private const val FEED_CARD_WIDTH_RATIO = 0.8611f

@Composable
internal fun LibraryListItem(
    item: LibraryListItemModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NovelThumbnail(
                thumbnailUrl = item.novelImage,
                readStatus = item.readStatus,
                isInteresting = item.isInterest,
            )

            NovelInfo(
                item = item,
            )
        }

        if (item.keywords.isNotEmpty()) {
            NovelKeywordChipGroup(novelKeyword = item.keywords)
        }

        item.myFeeds
            .filter { it.isNotBlank() }
            .takeIf { it.isNotEmpty() }
            ?.let { myFeeds ->
                MyFeedCardGroup(myFeeds = myFeeds)
            }

        HorizontalDivider(
            modifier = Modifier.padding(top = 16.dp),
            thickness = 1.dp,
            color = Gray50,
        )
    }
}

@Composable
private fun NovelThumbnail(
    thumbnailUrl: String,
    readStatus: ReadStatusUiModel?,
    isInteresting: Boolean,
) {
    val size = calculateThumbnailSize()

    Column(modifier = Modifier.width(size.width)) {
        ReadStatusBadge(
            readStatus = readStatus,
            width = size.width,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .size(width = size.width, height = size.height)
                .clip(RoundedCornerShape(8.dp)),
        ) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )

            if (isInteresting) {
                Image(
                    imageVector = ImageVector.vectorResource(id = ic_library_interesting),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun ReadStatusBadge(
    readStatus: ReadStatusUiModel?,
    width: Dp,
) {
    if (readStatus != null) {
        Box(
            modifier = Modifier
                .width(width)
                .background(
                    color = readStatus.backgroundColor,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = readStatus.label,
                color = White,
                style = WebsosoTheme.typography.label2,
            )
        }
    }
}

@Composable
private fun calculateThumbnailSize(): ThumbnailUiSize {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    return ThumbnailUiSize(
        width = screenWidth * THUMBNAIL_WIDTH_RATIO,
        height = screenWidth * THUMBNAIL_HEIGHT_RATIO,
    )
}

@Composable
private fun NovelInfo(item: LibraryListItemModel) {
    Column {
        Spacer(modifier = Modifier.height(2.dp))
        NovelInfoDate(item = item)
        Spacer(modifier = Modifier.height(4.dp))
        NovelInfoContent(
            title = item.title,
            myRating = item.userNovelRating,
            totalRating = item.novelRating,
            attractivePoints = item.attractivePoints,
        )
    }
}

@Composable
private fun NovelInfoDate(item: LibraryListItemModel) {
    Box(modifier = Modifier.height(18.dp)) {
        item.formattedDateRange?.let {
            Text(
                text = it,
                style = WebsosoTheme.typography.body5,
                color = Gray300,
            )
        }
    }
}

@Composable
private fun NovelInfoContent(
    title: String,
    myRating: Float?,
    totalRating: Float,
    attractivePoints: List<AttractivePointUiModel>,
) {
    val size = calculateThumbnailSize()

    Column(
        modifier = Modifier.height(size.height),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            style = WebsosoTheme.typography.title2,
            color = Black,
        )

        Spacer(modifier = Modifier.height(4.dp))

        NovelRatings(
            myRating = myRating,
            totalRating = totalRating,
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (attractivePoints.isNotEmpty()) {
            AttractivePointTags(types = attractivePoints)
        } else {
            Box(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun NovelRatings(
    myRating: Float?,
    totalRating: Float,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        myRating?.let {
            MyRatingSection(rating = it)
            Spacer(modifier = Modifier.width(10.dp))
        }
        TotalRatingSection(rating = totalRating)
    }
}

@Composable
private fun MyRatingSection(rating: Float) {
    Image(
        imageVector = ImageVector.vectorResource(id = ic_storage_star),
        contentDescription = null,
        modifier = Modifier.size(10.dp),
    )

    Spacer(modifier = Modifier.width(2.dp))

    Text(
        text = "$rating",
        style = WebsosoTheme.typography.body5Secondary,
        color = Secondary100,
    )

    Spacer(modifier = Modifier.width(4.dp))

    Text(
        text = "내 별점",
        style = WebsosoTheme.typography.body5,
        color = Gray300,
    )
}

@Composable
private fun TotalRatingSection(rating: Float) {
    Icon(
        imageVector = ImageVector.vectorResource(id = ic_storage_star),
        contentDescription = null,
        modifier = Modifier.size(10.dp),
        tint = Gray200,
    )

    Spacer(modifier = Modifier.width(2.dp))

    Text(
        text = "$rating 전체 별점",
        style = WebsosoTheme.typography.body5,
        color = Gray200,
    )
}

@Composable
private fun AttractivePointTags(types: List<AttractivePointUiModel>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        types.forEachIndexed { index, type ->
            AttractivePointItem(type)

            if (index < types.lastIndex) {
                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "•",
                    style = WebsosoTheme.typography.body4,
                    color = Primary100,
                )

                Spacer(modifier = Modifier.width(6.dp))
            }
        }
    }
}

@Composable
private fun AttractivePointItem(type: AttractivePointUiModel) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            imageVector = attractivePointIcon(type),
            contentDescription = type.label,
            modifier = Modifier.size(16.dp),
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = type.label,
            style = WebsosoTheme.typography.body4,
            color = Gray300,
        )
    }
}

@Composable
private fun attractivePointIcon(points: AttractivePointUiModel): ImageVector {
    val resId = when (points.type) {
        AttractivePoint.CHARACTER -> ic_library_character
        AttractivePoint.MATERIAL -> ic_library_material
        AttractivePoint.WORLDVIEW -> ic_library_world_view
        AttractivePoint.RELATIONSHIP -> ic_library_relationship
        AttractivePoint.VIBE -> ic_library_vibe
    }
    return ImageVector.vectorResource(id = resId)
}

@Composable
private fun NovelKeywordChipGroup(novelKeyword: List<String>) {
    LazyRow(
        contentPadding = PaddingValues(end = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(novelKeyword) {
            NovelKeywordChip(it)
        }
    }
}

@Composable
private fun NovelKeywordChip(keyword: String) {
    Text(
        text = keyword,
        style = WebsosoTheme.typography.body5,
        color = Gray200,
        modifier = Modifier
            .background(color = Primary20, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
    )
}

@Composable
private fun MyFeedCardGroup(myFeeds: List<String>) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(end = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(myFeeds, key = { it }) { myFeed ->
            MyFeedCard(myFeed)
        }
    }
}

@Composable
private fun MyFeedCard(myFeed: String) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth * FEED_CARD_WIDTH_RATIO

    Box(
        modifier = Modifier
            .width(cardWidth)
            .height(54.dp)
            .background(color = Primary50, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = ic_library_quote_started),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.TopStart),
        )

        Text(
            text = myFeed,
            style = WebsosoTheme.typography.body5,
            color = Black,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
        )

        Image(
            imageVector = ImageVector.vectorResource(id = ic_library_quote_finished),
            contentDescription = null,
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.BottomEnd),
        )
    }
}

private data class ThumbnailUiSize(
    val width: Dp,
    val height: Dp,
)
