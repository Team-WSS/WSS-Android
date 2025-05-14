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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
        private var _uiEffect: MutableSharedFlow<UiEffect> = MutableSharedFlow(replay = 1)
        val uiEffect: SharedFlow<UiEffect> get() = _uiEffect.asSharedFlow()

        init {
            checkMinimumVersion()
            handleAutoLogin()
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

        private fun checkMinimumVersion() {
            viewModelScope.launch {
                runCatching {
                    versionRepository.isUpdateRequired()
                }.onSuccess { isRequired ->
                    if (isRequired) _uiEffect.emit(ShowDialog)
                }
            }
        }

        private fun handleAutoLogin() {
            viewModelScope.launch {
                if (shouldRefresh()) {
                    _uiEffect.emit(NavigateToLogin)
                    return@launch
                }

                runCatching { accountRepository.renewToken() }
                    .onSuccess {
                        _uiEffect.emit(NavigateToMain)
                    }.onFailure {
                        _uiEffect.emit(NavigateToLogin)
                    }
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
