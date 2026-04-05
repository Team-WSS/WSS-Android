package com.into.websoso.user.model

data class UserFeedsEntity(
    val isLoadable: Boolean,
    val feedsCount: Int,
    val feeds: List<UserFeedEntity>,
) {
    data class UserFeedEntity(
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
}
