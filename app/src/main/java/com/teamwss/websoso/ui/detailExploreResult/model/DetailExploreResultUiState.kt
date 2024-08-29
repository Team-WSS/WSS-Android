package com.teamwss.websoso.ui.detailExploreResult.model

import com.teamwss.websoso.common.ui.model.CategoriesModel.CategoryModel
import com.teamwss.websoso.data.model.ExploreResultEntity.NovelEntity

data class DetailExploreResultUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val novels: List<NovelEntity> = emptyList(),
    val categories: List<CategoryModel> = emptyList(),
    val selectedKeywords: List<Int> = emptyList(),
    val isSearchKeywordProceeding: Boolean = false,
    val isInitialSearchKeyword: Boolean = true,
    val isSearchResultKeywordsEmpty: Boolean = false,
)