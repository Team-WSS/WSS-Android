package com.teamwss.websoso.data.model

data class NovelPreferenceEntity(
    val attractivePoint: Array<String>,
    val keywords: Array<KeywordEntity>
){
    data class KeywordEntity(
        val keywordName: String,
        val keywordCount: Int
    )
}