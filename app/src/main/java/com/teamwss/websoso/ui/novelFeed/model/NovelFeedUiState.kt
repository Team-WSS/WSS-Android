package com.teamwss.websoso.ui.novelFeed.model

import com.teamwss.websoso.ui.main.feed.model.FeedModel

data class NovelFeedUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val isLoadable: Boolean = true,
    val feeds: List<FeedModel> = emptyList(),
)
