package com.into.websoso.feature.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.auth.AuthToken
import com.into.websoso.data.account.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel
    @Inject
    constructor(
        private val accountRepository: AccountRepository,
    ) : ViewModel() {
        private val _uiEvent = Channel<UiEffect>(Channel.CONFLATED)
        private val autoScrollEventFlow = flow(block = ::startAutoScroll).shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )

        val uiEvent = merge(autoScrollEventFlow, _uiEvent.receiveAsFlow())

        fun signIn(
            platform: AuthPlatform,
            signInToPlatform: suspend () -> AuthToken,
        ) {
            viewModelScope.launch {
                runCatching {
                    signInToPlatform()
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
                accountRepository
                    .saveTokens(
                        platform = platform,
                        authToken = authToken,
                    ).onSuccess { isRegister ->
                        when (isRegister) {
                            true -> _uiEvent.send(UiEffect.NavigateToHome)
                            false -> _uiEvent.send(UiEffect.NavigateToOnboarding)
                        }
                    }.onFailure {
                        signInWithFailure()
                    }
            }
        }

        private fun signInWithFailure() {
            viewModelScope.launch {
                _uiEvent.send(UiEffect.ShowToast)
            }
        }

        private suspend fun startAutoScroll(effectFlow: FlowCollector<UiEffect>) {
            while (viewModelScope.isActive) {
                delay(ONBOARDING_TRANSITION_PERIOD)
                effectFlow.emit(UiEffect.ScrollToPage)
            }
        }

        companion object {
            private const val ONBOARDING_TRANSITION_PERIOD: Long = 2000
        }
    }

sealed interface UiEffect {
    data object ScrollToPage : UiEffect

    data object ShowToast : UiEffect

    data object NavigateToHome : UiEffect

    data object NavigateToOnboarding : UiEffect
}
