package com.teamwss.websoso.ui.detailExplore.keyword.model

import com.teamwss.websoso.common.ui.model.CategoriesModel.*

data class DetailExploreKeywordUiState(
    val categories: List<CategoryModel> = emptyList(),
    val loading: Boolean = true,
    val error: Boolean = false,
)