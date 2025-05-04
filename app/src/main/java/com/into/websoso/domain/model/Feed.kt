package com.into.websoso.domain.model

data class Feed(
    val user: User,
    val createdDate: String,
    val id: Long,
    val content: String,
    val relevantCategories: List<String>,
    val likeCount: Int,
    val isLiked: Boolean,
    val commentCount: Int,
    val isModified: Boolean,
    val isSpoiler: Boolean,
    val isMyFeed: Boolean,
    val isPublic: Boolean,
    val novel: Novel,
) {
    data class User(
        val id: Long,
        val nickname: String,
        val avatarImage: String,
    )

    data class Novel(
        val id: Long?,
        val title: String?,
        val rating: Float?,
        val ratingCount: Int?,
    )
}
