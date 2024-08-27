package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedsResponseDto(
    @SerialName("category")
    val category: String,
    @SerialName("isLoadable")
    val isLoadable: Boolean,
    @SerialName("feeds")
    val feeds: List<FeedResponseDto>,
)
