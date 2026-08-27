package com.into.websoso.data.model

data class NovelNotificationSubscriptionEntity(
    val subscriptionId: Long,
    val novelId: Long,
    val novelTitle: String,
    val novelAuthor: String,
    val novelImage: String,
    val registeredDate: String,
)
