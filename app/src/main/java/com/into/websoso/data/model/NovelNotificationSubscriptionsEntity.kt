package com.into.websoso.data.model

data class NovelNotificationSubscriptionsEntity(
    val isLoadable: Boolean,
    val nextSubscriptionId: Long?,
    val subscriptions: List<NovelNotificationSubscriptionEntity>,
)
