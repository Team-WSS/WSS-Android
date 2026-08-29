package com.into.websoso.domain.usecase

import com.into.websoso.data.repository.NovelNotificationRepository
import com.into.websoso.domain.mapper.toDomain
import com.into.websoso.domain.model.NovelNotificationSubscriptions
import com.into.websoso.domain.model.NovelNotificationSubscriptions.Companion.DEFAULT_SUBSCRIPTION_ID
import com.into.websoso.domain.model.NovelNotificationType
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GetNovelNotificationSubscriptionsUseCase
    @Inject
    constructor(
        private val novelNotificationRepository: NovelNotificationRepository,
    ) {
        suspend operator fun invoke(
            notificationType: NovelNotificationType,
            lastSubscriptionId: Long = DEFAULT_SUBSCRIPTION_ID,
        ): Result<NovelNotificationSubscriptions> =
            try {
                val size = when (lastSubscriptionId == DEFAULT_SUBSCRIPTION_ID) {
                    true -> DEFAULT_LOAD_SIZE
                    false -> ADDITIONAL_LOAD_SIZE
                }
                val subscriptions = novelNotificationRepository
                    .fetchNovelNotificationSubscriptions(
                        notificationType = notificationType.name,
                        lastSubscriptionId = lastSubscriptionId,
                        size = size,
                    ).toDomain()
                Result.success(subscriptions)
            } catch (e: CancellationException) {
                // 취소는 실패가 아니므로 Result로 감싸지 않고 그대로 전파한다
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }

        companion object {
            private const val DEFAULT_LOAD_SIZE = 20
            private const val ADDITIONAL_LOAD_SIZE = 10
        }
    }
