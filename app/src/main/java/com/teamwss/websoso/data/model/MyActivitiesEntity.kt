package com.teamwss.websoso.data.model

data class MyActivitiesEntity(
    val isLoadable: Boolean,
    val feeds: List<MyActivityEntity>,
) {
    data class MyActivityEntity(
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
        val novelId: Int?,
        val title: String?,
        val novelRatingCount: Int?,
        val novelRating: Float?,
        val relevantCategories: List<String>,
    )
}
