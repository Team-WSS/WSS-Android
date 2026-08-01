package com.into.websoso.feature.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.feature.collection.component.CollectionAppBar
import com.into.websoso.feature.collection.component.CollectionLibraryNovelItem
import com.into.websoso.feature.collection.model.CollectionLibraryNovelUiModel
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun CollectionLibraryNovelSelectionRoute(
    initialSelectedNovelIds: Set<Long>,
    onAddClick: (Set<Long>) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CollectionLibraryNovelSelectionViewModel = hiltViewModel(),
) {
    val novels = viewModel.novels.collectAsLazyPagingItems()
    val selectedNovelIds by viewModel.selectedNovelIds.collectAsStateWithLifecycle()

    LaunchedEffect(initialSelectedNovelIds) {
        viewModel.setSelectedNovelIds(initialSelectedNovelIds)
    }

    CollectionLibraryNovelSelectionScreen(
        novels = novels,
        selectedNovelIds = selectedNovelIds,
        onNovelSelectionChange = viewModel::toggleNovelSelection,
        onAddClick = { onAddClick(selectedNovelIds) },
        onNavigateBack = onNavigateBack,
    )
}

@Composable
internal fun CollectionLibraryNovelSelectionScreen(
    novels: LazyPagingItems<CollectionLibraryNovelUiModel>,
    selectedNovelIds: Set<Long>,
    onNovelSelectionChange: (Long) -> Unit,
    onAddClick: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding(),
    ) {
        CollectionAppBar(
            title = "서재",
            actionLabel = "추가",
            onNavigateBack = onNavigateBack,
            onActionClick = onAddClick,
            isActionEnabled = selectedNovelIds.isNotEmpty(),
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = 11.dp,
                end = 20.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            items(novels.itemCount) { index ->
                novels[index]?.let { novel ->
                    CollectionLibraryNovelItem(
                        novel = novel,
                        isSelected = novel.novelId in selectedNovelIds,
                        onSelectionChange = { onNovelSelectionChange(novel.novelId) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionLibraryNovelSelectionScreenPreview() {
    WebsosoTheme {
        CollectionLibraryNovelSelectionScreen(
            novels = flowOf(PagingData.empty<CollectionLibraryNovelUiModel>())
                .collectAsLazyPagingItems(),
            selectedNovelIds = emptySet(),
            onNovelSelectionChange = {},
            onAddClick = {},
            onNavigateBack = {},
        )
    }
}
