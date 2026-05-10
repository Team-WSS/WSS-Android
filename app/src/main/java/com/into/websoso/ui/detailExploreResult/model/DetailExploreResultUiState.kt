package com.into.websoso.ui.detailExploreResult.model

import com.into.websoso.ui.normalExplore.model.NormalExploreModel

data class DetailExploreResultUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val isLoadable: Boolean = false,
    val novels: List<NormalExploreModel.NovelModel> = emptyList(),
    val novelCount: Long = 0,
)
