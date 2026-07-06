package com.into.websoso.feature.library.filter.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.into.websoso.domain.library.model.AttractivePoint
import com.into.websoso.domain.library.model.Genre
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.domain.library.model.SeriesStatus
import com.into.websoso.feature.library.model.LibraryFilterUiModel

@Composable
internal fun LibraryFilterSelectedChips(
    filterUiState: LibraryFilterUiModel,
    onReadStatusClick: (ReadStatus) -> Unit,
    onGenreClick: (Genre) -> Unit,
    onSeriesStatusClick: (SeriesStatus) -> Unit,
    onAttractivePointClick: (AttractivePoint) -> Unit,
    onRatingRemove: () -> Unit,
    onKeywordClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(end = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        filterUiState.readStatuses.value.filterValues { it }.keys.forEach { status ->
            LibraryFilterRemovableChip(
                label = status.label,
                onRemove = { onReadStatusClick(status) },
            )
        }
        filterUiState.genres.selectedGenres.forEach { genre ->
            LibraryFilterRemovableChip(
                label = genre.label,
                onRemove = { onGenreClick(genre) },
            )
        }
        filterUiState.seriesStatuses.selectedStatuses.forEach { status ->
            LibraryFilterRemovableChip(
                label = status.label,
                onRemove = { onSeriesStatusClick(status) },
            )
        }
        if (filterUiState.ratingFilter.isSelected) {
            LibraryFilterRemovableChip(
                label = filterUiState.ratingFilter.chipLabel,
                onRemove = onRatingRemove,
            )
        }
        filterUiState.attractivePoints.selectedAttractivePoints.forEach { point ->
            LibraryFilterRemovableChip(
                label = point.label,
                onRemove = { onAttractivePointClick(point) },
            )
        }
        filterUiState.keywords.selectedNames.forEach { keywordName ->
            LibraryFilterRemovableChip(
                label = keywordName,
                onRemove = { onKeywordClick(keywordName) },
            )
        }
    }
}
