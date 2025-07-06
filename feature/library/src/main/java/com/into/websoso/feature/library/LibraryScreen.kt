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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
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
import kotlinx.coroutines.flow.map

@Composable
fun LibraryScreen(libraryViewModel: LibraryViewModel = hiltViewModel()) {
    val uiState by libraryViewModel.uiState.collectAsState()

    val isGrid = uiState.isGrid
    val filterState = uiState.filterUiState
    val sortType = uiState.selectedSortType

    val pagingItems = libraryViewModel.novelPagingData
        .map { pagingData -> pagingData.map { it.toUiModel() } }
        .collectAsLazyPagingItems()

    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    Column(modifier = Modifier.fillMaxSize()) {
        LibraryTopBar(
            onSearchClick = {
                // TODO: 검색 화면으로 이동
            },
        )

        LibraryFilterTopBar(
            libraryFilterUiState = filterState,
            totalCount = pagingItems.itemCount,
            selectedSortType = sortType,
            isGrid = isGrid,
            onFilterClick = { type -> libraryViewModel.onFilterClick(type) },
            onSortClick = { libraryViewModel.onSortClick(sortType) },
            onToggleViewType = { libraryViewModel.updateViewType() },
        )

        Spacer(modifier = Modifier.height(4.dp))

        when {
            pagingItems.itemCount == 0 && pagingItems.loadState.refresh !is LoadState.Loading -> {
                LibraryEmptyView(
                    onExploreClick = { libraryViewModel.navigateToExplore() },
                )
            }

            isGrid -> {
                LibraryGridList(
                    novels = pagingItems,
                    gridState = gridState,
                    onItemClick = { libraryViewModel.onItemClick(it) },
                )
            }

            else -> {
                LibraryList(
                    novels = pagingItems,
                    listState = listState,
                    onItemClick = { libraryViewModel.onItemClick(it) },
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
