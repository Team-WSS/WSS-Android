package com.into.websoso.feature.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.into.websoso.feature.feed.model.FeedTab
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedRoute(
    onWriteClick: () -> Unit,
    onProfileClick: (userId: Long, isMyFeed: Boolean) -> Unit,
    onNovelClick: (novelId: Long) -> Unit,
    onContentClick: (feedId: Long, isLiked: Boolean) -> Unit,
    onFirstItemClick: (feedId: Long, isMyFeed: Boolean) -> Unit,
    onSecondItemClick: (feedId: Long, isMyFeed: Boolean) -> Unit,
    viewModel: FeedViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newValue ->
            newValue != SheetValue.Hidden
        },
    )
    var isFilterSheetVisible by remember { mutableStateOf(false) }

    Box {
        FeedScreen(
            uiState = uiState,
            bottomSheetState = bottomSheetState,
            onTabSelected = viewModel::updateTab,
            onSortSelected = viewModel::updateMyFeedSort,
            onSosoTypeSelected = viewModel::updateSosoCategory,
            onWriteClick = onWriteClick,
            onProfileClick = { userId, feedTab ->
                onProfileClick(
                    userId,
                    feedTab == FeedTab.MY_FEED,
                )
            },
            onMoreClick = { },
            onNovelClick = onNovelClick,
            onLikeClick = viewModel::updateLike,
            onContentClick = onContentClick,
            onFilterClick = {
                scope.launch { bottomSheetState.show() }
                    .invokeOnCompletion { isFilterSheetVisible = true }
            },
            onApplyFilterClick = {
                scope.launch {
                    viewModel.applyMyFilter(filter = it)
                    bottomSheetState.hide()
                }.invokeOnCompletion { isFilterSheetVisible = false }
            },
            isFilterSheetVisible = isFilterSheetVisible,
            onFilterCloseClick = {
                scope.launch { bottomSheetState.hide() }
                    .invokeOnCompletion { isFilterSheetVisible = false }
            },
            onFirstItemClick = onFirstItemClick,
            onSecondItemClick = onSecondItemClick,
            onRefreshPull = viewModel::refresh,
        )

        if (uiState.loading) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}
