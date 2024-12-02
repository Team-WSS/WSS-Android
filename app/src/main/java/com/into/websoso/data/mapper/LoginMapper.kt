package com.into.websoso.data.mapper

import com.into.websoso.data.model.LoginEntity
import com.into.websoso.data.remote.response.KakaoLoginResponseDto

fun KakaoLoginResponseDto.toData(): LoginEntity {
    return LoginEntity(
        authorization = this.authorization,
        refreshToken = this.refreshToken,
        isRegister = this.isRegister,
    )
}