package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PopularFeedsResponseDto(
    @SerialName("popularFeeds")
    val popularFeeds: List<PopularFeedResponseDto>,
) {
    @Serializable
    data class PopularFeedResponseDto(
        @SerialName("feedId")
        val feedId: Long,
        @SerialName("feedContent")
        val feedContent: String? = null,
        @SerialName("likeCount")
        val likeCount: Int,
        @SerialName("commentCount")
        val commentCount: Int,
        @SerialName("isSpoiler")
        val isSpoiler: Boolean,
        @SerialName("isPublic")
        val isPublic: Boolean? = null,
        @SerialName("title")
        val title: String? = null,
        @SerialName("novelTitle")
        val novelTitle: String? = null,
        @SerialName("novelImage")
        val novelImage: String? = null,
        @SerialName("novelThumbnailImage")
        val novelThumbnailImage: String? = null,
        @SerialName("novelGenreImage")
        val novelGenreImage: String? = null,
    )
}
