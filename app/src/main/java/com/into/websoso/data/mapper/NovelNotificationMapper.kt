package com.into.websoso.data.mapper

import com.into.websoso.data.model.NovelNotificationSettingEntity
import com.into.websoso.data.model.NovelNotificationSubscriptionEntity
import com.into.websoso.data.model.NovelNotificationSubscriptionsEntity
import com.into.websoso.data.remote.response.NovelNotificationSettingResponseDto
import com.into.websoso.data.remote.response.NovelNotificationSubscriptionsResponseDto

fun NovelNotificationSettingResponseDto.toData(): NovelNotificationSettingEntity =
    NovelNotificationSettingEntity(
        isCompletionNotificationEnabled = isCompletionNotificationEnabled,
        isHiatusReturnNotificationEnabled = isHiatusReturnNotificationEnabled,
    )

fun NovelNotificationSubscriptionsResponseDto.toData(): NovelNotificationSubscriptionsEntity =
    NovelNotificationSubscriptionsEntity(
        isLoadable = isLoadable,
        nextSubscriptionId = nextSubscriptionId,
        subscriptions = subscriptions.map {
            NovelNotificationSubscriptionEntity(
                subscriptionId = it.subscriptionId,
                novelId = it.novelId,
                novelTitle = it.novelTitle,
                novelAuthor = it.novelAuthor,
                novelImage = it.novelImage,
                registeredDate = it.registeredDate,
            )
        },
    )
