package com.teamwss.websoso.ui.feedDetail.model

import com.teamwss.websoso.ui.feed.model.FeedModel

data class FeedDetailModel(
    val feed: FeedModel,
    val comments: List<CommentModel>,
    val commentsCount: Int,
) {

    data class CommentModel(
        val user: FeedModel.UserModel,
        val commentContent: String,
        val commentId: Int,
        val createdDate: String,
        val isModified: Boolean,
        val isMyComment: Boolean,
    )
}
