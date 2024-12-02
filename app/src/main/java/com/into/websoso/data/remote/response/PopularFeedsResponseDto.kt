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
        val feedContent: String,
        @SerialName("likeCount")
        val likeCount: Int,
        @SerialName("commentCount")
        val commentCount: Int,
        @SerialName("isSpoiler")
        val isSpoiler: Boolean,
    )
}
