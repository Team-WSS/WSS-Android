package com.into.websoso.feature.library.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.into.websoso.feature.library.model.LibraryListItemModel

@Composable
internal fun LibraryList(
    novels: LazyPagingItems<LibraryListItemModel>,
    listState: LazyListState,
    onItemClick: (LibraryListItemModel) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(start = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        items(novels.itemCount) { index ->
            novels[index]?.let { novel ->
                LibraryListItem(
                    item = novel,
                    onClick = { onItemClick(novel) },
                )
            }
        }
    }
}
