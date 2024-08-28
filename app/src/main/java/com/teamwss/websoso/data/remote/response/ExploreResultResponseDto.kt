package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExploreResultResponseDto(
    @SerialName("resultCount")
    val resultCount: Long,
    @SerialName("isLoadable")
    val isLoadable: Boolean,
    @SerialName("novels")
    val novels: List<ExploreResultNovelResponseDto>,
) {
    @Serializable
    data class ExploreResultNovelResponseDto(
        @SerialName("novelId")
        val novelId: Long,
        @SerialName("title")
        val novelTitle: String,
        @SerialName("author")
        val novelAuthor: String,
        @SerialName("novelImage")
        val novelImage: String,
        @SerialName("interestCount")
        val interestedCount: Long,
        @SerialName("novelRating")
        val novelRating: Float,
        @SerialName("novelRatingCount")
        val novelRatingCount: Long,
    )
}