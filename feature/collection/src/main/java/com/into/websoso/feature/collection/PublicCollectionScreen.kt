package com.into.websoso.feature.collection

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.into.websoso.core.designsystem.theme.White
import com.into.websoso.core.resource.R
import com.into.websoso.feature.collection.component.CollectionAppBar
import com.into.websoso.feature.collection.component.CollectionNetworkError
import com.into.websoso.feature.collection.component.PublicCollectionCard

@Composable
internal fun PublicCollectionScreen(
    onNavigateBack: () -> Unit,
    onCollectionClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: PublicCollectionViewModel = hiltViewModel()
    val collections = viewModel.collections.collectAsLazyPagingItems()
    val view = LocalView.current

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { collections.refresh() }

    Column(modifier.fillMaxSize().background(White).statusBarsPadding()) {
        CollectionAppBar(
            title = stringResource(R.string.collection_public_title),
            onNavigateBack = onNavigateBack,
        )
        PullToRefreshBox(
            isRefreshing = collections.loadState.refresh is LoadState.Loading && collections.itemCount > 0,
            onRefresh = {
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                collections.refresh()
            },
            modifier = Modifier.weight(1f),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(count = collections.itemCount, key = collections.itemKey { it.id }) { index ->
                    collections[index]?.let { collection ->
                        PublicCollectionCard(collection, onClick = { onCollectionClick(collection.id) })
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

                    is LoadState.NotLoading -> CollectionEmpty(
                        message = stringResource(R.string.collection_empty),
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }
}
