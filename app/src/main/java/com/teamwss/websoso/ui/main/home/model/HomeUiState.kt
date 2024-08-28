package com.teamwss.websoso.ui.main.home.model

import com.teamwss.websoso.data.model.PopularFeedsEntity.PopularFeedEntity
import com.teamwss.websoso.data.model.PopularNovelsEntity.PopularNovelEntity
import com.teamwss.websoso.data.model.RecommendedNovelsByUserTasteEntity.RecommendedNovelByUserTasteEntity
import com.teamwss.websoso.data.model.UserInterestFeedsEntity.UserInterestFeedEntity

data class HomeUiState(
    val isLogin: Boolean = false,
    val nickname: String? = "웹소소",
    val loading: Boolean = true,
    val error: Boolean = false,
    val popularNovels: List<PopularNovelEntity> = listOf(),
    val popularFeeds: List<List<PopularFeedEntity>> = listOf(),
    val userInterestFeeds: List<UserInterestFeedEntity> = listOf(),
    val recommendedNovelsByUserTaste: List<RecommendedNovelByUserTasteEntity> = listOf(),
)