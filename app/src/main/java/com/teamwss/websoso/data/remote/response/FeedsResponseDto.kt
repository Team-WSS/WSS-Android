package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedsResponseDto(
    @SerialName("category")
    val category: String,
    @SerialName("feedsResponseDto")
    val feedsResponseDto: List<FeedResponseDto>,
)
