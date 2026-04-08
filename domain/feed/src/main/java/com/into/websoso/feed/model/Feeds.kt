package com.into.websoso.feed.model

data class Feeds(
    // 내 피드 총 개수
    val totalCount: Int = 0,
    val isLoadable: Boolean,
    val feeds: List<Feed>,
)
