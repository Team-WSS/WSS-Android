package com.into.websoso.ui.main.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.library.LibraryRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.main.library.model.LibraryUiState
import com.into.websoso.ui.mapper.toUi
import com.into.websoso.ui.userStorage.model.SortType
import com.into.websoso.ui.userStorage.model.StorageTab
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val libraryRepository: LibraryRepository,
    ) : ViewModel() {
        private val _uiState: MutableLiveData<LibraryUiState> =
            MutableLiveData(LibraryUiState())
        val uiState: LiveData<LibraryUiState> get() = _uiState

        private val _isRatingChanged = MutableLiveData<Boolean>()
        val isRatingChanged: LiveData<Boolean> get() = _isRatingChanged

        fun updateRatingChanged() {
            _isRatingChanged.value = true
        }

        fun updateReadStatus(
            readStatus: String,
            forceLoad: Boolean = false,
        ) {
            when {
                _uiState.value?.loading == true -> return
                !forceLoad && _uiState.value?.isLoadable == false -> return
            }

            _uiState.value = uiState.value?.copy(readStatus = readStatus)

            updateUserNovelsLibrary(
                readStatus,
                _uiState.value?.sortType ?: SortType.NEWEST,
            )
        }

        fun updateSortType(sortType: SortType) {
            if (_uiState.value?.loading == true) return

            _uiState.value = uiState.value?.copy(sortType = sortType)

            updateUserNovelsLibrary(
                _uiState.value?.readStatus ?: StorageTab.INTEREST.readStatus,
                sortType,
            )
        }

        private fun updateUserNovelsLibrary(
            readStatus: String,
            sortType: SortType,
        ) {
            _uiState.value = uiState.value?.copy(loading = true)

            viewModelScope.launch {
                libraryRepository
                    .fetchUserStorage(
                        userId = userRepository.fetchUserId(),
                        readStatus = readStatus,
                        lastUserNovelId = uiState.value?.lastUserNovelId ?: 0L,
                        size = STORAGE_NOVEL_SIZE,
                        sortType = sortType.titleEn,
                    ).onSuccess { response ->
                        val isLoadable = response.isLoadable && response.userNovels.isNotEmpty()

                        _uiState.value = uiState.value?.copy(
                            loading = false,
                            userNovelCount = response.userNovelCount,
                            userNovelRating = response.userNovelRating,
                            storageNovels = response.userNovels.map { it.toUi() },
                            lastUserNovelId = response.userNovels.lastOrNull()?.userNovelId ?: 0L,
                            isLoadable = isLoadable,
                        )
                    }.onFailure {
                        _uiState.value = uiState.value?.copy(
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
