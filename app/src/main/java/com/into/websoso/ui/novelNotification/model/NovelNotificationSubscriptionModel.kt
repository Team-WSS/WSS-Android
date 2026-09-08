package com.into.websoso.ui.novelNotification.model

data class NovelNotificationSubscriptionModel(
    val subscriptionId: Long,
    val novelId: Long,
    val novelTitle: String,
    val novelAuthor: String,
    val novelImage: String,
    val registeredDate: String,
)
