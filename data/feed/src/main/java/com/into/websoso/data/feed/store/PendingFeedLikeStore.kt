package com.into.websoso.data.feed.store

import kotlinx.coroutines.flow.Flow

interface PendingFeedLikeStore {
    val pendingLikes: Flow<Map<Long, Boolean>>

    suspend fun getPendingLikes(): Map<Long, Boolean>

    suspend fun updatePendingLike(
        feedId: Long,
        isLiked: Boolean,
    )

    suspend fun deletePendingLikeIfMatched(
        feedId: Long,
        isLiked: Boolean,
    ): Boolean
}
