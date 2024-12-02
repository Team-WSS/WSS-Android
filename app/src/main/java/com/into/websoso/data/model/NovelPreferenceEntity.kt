package com.into.websoso.data.model

data class NovelPreferenceEntity(
    val attractivePoints: List<String>,
    val keywords: List<KeywordEntity>,
) {
    data class KeywordEntity(
        val keywordName: String,
        val keywordCount: Int,
    )
}
