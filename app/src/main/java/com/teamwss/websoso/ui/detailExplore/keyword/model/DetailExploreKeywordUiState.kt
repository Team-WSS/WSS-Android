package com.teamwss.websoso.ui.detailExplore.keyword.model

data class DetailExploreKeywordUiState(
    val keywordModel: DetailExploreKeywordModel = DetailExploreKeywordModel(emptyList()),
    val loading: Boolean = true,
    val error: Boolean = false,
)