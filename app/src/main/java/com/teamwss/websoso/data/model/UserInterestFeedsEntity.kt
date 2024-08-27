package com.teamwss.websoso.data.model

data class UserInterestFeedsEntity(
    val userInterestFeeds: List<UserInterestFeedEntity>,
) {
    data class UserInterestFeedEntity(
        val avatarImage: String,
        val feedContent: String,
        val nickname: String,
        val novelId: Int,
        val novelImage: String,
        val novelRating: Double,
        val novelRatingCount: Int,
        val novelTitle: String,
    )
}