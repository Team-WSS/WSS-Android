package com.teamwss.websoso.ui.detailExplore.keyword.model

data class DetailExploreKeywordModel(
    val categories: List<CategoryModel>,
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
            val keywordId: Long,
            val keywordName: String,
            val isSelected: Boolean = false,
        )
    }
}