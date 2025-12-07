package com.into.websoso.core.network.datasource.feed.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CommentResponseDto(
    @SerialName("userId")
    val userId: Long,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("avatarImage")
    val avatarImage: String,
    @SerialName("commentId")
    val commentId: Long,
    @SerialName("createdDate")
    val createdDate: String,
    @SerialName("commentContent")
    val commentContent: String,
    @SerialName("isModified")
    val isModified: Boolean,
    @SerialName("isMyComment")
    val isMyComment: Boolean,
    @SerialName("isSpoiler")
    val isSpoiler: Boolean,
    @SerialName("isBlocked")
    val isBlocked: Boolean,
    @SerialName("isHidden")
    val isHidden: Boolean,
)
