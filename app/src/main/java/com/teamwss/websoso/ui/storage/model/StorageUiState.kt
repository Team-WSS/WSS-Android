package com.teamwss.websoso.ui.storage.model

import com.teamwss.websoso.data.model.StorageEntity

data class StorageUiState(
    val loading: Boolean = false,
    val isLoadable:Boolean = true,
    val error: Boolean = false,
    val userNovelCount: Long = 0L,
    val userNovelRating: Float = 0f,
    val storageNovels: List<StorageEntity.StorageNovelEntity> = emptyList(),
    val lastUserNovelId: Long = 0L,
    val readStatus: String = StorageTab.INTEREST.readStatus,
    val sortType: SortType = SortType.NEWEST,
)