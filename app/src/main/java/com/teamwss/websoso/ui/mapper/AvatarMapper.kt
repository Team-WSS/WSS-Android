package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.AvatarEntity
import com.teamwss.websoso.ui.profileEdit.model.AvatarModel

fun AvatarEntity.toUi() = AvatarModel(
    avatarId = avatarId,
    avatarName = avatarName,
    avatarLine = avatarLine,
    avatarThumbnail = avatarImage,
    isRepresentative = isRepresentative,
)
