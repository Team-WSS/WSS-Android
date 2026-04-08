package com.into.websoso.feed.model

data class UserFeeds(
    val isLoadable: Boolean,
    val feeds: List<UserFeed>,
) {
    data class UserFeed(
        val feedId: Long,
        val isSpoiler: Boolean,
        val feedContent: String,
        val createdDate: String,
        val isModified: Boolean,
        val isLiked: Boolean,
        val isPublic: Boolean,
        val likeCount: Int,
        val commentCount: Int,
        val novelId: Long?,
        val title: String?,
        val novelRatingCount: Int?,
        val novelRating: Float?,
        val genre: String?,
        val userNovelRating: Float?,
        val thumbnailUrl: String?,
        val imageCount: Int,
        val feedWriterNovelRating: Float?,
    )

    data class User(
        val id: Long,
        val nickname: String,
        val avatarImage: String,
    )
}
