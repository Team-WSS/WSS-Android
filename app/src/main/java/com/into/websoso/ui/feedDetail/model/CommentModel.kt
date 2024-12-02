package com.into.websoso.ui.feedDetail.model

import com.into.websoso.ui.main.feed.model.FeedModel.UserModel

data class CommentModel(
    val user: UserModel,
    val commentContent: String,
    val commentId: Long,
    val createdDate: String,
    val isModified: Boolean,
    val isMyComment: Boolean,
    val isSpoiler: Boolean,
    val isBlocked: Boolean,
    val isHidden: Boolean,
)
