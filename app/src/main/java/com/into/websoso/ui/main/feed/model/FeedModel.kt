package com.into.websoso.ui.main.feed.model

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
    val isPublic: Boolean,
    val novel: NovelModel,
    val categories: String = relevantCategories.joinToString(prefix = "", postfix = ""),
) {
    val formattedCreatedDate: String = " · $createdDate"
    val isEmptyOfRelevantCategories: Boolean = relevantCategories.isEmpty()

    data class UserModel(
        val id: Long,
        val nickname: String,
        val avatarImage: String,
    )

    data class NovelModel(
        val id: Long?,
        val title: String?,
        val rating: Float?,
        val ratingCount: Int?,
    ) {
        val isNothing: Boolean = id == null
    }
}
