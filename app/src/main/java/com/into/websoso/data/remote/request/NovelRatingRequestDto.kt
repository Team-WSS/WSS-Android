package com.into.websoso.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelRatingRequestDto(
    @SerialName("novelId")
    val novelId: Long?,
    @SerialName("userNovelRating")
    val userNovelRating: Float,
    @SerialName("status")
    val status: String?,
    @SerialName("startDate")
    val startDate: String?,
    @SerialName("endDate")
    val endDate: String?,
    @SerialName("attractivePoints")
    val attractivePoints: List<String>,
    @SerialName("keywordIds")
    val keywordIds: List<Int>,
)
