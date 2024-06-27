package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.domain.model.NovelDetail
import com.teamwss.websoso.ui.novelDetail.model.NovelDetailModel

fun NovelDetail.toUi(): NovelDetailModel {
    return NovelDetailModel(
        userNovel =
            NovelDetailModel.UserNovelModel(
                userNovelId = userNovel.userNovelId,
                readStatus = userNovel.readStatus,
                startDate = userNovel.startDate,
                endDate = userNovel.endDate,
                isUserNovelInterest = userNovel.isUserNovelInterest,
                userNovelRating = userNovel.userNovelRating,
            ),
        novel =
            NovelDetailModel.NovelModel(
                novelTitle = novel.novelTitle,
                novelImage = novel.novelImage,
                novelGenres = novel.novelGenres,
                novelGenreImage = novel.novelGenreImage,
                isNovelCompleted = novel.isNovelCompleted,
                author = novel.author,
            ),
        userRating =
            NovelDetailModel.UserRatingModel(
                interestCount = userRating.interestCount,
                novelRating = userRating.novelRating,
                novelRatingCount = userRating.novelRatingCount,
                feedCount = userRating.feedCount,
            ),
    )
}
