package com.into.websoso.core.datastore.datasource.feed.model

import kotlinx.serialization.Serializable

@Serializable
internal data class PendingFeedLikesPreferences(
    val likes: List<PendingFeedLikePreferences>,
)

@Serializable
internal data class PendingFeedLikePreferences(
    val feedId: Long,
    val isLiked: Boolean,
)
