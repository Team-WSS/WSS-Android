package com.into.websoso.core.network.datasource.account

import com.into.websoso.data.account.AccountEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class KakaoLoginResponseDto(
    @SerialName("Authorization")
    val authorization: String,
    @SerialName("refreshToken")
    val refreshToken: String,
    @SerialName("isRegister")
    val isRegister: Boolean,
) {
    internal fun toData(): AccountEntity =
        AccountEntity(
            accessToken = authorization,
            refreshToken = refreshToken,
            isRegister = isRegister,
        )
}
