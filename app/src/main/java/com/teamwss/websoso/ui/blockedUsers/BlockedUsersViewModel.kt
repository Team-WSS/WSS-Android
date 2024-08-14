package com.teamwss.websoso.ui.blockedUsers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.ui.blockedUsers.model.BlockedUsersModel
import com.teamwss.websoso.ui.blockedUsers.model.BlockedUsersModel.BlockedUserModel
import com.teamwss.websoso.ui.blockedUsers.model.BlockedUsersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockedUsersViewModel @Inject constructor() : ViewModel() {
    private val _uiState: MutableLiveData<BlockedUsersUiState> =
        MutableLiveData(BlockedUsersUiState())
    val uiState: LiveData<BlockedUsersUiState> get() = _uiState

    private val blockedUserDummyData: BlockedUsersModel = BlockedUsersModel(
        listOf(
            BlockedUserModel(
                blockId = 1L,
                userId = 10L,
                nickName = "내이름은뽀로로",
                avatarImage = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQeC1zXmCckQVYSzvR7xdHzWE_MWojW_ZwEvA&s",
            )
        )
    )

    init {
        updateBlockedUsers()
    }

    private fun updateBlockedUsers() {
        viewModelScope.launch {
            runCatching {
                blockedUserDummyData
            }.onSuccess { blockedUsersModel ->
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    blockedUsers = blockedUsersModel.blockedUsers
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