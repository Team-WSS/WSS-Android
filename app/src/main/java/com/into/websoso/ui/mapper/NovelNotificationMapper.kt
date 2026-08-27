package com.into.websoso.ui.mapper

import com.into.websoso.domain.model.NovelNotificationSubscription
import com.into.websoso.ui.novelNotification.model.NovelNotificationSubscriptionModel

fun NovelNotificationSubscription.toUi(): NovelNotificationSubscriptionModel =
    NovelNotificationSubscriptionModel(
        subscriptionId = subscriptionId,
        novelId = novelId,
        novelTitle = novelTitle,
        novelAuthor = novelAuthor,
        novelImage = novelImage,
        registeredDate = registeredDate,
    )
