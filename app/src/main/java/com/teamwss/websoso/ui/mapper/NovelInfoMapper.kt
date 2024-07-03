package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.NovelInfoEntity
import com.teamwss.websoso.ui.novelDetail.novelInfo.model.KeywordModel
import com.teamwss.websoso.ui.novelDetail.novelInfo.model.NovelInfoUiModel
import com.teamwss.websoso.ui.novelDetail.novelInfo.model.PlatformModel
import com.teamwss.websoso.ui.novelDetail.novelInfo.model.ReadStatus
import com.teamwss.websoso.ui.novelDetail.novelInfo.model.ReviewCountModel
import com.teamwss.websoso.ui.novelDetail.novelInfo.model.UnifiedReviewCountModel

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
