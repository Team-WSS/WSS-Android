package com.into.websoso.feature.library.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.into.websoso.feature.library.model.LibraryListItemModel

@Composable
fun LibraryList(
    novels: List<LibraryListItemModel>,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onItemClick: (LibraryListItemModel) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        items(novels, key = { it.title }) { novel ->
            LibraryListItem(
                item = novel,
                onClick = { onItemClick(novel) },
            )
        }
    }
}
