package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedDetailResponseDto(
    @SerialName("userId")
    val userId: Long,
    @SerialName("feedId")
    val feedId: Long,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("avatarImage")
    val avatarImage: String,
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
    @SerialName("relevantCategories")
    val relevantCategories: List<String>,
    @SerialName("isSpoiler")
    val isSpoiler: Boolean,
    @SerialName("isModified")
    val isModified: Boolean,
    @SerialName("isMyFeed")
    val isMyFeed: Boolean,
    @SerialName("isPublic")
    val isPublic: Boolean,
)
