package com.into.websoso.feature.feed

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.into.websoso.feature.feed.model.FeedTab
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateFeedRoute(
    onWriteClick: () -> Unit,
    onProfileClick: (userId: Long, isMyFeed: Boolean) -> Unit,
    onNovelClick: (novelId: Long) -> Unit,
    onContentClick: (feedId: Long, isLiked: Boolean) -> Unit,
    onFirstItemClick: (feedId: Long, isMyFeed: Boolean) -> Unit,
    onSecondItemClick: (feedId: Long, isMyFeed: Boolean) -> Unit,
    onWriteFeedClick: () -> Unit,
    viewModel: UpdatedFeedViewModel = hiltViewModel(),
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
        onWriteFeedClick = onWriteFeedClick,
    )
}
