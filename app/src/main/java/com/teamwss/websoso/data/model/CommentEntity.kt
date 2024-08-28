package com.teamwss.websoso.data.model

import com.teamwss.websoso.data.model.FeedEntity.UserEntity

data class CommentEntity(
    val user: UserEntity,
    val commentContent: String,
    val commentId: Long,
    val createdDate: String,
    val isModified: Boolean,
    val isMyComment: Boolean,
)
