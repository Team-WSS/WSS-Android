package com.into.websoso.domain.mapper

import com.into.websoso.data.model.NovelNotificationSettingEntity
import com.into.websoso.data.model.NovelNotificationSubscriptionEntity
import com.into.websoso.data.model.NovelNotificationSubscriptionsEntity
import com.into.websoso.domain.model.NovelNotificationSetting
import com.into.websoso.domain.model.NovelNotificationSubscription
import com.into.websoso.domain.model.NovelNotificationSubscriptions
import com.into.websoso.domain.model.NovelNotificationSubscriptions.Companion.DEFAULT_SUBSCRIPTION_ID

fun NovelNotificationSettingEntity.toDomain(): NovelNotificationSetting =
    NovelNotificationSetting(
        isCompletionNotificationEnabled = isCompletionNotificationEnabled,
        isHiatusReturnNotificationEnabled = isHiatusReturnNotificationEnabled,
    )

fun NovelNotificationSubscriptionsEntity.toDomain(): NovelNotificationSubscriptions =
    NovelNotificationSubscriptions(
        isLoadable = isLoadable,
        nextSubscriptionId = nextSubscriptionId ?: DEFAULT_SUBSCRIPTION_ID,
        subscriptions = subscriptions.map { it.toDomain() },
    )

fun NovelNotificationSubscriptionEntity.toDomain(): NovelNotificationSubscription =
    NovelNotificationSubscription(
        subscriptionId = subscriptionId,
        novelId = novelId,
        novelTitle = novelTitle,
        novelAuthor = novelAuthor,
        novelImage = novelImage,
        registeredDate = registeredDate,
    )
