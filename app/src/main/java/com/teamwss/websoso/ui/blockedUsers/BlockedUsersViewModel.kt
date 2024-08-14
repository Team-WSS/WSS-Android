package com.teamwss.websoso.ui.blockedUsers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        updateBlockedUsers()
    }

    private fun updateBlockedUsers() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchBlockedUsers()
            }.onSuccess { blockedUserEntity ->
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    blockedUsers = blockedUserEntity.blockedUsers,
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    fun deleteBlockedUser(blockId: Long) {
        // TODO 삭제 로직 구현 예정
    }
}