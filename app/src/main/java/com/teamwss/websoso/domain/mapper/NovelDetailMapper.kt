package com.teamwss.websoso.domain.mapper

import com.teamwss.websoso.data.model.NovelDetailEntity
import com.teamwss.websoso.domain.model.NovelDetail

fun NovelDetailEntity.toDomain(): NovelDetail {
    return NovelDetail(
        userNovel =
            NovelDetail.UserNovel(
                userNovelId = userNovel.userNovelId,
                readStatus = userNovel.readStatus,
                startDate = userNovel.startDate,
                endDate = userNovel.endDate,
                isUserNovelInterest = userNovel.isUserNovelInterest,
                userNovelRating = userNovel.userNovelRating,
            ),
        novel =
            NovelDetail.Novel(
                novelTitle = novel.novelTitle,
                novelImage = novel.novelImage,
                novelGenres = novel.novelGenres,
                novelGenreImage = novel.novelGenreImage,
                isNovelCompleted = novel.isNovelCompleted,
                author = novel.author,
            ),
        userRating =
            NovelDetail.UserRating(
                interestCount = userRating.interestCount,
                novelRating = userRating.novelRating,
                novelRatingCount = userRating.novelRatingCount,
                feedCount = userRating.feedCount,
            ),
    )
}
