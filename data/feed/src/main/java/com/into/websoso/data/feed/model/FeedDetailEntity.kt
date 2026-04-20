package com.into.websoso.data.feed.model

data class FeedDetailEntity(
    val user: UserEntity,
    val createdDate: String,
    val id: Long,
    val content: String,
    val likeCount: Int,
    val isLiked: Boolean,
    val commentCount: Int,
    val isModified: Boolean,
    val isSpoiler: Boolean,
    val isMyFeed: Boolean,
    val isPublic: Boolean,
    val novel: NovelEntity?,
    val images: List<String>,
    val imageCount: Int,
) {
    data class UserEntity(
        val id: Long,
        val nickname: String,
        val avatarImage: String,
    )

    data class NovelEntity(
        val id: Long,
        val title: String,
        val rating: Float?,
        val ratingCount: Int,
        val thumbnail: String,
        val genre: String,
        val author: String,
        val description: String,
        val feedWriterNovelRating: Float?,
    )
}
