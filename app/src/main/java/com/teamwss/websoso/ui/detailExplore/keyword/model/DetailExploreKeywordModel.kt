package com.teamwss.websoso.ui.detailExplore.keyword.model

data class DetailExploreKeywordModel(
    val categories: List<CategoryModel> = emptyList(),
    val previousSelectedKeywords: List<CategoryModel.KeywordModel> =
        categories.flatMap {
            it.keywords.filter { keyword ->
                keyword.isSelected
            }
        },
    val currentSelectedKeywords: List<CategoryModel.KeywordModel> = emptyList(),
) {
    data class CategoryModel(
        val categoryName: String,
        val categoryImage: String,
        val keywords: List<KeywordModel>,
    ) {
        data class KeywordModel(
            val keywordId: Int,
            val keywordName: String,
            val isSelected: Boolean = false,
        )
    }
}