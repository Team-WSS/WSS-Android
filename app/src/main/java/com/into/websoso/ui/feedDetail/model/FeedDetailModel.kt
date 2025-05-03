package com.into.websoso.ui.feedDetail.model

import com.into.websoso.ui.main.feed.model.FeedModel

data class FeedDetailModel(
    val feed: FeedModel? = null,
    val comments: List<CommentModel> = emptyList(),
    val user: UserModel? = null,
) {
    data class UserModel(
        val avatarImage: String,
    )
}
