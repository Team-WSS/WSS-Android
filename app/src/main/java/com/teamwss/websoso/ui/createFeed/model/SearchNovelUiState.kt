package com.teamwss.websoso.ui.createFeed.model

import com.teamwss.websoso.ui.normalExplore.model.NormalExploreModel.NovelModel

data class SearchNovelUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val isLoadable: Boolean = true,
    val novelCount: Long = 0,
    val novels: List<NovelModel> = emptyList(),
)
