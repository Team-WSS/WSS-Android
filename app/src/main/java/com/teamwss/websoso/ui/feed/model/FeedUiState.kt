package com.teamwss.websoso.ui.feed.model

data class FeedUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val category: String = "전체",
    val feeds: List<FeedModel> = emptyList(),
)

data class FeedModel(
    val user: UserModel,
    val createdDate: String,
    val id: Int,
    val content: String,
    val relevantCategories: List<String>,
    val likeCount: String,
    val likeUsers: List<Int>,
    val commentCount: String,
    val isModified: Boolean,
    val isSpoiled: Boolean,
    val novel: NovelModel,
    val isThumbUpSelected: Boolean = likeUsers.any { it == user.id },
    val categories: String = relevantCategories.joinToString(prefix = "", postfix = "")
) {
    data class UserModel(
        val id: Int,
        val nickname: String,
        val profileImage: String,
    )

    data class NovelModel(
        val id: Int,
        val title: String,
        val rating: Double,
        val ratingCount: Int,
    )
}
