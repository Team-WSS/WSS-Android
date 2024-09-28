package com.teamwss.websoso.ui.main.feed.model

data class FeedUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val isLoadable: Boolean = true,
    val feeds: List<FeedModel> = emptyList(),
    val isRefreshed: Boolean = false,
)
