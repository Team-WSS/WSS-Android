package com.teamwss.websoso.data.remote.response

data class FeedResponseDto(
    val commentCount: Int,
    val createdDate: String,
    val feedContent: String,
    val feedId: Int,
    val isModified: Boolean,
    val isSpoiler: Boolean,
    val likeCount: Int,
    val likeUsers: List<Int>,
    val nickname: String,
    val novelId: Int,
    val novelRating: Double,
    val novelRatingCount: Int,
    val profileImage: String,
    val relevantCategories: List<String>,
    val title: String,
    val userId: Int
)
