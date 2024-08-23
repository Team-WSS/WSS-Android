package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.UserNovelStatsEntity
import com.teamwss.websoso.ui.withdraw.first.model.UserNovelStatsModel

fun UserNovelStatsEntity.toUi(): UserNovelStatsModel {
    return UserNovelStatsModel(
        interestNovelCount = interestNovelCount,
        watchingNovelCount = watchingNovelCount,
        watchedNovelCount = watchedNovelCount,
        quitNovelCount = quitNovelCount,
    )
}