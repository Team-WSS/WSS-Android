package com.into.websoso.feature.library

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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import com.into.websoso.core.common.extensions.collectAsEventWithLifecycle
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.data.library.model.NovelEntity
import com.into.websoso.feature.filter.LibraryFilterBottomSheetScreen
import com.into.websoso.feature.filter.LibraryFilterViewModel
import com.into.websoso.feature.library.component.LibraryEmptyView
import com.into.websoso.feature.library.component.LibraryFilterTopBar
import com.into.websoso.feature.library.component.LibraryGridList
import com.into.websoso.feature.library.component.LibraryList
import com.into.websoso.feature.library.component.LibraryTopBar
import com.into.websoso.feature.library.mapper.toUiModel
import com.into.websoso.feature.library.model.LibraryFilterType
import com.into.websoso.feature.library.model.LibraryListItemModel
import com.into.websoso.feature.library.model.LibraryUiState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private const val SCROLL_POSITION_TOP = 0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    navigateToNormalExploreActivity: () -> Unit,
    navigateToNovelDetailActivity: (novelId: Long) -> Unit,
    libraryViewModel: LibraryViewModel,
    libraryFilterViewModel: LibraryFilterViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val uiState by libraryViewModel.uiState.collectAsStateWithLifecycle()
    val pagingItems = libraryViewModel.novelPagingData
        .map { it.map(NovelEntity::toUiModel) }
        .collectAsLazyPagingItems()
    val latestEffect by rememberUpdatedState(libraryViewModel.scrollToTopEvent)
    var isShowBottomSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { false },
    )

    latestEffect.collectAsEventWithLifecycle {
        if (uiState.isGrid) {
            gridState.scrollToItem(SCROLL_POSITION_TOP)
        } else {
            listState.scrollToItem(SCROLL_POSITION_TOP)
        }
    }

    LibraryScreen(
        libraryFilterViewModel = libraryFilterViewModel,
        pagingItems = pagingItems,
        uiState = uiState,
        listState = listState,
        gridState = gridState,
        sheetState = bottomSheetState,
        isShowBottomSheet = isShowBottomSheet,
        onDismissRequest = {
            scope.launch {
                isShowBottomSheet = false
                bottomSheetState.hide()
            }
        },
        onFilterClick = {
            scope
                .launch {
                    isShowBottomSheet = true
                    bottomSheetState.show()
                }.invokeOnCompletion {
                    libraryFilterViewModel.updateMyLibraryFilter(uiState.libraryFilterUiState)
                }
        },
        onSortClick = libraryViewModel::updateSortType,
        onToggleViewType = libraryViewModel::updateViewType,
        onItemClick = { navigateToNovelDetailActivity(it.novelId) },
        onSearchClick = { /* TODO */ },
        onExploreClick = navigateToNormalExploreActivity,
        onInterestClick = libraryViewModel::updateInterestedNovels,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LibraryScreen(
    libraryFilterViewModel: LibraryFilterViewModel,
    pagingItems: LazyPagingItems<LibraryListItemModel>,
    uiState: LibraryUiState,
    listState: LazyListState,
    gridState: LazyGridState,
    sheetState: SheetState,
    isShowBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    onFilterClick: (LibraryFilterType) -> Unit,
    onSortClick: () -> Unit,
    onToggleViewType: () -> Unit,
    onItemClick: (LibraryListItemModel) -> Unit,
    onSearchClick: () -> Unit,
    onExploreClick: () -> Unit,
    onInterestClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
    ) {
        LibraryTopBar(onSearchClick = onSearchClick)

        LibraryFilterTopBar(
            libraryFilterUiState = uiState.libraryFilterUiState,
            totalCount = pagingItems.itemCount,
            isGrid = uiState.isGrid,
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
        LibraryFilterBottomSheetScreen(
            sheetState = sheetState,
            onDismissRequest = onDismissRequest,
            viewModel = libraryFilterViewModel,
        )
    }
}
