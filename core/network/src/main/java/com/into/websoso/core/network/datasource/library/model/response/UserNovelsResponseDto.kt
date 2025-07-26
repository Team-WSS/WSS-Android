package com.into.websoso.core.network.datasource.library.model.response

import com.into.websoso.data.library.model.UserNovelsEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserNovelsResponseDto(
    @SerialName("userNovelCount")
    val userNovelCount: Long,
    @SerialName("isLoadable")
    val isLoadable: Boolean,
    @SerialName("userNovels")
    val userNovels: List<NovelResponseDto>,
) {
    fun toData(): UserNovelsEntity =
        UserNovelsEntity(
            userNovelCount = userNovelCount,
            isLoadable = isLoadable,
            userNovels = userNovels.map(NovelResponseDto::toData),
        )
}
