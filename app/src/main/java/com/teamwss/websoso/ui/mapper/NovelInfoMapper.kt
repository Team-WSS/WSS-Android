package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.NovelInfoEntity
import com.teamwss.websoso.ui.novelDetail.novelInfo.model.KeywordModel
import com.teamwss.websoso.ui.novelDetail.novelInfo.model.NovelInfoUiModel
import com.teamwss.websoso.ui.novelDetail.novelInfo.model.PlatformModel

fun NovelInfoEntity.toUi() = NovelInfoUiModel(
    novelDescription = novelDescription,
    attractivePoints = attractivePoints,
    watchingCount = watchingCount,
    watchedCount = watchedCount,
    quitCount = quitCount,
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
