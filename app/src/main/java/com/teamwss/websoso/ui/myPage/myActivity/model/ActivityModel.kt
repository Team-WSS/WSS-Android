package com.teamwss.websoso.ui.myPage.myActivity.model

data class ActivityModel(
    val feedId: Int,
    val userId: Int,
    val profileImg: String,
    val nickname: String,
    val isSpoiler: Boolean,
    val feedContent: String,
    val createdDate: String,
    val isModified: Boolean,
    val isLiked: Boolean,
    val likeCount: Int,
    val commentCount: Int,
    val novelId: Int,
    val title: String,
    val novelRatingCount: Int?,
    val novelRating: Float?,
    val relevantCategories:String,
){
    val formattedScore: String
        get() = String.format(
            "%s (%,d)",
            novelRating?.takeIf { it != null } ?: 0.0f,
            novelRatingCount?.takeIf { it != null } ?: 0
        )
}