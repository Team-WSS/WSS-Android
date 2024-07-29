package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.NovelDetailEntity
import com.teamwss.websoso.ui.novelDetail.model.Category
import com.teamwss.websoso.ui.novelDetail.model.NovelDetailModel
import com.teamwss.websoso.ui.novelRating.model.ReadStatus

fun NovelDetailEntity.toUi(novelId: Long): NovelDetailModel {
    return NovelDetailModel(
        userNovel =
        NovelDetailModel.UserNovelModel(
            userNovelId = userNovel.userNovelId,
            readStatus = when (userNovel.readStatus) {
                "WATCHING" -> ReadStatus.WATCHING
                "WATCHED" -> ReadStatus.WATCHED
                "QUIT" -> ReadStatus.QUIT
                else -> null
            },
            startDate = userNovel.startDate,
            endDate = userNovel.endDate,
            isUserNovelInterest = userNovel.isUserNovelInterest,
            userNovelRating = userNovel.userNovelRating,
        ),
        novel =
        NovelDetailModel.NovelModel(
            novelId = novelId,
            novelTitle = novel.novelTitle,
            novelImage = novel.novelImage,
            novelGenres = novel.novelGenres.map { Category.from(it) },
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
