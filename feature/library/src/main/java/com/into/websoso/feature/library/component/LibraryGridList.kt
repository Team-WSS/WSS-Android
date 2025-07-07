package com.into.websoso.feature.library.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.into.websoso.feature.library.model.LibraryListItemModel

@Composable
internal fun LibraryGridList(
    novels: LazyPagingItems<LibraryListItemModel>,
    gridState: LazyGridState,
    modifier: Modifier = Modifier,
    onItemClick: (LibraryListItemModel) -> Unit = {},
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        state = gridState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        items(novels.itemCount) { index ->
            novels[index]?.let { novel ->
                NovelGridListItem(
                    item = novel,
                    onItemClick = { onItemClick(novel) },
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            if (novels.loadState.append is LoadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
    }
}
