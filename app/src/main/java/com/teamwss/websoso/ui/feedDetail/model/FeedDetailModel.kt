package com.teamwss.websoso.ui.feedDetail.model

import com.teamwss.websoso.ui.main.feed.model.FeedModel

data class FeedDetailModel(
    val feed: FeedModel,
    val comments: List<CommentModel>,
)
