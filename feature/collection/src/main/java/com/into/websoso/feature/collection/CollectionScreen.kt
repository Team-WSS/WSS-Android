package com.into.websoso.feature.collection

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.into.websoso.core.designsystem.theme.Gray200
import com.into.websoso.core.designsystem.theme.WebsosoTheme
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R
import com.into.websoso.feature.collection.component.CollectionAppBar
import com.into.websoso.feature.collection.component.CollectionCard
import com.into.websoso.feature.collection.component.CollectionCreateButton
import com.into.websoso.feature.collection.component.CollectionNetworkError
import com.into.websoso.feature.collection.component.CollectionTabRow
import com.into.websoso.feature.collection.model.CollectionTab

@Composable
fun CollectionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCreate: () -> Unit,
    onCollectionClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    userId: Long? = null,
    showMyCollections: Int = 0,
) {
    val viewModel: CollectionViewModel = hiltViewModel()
    var selectedTab by rememberSaveable { mutableStateOf(CollectionTab.MY_COLLECTION) }
    LaunchedEffect(showMyCollections) {
        if (showMyCollections > 0) selectedTab = CollectionTab.MY_COLLECTION
    }
    val myCollections = viewModel.collections(userId).collectAsLazyPagingItems()
    val likedCollections = viewModel.likedCollections.collectAsLazyPagingItems()
    val collections =
        if (selectedTab == CollectionTab.MY_COLLECTION) myCollections else likedCollections
    val myListState = rememberLazyListState()
    val likedListState = rememberLazyListState()

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        myCollections.refresh()
        likedCollections.refresh()
    }

    Column(modifier.fillMaxSize().background(White).statusBarsPadding()) {
        CollectionAppBar(
            title = stringResource(R.string.collection_title),
            onNavigateBack = onNavigateBack,
        )
        if (userId == null) {
            CollectionTabRow(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        }
        PullToRefreshBox(
            isRefreshing = collections.loadState.refresh is LoadState.Loading && collections.itemCount > 0,
            onRefresh = collections::refresh,
            modifier = Modifier.weight(1f),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = if (selectedTab == CollectionTab.MY_COLLECTION) myListState else likedListState,
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (userId == null && selectedTab == CollectionTab.MY_COLLECTION) {
                    item { CollectionCreateButton(onClick = onNavigateToCreate) }
                }
                items(count = collections.itemCount, key = collections.itemKey { it.id }) { index ->
                    collections[index]?.let { collection ->
                        CollectionCard(collection, onClick = { onCollectionClick(collection.id) })
                    }
                }
                if (collections.loadState.append is LoadState.Loading) {
                    item { CircularProgressIndicator() }
                }
                if (collections.itemCount > 0 &&
                    (collections.loadState.append is LoadState.Error || collections.loadState.refresh is LoadState.Error)
                ) {
                    item { TextButton(onClick = collections::retry) { Text(stringResource(R.string.collection_retry)) } }
                }
            }
            if (collections.itemCount == 0) {
                when (collections.loadState.refresh) {
                    is LoadState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                    is LoadState.Error -> CollectionNetworkError(
                        collections::retry,
                        Modifier.align(Alignment.Center),
                    )

                    is LoadState.NotLoading -> if (selectedTab == CollectionTab.LIKED_COLLECTION || userId != null) {
                        CollectionEmpty(
                            message = stringResource(if (userId == null) R.string.collection_liked_empty else R.string.collection_empty),
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun CollectionEmpty(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Image(painterResource(R.drawable.ic_collection_empty), null, Modifier.size(39.dp, 48.dp))
        Text(message, color = Gray200, style = WebsosoTheme.typography.body1)
    }
}
