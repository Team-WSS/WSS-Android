package com.teamwss.websoso.ui.feed.model

data class FeedUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val feeds: FeedsModel = FeedsModel(),
)

data class FeedsModel(
    val category: String = "전체", // 카테고리 불일치할 경우 어떻게 할것인지
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

    fun getCategory(): String = relevantCategories.joinToString(prefix = "", postfix = "")
}
