package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.LoginEntity
import com.teamwss.websoso.data.remote.response.KakaoLoginResponseDto

fun KakaoLoginResponseDto.toData(): LoginEntity {
    return LoginEntity(
        authorization = this.authorization,
        refreshToken = this.refreshToken,
        isRegister = this.isRegister
    )
}