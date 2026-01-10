package com.into.websoso.feature.feed

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FeedRoute(viewModel: FeedViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box {
        FeedScreen(
            uiState = uiState,
            onTabSelected = viewModel::updateTab,
            onSortSelected = viewModel::updateMyFeedSort,
            onSosoTypeSelected = viewModel::updateSosoCategory,
            onWriteClick = { },
            onFilterClick = { },
        )

        if (uiState.loading) CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}
