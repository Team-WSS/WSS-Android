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
                // 취소는 실패가 아니므로 Result로 감싸지 않고 그대로 전파한다
                throw e
            } catch (e: Exception) {
                when (deletedNovelIds.isEmpty()) {
                    // 하나도 지우지 못했으면 기존과 같이 실패로 다룬다
                    true -> Result.failure(e)
                    // 앞쪽 요청이 이미 서버에 반영됐으므로 그만큼은 화면에서도 지워야 한다
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
