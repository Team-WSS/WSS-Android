package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInterestFeedsResponseDto(
    @SerialName("recommendFeeds")
    val userInterestFeeds: List<UserInterestFeedResponseDto>,
    @SerialName("message")
    val message: String,
) {
    @Serializable
    data class UserInterestFeedResponseDto(
        @SerialName("avatarImage")
        val avatarImage: String,
        @SerialName("feedContent")
        val feedContent: String,
        @SerialName("nickname")
        val nickname: String,
        @SerialName("novelId")
        val novelId: Int,
        @SerialName("novelImage")
        val novelImage: String,
        @SerialName("novelRating")
        val novelRating: Double,
        @SerialName("novelRatingCount")
        val novelRatingCount: Int,
        @SerialName("novelTitle")
        val novelTitle: String,
    )
}
