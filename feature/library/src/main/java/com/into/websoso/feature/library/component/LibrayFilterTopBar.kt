package com.into.websoso.feature.library.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.into.websoso.core.common.extensions.debouncedClickable
import com.into.websoso.core.common.extensions.debouncedSelectable
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray100
import com.into.websoso.core.designsystem.theme.Gray20
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Gray70New
import com.into.websoso.core.designsystem.theme.Gray80
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_library_drop_down_fill
import com.into.websoso.core.resource.R.drawable.ic_library_grid
import com.into.websoso.core.resource.R.drawable.ic_library_list
import com.into.websoso.core.resource.R.drawable.ic_library_notification
import com.into.websoso.core.resource.R.drawable.ic_library_sort
import com.into.websoso.domain.library.model.SortCriteria
import com.into.websoso.feature.library.filter.LibraryFilterTab
import com.into.websoso.feature.library.model.LibraryFilterUiModel

private val VIEW_TYPE_TOGGLE_SHAPE = RoundedCornerShape(16.dp)
private val VIEW_TYPE_TOGGLE_BUTTON_SHAPE = RoundedCornerShape(13.dp)

@Composable
internal fun LibraryFilterTopBar(
    libraryFilterUiModel: LibraryFilterUiModel,
    totalCount: Long,
    onFilterClick: (LibraryFilterTab) -> Unit,
    onSortClick: () -> Unit,
    isGrid: Boolean,
    onToggleViewType: () -> Unit,
    onInterestClick: () -> Unit,
    modifier: Modifier = Modifier,
    onNotificationManageClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp)
                .padding(vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LibraryViewTypeToggle(
                isGrid = isGrid,
                onToggleViewType = onToggleViewType,
            )

            Spacer(modifier = Modifier.width(12.dp))

            VerticalDivider(height = 29.dp)

            Spacer(modifier = Modifier.width(10.dp))

            NovelFilterChipSection(
                libraryFilterUiModel = libraryFilterUiModel,
                onFilterClick = onFilterClick,
                onInterestClick = onInterestClick,
            )
        }

        NovelFilterStatusBar(
            totalCount = totalCount,
            sortCriteria = libraryFilterUiModel.sortCriteria,
            onSortClick = onSortClick,
            onNotificationManageClick = onNotificationManageClick,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = Gray50),
        )
    }
}

@Composable
private fun LibraryViewTypeToggle(
    isGrid: Boolean,
    onToggleViewType: () -> Unit,
) {
    Row(
        modifier = Modifier
            .width(70.dp)
            .height(33.dp)
            .background(color = Gray20, shape = VIEW_TYPE_TOGGLE_SHAPE)
            .border(width = 1.dp, color = Gray70New, shape = VIEW_TYPE_TOGGLE_SHAPE)
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LibraryViewTypeToggleButton(
            iconRes = ic_library_grid,
            iconSize = 12.dp,
            isSelected = isGrid,
            onClick = { if (!isGrid) onToggleViewType() },
        )

        LibraryViewTypeToggleButton(
            iconRes = ic_library_list,
            iconSize = 13.dp,
            isSelected = isGrid.not(),
            onClick = { if (isGrid) onToggleViewType() },
        )
    }
}

@Composable
private fun RowScope.LibraryViewTypeToggleButton(
    @DrawableRes iconRes: Int,
    iconSize: Dp,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val selectedModifier = when (isSelected) {
        true -> {
            Modifier
                .shadow(elevation = 2.dp, shape = VIEW_TYPE_TOGGLE_BUTTON_SHAPE)
                .background(color = White, shape = VIEW_TYPE_TOGGLE_BUTTON_SHAPE)
        }

        false -> {
            Modifier
        }
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .then(selectedModifier)
            .clip(VIEW_TYPE_TOGGLE_BUTTON_SHAPE)
            .debouncedSelectable(selected = isSelected, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconRes),
            contentDescription = null,
            tint = if (isSelected) Black else Gray100,
            modifier = Modifier.size(iconSize),
        )
    }
}

@Composable
private fun VerticalDivider(
    height: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(1.dp)
            .height(height)
            .background(color = Gray80),
    )
}

@Composable
private fun NovelFilterChipSection(
    libraryFilterUiModel: LibraryFilterUiModel,
    onInterestClick: () -> Unit,
    onFilterClick: (LibraryFilterTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(end = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        NovelFilterChip(
            text = "관심",
            isSelected = libraryFilterUiModel.isInterested,
            onClick = onInterestClick,
            showDropdownIcon = false,
        )

        NovelFilterChip(
            text = libraryFilterUiModel.readStatusLabelText,
            isSelected = libraryFilterUiModel.readStatuses.isSelected,
            onClick = { onFilterClick(LibraryFilterTab.READ_STATUS) },
        )

        NovelFilterChip(
            text = libraryFilterUiModel.genreLabelText,
            isSelected = libraryFilterUiModel.genres.isSelected,
            onClick = { onFilterClick(LibraryFilterTab.GENRE) },
        )

        NovelFilterChip(
            text = libraryFilterUiModel.seriesStatusLabelText,
            isSelected = libraryFilterUiModel.seriesStatuses.isSelected,
            onClick = { onFilterClick(LibraryFilterTab.SERIES_STATUS) },
        )

        NovelFilterChip(
            text = libraryFilterUiModel.ratingText,
            isSelected = libraryFilterUiModel.ratingFilter.isSelected,
            onClick = { onFilterClick(LibraryFilterTab.RATING) },
        )

        NovelFilterChip(
            text = libraryFilterUiModel.attractivePointLabelText,
            isSelected = libraryFilterUiModel.attractivePoints.isSelected,
            onClick = { onFilterClick(LibraryFilterTab.ATTRACTIVE_POINT) },
        )

        NovelFilterChip(
            text = libraryFilterUiModel.keywordLabelText,
            isSelected = libraryFilterUiModel.keywords.isSelected,
            onClick = { onFilterClick(LibraryFilterTab.KEYWORD) },
        )
    }
}

@Composable
private fun NovelFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    showDropdownIcon: Boolean = true,
) {
    val backgroundColor = if (isSelected) Black else White
    val textColor = if (isSelected) White else Gray300

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .defaultMinSize(minHeight = 33.dp)
            .debouncedClickable(onClick = onClick),
        border = if (!isSelected) BorderStroke(1.dp, Gray80) else null,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
        ) {
            Text(
                text = text,
                color = textColor,
                style = WebsosoTheme.typography.body5,
            )
            if (showDropdownIcon) {
                Image(
                    imageVector = ImageVector.vectorResource(id = ic_library_drop_down_fill),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .size(14.dp),
                )
            }
        }
    }
}

@Composable
private fun NovelFilterStatusBar(
    totalCount: Long,
    sortCriteria: SortCriteria,
    onSortClick: () -> Unit,
    onNotificationManageClick: (() -> Unit)?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = "${totalCount}개",
            style = WebsosoTheme.typography.body4,
            color = Gray200,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            onNotificationManageClick?.let { onClick ->
                StatusBarAction(
                    iconRes = ic_library_notification,
                    iconSize = 12.dp,
                    text = "알림 관리",
                    onClick = onClick,
                )

                VerticalDivider(height = 8.dp)
            }

            StatusBarAction(
                iconRes = ic_library_sort,
                iconSize = 16.dp,
                text = sortCriteria.label,
                onClick = onSortClick,
            )
        }
    }
}

@Composable
private fun StatusBarAction(
    @DrawableRes iconRes: Int,
    iconSize: Dp,
    text: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .debouncedClickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Image(
            imageVector = ImageVector.vectorResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
        )

        Text(
            text = text,
            style = WebsosoTheme.typography.body3,
            color = Gray300,
        )
    }
}
