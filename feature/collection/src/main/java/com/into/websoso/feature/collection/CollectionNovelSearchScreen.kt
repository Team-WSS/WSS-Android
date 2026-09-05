package com.into.websoso.feature.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.Gray50
import com.into.websoso.core.designsystem.theme.Primary100
import com.into.websoso.core.designsystem.theme.Primary50
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R.drawable.ic_explore_not_exist_result
import com.into.websoso.core.resource.R.string.collection_create_complete
import com.into.websoso.core.resource.R.string.collection_create_novel_list
import com.into.websoso.core.resource.R.string.normal_explore_add_novel_inquire
import com.into.websoso.core.resource.R.string.normal_explore_not_exist_result
import com.into.websoso.core.resource.R.string.novel_inquire_link
import com.into.websoso.data.novel.model.NovelSearchEntity
import com.into.websoso.feature.collection.component.CollectionAppBar
import com.into.websoso.feature.collection.component.CollectionNetworkError
import com.into.websoso.feature.collection.component.CollectionNovelSearchField
import com.into.websoso.feature.collection.component.CollectionNovelSearchItem
import com.into.websoso.feature.collection.component.CollectionNovelSelectionInfo
import com.into.websoso.feature.collection.component.CollectionSelectedNovelItem
import com.into.websoso.feature.collection.model.CollectionSelectedNovel
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun CollectionNovelSearchRoute(
    onNavigateBack: () -> Unit,
    onNavigateToLibraryNovelSelection: () -> Unit,
    viewModel: CollectionNovelSearchViewModel,
) {
    val selectedNovels by viewModel.selectedNovels.collectAsStateWithLifecycle()
    val submittedQuery by viewModel.submittedQuery.collectAsStateWithLifecycle()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()

    CollectionNovelSearchScreen(
        searchResults = searchResults,
        submittedQuery = submittedQuery,
        selectedNovels = selectedNovels,
        onSearch = { query ->
            if (query.trim() == submittedQuery && query.isNotBlank()) {
                searchResults.refresh()
            } else {
                viewModel.search(query)
            }
        },
        onAddNovel = viewModel::addNovel,
        onDeleteNovel = viewModel::removeNovel,
        onNavigateBack = onNavigateBack,
        onNavigateToLibraryNovelSelection = onNavigateToLibraryNovelSelection,
    )
}

@Composable
internal fun CollectionNovelSearchScreen(
    searchResults: LazyPagingItems<NovelSearchEntity>,
    submittedQuery: String,
    selectedNovels: List<CollectionSelectedNovel>,
    onSearch: (String) -> Unit,
    onAddNovel: (NovelSearchEntity) -> Unit,
    onDeleteNovel: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLibraryNovelSelection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    val searchFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val selectedNovelIds = selectedNovels.mapTo(mutableSetOf()) { it.novelId }
    val isInitialLoading =
        submittedQuery.isNotBlank() &&
            searchResults.itemCount == 0 &&
            searchResults.loadState.refresh is LoadState.Loading

    LaunchedEffect(Unit) {
        searchFocusRequester.requestFocus()
        keyboardController?.show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .statusBarsPadding(),
    ) {
        CollectionAppBar(
            title = stringResource(collection_create_novel_list),
            actionLabel = stringResource(collection_create_complete),
            onNavigateBack = onNavigateBack,
            onActionClick = onNavigateBack,
            isActionEnabled = selectedNovels.isNotEmpty(),
        )
        CollectionNovelSearchField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            onClearClick = {
                searchQuery = TextFieldValue()
                onSearch("")
            },
            onSearchClick = {
                keyboardController?.hide()
                onSearch(searchQuery.text)
            },
            focusRequester = searchFocusRequester,
            modifier = Modifier.padding(
                start = 20.dp,
                top = 10.dp,
                end = 20.dp,
            ),
        )
        CollectionNovelSelectionInfo(
            addedNovelCount = selectedNovels.size,
            onAddFromLibraryClick = onNavigateToLibraryNovelSelection,
            modifier = Modifier.padding(top = 16.dp),
        )
        PullToRefreshBox(
            isRefreshing =
                searchResults.loadState.refresh is LoadState.Loading &&
                    searchResults.itemCount > 0,
            onRefresh = searchResults::refresh,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            when {
                searchResults.loadState.refresh is LoadState.Error -> {
                    CollectionNetworkError(
                        onRetryClick = searchResults::retry,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                isInitialLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 20.dp,
                            vertical = 10.dp,
                        ),
                    ) {
                        items(
                            count = selectedNovels.size,
                            key = { index -> "selected-${selectedNovels[index].novelId}" },
                        ) { index ->
                            val novel = selectedNovels[index]
                            CollectionSelectedNovelItem(
                                novel = novel,
                                onDeleteClick = { onDeleteNovel(novel.novelId) },
                            )
                        }

                        if (selectedNovels.isNotEmpty() && submittedQuery.isNotBlank()) {
                            item {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    color = Gray50,
                                )
                            }
                        }

                        if (submittedQuery.isNotBlank() && searchResults.itemCount == 0) {
                            item { CollectionNovelSearchEmpty() }
                        }

                        items(searchResults.itemCount) { index ->
                            searchResults[index]
                                ?.takeUnless { it.novelId in selectedNovelIds }
                                ?.let { novel ->
                                    CollectionNovelSearchItem(
                                        novel = novel,
                                        onAddClick = { onAddNovel(novel) },
                                    )
                                }
                        }

                        if (searchResults.loadState.append is LoadState.Loading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CollectionNovelSearchEmpty() {
    val uriHandler = LocalUriHandler.current
    val inquireUrl = stringResource(novel_inquire_link)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(ic_explore_not_exist_result),
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(normal_explore_not_exist_result),
            color = Gray200,
            style = WebsosoTheme.typography.body1,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(36.dp))
        Button(
            onClick = { uriHandler.openUri(inquireUrl) },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary50),
            contentPadding = PaddingValues(horizontal = 40.dp, vertical = 20.dp),
            elevation = null,
        ) {
            Text(
                text = stringResource(normal_explore_add_novel_inquire),
                color = Primary100,
                style = WebsosoTheme.typography.title2,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CollectionNovelSearchScreenPreview() {
    WebsosoTheme {
        CollectionNovelSearchScreen(
            searchResults = flowOf(PagingData.empty<NovelSearchEntity>()).collectAsLazyPagingItems(),
            submittedQuery = "",
            selectedNovels = emptyList(),
            onSearch = {},
            onAddNovel = {},
            onDeleteNovel = {},
            onNavigateBack = {},
            onNavigateToLibraryNovelSelection = {},
        )
    }
}
