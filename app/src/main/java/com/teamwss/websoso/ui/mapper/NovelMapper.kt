package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.NovelDetailEntity
import com.teamwss.websoso.data.model.NovelInfoEntity
import com.teamwss.websoso.domain.model.ExploreResult
import com.teamwss.websoso.domain.model.ExploreResult.Novel
import com.teamwss.websoso.ui.normalExplore.model.NormalExploreModel
import com.teamwss.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel
import com.teamwss.websoso.ui.novelDetail.model.NovelDetailModel
import com.teamwss.websoso.ui.novelInfo.model.KeywordModel
import com.teamwss.websoso.ui.novelInfo.model.NovelInfoUiModel
import com.teamwss.websoso.ui.novelInfo.model.PlatformModel
import com.teamwss.websoso.ui.novelInfo.model.ReviewCountModel
import com.teamwss.websoso.ui.novelInfo.model.UnifiedReviewCountModel
import com.teamwss.websoso.ui.novelRating.model.NovelRatingModel.Companion.toCharmPoint
import com.teamwss.websoso.ui.novelRating.model.ReadStatus

fun NovelDetailEntity.toUi(novelId: Long): NovelDetailModel {
    return NovelDetailModel(
        userNovel = NovelDetailModel.UserNovelModel(
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
        novel = NovelDetailModel.NovelModel(
            novelId = novelId,
            novelTitle = novel.novelTitle,
            novelImage = novel.novelImage,
            novelGenres = novel.novelGenres,
            novelGenreImage = novel.novelGenreImage,
            isNovelCompleted = novel.isNovelCompleted,
            author = novel.author,
        ),
        userRating = NovelDetailModel.UserRatingModel(
            interestCount = userRating.interestCount,
            novelRating = userRating.novelRating,
            novelRatingCount = userRating.novelRatingCount,
            feedCount = userRating.feedCount,
        ),
    )
}

fun NovelInfoEntity.toUi() = NovelInfoUiModel(
    novelDescription = novelDescription,
    attractivePoints = attractivePoints.map { it.toCharmPoint() },
    unifiedReviewCount = reviewCount.toUi(),
    isAttractivePointsExist = attractivePoints.isNotEmpty(),
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

fun ExploreResult.toUi(): NormalExploreModel {
    return NormalExploreModel(
        resultCount = resultCount,
        isLoadable = isLoadable,
        novelModels = novels.map { it.toUi() },
    )
}

fun Novel.toUi(): NovelModel {
    return NovelModel(
        id = id,
        title = title,
        author = author,
        image = image,
        interestedCount = interestedCount,
        rating = rating,
        ratingCount = ratingCount,
    )
}
