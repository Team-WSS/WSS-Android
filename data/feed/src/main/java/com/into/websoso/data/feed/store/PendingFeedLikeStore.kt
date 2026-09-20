package com.into.websoso.data.feed.store

import kotlinx.coroutines.flow.Flow

interface PendingFeedLikeStore {
    val pendingLikes: Flow<Map<Long, Boolean>>

    suspend fun getPendingLikes(): Map<Long, Boolean>

    suspend fun updatePendingLike(
        feedId: Long,
        isLiked: Boolean,
    )

    suspend fun deletePendingLike(feedId: Long)

    /**
     * 저장된 좋아요 상태가 [isLiked]와 일치하는 경우에만 삭제합니다.
     *
     * `@return` 매칭되어 삭제된 경우 true, 일치하지 않아 삭제하지 않은 경우 false
     * */
    suspend fun deletePendingLikeIfMatched(
        feedId: Long,
        isLiked: Boolean,
    ): Boolean
}
