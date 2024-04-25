package com.teamwss.websoso.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedsRequestDto(
    @SerialName("lastFeedId")
    val lastFeedId: Long,
    @SerialName("size")
    val size: Int,
)
