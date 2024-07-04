package com.teamwss.websoso.data.model

data class FeedsEntity(
    val category: String,
    val isLoadable: Boolean,
    val feeds: List<FeedEntity>,
)
