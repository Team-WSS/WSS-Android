package com.into.websoso.ui.userStorage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.mapper.toUi
import com.into.websoso.ui.userStorage.UserStorageActivity.Companion.SOURCE_MY_LIBRARY
import com.into.websoso.ui.userStorage.model.SortType
import com.into.websoso.ui.userStorage.model.StorageTab
import com.into.websoso.ui.userStorage.model.UserStorageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserStorageViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState: MutableLiveData<UserStorageUiState> =
        MutableLiveData(UserStorageUiState())
    val uiState: LiveData<UserStorageUiState> get() = _uiState

    private val _isRatingChanged = MutableLiveData<Boolean>()
    val isRatingChanged: LiveData<Boolean> get() = _isRatingChanged

    private var userId: Long = DEFAULT_USER_ID
    private var source: String = SOURCE_MY_LIBRARY

    fun updateUserStorage(
        source: String,
        receivedUserId: Long,
    ) {
        this.source = source
        this.userId = receivedUserId

        updateReadStatus(StorageTab.INTEREST.readStatus, forceLoad = true)
    }

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

        updateUserNovelsStorage(
            readStatus,
            _uiState.value?.sortType ?: SortType.NEWEST,
        )
    }

    fun updateSortType(sortType: SortType) {
        if (_uiState.value?.loading == true) return

        _uiState.value = uiState.value?.copy(sortType = sortType)

        updateUserNovelsStorage(
            _uiState.value?.readStatus ?: StorageTab.INTEREST.readStatus,
            sortType,
        )
    }

    private fun updateUserNovelsStorage(
        readStatus: String,
        sortType: SortType,
    ) {
        _uiState.value = uiState.value?.copy(loading = true)

        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserStorage(
                    userId = userId,
                    readStatus = readStatus,
                    lastUserNovelId = uiState.value?.lastUserNovelId ?: 0L,
                    size = STORAGE_NOVEL_SIZE,
                    sortType = sortType.titleEn,
                )
            }.onSuccess { response ->
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
        const val DEFAULT_USER_ID = -1L
    }
}