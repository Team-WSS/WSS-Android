package com.into.websoso.domain.usecase

import com.into.websoso.data.repository.NovelNotificationRepository
import com.into.websoso.domain.model.NovelNotificationType
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class DeleteNovelNotificationSubscriptionsUseCase
    @Inject
    constructor(
        private val novelNotificationRepository: NovelNotificationRepository,
    ) {
        suspend operator fun invoke(
            notificationType: NovelNotificationType,
            novelIds: List<Long>,
        ): Result<Unit> =
            try {
                novelIds.chunked(MAX_DELETABLE_SIZE).forEach { chunkedNovelIds ->
                    novelNotificationRepository.deleteNovelNotificationSubscriptions(
                        notificationType = notificationType.name,
                        novelIds = chunkedNovelIds,
                    )
                }
                Result.success(Unit)
            } catch (e: CancellationException) {
                // 취소는 실패가 아니므로 Result로 감싸지 않고 그대로 전파한다
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }

        companion object {
            private const val MAX_DELETABLE_SIZE = 100
        }
    }
