package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.NovelInfoEntity
import com.teamwss.websoso.domain.model.NovelDetail
import com.teamwss.websoso.ui.novelDetail.model.NovelDetailModel
import com.teamwss.websoso.ui.novelInfo.model.KeywordModel
import com.teamwss.websoso.ui.novelInfo.model.NovelInfoUiModel
import com.teamwss.websoso.ui.novelInfo.model.PlatformModel
import com.teamwss.websoso.ui.novelInfo.model.ReviewCountModel
import com.teamwss.websoso.ui.novelInfo.model.UnifiedReviewCountModel
import com.teamwss.websoso.ui.novelRating.model.ReadStatus

fun NovelDetail.toUi(): NovelDetailModel {
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
            hasUserNovelInfo = userNovel.hasUserNovelInfo,
        ),
        novel =
        NovelDetailModel.NovelModel(
            novelTitle = novel.novelTitle,
            novelImage = novel.novelImage,
            novelGenres = novel.novelGenres,
            novelGenreImage = novel.novelGenreImage,
            isNovelCompletedText = novel.isNovelCompletedText,
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

fun NovelInfoEntity.toUi() = NovelInfoUiModel(
    novelDescription = novelDescription,
    attractivePoints = attractivePoints,
    unifiedReviewCount = reviewCount.toUi(),
)

fun NovelInfoEntity.PlatformEntity.toUi() = PlatformModel(
    platformName = platformName,
    platformImage = platformImage,
    platformUrl = platformUrl,
    isVisible = true,
)

fun NovelInfoEntity.KeywordEntity.toUi() = KeywordModel(
    keywordName = keywordName,
    keywordCount = keywordCount,
)

fun NovelInfoEntity.ReviewCountEntity.toUi() = UnifiedReviewCountModel(
    watchingCount = ReviewCountModel(ReadStatus.WATCHING, watchingCount),
    watchedCount = ReviewCountModel(ReadStatus.WATCHED, watchedCount),
    quitCount = ReviewCountModel(ReadStatus.QUIT, quitCount),
)
