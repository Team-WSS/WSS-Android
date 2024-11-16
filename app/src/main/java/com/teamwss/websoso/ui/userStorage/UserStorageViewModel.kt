package com.teamwss.websoso.ui.userStorage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.userStorage.UserStorageActivity.Companion.SOURCE_MY_LIBRARY
import com.teamwss.websoso.ui.userStorage.model.SortType
import com.teamwss.websoso.ui.userStorage.model.StorageTab
import com.teamwss.websoso.ui.userStorage.model.UserStorageModel
import com.teamwss.websoso.ui.userStorage.model.UserStorageUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserStorageViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState: MutableLiveData<UserStorageUiState> = MutableLiveData(UserStorageUiState())
    val uiState: LiveData<UserStorageUiState> get() = _uiState

    private val _isRatingChanged = MutableLiveData<Boolean>(false)
    val isRatingChanged: LiveData<Boolean> get() = _isRatingChanged

    private val tabDataMap: MutableMap<String, MutableList<UserStorageModel.StorageNovelModel>> =
        mutableMapOf()

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

    private fun updateUserNovelsStorage(readStatus: String, sortType: SortType) {
        val currentState = _uiState.value ?: UserStorageUiState()

        _uiState.value = currentState.copy(loading = true)

        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserStorage(
                    userId = userId,
                    readStatus = readStatus,
                    lastUserNovelId = tabDataMap[readStatus]?.lastOrNull()?.userNovelId ?: 0L,
                    size = STORAGE_NOVEL_SIZE,
                    sortType = sortType.titleEn,
                )
            }.onSuccess { response ->
                val newNovels = response.userNovels.map { it.toUi() }
                val isLoadable = response.isLoadable

                val updatedNovels = (tabDataMap[readStatus] ?: mutableListOf()).apply {
                    addAll(newNovels)
                }
                tabDataMap[readStatus] = updatedNovels

                _uiState.value = currentState.copy(
                    loading = false,
                    userNovelCount = response.userNovelCount,
                    userNovelRating = response.userNovelRating,
                    storageNovels = updatedNovels,
                    lastUserNovelId = newNovels.lastOrNull()?.userNovelId
                        ?: currentState.lastUserNovelId,
                    isLoadable = isLoadable,
                )
            }.onFailure {
                _uiState.value = currentState.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    fun loadMoreNovels() {
        val currentState = _uiState.value ?: return

        if (currentState.loading || !currentState.isLoadable) {
            return
        }

        updateNovels(forceLoad = false)
    }

    private fun updateNovels(forceLoad: Boolean) {
        val currentState = _uiState.value ?: UserStorageUiState()

        if (currentState.loading || !currentState.isLoadable) return

        _uiState.value = currentState.copy(loading = true)

        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserStorage(
                    userId = userId,
                    readStatus = currentState.readStatus,
                    lastUserNovelId = if (forceLoad) 0L else currentState.storageNovels.lastOrNull()?.userNovelId
                        ?: 0L,
                    size = STORAGE_NOVEL_SIZE,
                    sortType = currentState.sortType.titleEn
                )
            }.onSuccess { response ->
                val newNovels = response.userNovels.map { it.toUi() }
                val isLoadable = response.isLoadable && newNovels.isNotEmpty()

                _uiState.value = currentState.copy(
                    loading = false,
                    storageNovels = if (forceLoad) newNovels else currentState.storageNovels + newNovels,
                    userNovelCount = response.userNovelCount,
                    userNovelRating = response.userNovelRating,
                    lastUserNovelId = newNovels.lastOrNull()?.userNovelId ?: 0L,
                    isLoadable = isLoadable,
                )

            }.onFailure {
                _uiState.value = currentState.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    companion object {
        const val STORAGE_NOVEL_SIZE = 9
        const val DEFAULT_USER_ID = -1L
    }
}