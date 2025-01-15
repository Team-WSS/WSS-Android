package com.into.websoso.ui.mapper

import com.into.websoso.data.model.NovelDetailEntity
import com.into.websoso.data.model.NovelInfoEntity
import com.into.websoso.data.model.UserStorageEntity
import com.into.websoso.data.model.UserStorageEntity.StorageNovelEntity
import com.into.websoso.domain.model.ExploreResult
import com.into.websoso.domain.model.ExploreResult.Novel
import com.into.websoso.ui.normalExplore.model.NormalExploreModel
import com.into.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel
import com.into.websoso.ui.novelDetail.model.NovelDetailModel
import com.into.websoso.ui.novelInfo.model.KeywordModel
import com.into.websoso.ui.novelInfo.model.NovelInfoUiModel
import com.into.websoso.ui.novelInfo.model.PlatformModel
import com.into.websoso.ui.novelInfo.model.ReviewCountModel
import com.into.websoso.ui.novelInfo.model.UnifiedReviewCountModel
import com.into.websoso.ui.novelRating.model.NovelRatingModel.Companion.toCharmPoint
import com.into.websoso.ui.novelRating.model.ReadStatus
import com.into.websoso.ui.userStorage.model.UserStorageModel
import com.into.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

fun NovelDetailEntity.toUi(novelId: Long): NovelDetailModel {
    return NovelDetailModel(
        userNovel = NovelDetailModel.UserNovelModel(
            userNovelId = userNovel.userNovelId,
            readStatus = ReadStatus.fromString(userNovel.readStatus),
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

fun UserStorageEntity.toUi(): UserStorageModel {
    return UserStorageModel(
        isLoadable = isLoadable,
        userNovelCount = userNovelCount,
        userNovelRating = userNovelRating,
        userNovels = userNovels.map { it.toUi() },
    )
}

fun StorageNovelEntity.toUi(): StorageNovelModel {
    return StorageNovelModel(
        author = author,
        novelId = novelId,
        novelImage = novelImage,
        title = title,
        userNovelId = userNovelId,
        novelRating = novelRating,
    )
}
