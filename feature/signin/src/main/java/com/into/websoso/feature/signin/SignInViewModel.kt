package com.into.websoso.feature.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.auth.AuthToken
import com.into.websoso.data.account.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel
    @Inject
    constructor(
        private val accountRepository: AccountRepository,
    ) : ViewModel() {
        private val _uiEvent = Channel<UiEffect>(Channel.CONFLATED)
        val uiEvent = _uiEvent.receiveAsFlow()

        init {
            startAutoScroll()
        }

        fun signIn(
            platform: AuthPlatform,
            getToken: suspend () -> AuthToken,
        ) {
            viewModelScope.launch {
                runCatching {
                    getToken()
                }.onSuccess { authToken ->
                    signInWithSuccess(platform, authToken)
                }.onFailure {
                    signInWithFailure()
                }
            }
        }

        private fun signInWithSuccess(
            platform: AuthPlatform,
            authToken: AuthToken,
        ) {
            viewModelScope.launch {
                runCatching {
                    // result 객체
                    accountRepository.saveToken(
                        platform = platform,
                        authToken = authToken,
                    )
                }.onSuccess { isRegister ->
                    // navigateToHome.emit()
                }.onFailure {
                }
            }
        }

        private fun signInWithFailure() {
            viewModelScope.launch {
                _uiEvent.send(UiEffect.ShowToast)
            }
        }

        private fun startAutoScroll() {
            viewModelScope.launch {
                while (true) {
                    delay(ONBOARDING_TRANSITION_PERIOD)
                    _uiEvent.send(UiEffect.ScrollToPage)
                }
            }
        }

        companion object {
            private const val ONBOARDING_TRANSITION_PERIOD: Long = 2000
        }
    }

sealed interface UiEffect {
    data object ScrollToPage : UiEffect

    data object ShowToast : UiEffect
}

sealed interface UiEvent {
    data object NavigateToHome : UiEvent
}
