package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.NovelInfoEntity
import com.teamwss.websoso.data.remote.response.NovelInfoResponseDto

fun NovelInfoResponseDto.toData(): NovelInfoEntity {
    return NovelInfoEntity(
        novelDescription = novelDescription,
        platforms = platforms.map {
            NovelInfoEntity.PlatformEntity(
                platformName = it.platformName,
                platformImage = it.platformImage,
                platformUrl = it.platformUrl,
            )
        },
        attractivePoints = attractivePoints,
        keywords = keywords.map {
            NovelInfoEntity.KeywordEntity(
                keywordName = it.keywordName,
                keywordCount = it.keywordCount,
            )
        },
        watchingCount = watchingCount,
        watchedCount = watchedCount,
        quitCount = quitCount,
    )
}