package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelRatingResponseDto(
    @SerialName("novelTitle")
    val novelTitle: String,
    @SerialName("status")
    val status: String?,
    @SerialName("startDate")
    val startDate: String?,
    @SerialName("endDate")
    val endDate: String?,
    @SerialName("userNovelRating")
    val userNovelRating: Float,
    @SerialName("attractivePoints")
    val attractivePoints: List<String>,
    @SerialName("keywords")
    val keywords: List<KeywordResponseDto>,
) {
    @Serializable
    data class KeywordResponseDto(
        @SerialName("keywordId")
        val keywordId: Int,
        @SerialName("keywordName")
        val keywordName: String,
    )
}
