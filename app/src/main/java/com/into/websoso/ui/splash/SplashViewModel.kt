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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
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

        private suspend fun handleAutoLogin() =
            coroutineScope {
                // Preserve the minimum display time while authentication runs alongside it.
                val minimumDisplay = launch { delay(1000) }
                val destination = if (shouldRefresh()) {
                    NavigateToLogin
                } else {
                    accountRepository.createTokens().fold(
                        onSuccess = { NavigateToMain },
                        onFailure = { NavigateToLogin },
                    )
                }
                minimumDisplay.join()
                _uiEffect.send(destination)
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
