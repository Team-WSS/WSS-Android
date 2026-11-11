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

    /**
     * 기록의 소유 계정을 [accountId]로 맞춥니다.
     * 다른 계정의 기록이거나 소유 계정이 없는 이전 형식의 기록이면, 화면 적용·전송에 쓰지 않도록 모두 지웁니다.
     */
    suspend fun claimPendingLikes(accountId: Long)

    /** 로그아웃·탈퇴 시 기록과 소유 계정을 모두 지웁니다. */
    suspend fun clearPendingLikes()

    /** 저장된 기록을 복원하지 못해 비운 표시(파일 손상, 해석 불가)가 있는지 반환합니다. 표시는 지우지 않습니다. */
    suspend fun readResetNotice(): Boolean

    /** 사용자에게 안내한 뒤 표시를 지웁니다. */
    suspend fun clearResetNotice()
}
