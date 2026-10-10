package com.into.websoso.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NovelNotificationSubscriptionsDeleteRequestDto(
    @SerialName("notificationType")
    val notificationType: String,
    @SerialName("novelIds")
    val novelIds: List<Long>,
)
