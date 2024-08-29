package com.teamwss.websoso.ui.feedDetail.model

import com.teamwss.websoso.ui.main.feed.model.FeedModel

data class FeedDetailUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val feed: FeedModel? = null,
    val comments: List<CommentModel> = emptyList(),
)
