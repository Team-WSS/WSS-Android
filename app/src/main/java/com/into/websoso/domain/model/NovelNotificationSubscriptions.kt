package com.into.websoso.domain.model

data class NovelNotificationSubscriptions(
    val isLoadable: Boolean = true,
    val nextSubscriptionId: Long = DEFAULT_SUBSCRIPTION_ID,
    val subscriptions: List<NovelNotificationSubscription> = emptyList(),
) {
    companion object {
        const val DEFAULT_SUBSCRIPTION_ID = 0L
    }
}
