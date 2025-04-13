package com.into.websoso.ui.userStorage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.mapper.toUi
import com.into.websoso.ui.userStorage.model.SortType
import com.into.websoso.ui.userStorage.model.StorageTab
import com.into.websoso.ui.userStorage.model.UserStorageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserStorageViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) : ViewModel() {
        private val _uiState = MutableLiveData(UserStorageUiState())
        val uiState: LiveData<UserStorageUiState> get() = _uiState

        private var userId: Long = 0L

        fun updateUserStorage(
            receivedUserId: Long,
            readStatus: String = StorageTab.INTEREST.readStatus,
        ) {
            this.userId = receivedUserId
            updateReadStatus(readStatus, forceLoad = true)
        }

        fun updateReadStatus(
            readStatus: String,
            forceLoad: Boolean = false,
        ) {
            if (_uiState.value?.loading == true) return
            if (!forceLoad && _uiState.value?.isLoadable == false) return

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
                        userId = userId,
                        readStatus = readStatus,
                        lastUserNovelId = _uiState.value?.lastUserNovelId ?: 0L,
                        size = STORAGE_NOVEL_SIZE,
                        sortType = sortType.titleEn,
                    )
                }.onSuccess { response ->
                    val isLoadable = response.isLoadable && response.userNovels.isNotEmpty()

                    _uiState.value = _uiState.value?.copy(
                        loading = false,
                        userNovelCount = response.userNovelCount,
                        userNovelRating = response.userNovelRating,
                        storageNovels = response.userNovels.map { it.toUi() },
                        lastUserNovelId = response.userNovels.lastOrNull()?.userNovelId ?: 0L,
                        isLoadable = isLoadable,
                    )
                }.onFailure {
                    _uiState.value = _uiState.value?.copy(
                        loading = false,
                        error = true,
                    )
                }
            }
        }

        companion object {
            const val STORAGE_NOVEL_SIZE = 100
        }
    }
