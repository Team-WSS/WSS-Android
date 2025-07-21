package com.into.websoso.feature.library.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.component.NetworkImage
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_library_half_star
import com.into.websoso.core.resource.R.drawable.ic_library_interesting
import com.into.websoso.core.resource.R.drawable.ic_library_null_star
import com.into.websoso.core.resource.R.drawable.ic_storage_star
import com.into.websoso.feature.library.model.LibraryListItemModel
import com.into.websoso.feature.library.model.RatingStarType
import com.into.websoso.feature.library.model.ReadStatusUiModel

private const val GRID_COLUMN_COUNT = 3
private val ITEM_SPACING = 6.dp
private val HORIZONTAL_PADDING = 20.dp
private const val IMAGE_ASPECT_WIDTH = 102.67f
private const val IMAGE_ASPECT_HEIGHT = 160f

@Composable
internal fun NovelGridListItem(
    item: LibraryListItemModel,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit = {},
) {
    val itemSize = rememberGridItemSize()

    Column(
        modifier = modifier
            .width(itemSize.width)
            .wrapContentHeight()
            .clickable { onItemClick() },
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        NovelGridThumbnail(
            item = item,
            size = itemSize,
        )

        Text(
            text = item.title,
            style = WebsosoTheme.typography.body4,
            color = Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        if (item.ratingStars.isNotEmpty()) {
            NovelRatingStar(stars = item.ratingStars)
        }

        item.formattedDateRange?.let {
            Text(
                text = it,
                style = WebsosoTheme.typography.label2,
                color = Gray200,
            )
        }
    }
}

@Composable
private fun NovelGridThumbnail(
    item: LibraryListItemModel,
    size: GridItemSize,
) {
    Box(
        modifier = Modifier
            .size(width = size.width, height = size.height)
            .clip(RoundedCornerShape(8.dp)),
    ) {
        NetworkImage(
            imageUrl = item.novelImage,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        item.readStatus?.let {
            ReadStatusBadge(
                readStatus = it,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp),
            )
        }

        if (item.isInterest) {
            Image(
                imageVector = ImageVector.vectorResource(id = ic_library_interesting),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.BottomEnd)
                    .padding(6.dp),
            )
        }
    }
}

@Composable
private fun ReadStatusBadge(
    readStatus: ReadStatusUiModel,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(48.dp)
            .background(
                color = readStatus.backgroundColor,
                shape = RoundedCornerShape(4.dp),
            ).padding(vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = readStatus.label,
            color = White,
            style = WebsosoTheme.typography.label2,
        )
    }
}

@Composable
private fun rememberGridItemSize(): GridItemSize {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val density = LocalDensity.current

    return remember(screenWidth) {
        with(density) {
            val totalSpacingPx =
                (ITEM_SPACING * (GRID_COLUMN_COUNT - 1) + HORIZONTAL_PADDING * 2).toPx()
            val itemWidthPx = ((screenWidth.dp.toPx() - totalSpacingPx) / GRID_COLUMN_COUNT)
            val itemHeightPx = itemWidthPx * (IMAGE_ASPECT_HEIGHT / IMAGE_ASPECT_WIDTH)

            GridItemSize(itemWidthPx.toDp(), itemHeightPx.toDp())
        }
    }
}

@Composable
internal fun NovelRatingStar(
    stars: List<RatingStarType>,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        stars.forEach { starType ->
            Image(
                imageVector = ratingStarIcon(starType),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
            )
        }
    }
}

@Composable
private fun ratingStarIcon(starType: RatingStarType): ImageVector {
    val resId = when (starType) {
        RatingStarType.FULL -> ic_storage_star
        RatingStarType.HALF -> ic_library_half_star
        RatingStarType.EMPTY -> ic_library_null_star
    }
    return ImageVector.vectorResource(id = resId)
}

private data class GridItemSize(
    val width: Dp,
    val height: Dp,
)
