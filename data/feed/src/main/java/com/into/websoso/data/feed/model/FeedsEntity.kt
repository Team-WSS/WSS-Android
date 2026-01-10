package com.into.websoso.data.feed.model

data class FeedsEntity(
    val category: String,
    val isLoadable: Boolean,
    val feeds: List<FeedEntity>,
)
