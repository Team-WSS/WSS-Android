package com.teamwss.websoso.data.mapper

import com.teamwss.websoso.data.model.NovelDetailEntity
import com.teamwss.websoso.data.remote.response.NovelDetailResponseDto

fun NovelDetailResponseDto.toData(): NovelDetailEntity {
    return NovelDetailEntity(
        userNovel =
        NovelDetailEntity.UserNovelEntity(
            userNovelId = userNovelId,
            readStatus = readStatus,
            startDate = startDate,
            endDate = endDate,
            isUserNovelInterest = isUserNovelInterest,
            userNovelRating = userNovelRating,
        ),
        novel =
        NovelDetailEntity.NovelEntity(
            novelTitle = novelTitle,
            novelImage = novelImage,
            novelGenres = novelGenres.split(","),
            novelGenreImage = novelGenreImage,
            isNovelCompleted = isNovelCompleted,
            author = author,
        ),
        userRating =
        NovelDetailEntity.UserRatingEntity(
            interestCount = interestCount,
            novelRating = novelRating,
            novelRatingCount = novelRatingCount,
            feedCount = feedCount,
        ),
    )
}
