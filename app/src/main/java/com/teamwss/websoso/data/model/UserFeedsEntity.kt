package com.teamwss.websoso.data.model

data class UserFeedsEntity(
    val isLoadable: Boolean,
    val feeds: List<UserFeedEntity>,
) {
    data class UserFeedEntity(
        val feedId: Int,
        val isSpoiler: Boolean,
        val feedContent: String,
        val createdDate: String,
        val isModified: Boolean,
        val isLiked: Boolean,
        val likeCount: Int,
        val commentCount: Int,
        val novelId: Int?,
        val title: String?,
        val novelRatingCount: Int?,
        val novelRating: Float?,
        val relevantCategories: List<String>,
    )
}
