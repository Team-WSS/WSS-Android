package com.into.websoso.data.feed.model

data class FeedEntity(
    val user: UserEntity,
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
    val novel: NovelEntity,
    val images: List<String>,
    val imageCount: Int,
    val genreName: String?,
    val userNovelRating: Float?,
    val feedWriterNovelRating: Float?,
) {
    data class UserEntity(
        val id: Long,
        val nickname: String,
        val avatarImage: String,
    )

    data class NovelEntity(
        val id: Long?,
        val title: String?,
        val rating: Float?,
        val ratingCount: Int?,
    )
}
