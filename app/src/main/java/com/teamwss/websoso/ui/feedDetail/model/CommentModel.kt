package com.teamwss.websoso.ui.feedDetail.model

import com.teamwss.websoso.ui.feed.model.FeedModel.UserModel

data class CommentModel(
    val user: UserModel,
    val commentContent: String,
    val commentId: Int,
    val createdDate: String,
    val isModified: Boolean,
    val isMyComment: Boolean,
)
