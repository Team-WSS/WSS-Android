package com.into.websoso.ui.main.library.model

import android.content.Context
import com.into.websoso.R
import com.into.websoso.ui.userStorage.model.SortType
import com.into.websoso.ui.userStorage.model.StorageTab
import com.into.websoso.ui.userStorage.model.UserStorageModel

data class LibraryUiState(
    val loading: Boolean = false,
    val isLoadable: Boolean = true,
    val error: Boolean = false,
    val userNovelCount: Long = 0L,
    val userNovelRating: Float = 0f,
    val storageNovels: List<UserStorageModel.StorageNovelModel> = emptyList(),
    val lastUserNovelId: Long = 0L,
    val readStatus: String = StorageTab.INTEREST.readStatus,
    val sortType: SortType = SortType.NEWEST,
) {
    fun getUserNovelCountText(context: Context): String = context.getString(R.string.storage_novel_count, userNovelCount)
}
