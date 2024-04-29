package com.teamwss.websoso.data.model

data class FeedEntity(
    val user: UserEntity,
    val createdDate: String,
    val id: Int,
    val content: String,
    val relevantCategories: List<String>,
    val likeCount: String,
    val likeUsers: List<Int>,
    val commentCount: String,
    val isModified: Boolean,
    val isSpoiled: Boolean,
    val novel: NovelEntity,
) {
    data class UserEntity(
        val id: Int,
        val nickname: String,
        val profileImage: String,
    )

    data class NovelEntity(
        val id: Int,
        val title: String,
        val rating: Double,
        val ratingCount: Int,
    )
}
