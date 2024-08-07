package com.teamwss.websoso.ui.feedDetail.model

import com.teamwss.websoso.ui.feed.model.FeedModel

data class FeedDetailModel(
    val feed: FeedModel,
    val comments: List<CommentModel>,
)