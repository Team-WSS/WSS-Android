package com.into.websoso.domain.model

/**
 * 삭제는 100개씩 나눠 순차 요청하므로 앞쪽 요청만 성공한 채 실패할 수 있다.
 * 이때 서버에서 이미 지워진 항목을 화면에 남겨두지 않도록 실제로 삭제된 목록을 함께 돌려준다.
 */
data class NovelNotificationDeleteResult(
    val deletedNovelIds: List<Long>,
    val isCompleted: Boolean,
)
