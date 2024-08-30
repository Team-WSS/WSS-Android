package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.AvatarEntity
import com.teamwss.websoso.data.remote.response.AvatarsResponseDto.AvatarResponseDto

fun AvatarResponseDto.toData(): AvatarEntity {
    return AvatarEntity(
        avatarId = avatarId,
        avatarName = avatarName,
        avatarLine = avatarLine,
        avatarImage = avatarImage,
        isRepresentative = isRepresentative,
    )
}
