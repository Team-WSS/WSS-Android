package com.teamwss.websoso.ui.feed.model

import com.teamwss.websoso.domain.model.Feeds

data class FeedUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val feeds: Feeds,
)