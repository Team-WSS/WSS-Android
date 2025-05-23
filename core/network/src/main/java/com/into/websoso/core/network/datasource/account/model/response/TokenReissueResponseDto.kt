package com.into.websoso.core.network.datasource.account.model.response

import com.into.websoso.data.account.model.TokenEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TokenReissueResponseDto(
    @SerialName("Authorization")
    val authorization: String,
    @SerialName("refreshToken")
    val refreshToken: String,
) {
    internal fun toData(): TokenEntity =
        TokenEntity(
            accessToken = authorization,
            refreshToken = refreshToken,
        )
}
