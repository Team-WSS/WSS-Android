package com.into.websoso.feature.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.into.websoso.core.designsystem.theme.Black
import com.into.websoso.core.designsystem.theme.Gray300
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.img_load_fail
import com.into.websoso.core.resource.R.string.load_fail_description
import com.into.websoso.core.resource.R.string.load_fail_reload
import com.into.websoso.core.resource.R.string.load_fail_title
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            when {
                novels.itemCount == 0 && novels.loadState.refresh is LoadState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                (novels.itemCount == 0 && novels.loadState.refresh is LoadState.Error) ||
                    novels.loadState.append is LoadState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(img_load_fail),
                            contentDescription = null,
                            modifier = Modifier.size(width = 166.dp, height = 160.dp),
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Text(
                            text = stringResource(load_fail_title),
                            color = Black,
                            style = WebsosoTheme.typography.title1,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = stringResource(load_fail_description),
                            color = Gray300,
                            style = WebsosoTheme.typography.body2,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Button(
                            onClick = novels::retry,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary100),
                            contentPadding = PaddingValues(horizontal = 38.dp, vertical = 14.dp),
                            elevation = null,
                        ) {
                            Text(
                                text = stringResource(load_fail_reload),
                                color = White,
                                style = WebsosoTheme.typography.label1,
                            )
                        }
                    }
                }

                else -> {
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
                                    onSelectionChange = { onNovelSelectionChange(novel.novelId) },
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
