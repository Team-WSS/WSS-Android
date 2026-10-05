package com.into.websoso.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.data.account.AccountRepository
import com.into.websoso.data.repository.UserRepository
import com.into.websoso.data.repository.VersionRepository
import com.into.websoso.ui.main.home.HomeStartupRequest
import com.into.websoso.ui.splash.UiEffect.NavigateToLogin
import com.into.websoso.ui.splash.UiEffect.NavigateToMain
import com.into.websoso.ui.splash.UiEffect.ShowDialog
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
    @Inject
    constructor(
        private val versionRepository: VersionRepository,
        private val userRepository: UserRepository,
        private val accountRepository: AccountRepository,
        private val homeStartupRequest: Lazy<HomeStartupRequest>,
    ) : ViewModel() {
        private val _uiEffect = MutableStateFlow<UiEffect?>(null)
        val uiEffect: Flow<UiEffect> get() = _uiEffect.filterNotNull()
        private var hasStarted = false
        private var isHomeDestination = true
        private var startupRequestId: String? = null

        fun start(isHomeDestination: Boolean) {
            this.isHomeDestination = isHomeDestination
            if (!isHomeDestination) {
                discardStartupRequest()
                if (_uiEffect.value is NavigateToMain) _uiEffect.value = NavigateToMain(null)
            }
            if (hasStarted) return
            hasStarted = true
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
                if (isRequired) _uiEffect.value = ShowDialog
            }

        private suspend fun handleAutoLogin() =
            coroutineScope {
                // Overlap authentication and Home requests with the minimum display time.
                val minimumDisplay = launch { delay(1000) }
                val authenticated = if (shouldRefresh()) {
                    false
                } else {
                    accountRepository.createTokens().isSuccess
                }
                if (authenticated) {
                    startupRequestId = if (isHomeDestination) homeStartupRequest.get().start() else null
                    // A new deep link may arrive while the session lookup is suspended.
                    if (!isHomeDestination) discardStartupRequest()
                }
                minimumDisplay.join()
                // Read startupRequestId here, not earlier: a deep link during the wait may have cleared it.
                _uiEffect.value = if (authenticated) NavigateToMain(startupRequestId) else NavigateToLogin
            }

        fun onUiEffectHandled(effect: UiEffect) {
            if (_uiEffect.value != effect) return
            // Main now owns the ID, even if Home has not taken its requests yet.
            if (effect is NavigateToMain) startupRequestId = null
            _uiEffect.value = null
        }

        fun onNavigationFailed() {
            discardStartupRequest()
            _uiEffect.value = null
        }

        private fun discardStartupRequest() {
            startupRequestId?.let { homeStartupRequest.get().discard(it) }
            startupRequestId = null
        }

        override fun onCleared() {
            discardStartupRequest()
            super.onCleared()
        }

        private suspend fun shouldRefresh(): Boolean =
            accountRepository.accessToken().isBlank() ||
                accountRepository
                    .refreshToken()
                    .isBlank()
    }

sealed interface UiEffect {
    data object NavigateToLogin : UiEffect

    data class NavigateToMain(
        val startupRequestId: String?,
    ) : UiEffect

    data object ShowDialog : UiEffect
}
