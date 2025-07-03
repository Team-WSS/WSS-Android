package com.into.websoso.feature.library.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.into.websoso.feature.library.model.LibraryListItemModel

@Composable
fun NovelGridList(
    novels: List<LibraryListItemModel>,
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
        items(novels, key = { it.title }) { novel ->
            NovelGridListItem(
                item = novel,
                onItemClick = { onItemClick(novel) },
            )
        }
    }
}
