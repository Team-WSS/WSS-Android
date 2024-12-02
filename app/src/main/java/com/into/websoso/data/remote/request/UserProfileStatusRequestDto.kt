package com.into.websoso.data.remote.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileStatusRequestDto(
    @SerialName("isProfilePublic")
    val isProfilePublic: Boolean,
)
