package com.teamwss.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedResponseDto(
    @SerialName("avatarImage")
    val avatarImage: String,
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
    @SerialName("isMyFeed")
    val isMyFeed: Boolean,
    @SerialName("isSpolier")
    val isSpoiled: Boolean,
    @SerialName("likeCount")
    val likeCount: Int,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("novelId")
    val novelId: Long,
    @SerialName("novelRating")
    val novelRating: Float,
    @SerialName("novelRatingCount")
    val novelRatingCount: Int,
    @SerialName("relevantCategories")
    val relevantCategories: List<String>,
    @SerialName("title")
    val title: String,
    @SerialName("userId")
    val userId: Long,
)
