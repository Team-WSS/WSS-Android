package com.into.websoso.data.model

import com.into.websoso.data.model.FeedEntity.UserEntity

data class CommentEntity(
    val user: UserEntity,
    val commentContent: String,
    val commentId: Long,
    val createdDate: String,
    val isModified: Boolean,
    val isMyComment: Boolean,
    val isSpoiler: Boolean,
    val isBlocked: Boolean,
    val isHidden: Boolean,
)
