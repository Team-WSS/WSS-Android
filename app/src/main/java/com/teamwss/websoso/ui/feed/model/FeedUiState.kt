package com.teamwss.websoso.ui.feed.model

data class FeedUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val categories: String = "",
    val feeds: List<FeedModel> = emptyList(),
)
