package com.into.websoso.domain.usecase

import com.into.websoso.data.repository.NotificationRepository
import com.into.websoso.domain.mapper.toDomain
import com.into.websoso.domain.model.NotificationInfo
import javax.inject.Inject

class GetNotificationListUseCase
    @Inject
    constructor(
        private val notificationRepository: NotificationRepository,
    ) {
        suspend operator fun invoke(lastNotificationId: Long = DEFAULT_LAST_NOTICE_ID): Result<NotificationInfo> =
            try {
                val size = when (lastNotificationId == DEFAULT_LAST_NOTICE_ID) {
                    true -> DEFAULT_LOAD_SIZE
                    false -> ADDITIONAL_LOAD_SIZE
                }
                val notifications = notificationRepository.fetchNotifications(lastNotificationId, size).toDomain()
                Result.success(notifications)
            } catch (e: Exception) {
                Result.failure(e)
            }

        companion object {
            private const val DEFAULT_LOAD_SIZE = 20
            private const val ADDITIONAL_LOAD_SIZE = 10
            private const val DEFAULT_LAST_NOTICE_ID = 0L
        }
    }
