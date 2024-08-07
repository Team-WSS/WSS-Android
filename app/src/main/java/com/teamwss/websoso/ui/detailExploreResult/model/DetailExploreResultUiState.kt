package com.teamwss.websoso.ui.detailExploreResult.model

import com.teamwss.websoso.data.model.ExploreResultEntity

data class DetailExploreResultUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val novels: List<ExploreResultEntity.NovelEntity> = emptyList(),
)