package com.into.websoso.domain.model

data class NovelNotificationSubscription(
    val subscriptionId: Long,
    val novelId: Long,
    val novelTitle: String,
    val novelAuthor: String,
    val novelImage: String,
    val registeredDate: String,
)
