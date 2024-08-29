package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.domain.model.Profile
import com.teamwss.websoso.ui.profileEdit.model.ProfileModel

fun ProfileModel.toDomain() = Profile(
    avatarId = avatarId,
    nickname = nicknameModel.nickname,
    introduction = introduction,
    genrePreferences = genrePreferences.map { it.name },
)
