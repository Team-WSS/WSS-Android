package com.into.websoso.core.network.datasource.account.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TokenReissueRequestDto(
    @SerialName("refreshToken")
    val refreshToken: String,
)
