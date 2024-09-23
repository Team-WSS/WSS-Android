package com.teamwss.websoso.ui.storage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.storage.model.SortType
import com.teamwss.websoso.ui.storage.model.StorageTab
import com.teamwss.websoso.ui.storage.model.StorageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StorageViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState: MutableLiveData<StorageUiState> = MutableLiveData(StorageUiState())
    val uiState: LiveData<StorageUiState> get() = _uiState

    init {
        updateReadStatus(
            StorageTab.INTEREST.readStatus,
            forceLoad = true,
        )
    }

    fun updateReadStatus(
        readStatus: String,
        forceLoad: Boolean = false,
    ) {

        if (_uiState.value?.loading == true || (!forceLoad && _uiState.value?.isLoadable == false)) {
            return
        }

        _uiState.value = _uiState.value?.copy(readStatus = readStatus)

        updateUserNovelsStorage(
            readStatus,
            _uiState.value?.sortType ?: SortType.NEWEST,
        )
    }

    fun updateSortType(sortType: SortType) {
        if (_uiState.value?.loading == true) return

        _uiState.value = _uiState.value?.copy(sortType = sortType)

        updateUserNovelsStorage(
            _uiState.value?.readStatus ?: StorageTab.INTEREST.readStatus,
            sortType,
        )
    }

    private fun updateUserNovelsStorage(
        readStatus: String,
        sortType: SortType,
    ) {

        _uiState.value = _uiState.value?.copy(loading = true)

        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserStorage(
                    userId = 2L,
                    readStatus = readStatus,
                    lastUserNovelId = _uiState.value?.lastUserNovelId ?: 0L,
                    size = 20,
                    sortType = sortType.apiValue,
                )
            }.onSuccess { response ->
                val storageResponse = response
                val canLoadMore =
                    storageResponse.isLoadable && storageResponse.userNovels.isNotEmpty()

                _uiState.value = _uiState.value?.copy(
                    loading = false,
                    userNovelCount = storageResponse.userNovelCount,
                    userNovelRating = storageResponse.userNovelRating,
                    storageNovels = storageResponse.userNovels,
                    lastUserNovelId = storageResponse.userNovels.lastOrNull()?.userNovelId ?: 0L,
                    isLoadable = canLoadMore,
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }
}
