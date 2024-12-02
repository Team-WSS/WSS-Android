package com.into.websoso.ui.normalExplore.model

import com.into.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel

data class NormalExploreUiState(
    val loading: Boolean = false,
    val error: Boolean = false,
    val isLoadable: Boolean = true,
    val novelCount: Long = 0,
    val novels: List<NovelModel> = emptyList(),
)