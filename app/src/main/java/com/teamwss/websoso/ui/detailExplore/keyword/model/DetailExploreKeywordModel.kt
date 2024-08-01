package com.teamwss.websoso.ui.detailExplore.keyword.model

import com.teamwss.websoso.ui.detailExplore.keyword.model.DetailExploreKeywordModel.CategoryModel.KeywordModel

data class DetailExploreKeywordModel(
    val categories: List<CategoryModel> = emptyList(),
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

    companion object {

        fun List<CategoryModel>.findKeywordByName(keywordName: String): KeywordModel? {
            return this.flatMap { it.keywords }
                .find { it.keywordName == keywordName }
        }
    }
}