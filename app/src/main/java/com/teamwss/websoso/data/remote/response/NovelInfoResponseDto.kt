package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelInfoResponseDto(
    @SerialName("novelDescription")
    val novelDescription: String,
    @SerialName("platforms")
    val platforms: List<PlatformResponseDto>,
    @SerialName("attractivePoints")
    val attractivePoints: List<String>,
    @SerialName("keywords")
    val keywords: List<KeywordResponseDto>,
    @SerialName("watchingCount")
    val watchingCount: Int,
    @SerialName("watchedCount")
    val watchedCount: Int,
    @SerialName("quitCount")
    val quitCount: Int,
) {
    @Serializable
    data class PlatformResponseDto(
        @SerialName("platformName")
        val platformName: String,
        @SerialName("platformImage")
        val platformImage: String,
        @SerialName("platformUrl")
        val platformUrl: String,
    )

    @Serializable
    data class KeywordResponseDto(
        @SerialName("keywordName")
        val keywordName: String,
        @SerialName("keywordCount")
        val keywordCount: Int,
    )
}
