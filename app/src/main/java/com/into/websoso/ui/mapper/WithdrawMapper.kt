package com.into.websoso.ui.mapper

import com.into.websoso.data.model.UserNovelStatsEntity
import com.into.websoso.ui.withdraw.first.model.UserNovelStatsModel

fun UserNovelStatsEntity.toUi(): UserNovelStatsModel =
    UserNovelStatsModel(
        interestNovelCount = interestNovelCount,
        watchingNovelCount = watchingNovelCount,
        watchedNovelCount = watchedNovelCount,
        quitNovelCount = quitNovelCount,
    )
