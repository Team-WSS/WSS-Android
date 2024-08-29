package com.teamwss.websoso.domain.mapper

import com.teamwss.websoso.data.remote.request.UserProfileRequestDto
import com.teamwss.websoso.domain.model.Profile

fun Profile.toData() = UserProfileRequestDto(
    avatarId = avatarId,
    nickname = nickname,
    intro = introduction,
    genrePreferences = genrePreferences,
)
