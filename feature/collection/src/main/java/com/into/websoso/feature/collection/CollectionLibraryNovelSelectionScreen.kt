package com.into.websoso.feature.collection

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.string.collection_create_add
import com.into.websoso.core.resource.R.string.library
import com.into.websoso.feature.collection.component.CollectionAppBar
import com.into.websoso.feature.collection.component.CollectionLibraryNovelItem
import com.into.websoso.feature.collection.component.CollectionNetworkError
import com.into.websoso.feature.collection.model.CollectionLibraryNovelUiModel
import com.into.websoso.feature.collection.model.CollectionSelectedNovel
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun CollectionLibraryNovelSelectionRoute(
    initialSelectedNovels: List<CollectionSelectedNovel>,
    onAddClick: (List<CollectionSelectedNovel>) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: CollectionLibraryNovelSelectionViewModel = hiltViewModel(),
) {
    val novels = viewModel.novels.collectAsLazyPagingItems()
    val selectedNovels by viewModel.selectedNovels.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(initialSelectedNovels) {
        viewModel.setSelectedNovels(initialSelectedNovels)
    }

    CollectionLibraryNovelSelectionScreen(
        novels = novels,
        selectedNovelIds = selectedNovels.mapTo(mutableSetOf()) { it.novelId },
        onNovelSelectionChange = { novel ->
            if (selectedNovels.size >= 100 && selectedNovels.none { it.novelId == novel.novelId }) {
                Toast.makeText(context, com.into.websoso.core.resource.R.string.collection_selection_limit, Toast.LENGTH_SHORT).show()
            } else {
                viewModel.toggleNovelSelection(novel)
            }
        },
        onAddClick = { onAddClick(selectedNovels) },
        onNavigateBack = onNavigateBack,
    )
}

@Composable
internal fun CollectionLibraryNovelSelectionScreen(
    novels: LazyPagingItems<CollectionLibraryNovelUiModel>,
    selectedNovelIds: Set<Long>,
    onNovelSelectionChange: (CollectionLibraryNovelUiModel) -> Unit,
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
            title = stringResource(library),
            actionLabel = stringResource(collection_create_add),
            onNavigateBack = onNavigateBack,
            onActionClick = onAddClick,
            isActionEnabled = selectedNovelIds.isNotEmpty(),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            when {
                novels.itemCount == 0 && novels.loadState.refresh is LoadState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                novels.itemCount == 0 && novels.loadState.refresh is LoadState.Error -> {
                    CollectionNetworkError(
                        onRetryClick = novels::retry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                else -> {
                    if (novels.itemCount == 0) {
                        CollectionEmpty(
                            stringResource(com.into.websoso.core.resource.R.string.collection_library_empty),
                            Modifier.align(Alignment.Center),
                        )
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.fillMaxSize(),
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
                                    onSelectionChange = { onNovelSelectionChange(novel) },
                                )
                            }
                        }
                        if (novels.loadState.append is LoadState.Loading) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                                }
                            }
                        }
                        if (novels.loadState.append is LoadState.Error) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                TextButton(onClick = novels::retry, modifier = Modifier.fillMaxWidth()) {
                                    Text(stringResource(com.into.websoso.core.resource.R.string.collection_retry))
                                }
                            }
                        }
                    }
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
