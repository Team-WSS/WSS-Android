package com.into.websoso.core.network.datasource.feed.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CommentRequestDto(
    @SerialName("commentContent") val commentContent: String,
)
