package com.into.websoso.core.network.datasource.feed.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class FeedsResponseDto(
    @SerialName("category")
    val category: String,
    @SerialName("isLoadable")
    val isLoadable: Boolean,
    @SerialName("feeds")
    val feeds: List<FeedResponseDto>,
)
