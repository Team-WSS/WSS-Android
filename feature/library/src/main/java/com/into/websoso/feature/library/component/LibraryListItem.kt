package com.into.websoso.feature.library.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.into.websoso.core.common.extensions.debouncedClickable
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
import com.into.websoso.core.resource.R.drawable.ic_library_writingskill
import com.into.websoso.core.resource.R.drawable.ic_storage_star
import com.into.websoso.domain.library.model.AttractivePoint
import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.NovelRating
import com.into.websoso.feature.library.model.NovelUiModel
import com.into.websoso.feature.library.model.ReadStatusUiModel

private const val THUMBNAIL_WIDTH_RATIO = 60f / 360f
private const val THUMBNAIL_HEIGHT_RATIO = 80f / 360f
private const val FEED_CARD_WIDTH_RATIO = 0.8611f
private val CONTENT_SPACING = 16.dp
private val ATTRACTIVE_POINT_ROW_HEIGHT = 23.dp

@Composable
internal fun LibraryListItem(
    item: NovelUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .debouncedClickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        NovelStatusHeader(
            readStatus = item.readStatus,
            formattedDateRange = item.formattedDateRange,
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(CONTENT_SPACING),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            NovelThumbnail(
                thumbnailUrl = item.novelImage,
                isInteresting = item.isInterest,
            )

            NovelInfo(
                item = item,
                modifier = Modifier.weight(1f),
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
            modifier = Modifier.padding(top = 10.dp),
            thickness = 1.dp,
            color = Gray50,
        )
    }
}

@Composable
private fun NovelStatusHeader(
    readStatus: ReadStatusUiModel?,
    formattedDateRange: String?,
) {
    if (readStatus == null && formattedDateRange == null) return

    val size = calculateThumbnailSize()

    Row(
        horizontalArrangement = Arrangement.spacedBy(CONTENT_SPACING),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ReadStatusBadge(
            readStatusUiModel = readStatus,
            width = size.width,
        )

        formattedDateRange?.let {
            Text(
                text = it,
                style = WebsosoTheme.typography.body5,
                color = Gray300,
            )
        }
    }
}

@Composable
private fun NovelThumbnail(
    thumbnailUrl: String,
    isInteresting: Boolean,
) {
    val size = calculateThumbnailSize()

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
                    .padding(10.dp)
                    .size(13.dp),
            )
        }
    }
}

@Composable
private fun ReadStatusBadge(
    readStatusUiModel: ReadStatusUiModel?,
    width: Dp,
) {
    Box(modifier = Modifier.width(width)) {
        readStatusUiModel?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = it.backgroundColor,
                        shape = RoundedCornerShape(8.dp),
                    ).padding(horizontal = 6.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = it.readStatus.label,
                    color = White,
                    style = WebsosoTheme.typography.label2,
                    softWrap = false,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
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
private fun NovelInfo(
    item: NovelUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = item.title,
                style = WebsosoTheme.typography.title2,
                color = Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            NovelRatings(
                myRating = item.userNovelRating,
                totalRating = item.novelRating,
            )
        }

        AttractivePointTags(attractivePoints = item.attractivePoints)
    }
}

@Composable
private fun NovelRatings(
    myRating: NovelRating?,
    totalRating: Float,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        myRating?.let { MyRatingSection(rating = it.rating.value) }

        TotalRatingSection(rating = totalRating)
    }
}

@Composable
private fun MyRatingSection(rating: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Box(
                modifier = Modifier.size(12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(id = ic_storage_star),
                    contentDescription = null,
                    modifier = Modifier.size(9.dp),
                )
            }

            Text(
                text = "$rating",
                style = WebsosoTheme.typography.body5Secondary,
                color = Secondary100,
            )
        }

        Text(
            text = "내 별점",
            style = WebsosoTheme.typography.body5,
            color = Gray300,
        )
    }
}

@Composable
private fun TotalRatingSection(rating: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = ic_storage_star),
                contentDescription = null,
                modifier = Modifier.size(12.dp),
                tint = Gray200,
            )

            Text(
                text = "$rating",
                style = WebsosoTheme.typography.body5,
                color = Gray200,
            )
        }

        Text(
            text = "전체 별점",
            style = WebsosoTheme.typography.body5,
            color = Gray200,
        )
    }
}

@Composable
private fun AttractivePointTags(attractivePoints: AttractivePoints) {
    val selectedAttractivePoints = attractivePoints.selectedAttractivePoints

    Row(
        modifier = Modifier.height(ATTRACTIVE_POINT_ROW_HEIGHT),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        selectedAttractivePoints.forEachIndexed { index, attractivePoint ->
            AttractivePointItem(attractivePoint)

            if (index < selectedAttractivePoints.lastIndex) {
                Box(
                    modifier = Modifier
                        .size(2.dp)
                        .background(color = Primary100, shape = CircleShape),
                )
            }
        }
    }
}

@Composable
private fun AttractivePointItem(attractivePoint: AttractivePoint) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        Image(
            imageVector = attractivePointIcon(attractivePoint),
            contentDescription = attractivePoint.label,
            modifier = Modifier.size(12.dp),
        )

        Text(
            text = attractivePoint.label,
            style = WebsosoTheme.typography.body5,
            color = Gray300,
        )
    }
}

@Composable
private fun attractivePointIcon(attractivePoint: AttractivePoint): ImageVector {
    val resId = when (attractivePoint) {
        AttractivePoint.CHARACTER -> ic_library_character
        AttractivePoint.MATERIAL -> ic_library_material
        AttractivePoint.WORLDVIEW -> ic_library_world_view
        AttractivePoint.RELATIONSHIP -> ic_library_relationship
        AttractivePoint.VIBE -> ic_library_vibe
        AttractivePoint.WRITINGSKILL -> ic_library_writingskill
    }
    return ImageVector.vectorResource(id = resId)
}

@Composable
private fun NovelKeywordChipGroup(novelKeyword: List<String>) {
    LazyRow(
        contentPadding = PaddingValues(end = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
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
