package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.UserEmailEntity
import com.teamwss.websoso.data.remote.response.UserEmailResponseDto

fun UserEmailResponseDto.toData(): UserEmailEntity {
    return UserEmailEntity(
        email = email,
    )
}