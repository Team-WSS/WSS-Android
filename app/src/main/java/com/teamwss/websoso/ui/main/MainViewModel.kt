package com.teamwss.websoso.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.model.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private var userId : Long = DEFAULT_USER_ID

    private val _mainUiState: MutableLiveData<MainUiState> = MutableLiveData(MainUiState())
    val mainUiState: LiveData<MainUiState> get() = _mainUiState

    fun updateUserInfo() {
        if (mainUiState.value?.isLogin == true) {
            viewModelScope.launch {
                _mainUiState.value = mainUiState.value?.copy(
                    loading = true,
                )
                runCatching {
                    userRepository.fetchUserInfo()
                }.onSuccess { userInfo ->
                    _mainUiState.value = mainUiState.value?.copy(
                        nickname = userInfo.nickname,
                        isLogin = userInfo.userId != DEFAULT_USER_ID,
                        userId = userInfo.userId,
                        loading = false,
                    )
                }.onFailure {
                    _mainUiState.value = mainUiState.value?.copy(
                        error = true,
                        loading = false,
                    )
                }
            }
        }
    }

    fun isUserId(id: Long): Boolean = userId == id

    companion object{
        const val DEFAULT_USER_ID = -1L
    }
}
