package com.into.websoso.ui.detailExploreResult.model

import com.into.websoso.common.ui.model.CategoriesModel.CategoryModel
import com.into.websoso.ui.normalExplore.model.NormalExploreModel

data class DetailExploreResultUiState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val isLoadable: Boolean = false,
    val novels: List<NormalExploreModel.NovelModel> = emptyList(),
    val novelCount: Long = 0,
    val categories: List<CategoryModel> = emptyList(),
    val searchResultKeywords: List<CategoryModel.KeywordModel> = emptyList(),
    val isSearchKeywordProceeding: Boolean = false,
    val isInitialSearchKeyword: Boolean = true,
    val isSearchResultKeywordsEmpty: Boolean = false,
)