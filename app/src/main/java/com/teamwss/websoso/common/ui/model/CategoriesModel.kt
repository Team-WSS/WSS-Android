package com.teamwss.websoso.common.ui.model

import com.teamwss.websoso.common.ui.model.CategoriesModel.CategoryModel.KeywordModel

data class CategoriesModel(
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