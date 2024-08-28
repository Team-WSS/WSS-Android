package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponseDto(
    @SerialName("avatarImage")
    val avatarImage: String,
    @SerialName("commentContent")
    val commentContent: String,
    @SerialName("commentId")
    val commentId: Int,
    @SerialName("createdDate")
    val createdDate: String,
    @SerialName("isModified")
    val isModified: Boolean,
    @SerialName("isMyComment")
    val isMyComment: Boolean,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("userId")
    val userId: Int,
)
