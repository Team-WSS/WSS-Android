package com.into.websoso.ui.userStorage.model

import android.content.Context
import com.into.websoso.core.resource.R.string.storage_novel_count
import com.into.websoso.ui.userStorage.model.UserStorageModel.StorageNovelModel

data class UserStorageUiState(
    val loading: Boolean = false,
    val isLoadable: Boolean = true,
    val error: Boolean = false,
    val userNovelCount: Long = 0L,
    val userNovelRating: Float = 0f,
    val storageNovels: List<StorageNovelModel> = emptyList(),
    val lastUserNovelId: Long = 0L,
    val readStatus: String = StorageTab.INTEREST.readStatus,
    val sortType: SortType = SortType.NEWEST,
) {
    fun getUserNovelCountText(context: Context): String {
        return context.getString(storage_novel_count, userNovelCount)
    }
}
