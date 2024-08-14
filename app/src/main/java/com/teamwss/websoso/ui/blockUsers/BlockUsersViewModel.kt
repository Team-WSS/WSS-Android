package com.teamwss.websoso.ui.blockUsers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.ui.blockUsers.model.BlockUsersModel
import com.teamwss.websoso.ui.blockUsers.model.BlockUsersModel.BlockUserModel
import com.teamwss.websoso.ui.blockUsers.model.BlockUsersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockUsersViewModel @Inject constructor() : ViewModel() {
    private val _uiState: MutableLiveData<BlockUsersUiState> = MutableLiveData(BlockUsersUiState())
    val uiState: LiveData<BlockUsersUiState> get() = _uiState

    private val blockUserDummyData: BlockUsersModel = BlockUsersModel(
        listOf(
            BlockUserModel(
                blockId = 1L,
                userId = 10L,
                nickName = "내이름은뽀로로",
                avatarImage = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQeC1zXmCckQVYSzvR7xdHzWE_MWojW_ZwEvA&s",
            )
        )
    )

    init {
        updateBlockUsers()
    }

    private fun updateBlockUsers() {
        viewModelScope.launch {
            runCatching {
                blockUserDummyData
            }.onSuccess { blockUsersModel ->
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    blockUsers = blockUsersModel.blockUsers
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    error = true,
                )
            }
        }
    }

    fun deleteBlockUser(blockId: Long) {
        // TODO 삭제 로직 구현 예정
    }
}