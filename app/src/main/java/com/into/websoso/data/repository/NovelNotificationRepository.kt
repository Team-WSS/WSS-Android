package com.into.websoso.data.repository

import com.into.websoso.data.mapper.toData
import com.into.websoso.data.model.NovelNotificationSettingEntity
import com.into.websoso.data.model.NovelNotificationSubscriptionsEntity
import com.into.websoso.data.remote.api.NovelNotificationApi
import com.into.websoso.data.remote.request.NovelNotificationSettingRequestDto
import com.into.websoso.data.remote.request.NovelNotificationSubscriptionsDeleteRequestDto
import javax.inject.Inject

class NovelNotificationRepository
    @Inject
    constructor(
        private val novelNotificationApi: NovelNotificationApi,
    ) {
        suspend fun fetchNovelNotificationSetting(novelId: Long): NovelNotificationSettingEntity =
            novelNotificationApi.getNovelNotificationSetting(novelId).toData()

        suspend fun saveNovelNotificationSetting(
            novelId: Long,
            isCompletionNotificationEnabled: Boolean,
            isHiatusReturnNotificationEnabled: Boolean,
        ) {
            novelNotificationApi.putNovelNotificationSetting(
                novelId = novelId,
                novelNotificationSettingRequestDto = NovelNotificationSettingRequestDto(
                    isCompletionNotificationEnabled = isCompletionNotificationEnabled,
                    isHiatusReturnNotificationEnabled = isHiatusReturnNotificationEnabled,
                ),
            )
        }

        suspend fun fetchNovelNotificationSubscriptions(
            notificationType: String,
            lastSubscriptionId: Long,
            size: Int,
        ): NovelNotificationSubscriptionsEntity =
            novelNotificationApi
                .getNovelNotificationSubscriptions(
                    notificationType = notificationType,
                    lastSubscriptionId = lastSubscriptionId,
                    size = size,
                ).toData()

        suspend fun deleteNovelNotificationSubscriptions(
            notificationType: String,
            novelIds: List<Long>,
        ) {
            novelNotificationApi.deleteNovelNotificationSubscriptions(
                novelNotificationSubscriptionsDeleteRequestDto = NovelNotificationSubscriptionsDeleteRequestDto(
                    notificationType = notificationType,
                    novelIds = novelIds,
                ),
            )
        }
    }
