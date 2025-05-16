package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserFeedsResponseDto(
    @SerialName("feeds")
    val feeds: List<UserFeedResponseDto>,
    @SerialName("isLoadable")
    val isLoadable: Boolean,
) {
    @Serializable
    data class UserFeedResponseDto(
        @SerialName("commentCount")
        val commentCount: Int,
        @SerialName("createdDate")
        val createdDate: String,
        @SerialName("feedContent")
        val feedContent: String,
        @SerialName("feedId")
        val feedId: Long,
        @SerialName("isLiked")
        val isLiked: Boolean,
        @SerialName("isModified")
        val isModified: Boolean,
        @SerialName("isSpoiler")
        val isSpoiler: Boolean,
        @SerialName("isPublic")
        val isPublic: Boolean,
        @SerialName("likeCount")
        val likeCount: Int,
        @SerialName("novelId")
        val novelId: Long?,
        @SerialName("novelRating")
        val novelRating: Float?,
        @SerialName("novelRatingCount")
        val novelRatingCount: Int?,
        @SerialName("relevantCategories")
        val relevantCategories: List<String>,
        @SerialName("title")
        val title: String?,
    )
}
