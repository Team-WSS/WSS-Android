package com.into.websoso.domain.usecase

import com.into.websoso.data.repository.NoticeRepository
import com.into.websoso.domain.mapper.toDomain
import com.into.websoso.domain.model.NoticeInfo
import javax.inject.Inject

class GetNoticeListUseCase @Inject constructor(
    private val noticeRepository: NoticeRepository,
) {

    suspend operator fun invoke(lastNoticeId: Long = 0): Result<NoticeInfo> {
        return try {
            val notices = noticeRepository.fetchNotices(lastNoticeId)
            Result.success(notices.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
