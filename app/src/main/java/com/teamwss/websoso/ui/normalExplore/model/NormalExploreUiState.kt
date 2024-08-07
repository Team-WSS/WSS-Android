package com.teamwss.websoso.ui.normalExplore.model

import com.teamwss.websoso.data.model.NormalExploreEntity

data class NormalExploreUiState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val novels: List<NormalExploreEntity.NovelEntity> = emptyList(),
)