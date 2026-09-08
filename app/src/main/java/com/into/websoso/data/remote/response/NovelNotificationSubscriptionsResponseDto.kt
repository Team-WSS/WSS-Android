package com.into.websoso.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelNotificationSubscriptionsResponseDto(
    @SerialName("isLoadable")
    val isLoadable: Boolean,
    @SerialName("nextSubscriptionId")
    val nextSubscriptionId: Long? = null,
    @SerialName("subscriptions")
    val subscriptions: List<NovelNotificationSubscriptionResponseDto>,
) {
    @Serializable
    data class NovelNotificationSubscriptionResponseDto(
        @SerialName("subscriptionId")
        val subscriptionId: Long,
        @SerialName("novelId")
        val novelId: Long,
        @SerialName("novelTitle")
        val novelTitle: String,
        @SerialName("novelAuthor")
        val novelAuthor: String,
        @SerialName("novelImage")
        val novelImage: String,
        @SerialName("registeredDate")
        val registeredDate: String,
    )
}
