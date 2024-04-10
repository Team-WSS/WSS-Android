package com.teamwss.websoso.domain.model

data class Feed(
    val user: User,
    val createdDate: String,
    val id: Int,
    val content: String,
    val relevantCategories: List<String>,
    val likeCount: String,
    val likeUsers: List<Int>,
    val commentCount: String,
    val isModified: Boolean,
    val isSpoiled: Boolean,
    val novel: Novel,
) {
    data class User(
        val id: Int,
        val nickname: String,
        val profileImage: String,
    )

    data class Novel(
        val id: Int,
        val title: String,
        val rating: Double,
        val ratingCount: Int,
    )
}
