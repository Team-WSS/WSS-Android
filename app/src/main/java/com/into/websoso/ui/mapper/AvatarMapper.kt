package com.into.websoso.ui.mapper

import com.into.websoso.data.model.AvatarEntity
import com.into.websoso.ui.profileEdit.model.AvatarModel

fun AvatarEntity.toUi(nickname: String) = AvatarModel(
    avatarId = avatarId,
    avatarName = avatarName,
    avatarLine = avatarLine.format(nickname),
    avatarThumbnail = avatarImage,
    isRepresentative = isRepresentative,
)
