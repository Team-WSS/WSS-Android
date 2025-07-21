package com.into.websoso.feature.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import com.into.websoso.domain.library.model.AttractivePoints
import com.into.websoso.domain.library.model.ReadStatus
import com.into.websoso.feature.library.component.LibraryEmptyView
import com.into.websoso.feature.library.component.LibraryFilterBottomSheet
import com.into.websoso.feature.library.component.LibraryFilterTopBar
import com.into.websoso.feature.library.component.LibraryGridList
import com.into.websoso.feature.library.component.LibraryList
import com.into.websoso.feature.library.component.LibraryTopBar
import com.into.websoso.feature.library.mapper.toUiModel
import com.into.websoso.feature.library.model.LibraryFilterType
import com.into.websoso.feature.library.model.LibraryListItemModel
import com.into.websoso.feature.library.model.LibraryUiState
import com.into.websoso.feature.library.model.SortTypeUiModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navigateToNormalExploreActivity: () -> Unit,
    navigateToNovelDetailActivity: (novelId: Long) -> Unit,
    libraryViewModel: LibraryViewModel = hiltViewModel(),
    libraryFilterViewModel: LibraryFilterViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val uiState by libraryViewModel.uiState.collectAsStateWithLifecycle()
    val filterUiState by libraryFilterViewModel.libraryFilterUiState.collectAsStateWithLifecycle()
    val pagingItems = libraryViewModel.novelPagingData
        .map { it.map { novel -> novel.toUiModel() } }
        .collectAsLazyPagingItems()
    var isShowBottomSheet by remember { mutableStateOf(false) }
    val listState = libraryViewModel.listState
    val gridState = libraryViewModel.gridState
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { false },
    )

    LibraryScreen(
        uiState = uiState,
        pagingItems = pagingItems,
        listState = listState,
        gridState = gridState,
        sheetState = bottomSheetState,
        onDismissRequest = {
            scope.launch {
                isShowBottomSheet = false
                bottomSheetState.hide()
            }
        },
        isShowBottomSheet = isShowBottomSheet,
        onFilterClick = {
            scope.launch {
                isShowBottomSheet = true
                bottomSheetState.show()
                libraryFilterViewModel.updateMyLibraryFilter(uiState.libraryFilterUiState)
            }
        },
        onSortClick = libraryViewModel::updateSortType,
        onToggleViewType = libraryViewModel::updateViewType,
        onItemClick = { navigateToNovelDetailActivity(it.novelId) },
        onSearchClick = { /* TODO */ },
        onExploreClick = navigateToNormalExploreActivity,
        onReadStatusClick = libraryFilterViewModel::updateReadStatus,
        onAttractivePointClick = libraryFilterViewModel::updateAttractivePoints,
        onRatingClick = libraryFilterViewModel::updateRating,
        onResetClick = libraryFilterViewModel::resetFilter,
        onInterestClick = libraryViewModel::updateInterestedNovels,
        onFilterSearchClick = {
            scope.launch {
                isShowBottomSheet = false
                bottomSheetState.hide()
                libraryFilterViewModel.searchFilteredNovels()
            }
        },
        attractivePoints = filterUiState.attractivePoints,
        readStatues = filterUiState.readStatuses,
        selectedRating = filterUiState.novelRating,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryScreen(
    uiState: LibraryUiState,
    pagingItems: LazyPagingItems<LibraryListItemModel>,
    listState: LazyListState,
    gridState: LazyGridState,
    sheetState: SheetState,
    isShowBottomSheet: Boolean,
    readStatues: Map<ReadStatus, Boolean>,
    attractivePoints: Map<AttractivePoints, Boolean>,
    selectedRating: Float,
    onRatingClick: (rating: Float) -> Unit,
    onReadStatusClick: (ReadStatus) -> Unit,
    onAttractivePointClick: (AttractivePoints) -> Unit,
    onDismissRequest: () -> Unit,
    onFilterClick: (LibraryFilterType) -> Unit,
    onSortClick: (SortTypeUiModel) -> Unit,
    onToggleViewType: () -> Unit,
    onItemClick: (LibraryListItemModel) -> Unit,
    onSearchClick: () -> Unit,
    onExploreClick: () -> Unit,
    onResetClick: () -> Unit,
    onFilterSearchClick: () -> Unit,
    onInterestClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.height(20.dp))

        LibraryTopBar(onSearchClick = onSearchClick)

        Spacer(modifier = Modifier.height(28.dp))

        LibraryFilterTopBar(
            libraryFilterUiState = uiState.libraryFilterUiState,
            totalCount = pagingItems.itemCount,
            selectedSortType = uiState.selectedSortType,
            isGrid = uiState.isGrid,
            isInterested = uiState.isInterested,
            onFilterClick = onFilterClick,
            onSortClick = onSortClick,
            onToggleViewType = onToggleViewType,
            onInterestClick = onInterestClick,
        )

        Spacer(modifier = Modifier.height(4.dp))

        when {
            pagingItems.itemCount == 0 && pagingItems.loadState.refresh !is LoadState.Loading -> {
                LibraryEmptyView(onExploreClick = onExploreClick)
            }

            uiState.isGrid -> {
                LibraryGridList(
                    novels = pagingItems,
                    gridState = gridState,
                    onItemClick = onItemClick,
                )
            }

            else -> {
                LibraryList(
                    novels = pagingItems,
                    listState = listState,
                    onItemClick = onItemClick,
                )
            }
        }
    }

    if (isShowBottomSheet) {
        LibraryFilterBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            onReadStatusClick = onReadStatusClick,
            onAttractivePointClick = onAttractivePointClick,
            onRatingClick = onRatingClick,
            attractivePoints = attractivePoints,
            readStatues = readStatues,
            selectedRating = selectedRating,
            onResetClick = onResetClick,
            onFilterSearchClick = onFilterSearchClick,
        )
    }
}
