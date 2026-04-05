package com.into.websoso.data.feed.model

data class FeedsEntity(
    val isLoadable: Boolean,
    val feedsCount: Int,
    val feeds: List<FeedEntity>,
)
