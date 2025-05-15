package com.into.websoso.core.network.datasource.account.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class KakaoLogoutRequestDto(
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("deviceIdentifier")
    val deviceIdentifier: String,
)
