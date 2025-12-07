package com.into.websoso.core.network.datasource.feed.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CommentsResponseDto(
    @SerialName("comments")
    val comments: List<CommentResponseDto>,
    @SerialName("commentsCount")
    val commentsCount: Int,
)
