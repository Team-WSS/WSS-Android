package com.into.websoso.feature.feed.model

data class MyFeedFilter(
    val selectedGenres: Set<NovelCategory> = NovelCategory.entries.toSet(),
    val isVisible: Boolean? = true,
    val isUnVisible: Boolean? = true,
)
