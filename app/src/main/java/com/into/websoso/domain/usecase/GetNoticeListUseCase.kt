package com.into.websoso.domain.usecase

import com.into.websoso.data.repository.NoticeRepository
import com.into.websoso.domain.mapper.toDomain
import com.into.websoso.domain.model.NoticeInfo
import javax.inject.Inject

class GetNoticeListUseCase @Inject constructor(
    private val noticeRepository: NoticeRepository,
) {

    suspend operator fun invoke(lastNoticeId: Long = DEFAULT_LAST_NOTICE_ID): Result<NoticeInfo> {
        return try {
            val size = when (lastNoticeId) {
                DEFAULT_LAST_NOTICE_ID -> DEFAULT_LOAD_SIZE
                else -> ADDITIONAL_LOAD_SIZE
            }
            val notices = noticeRepository.fetchNotices(lastNoticeId, size).toDomain()
            Result.success(notices)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val DEFAULT_LOAD_SIZE = 20
        private const val ADDITIONAL_LOAD_SIZE = 10
        private const val DEFAULT_LAST_NOTICE_ID = 0L
        const val DEFAULT_INTRINSIC_ID = -1L
    }
}
