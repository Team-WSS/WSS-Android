package com.teamwss.websoso.ui.detailExplore.keyword.model

data class DetailExploreKeywordUiState(
    val keywordModel: DetailExploreKeywordModel = DetailExploreKeywordModel(),
    val loading: Boolean = true,
    val error: Boolean = false,
)