package com.teamwss.websoso.data.model

data class NovelFeedsEntity(
    val isLoadable: Boolean = false,
    val feeds: List<FeedEntity> = emptyList(),
)
