package com.into.websoso.feed.model

data class Feeds(
    val category: String,
    val isLoadable: Boolean,
    val feeds: List<Feed>,
)
