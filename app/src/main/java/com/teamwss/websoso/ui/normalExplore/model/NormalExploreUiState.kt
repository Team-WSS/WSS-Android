package com.teamwss.websoso.ui.normalExplore.model

import com.teamwss.websoso.data.model.NormalExploreEntity

data class NormalExploreUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val novelCount: Long = 0,
    val novels: List<NormalExploreEntity.NovelEntity> = emptyList(),
)