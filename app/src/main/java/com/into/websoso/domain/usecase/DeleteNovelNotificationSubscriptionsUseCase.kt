package com.into.websoso.domain.usecase

import com.into.websoso.data.repository.NovelNotificationRepository
import com.into.websoso.domain.model.NovelNotificationDeleteResult
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
        ): Result<NovelNotificationDeleteResult> {
            val deletedNovelIds = mutableListOf<Long>()

            return try {
                novelIds.chunked(MAX_DELETABLE_SIZE).forEach { chunkedNovelIds ->
                    novelNotificationRepository.deleteNovelNotificationSubscriptions(
                        notificationType = notificationType.name,
                        novelIds = chunkedNovelIds,
                    )
                    deletedNovelIds += chunkedNovelIds
                }
                Result.success(
                    NovelNotificationDeleteResult(
                        deletedNovelIds = deletedNovelIds,
                        isCompleted = true,
                    ),
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                when (deletedNovelIds.isEmpty()) {
                    true -> Result.failure(e)
                    false -> Result.success(
                        NovelNotificationDeleteResult(
                            deletedNovelIds = deletedNovelIds,
                            isCompleted = false,
                        ),
                    )
                }
            }
        }

        companion object {
            private const val MAX_DELETABLE_SIZE = 100
        }
    }
