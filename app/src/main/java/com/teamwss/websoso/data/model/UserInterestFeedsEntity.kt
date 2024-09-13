package com.teamwss.websoso.data.model

data class UserInterestFeedsEntity(
    val userInterestFeeds: List<UserInterestFeedEntity>,
    val message: String,
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

enum class UserInterestFeedMessage(val message: String) {
    NO_INTEREST_NOVELS("NO_INTEREST_NOVELS"),
    NO_ASSOCIATED_FEEDS("NO_ASSOCIATED_FEEDS");

    companion object {
        fun fromMessage(message: String): UserInterestFeedMessage? {
            return entries.find { it.message == message }
        }
    }
}