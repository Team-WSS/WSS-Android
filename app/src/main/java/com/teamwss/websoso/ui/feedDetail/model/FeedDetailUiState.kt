package com.teamwss.websoso.ui.feedDetail.model

import com.teamwss.websoso.common.ui.model.ResultFrom
import com.teamwss.websoso.common.ui.model.ResultFrom.Feed
import com.teamwss.websoso.ui.main.feed.model.FeedModel

data class FeedDetailUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val previousStack: PreviousStack = PreviousStack(Feed),
    val feed: FeedModel? = null,
    val comments: List<CommentModel> = emptyList(),
) {

    data class PreviousStack(
        val from: ResultFrom,
    )
}
