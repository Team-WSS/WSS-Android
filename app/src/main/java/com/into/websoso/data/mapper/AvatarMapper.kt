package com.into.websoso.data.mapper

import com.into.websoso.data.model.AvatarEntity
import com.into.websoso.data.remote.response.AvatarsResponseDto.AvatarResponseDto

fun AvatarResponseDto.toData(): AvatarEntity {
    return AvatarEntity(
        avatarId = avatarId,
        avatarName = avatarName,
        avatarLine = avatarLine,
        avatarImage = avatarImage,
        isRepresentative = isRepresentative,
    )
}
