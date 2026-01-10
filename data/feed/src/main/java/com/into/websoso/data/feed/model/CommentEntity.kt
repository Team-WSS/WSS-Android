package com.into.websoso.data.feed.model

data class CommentEntity(
    val user: FeedEntity.UserEntity,
    val commentContent: String,
    val commentId: Long,
    val createdDate: String,
    val isModified: Boolean,
    val isMyComment: Boolean,
    val isSpoiler: Boolean,
    val isBlocked: Boolean,
    val isHidden: Boolean,
)
