package com.teamwss.websoso.ui.normalExplore.model

import com.teamwss.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel

data class NormalExploreUiState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val isLoadable: Boolean = true,
    val novels: List<NovelModel> = emptyList(),
)