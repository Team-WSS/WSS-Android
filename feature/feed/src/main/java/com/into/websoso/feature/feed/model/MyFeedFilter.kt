package com.into.websoso.feature.feed.model

data class MyFeedFilter(
    val selectedGenres: Set<NovelCategory> = emptySet(),
    val isVisible: Boolean? = null,
    val isUnVisible: Boolean? = null,
)
