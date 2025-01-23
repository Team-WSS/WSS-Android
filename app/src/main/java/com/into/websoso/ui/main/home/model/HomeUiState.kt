package com.into.websoso.ui.main.home.model

import com.into.websoso.data.model.PopularFeedsEntity.PopularFeedEntity
import com.into.websoso.data.model.PopularNovelsEntity.PopularNovelEntity
import com.into.websoso.data.model.RecommendedNovelsByUserTasteEntity.RecommendedNovelByUserTasteEntity
import com.into.websoso.data.model.UserInterestFeedsEntity.UserInterestFeedEntity

data class HomeUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val isInterestNovel: Boolean = false,
    val isNotificationUnread: Boolean = false,
    val popularNovels: List<PopularNovelEntity> = listOf(),
    val popularFeeds: List<List<PopularFeedEntity>> = listOf(),
    val userInterestFeeds: List<UserInterestFeedEntity> = listOf(),
    val recommendedNovelsByUserTaste: List<RecommendedNovelByUserTasteEntity> = listOf(),
)
