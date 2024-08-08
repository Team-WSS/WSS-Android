package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName

data class CommentsResponseDto(
    @SerialName("comments")
    val comments: List<CommentResponseDto>,
    @SerialName("commentsCount")
    val commentsCount: Int,
)
