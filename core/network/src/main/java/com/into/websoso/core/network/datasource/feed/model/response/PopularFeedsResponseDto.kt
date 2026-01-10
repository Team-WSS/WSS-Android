package com.into.websoso.core.network.datasource.feed.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PopularFeedsResponseDto(
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
