package com.into.websoso.data.feed.model

data class PopularFeedsEntity(
    val popularFeeds: List<PopularFeedEntity>,
) {
    data class PopularFeedEntity(
        val feedId: Long,
        val feesContent: String,
        val likeCount: Int,
        val commentCount: Int,
        val isSpoiler: Boolean,
    )
}
