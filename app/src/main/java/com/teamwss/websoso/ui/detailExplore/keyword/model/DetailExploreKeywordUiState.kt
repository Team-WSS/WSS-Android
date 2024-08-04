package com.teamwss.websoso.ui.detailExplore.keyword.model

import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordModel.*

data class DetailExploreKeywordUiState(
    val categories: List<CategoryModel> = emptyList(),
    val loading: Boolean = true,
    val error: Boolean = false,
)