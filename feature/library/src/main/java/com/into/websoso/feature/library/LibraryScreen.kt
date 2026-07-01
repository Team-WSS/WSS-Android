package com.into.websoso.feature.library

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState.Loading
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.into.websoso.core.common.extensions.collectAsEventWithLifecycle
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.domain.library.model.AttractivePoint
import com.into.websoso.domain.library.model.Genre
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.domain.library.model.SeriesStatus
import com.into.websoso.domain.library.model.SortCriteria
import com.into.websoso.feature.library.component.LibraryEmptyView
import com.into.websoso.feature.library.component.LibraryFilterEmptyView
import com.into.websoso.feature.library.component.LibraryFilterTopBar
import com.into.websoso.feature.library.component.LibraryGridList
import com.into.websoso.feature.library.component.LibraryList
import com.into.websoso.feature.library.component.LibrarySortBottomSheet
import com.into.websoso.feature.library.component.LibraryTopBar
import com.into.websoso.feature.library.filter.LibraryFilterBottomSheetScreen
import com.into.websoso.feature.library.filter.LibraryFilterTab
import com.into.websoso.feature.library.model.LibraryFilterUiModel
import com.into.websoso.feature.library.model.LibraryUiState
import com.into.websoso.feature.library.model.NovelUiModel
import kotlinx.coroutines.launch

private const val SCROLL_POSITION_TOP = 0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navigateToNormalExploreActivity: () -> Unit,
    navigateToNovelDetailActivity: (novelId: Long) -> Unit,
    libraryViewModel: LibraryViewModel,
) {
    val scope = rememberCoroutineScope()
    val uiState by libraryViewModel.uiState.collectAsStateWithLifecycle()
    val filterUiState by libraryViewModel.tempFilterUiState.collectAsStateWithLifecycle()
    val novels = libraryViewModel.novels.collectAsLazyPagingItems()
    val latestEffect by rememberUpdatedState(libraryViewModel.scrollToTopEvent)
    val isNovelsRefreshing = novels.loadState.refresh is Loading
    var isShowBottomSheet by remember { mutableStateOf(false) }
    var isShowSortBottomSheet by remember { mutableStateOf(false) }
    var filterInitialTab by remember { mutableStateOf(LibraryFilterTab.READ_STATUS) }
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { true },
    )
    val sortSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    latestEffect.collectAsEventWithLifecycle {
        if (uiState.isGrid) {
            gridState.scrollToItem(SCROLL_POSITION_TOP)
        } else {
            listState.scrollToItem(SCROLL_POSITION_TOP)
        }
    }

    val context = LocalContext.current
    val latestKeywordLimitEvent by rememberUpdatedState(libraryViewModel.keywordLimitEvent)
    latestKeywordLimitEvent.collectAsEventWithLifecycle {
        Toast.makeText(context, "최대 20개까지 선택 가능해요", Toast.LENGTH_SHORT).show()
    }

    LibraryScreen(
        novels = novels,
        uiState = uiState,
        filterUiState = filterUiState,
        listState = listState,
        gridState = gridState,
        sheetState = bottomSheetState,
        sortSheetState = sortSheetState,
        isShowBottomSheet = isShowBottomSheet,
        isShowSortBottomSheet = isShowSortBottomSheet,
        isNovelsRefreshing = isNovelsRefreshing,
        initialFilterTab = filterInitialTab,
        onDismissRequest = {
            scope.launch {
                isShowBottomSheet = false
                bottomSheetState.hide()
            }
        },
        onSortDismiss = {
            scope.launch {
                isShowSortBottomSheet = false
                sortSheetState.hide()
            }
        },
        onSortSelected = libraryViewModel::updateSortCriteria,
        onFilterClick = { tab ->
            filterInitialTab = tab
            scope
                .launch {
                    isShowBottomSheet = true
                    bottomSheetState.show()
                }.invokeOnCompletion {
                    libraryViewModel.updateMyLibraryFilter()
                }
        },
        onSortClick = {
            scope.launch {
                isShowSortBottomSheet = true
                sortSheetState.show()
            }
        },
        onToggleViewType = libraryViewModel::updateViewType,
        onItemClick = { navigateToNovelDetailActivity(it.novelId) },
        onSearchClick = navigateToNormalExploreActivity,
        onExploreClick = navigateToNormalExploreActivity,
        onInterestClick = libraryViewModel::updateInterestedNovels,
        onAttractivePointClick = libraryViewModel::updateAttractivePoints,
        onReadStatusClick = libraryViewModel::updateReadStatus,
        onGenreClick = libraryViewModel::updateGenre,
        onSeriesStatusClick = libraryViewModel::updateSeriesStatus,
        onRatingRangeChange = libraryViewModel::updateRatingRange,
        onRatinglessToggle = libraryViewModel::updateRatingless,
        onRatingRemove = libraryViewModel::clearRating,
        onKeywordClick = libraryViewModel::updateKeyword,
        onResetClick = libraryViewModel::resetFilter,
        onFilterSearchClick = libraryViewModel::searchFilteredNovels,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryScreen(
    novels: LazyPagingItems<NovelUiModel>,
    uiState: LibraryUiState,
    filterUiState: LibraryFilterUiModel,
    listState: LazyListState,
    gridState: LazyGridState,
    sheetState: SheetState,
    sortSheetState: SheetState,
    isNovelsRefreshing: Boolean,
    isShowBottomSheet: Boolean,
    isShowSortBottomSheet: Boolean,
    initialFilterTab: LibraryFilterTab,
    onDismissRequest: () -> Unit,
    onSortDismiss: () -> Unit,
    onSortSelected: (SortCriteria) -> Unit,
    onFilterClick: (LibraryFilterTab) -> Unit,
    onSortClick: () -> Unit,
    onToggleViewType: () -> Unit,
    onItemClick: (NovelUiModel) -> Unit,
    onSearchClick: () -> Unit,
    onExploreClick: () -> Unit,
    onInterestClick: () -> Unit,
    onAttractivePointClick: (AttractivePoint) -> Unit,
    onReadStatusClick: (ReadStatus) -> Unit,
    onGenreClick: (Genre) -> Unit,
    onSeriesStatusClick: (SeriesStatus) -> Unit,
    onRatingRangeChange: (Float, Float) -> Unit,
    onRatinglessToggle: () -> Unit,
    onRatingRemove: () -> Unit,
    onKeywordClick: (String) -> Unit,
    onResetClick: () -> Unit,
    onFilterSearchClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        LibraryTopBar(onSearchClick = onSearchClick)

        Spacer(modifier = Modifier.height(16.dp))

        LibraryFilterTopBar(
            libraryFilterUiModel = uiState.libraryFilterUiModel,
            totalCount = uiState.novelTotalCount,
            isGrid = uiState.isGrid,
            onFilterClick = onFilterClick,
            onSortClick = onSortClick,
            onToggleViewType = onToggleViewType,
            onInterestClick = onInterestClick,
        )

        Spacer(modifier = Modifier.height(12.dp))

        PullToRefreshBox(
            isRefreshing = isNovelsRefreshing,
            onRefresh = novels::refresh,
        ) {
            when {
                novels.itemCount == 0 &&
                    novels.loadState.refresh !is Loading -> {
                    if (uiState.libraryFilterUiModel.isFilterApplied) {
                        LibraryFilterEmptyView()
                    } else {
                        LibraryEmptyView(onExploreClick = onExploreClick)
                    }
                }

                uiState.isGrid -> {
                    LibraryGridList(
                        novels = novels,
                        gridState = gridState,
                        onItemClick = onItemClick,
                    )
                }

                else -> {
                    LibraryList(
                        novels = novels,
                        listState = listState,
                        onItemClick = onItemClick,
                    )
                }
            }
        }
    }

    if (isShowBottomSheet) {
        LibraryFilterBottomSheetScreen(
            filterUiState = filterUiState,
            sheetState = sheetState,
            initialTab = initialFilterTab,
            onDismissRequest = onDismissRequest,
            onReadStatusClick = onReadStatusClick,
            onGenreClick = onGenreClick,
            onSeriesStatusClick = onSeriesStatusClick,
            onAttractivePointClick = onAttractivePointClick,
            onRatingRangeChange = onRatingRangeChange,
            onRatinglessToggle = onRatinglessToggle,
            onRatingRemove = onRatingRemove,
            onKeywordClick = onKeywordClick,
            onResetClick = onResetClick,
            onFilterSearchClick = onFilterSearchClick,
        )
    }

    if (isShowSortBottomSheet) {
        LibrarySortBottomSheet(
            selectedSortCriteria = uiState.libraryFilterUiModel.sortCriteria,
            sheetState = sortSheetState,
            onDismissRequest = onSortDismiss,
            onSortSelected = onSortSelected,
        )
    }
}
