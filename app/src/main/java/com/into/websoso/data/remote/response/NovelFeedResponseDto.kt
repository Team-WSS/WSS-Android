package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelFeedResponseDto(
    @SerialName("isLoadable")
    val isLoadable: Boolean,
    @SerialName("feeds")
    val feeds: List<FeedResponseDto>,
)
