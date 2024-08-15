package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.UserEmailEntity
import com.teamwss.websoso.data.model.UserNovelStatsEntity
import com.teamwss.websoso.data.remote.response.UserEmailResponseDto
import com.teamwss.websoso.data.remote.response.UserNovelStatsResponseDto

fun UserEmailResponseDto.toData(): UserEmailEntity {
    return UserEmailEntity(
        email = email,
    )
}

fun UserNovelStatsResponseDto.toData(): UserNovelStatsEntity {
    return UserNovelStatsEntity(
        interestNovelCount = interestNovelCount,
        watchingNovelCount = watchingNovelCount,
        watchedNovelCount = watchedNovelCount,
        quitNovelCount = quitNovelCount,
    )
}