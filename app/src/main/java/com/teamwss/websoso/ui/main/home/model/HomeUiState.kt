package com.teamwss.websoso.ui.main.home.model

import com.teamwss.websoso.data.model.PopularNovelsEntity.PopularNovelEntity

data class HomeUiState(
    val isLogin: Boolean = false,
    val loading: Boolean = true,
    val error: Boolean = false,
    val popularNovels: List<PopularNovelEntity> = listOf(),
)