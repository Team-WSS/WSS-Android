package com.into.websoso.feature.library.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.domain.library.model.AttractivePoint
import com.into.websoso.domain.library.model.Genre
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.domain.library.model.SeriesStatus
import com.into.websoso.feature.library.filter.LibraryFilterTab.ATTRACTIVE_POINT
import com.into.websoso.feature.library.filter.LibraryFilterTab.GENRE
import com.into.websoso.feature.library.filter.LibraryFilterTab.KEYWORD
import com.into.websoso.feature.library.filter.LibraryFilterTab.RATING
import com.into.websoso.feature.library.filter.LibraryFilterTab.READ_STATUS
import com.into.websoso.feature.library.filter.LibraryFilterTab.SERIES_STATUS
import com.into.websoso.feature.library.filter.component.LibraryFilterBottomSheetAttractivePoints
import com.into.websoso.feature.library.filter.component.LibraryFilterBottomSheetButtons
import com.into.websoso.feature.library.filter.component.LibraryFilterBottomSheetGenre
import com.into.websoso.feature.library.filter.component.LibraryFilterBottomSheetHeader
import com.into.websoso.feature.library.filter.component.LibraryFilterBottomSheetKeyword
import com.into.websoso.feature.library.filter.component.LibraryFilterBottomSheetRating
import com.into.websoso.feature.library.filter.component.LibraryFilterBottomSheetReadStatus
import com.into.websoso.feature.library.filter.component.LibraryFilterBottomSheetSeriesStatus
import com.into.websoso.feature.library.filter.component.LibraryFilterSelectedChips
import com.into.websoso.feature.library.filter.component.LibraryFilterTabRow
import com.into.websoso.feature.library.model.LibraryFilterUiModel

private val SELECTED_CHIPS_ROW_HEIGHT = 36.5.dp

private val FILTER_CONTENT_REGION_HEIGHT = 333.5.dp

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun LibraryFilterBottomSheetScreen(
    filterUiState: LibraryFilterUiModel,
    sheetState: SheetState,
    initialTab: LibraryFilterTab = READ_STATUS,
    onDismissRequest: () -> Unit,
    onReadStatusClick: (ReadStatus) -> Unit,
    onGenreClick: (Genre) -> Unit,
    onSeriesStatusClick: (SeriesStatus) -> Unit,
    onAttractivePointClick: (AttractivePoint) -> Unit,
    onRatingRangeChange: (Float, Float) -> Unit,
    onRatinglessToggle: () -> Unit,
    onRatingRemove: () -> Unit,
    onKeywordClick: (String) -> Unit,
    onResetClick: () -> Unit,
    onFilterSearchClick: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }

    val activeTabs = buildSet {
        if (filterUiState.readStatuses.isSelected) add(READ_STATUS)
        if (filterUiState.genres.isSelected) add(GENRE)
        if (filterUiState.seriesStatuses.isSelected) add(SERIES_STATUS)
        if (filterUiState.ratingFilter.isSelected) add(RATING)
        if (filterUiState.attractivePoints.isSelected) add(ATTRACTIVE_POINT)
        if (filterUiState.keywords.isSelected) add(KEYWORD)
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(size = 16.dp),
        containerColor = White,
        dragHandle = null,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            LibraryFilterBottomSheetHeader(
                onDismissRequest = onDismissRequest,
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            LibraryFilterTabRow(
                selectedTab = selectedTab,
                activeTabs = activeTabs,
                onTabSelected = { selectedTab = it },
                modifier = Modifier.padding(start = 20.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(FILTER_CONTENT_REGION_HEIGHT),
            ) {
                if (activeTabs.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(SELECTED_CHIPS_ROW_HEIGHT)
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        LibraryFilterSelectedChips(
                            filterUiState = filterUiState,
                            onReadStatusClick = onReadStatusClick,
                            onGenreClick = onGenreClick,
                            onSeriesStatusClick = onSeriesStatusClick,
                            onAttractivePointClick = onAttractivePointClick,
                            onRatingRemove = onRatingRemove,
                            onKeywordClick = onKeywordClick,
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Gray50),
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                ) {
                    when (selectedTab) {
                        READ_STATUS -> LibraryFilterBottomSheetReadStatus(
                            readStatuses = filterUiState.readStatuses,
                            onReadStatusClick = onReadStatusClick,
                        )

                        GENRE -> LibraryFilterBottomSheetGenre(
                            genres = filterUiState.genres,
                            onGenreClick = onGenreClick,
                        )

                        SERIES_STATUS -> LibraryFilterBottomSheetSeriesStatus(
                            seriesStatuses = filterUiState.seriesStatuses,
                            onSeriesStatusClick = onSeriesStatusClick,
                        )

                        RATING -> LibraryFilterBottomSheetRating(
                            ratingFilter = filterUiState.ratingFilter,
                            onRangeChange = onRatingRangeChange,
                            onRatinglessToggle = onRatinglessToggle,
                        )

                        ATTRACTIVE_POINT -> LibraryFilterBottomSheetAttractivePoints(
                            attractivePoints = filterUiState.attractivePoints,
                            onAttractivePointClick = onAttractivePointClick,
                        )

                        KEYWORD -> LibraryFilterBottomSheetKeyword(
                            keywords = filterUiState.keywords,
                            onKeywordClick = onKeywordClick,
                        )
                    }
                }
            }

            LibraryFilterBottomSheetButtons(
                onResetClick = onResetClick,
                onFilterSearchClick = {
                    onDismissRequest()
                    onFilterSearchClick()
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun LibraryFilterBottomSheetPreview() {
    WebsosoTheme {
        LibraryFilterBottomSheetScreen(
            filterUiState = LibraryFilterUiModel(),
            sheetState = rememberStandardBottomSheetState(
                initialValue = SheetValue.Expanded,
                skipHiddenState = false,
            ),
            onDismissRequest = {},
            onReadStatusClick = {},
            onGenreClick = {},
            onSeriesStatusClick = {},
            onAttractivePointClick = {},
            onRatingRangeChange = { _, _ -> },
            onRatinglessToggle = {},
            onRatingRemove = {},
            onKeywordClick = { _ -> },
            onResetClick = {},
            onFilterSearchClick = {},
        )
    }
}
