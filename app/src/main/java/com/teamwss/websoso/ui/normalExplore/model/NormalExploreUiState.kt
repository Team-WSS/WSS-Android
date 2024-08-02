package com.teamwss.websoso.ui.normalExplore.model

import com.teamwss.websoso.data.model.ExploreResultEntity

data class NormalExploreUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val novels: List<ExploreResultEntity.NovelEntity> = emptyList(),
)