package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendedNovelsByUserTasteResponseDto(
    @SerialName("tasteNovels")
    val tasteNovels: List<RecommendedNovelByUserTasteResponseDto>,
) {
    @Serializable
    data class RecommendedNovelByUserTasteResponseDto(
        @SerialName("novelId")
        val novelId: Long,
        @SerialName("title")
        val title: String,
        @SerialName("author")
        val author: String,
        @SerialName("novelImage")
        val novelImage: String,
        @SerialName("interestCount")
        val interestCount: Int,
        @SerialName("novelRating")
        val novelRating: Double,
        @SerialName("novelRatingCount")
        val novelRatingCount: Int,
    )
}
