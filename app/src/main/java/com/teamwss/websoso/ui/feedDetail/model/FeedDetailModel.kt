package com.teamwss.websoso.ui.feedDetail.model

import com.teamwss.websoso.ui.main.feed.model.FeedModel

data class FeedDetailModel(
    val feed: FeedModel? = null,
    val comments: List<CommentModel> = emptyList(),
    val user: UserModel? = null,
) {

    data class UserModel(
        val avatarImage: String,
    )
}
