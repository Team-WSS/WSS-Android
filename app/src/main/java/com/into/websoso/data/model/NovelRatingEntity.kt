package com.into.websoso.data.model

import com.into.websoso.data.model.CategoriesEntity.CategoryEntity.KeywordEntity

data class NovelRatingEntity(
    val userNovelId: Long? = null,
    val novelId: Long? = null,
    val novelTitle: String? = null,
    val novelImage: String? = null,
    val novelRating: Float = 0.0f,
    val readStatus: String?,
    val startDate: String?,
    val endDate: String?,
    val userNovelRating: Float,
    val charmPoints: List<String>,
    val userKeywords: List<KeywordEntity>,
)
