package com.into.websoso.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.account.AccountRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.data.repository.VersionRepository
import com.into.websoso.ui.splash.UiEffect.NavigateToLogin
import com.into.websoso.ui.splash.UiEffect.NavigateToMain
import com.into.websoso.ui.splash.UiEffect.ShowDialog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val versionRepository: VersionRepository,
        private val userRepository: UserRepository,
        private val accountRepository: AccountRepository,
    ) : ViewModel() {
        private val _uiEffect = Channel<UiEffect>(Channel.BUFFERED)
        val uiEffect: Flow<UiEffect> get() = _uiEffect.receiveAsFlow()

        init {
            viewModelScope.launch {
                val isUpdateRequired = checkMinimumVersion()
                if (isUpdateRequired.not()) handleAutoLogin()
            }
        }

        fun updateUserDeviceIdentifier(deviceIdentifier: String) {
            viewModelScope.launch {
                runCatching {
                    userRepository.saveUserDeviceIdentifier(deviceIdentifier)
                }.onFailure {
                    it.printStackTrace()
                }
            }
        }

        private suspend fun checkMinimumVersion(): Boolean =
            runCatching {
                versionRepository.isUpdateRequired()
            }.getOrElse { false }.also { isRequired ->
                if (isRequired) _uiEffect.send(ShowDialog)
            }

        private suspend fun handleAutoLogin() {
            if (shouldRefresh()) {
                _uiEffect.send(NavigateToLogin)
                return
            }

            runCatching { accountRepository.renewToken() }
                .onSuccess {
                    _uiEffect.send(NavigateToMain)
                }.onFailure {
                    _uiEffect.send(NavigateToLogin)
                }
        }

        private suspend fun shouldRefresh(): Boolean =
            accountRepository.accessToken().isBlank() ||
                accountRepository
                    .refreshToken()
                    .isBlank()
    }

sealed interface UiEffect {
    data object NavigateToLogin : UiEffect

    data object NavigateToMain : UiEffect

    data object ShowDialog : UiEffect
}
