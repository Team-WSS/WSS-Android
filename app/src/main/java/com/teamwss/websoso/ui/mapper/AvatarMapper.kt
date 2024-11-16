package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.AvatarEntity
import com.teamwss.websoso.ui.profileEdit.model.AvatarModel

fun AvatarEntity.toUi(nickname: String) = AvatarModel(
    avatarId = avatarId,
    avatarName = avatarName,
    avatarLine = avatarLine.format(nickname),
    avatarThumbnail = avatarImage,
    isRepresentative = isRepresentative,
)
