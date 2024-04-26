package com.teamwss.websoso.domain.model

sealed interface FeedType {
    data object MainFeed : FeedType
    data class NovelFeed(
        val novelId: Long,
    ) : FeedType

    data class MyFeed(
        val userId: Long,
    ) : FeedType
}
