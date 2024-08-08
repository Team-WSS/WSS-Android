package com.teamwss.websoso.data.model

data class CategoriesEntity(
    val categories: List<CategoryEntity>,
) {
    data class CategoryEntity(
        val categoryName: String,
        val categoryImage: String,
        val keywords: List<KeywordEntity>,
    ) {
        data class KeywordEntity(
            val keywordId: Int,
            val keywordName: String,
        )
    }
}