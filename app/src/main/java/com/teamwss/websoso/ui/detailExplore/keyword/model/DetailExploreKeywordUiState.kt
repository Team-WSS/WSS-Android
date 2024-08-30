package com.teamwss.websoso.ui.detailExplore.keyword.model

import com.teamwss.websoso.common.ui.model.CategoriesModel.CategoryModel
import com.teamwss.websoso.common.ui.model.CategoriesModel.CategoryModel.KeywordModel

data class DetailExploreKeywordUiState(
    val categories: List<CategoryModel> = emptyList(),
    val loading: Boolean = true,
    val error: Boolean = false,
    val searchResultKeywords: List<KeywordModel> = emptyList(),
    val isSearchKeywordProceeding: Boolean = false,
    val isInitialSearchKeyword: Boolean = true,
    val isSearchResultKeywordsEmpty: Boolean = false,
)
