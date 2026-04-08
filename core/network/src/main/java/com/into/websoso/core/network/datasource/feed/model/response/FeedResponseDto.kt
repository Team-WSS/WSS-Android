package com.into.websoso.core.network.datasource.feed.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedResponseDto(
    @SerialName("userId")
    val userId: Long,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("avatarImage")
    val avatarImage: String,
    @SerialName("feedId")
    val feedId: Long,
    @SerialName("createdDate")
    val createdDate: String,
    @SerialName("feedContent")
    val feedContent: String,
    @SerialName("likeCount")
    val likeCount: Int,
    @SerialName("isLiked")
    val isLiked: Boolean,
    @SerialName("commentCount")
    val commentCount: Int,
    @SerialName("novelId")
    val novelId: Long?,
    @SerialName("title")
    val title: String?,
    @SerialName("novelRating")
    val novelRating: Float?,
    @SerialName("novelRatingCount")
    val novelRatingCount: Int?,
    @SerialName("isSpoiler")
    val isSpoiler: Boolean,
    @SerialName("isModified")
    val isModified: Boolean,
    @SerialName("isMyFeed")
    val isMyFeed: Boolean,
    @SerialName("isPublic")
    val isPublic: Boolean,
    @SerialName("thumbnailUrl")
    val thumbnailUrl: String?,
    @SerialName("imageCount")
    val imageCount: Int,
    @SerialName("genreName")
    val genreName: String?,
    @SerialName("userNovelRating")
    val userNovelRating: Float?,
    @SerialName("feedWriterNovelRating")
    val feedWriterNovelRating: Float?,
)
