package com.into.websoso.feature.library

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.resource.R.drawable.ic_common_search
import com.into.websoso.feature.library.R.string.library_title
import com.into.websoso.feature.library.component.LibraryEmptyView
import com.into.websoso.feature.library.component.LibraryFilterTopBar
import com.into.websoso.feature.library.component.LibraryGridList
import com.into.websoso.feature.library.component.LibraryList
import com.into.websoso.feature.library.mapper.toUiModel
import com.into.websoso.feature.library.model.LibraryFilterType
import com.into.websoso.feature.library.model.LibraryListItemModel
import com.into.websoso.feature.library.model.LibraryUiState
import com.into.websoso.feature.library.model.SortTypeUiModel
import kotlinx.coroutines.flow.map

@Composable
fun LibraryRoute(libraryViewModel: LibraryViewModel = hiltViewModel()) {
    val uiState by libraryViewModel.uiState.collectAsStateWithLifecycle()

    val pagingItems = libraryViewModel.novelPagingData
        .map { it.map { novel -> novel.toUiModel() } }
        .collectAsLazyPagingItems()

    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    LibraryScreen(
        uiState = uiState,
        pagingItems = pagingItems,
        listState = listState,
        gridState = gridState,
        onFilterClick = { /* TODO */  },
        onSortClick = { libraryViewModel.updateSortType(it) },
        onToggleViewType = { libraryViewModel.updateViewType() },
        onItemClick = { /* TODO */  },
        onSearchClick = { /* TODO */ },
        onExploreClick = { /* TODO */ },
    )
}

@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    pagingItems: LazyPagingItems<LibraryListItemModel>,
    listState: LazyListState,
    gridState: LazyGridState,
    onFilterClick: (LibraryFilterType) -> Unit,
    onSortClick: (SortTypeUiModel) -> Unit,
    onToggleViewType: () -> Unit,
    onItemClick: (LibraryListItemModel) -> Unit,
    onSearchClick: () -> Unit,
    onExploreClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LibraryTopBar(onSearchClick = onSearchClick)

        LibraryFilterTopBar(
            libraryFilterUiState = uiState.filterUiState,
            totalCount = pagingItems.itemCount,
            selectedSortType = uiState.selectedSortType,
            isGrid = uiState.isGrid,
            onFilterClick = onFilterClick,
            onSortClick = onSortClick,
            onToggleViewType = onToggleViewType,
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
}

@Composable
fun LibraryTopBar(onSearchClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(id = library_title),
            style = WebsosoTheme.typography.headline1,
            color = Black,
        )

        IconButton(onClick = onSearchClick) {
            Image(
                imageVector = ImageVector.vectorResource(id = ic_common_search),
                contentDescription = "검색",
                modifier = Modifier.size(24.dp),
            )
        }
    }
}
