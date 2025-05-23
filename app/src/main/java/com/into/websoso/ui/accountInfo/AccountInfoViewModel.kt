package com.into.websoso.ui.accountInfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.account.AccountRepository
import com.into.websoso.data.repository.PushMessageRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.ui.accountInfo.UiEffect.NavigateToLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountInfoViewModel
    @Inject
    constructor(
        private val userRepository: UserRepository,
        private val pushMessageRepository: PushMessageRepository,
        private val accountRepository: AccountRepository,
    ) : ViewModel() {
        private val _userEmail: MutableStateFlow<String> = MutableStateFlow("")
        val userEmail: StateFlow<String> get() = _userEmail.asStateFlow()

        private val _uiEffect = Channel<UiEffect>(Channel.BUFFERED)
        val uiEffect: Flow<UiEffect> get() = _uiEffect.receiveAsFlow()

        init {
            updateUserEmail()
        }

        private fun updateUserEmail() {
            viewModelScope.launch {
                runCatching {
                    userRepository.fetchUserInfoDetail()
                }.onSuccess { userInfo ->
                    _userEmail.update { userInfo.email }
                }
            }
        }

        fun signOut() {
            viewModelScope.launch {
                val userDeviceIdentifier = userRepository.fetchUserDeviceIdentifier()

                accountRepository
                    .deleteTokens(userDeviceIdentifier)
                    .onSuccess {
                        pushMessageRepository.clearFCMToken()
                        _uiEffect.send(NavigateToLogin)
                    }.onFailure {
                        _uiEffect.send(NavigateToLogin)
                    }
            }
        }
    }

sealed interface UiEffect {
    data object NavigateToLogin : UiEffect
}
