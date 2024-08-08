package com.teamwss.websoso.ui.common.model

import com.teamwss.websoso.ui.common.model.KeywordsModel.CategoryModel.KeywordModel

data class KeywordsModel(
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