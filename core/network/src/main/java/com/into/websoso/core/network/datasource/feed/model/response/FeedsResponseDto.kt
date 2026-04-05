package com.into.websoso.core.network.datasource.feed.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedsResponseDto(
    @SerialName("isLoadable")
    val isLoadable: Boolean,
    @SerialName("feeds")
    val feeds: List<FeedResponseDto>,
    @SerialName("feedsCount")
    val feedsCount: Int,
)
