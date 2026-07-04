package com.into.websoso.core.network.datasource.library.model.response

import com.into.websoso.data.library.model.LibraryKeywordEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserNovelKeywordsResponseDto(
    @SerialName("keywords")
    val keywords: List<KeywordResponseDto>,
) {
    fun toData(): List<LibraryKeywordEntity> = keywords.map(KeywordResponseDto::toData)

    @Serializable
    internal data class KeywordResponseDto(
        @SerialName("keywordId")
        val keywordId: Int,
        @SerialName("keywordName")
        val keywordName: String,
    ) {
        fun toData(): LibraryKeywordEntity =
            LibraryKeywordEntity(
                keywordId = keywordId,
                keywordName = keywordName,
            )
    }
}
