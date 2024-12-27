package com.into.websoso.data.model

import com.into.websoso.data.model.CategoriesEntity.CategoryEntity.KeywordEntity

data class NovelRatingEntity(
    val novelId: Long? = null,
    val novelTitle: String? = null,
    val readStatus: String?,
    val startDate: String?,
    val endDate: String?,
    val userNovelRating: Float,
    val charmPoints: List<String>,
    val userKeywords: List<KeywordEntity>,
)
