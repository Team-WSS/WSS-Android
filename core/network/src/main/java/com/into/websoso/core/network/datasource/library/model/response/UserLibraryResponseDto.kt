package com.into.websoso.core.network.datasource.library.model.response

import com.into.websoso.data.library.model.UserStorageEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserLibraryResponseDto(
    @SerialName("userNovelCount")
    val userNovelCount: Long,
    @SerialName("userNovelRating")
    val userNovelRating: Float,
    @SerialName("isLoadable")
    val isLoadable: Boolean,
    @SerialName("userNovels")
    val userNovels: List<NovelResponseDto>,
) {
    fun toData(): UserStorageEntity =
        UserStorageEntity(
            userNovelCount = userNovelCount,
            userNovelRating = userNovelRating,
            isLoadable = isLoadable,
            userNovels = userNovels.map(NovelResponseDto::toData),
        )
}
