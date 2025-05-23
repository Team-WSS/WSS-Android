package com.into.websoso.core.network.datasource.account.model.response

import com.into.websoso.data.account.model.AccountEntity
import com.into.websoso.data.account.model.TokenEntity
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
            token = TokenEntity(
                accessToken = authorization,
                refreshToken = refreshToken,
            ),
            isRegister = isRegister,
        )
}
