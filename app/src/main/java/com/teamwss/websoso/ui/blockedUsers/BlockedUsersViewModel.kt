package com.teamwss.websoso.ui.blockedUsers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.BlockedUsersEntity.BlockedUserEntity
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.blockedUsers.model.BlockedUsersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockedUsersViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState: MutableLiveData<BlockedUsersUiState> =
        MutableLiveData(BlockedUsersUiState())
    val uiState: LiveData<BlockedUsersUiState> get() = _uiState

    private val _isBlockedUserEmptyBoxVisibility: MutableLiveData<Boolean> = MutableLiveData()
    val isBlockedUserEmptyBoxVisibility: LiveData<Boolean> get() = _isBlockedUserEmptyBoxVisibility

    init {
        updateBlockedUsers()
    }

    private fun updateBlockedUsers() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchBlockedUsers()
            }.onSuccess { blockedUserEntity ->
                updateUiState(blockedUserEntity.blockedUsers)
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    fun deleteBlockedUser(blockId: Long) {
        viewModelScope.launch {
            runCatching {
                userRepository.deleteBlockedUser(blockId)
            }.onSuccess {
                val currentBlockedUsers: List<BlockedUserEntity> =
                    uiState.value?.blockedUsers ?: emptyList()
                val updatedBlockedUsers: List<BlockedUserEntity> =
                    currentBlockedUsers.filterNot { it.blockId == blockId }

                updateUiState(updatedBlockedUsers)
            }
        }
    }

    private fun updateUiState(blockedUsers: List<BlockedUserEntity>) {
        when (blockedUsers.isNotEmpty()) {
            true -> {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    blockedUsers = blockedUsers,
                )

                _isBlockedUserEmptyBoxVisibility.value = false
            }

            false -> {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    blockedUsers = emptyList(),
                )
                _isBlockedUserEmptyBoxVisibility.value = true
            }
        }
    }
}