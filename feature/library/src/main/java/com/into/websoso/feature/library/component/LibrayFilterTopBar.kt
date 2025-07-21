package com.into.websoso.feature.library.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Gray70
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_library_drop_down_fill
import com.into.websoso.core.resource.R.drawable.ic_library_grid
import com.into.websoso.core.resource.R.drawable.ic_library_list
import com.into.websoso.core.resource.R.drawable.ic_library_sort
import com.into.websoso.feature.library.R.string.library_interesting
import com.into.websoso.feature.library.R.string.library_novel_count
import com.into.websoso.feature.library.model.LibraryFilterType
import com.into.websoso.feature.library.model.LibraryFilterUiState
import com.into.websoso.feature.library.model.SortTypeUiModel

@Composable
internal fun LibraryFilterTopBar(
    libraryFilterUiState: LibraryFilterUiState,
    totalCount: Int,
    onFilterClick: (LibraryFilterType) -> Unit,
    selectedSortType: SortTypeUiModel,
    onSortClick: () -> Unit,
    isGrid: Boolean,
    onToggleViewType: () -> Unit,
    onInterestClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp),
    ) {
        NovelFilterChipSection(
            libraryFilterUiState = libraryFilterUiState,
            onFilterClick = onFilterClick,
            onInterestClick = onInterestClick,
        )

        Spacer(modifier = Modifier.height(10.dp))

        NovelFilterStatusBar(
            totalCount = totalCount,
            selectedSortType = selectedSortType,
            isGrid = isGrid,
            onSortClick = onSortClick,
            onToggleViewType = onToggleViewType,
        )
    }
}

@Composable
private fun NovelFilterChipSection(
    libraryFilterUiState: LibraryFilterUiState,
    onInterestClick: () -> Unit,
    onFilterClick: (LibraryFilterType) -> Unit,
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        NovelFilterChip(
            text = stringResource(id = library_interesting),
            isSelected = libraryFilterUiState.isInterested,
            onClick = onInterestClick,
            showDropdownIcon = false,
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .width(1.dp)
                .height(32.dp)
                .background(color = Gray70),
        )
        NovelFilterChip(
            text = libraryFilterUiState.readStatusLabelText,
            isSelected = libraryFilterUiState.readStatuses.any { it.value },
            onClick = { onFilterClick(LibraryFilterType.ReadStatus) },
        )

        NovelFilterChip(
            text = libraryFilterUiState.ratingText,
            isSelected = libraryFilterUiState.isRatingSelected,
            onClick = { onFilterClick(LibraryFilterType.Rating) },
        )

        NovelFilterChip(
            text = libraryFilterUiState.attractivePointLabelText,
            isSelected = libraryFilterUiState.attractivePoints.any { it.value },
            onClick = { onFilterClick(LibraryFilterType.AttractivePoint) },
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
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .defaultMinSize(minHeight = 32.dp)
            .clickable(onClick = onClick),
        border = if (!isSelected) BorderStroke(1.dp, Gray70) else null,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
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
                        .padding(start = 4.dp)
                        .size(12.dp),
                )
            }
        }
    }
}

@Composable
private fun NovelFilterStatusBar(
    totalCount: Int,
    selectedSortType: SortTypeUiModel,
    isGrid: Boolean,
    onSortClick: () -> Unit,
    onToggleViewType: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(library_novel_count, totalCount),
            style = WebsosoTheme.typography.body4,
            color = Gray200,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            SortTypeSelector(
                selectedSortType = selectedSortType,
                onClick = onSortClick,
            )

            IconButton(onClick = onToggleViewType) {
                Image(
                    imageVector = ImageVector.vectorResource(
                        id = if (isGrid) ic_library_grid else ic_library_list,
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun SortTypeSelector(
    selectedSortType: SortTypeUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = ImageVector.vectorResource(id = ic_library_sort),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = selectedSortType.displayName,
                style = WebsosoTheme.typography.body5,
                color = Gray300,
            )
        }
    }
}
