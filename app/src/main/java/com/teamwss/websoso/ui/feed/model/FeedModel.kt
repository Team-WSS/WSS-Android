package com.teamwss.websoso.ui.feed.model

data class FeedModel(
    val user: UserModel,
    val createdDate: String,
    val id: Long,
    val content: String,
    val relevantCategories: List<String>,
    val likeCount: Int,
    val commentCount: Int,
    val isModified: Boolean,
    val isSpoiler: Boolean,
    val isLiked: Boolean,
    val isMyFeed: Boolean,
    val novel: NovelModel,
    val categories: String = relevantCategories.joinToString(prefix = "", postfix = ""),
) {

    data class UserModel(
        val id: Long,
        val nickname: String,
        val avatarImage: String,
    )

    data class NovelModel(
        val id: Long?,
        val title: String?,
        val rating: Float,
        val ratingCount: Int,
    )
}
