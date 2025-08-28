package com.into.websoso.feature.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.core.auth.AuthPlatform
import com.into.websoso.core.auth.AuthToken
import com.into.websoso.data.account.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel
    @Inject
    constructor(
        private val accountRepository: AccountRepository,
    ) : ViewModel() {
        private val _uiEffect = Channel<UiEffect>(Channel.BUFFERED)
        private val autoScrollEventFlow = flow(block = ::startAutoScroll).shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )

        val uiEffect: Flow<UiEffect> = merge(autoScrollEventFlow, _uiEffect.receiveAsFlow())
        val error = MutableStateFlow("")

        fun signIn(
            platform: AuthPlatform,
            signInToPlatform: (suspend () -> AuthToken)?,
        ) {
            viewModelScope.launch {
                runCatching {
                    requireNotNull(signInToPlatform).invoke()
                }.onSuccess { authToken ->
                    error.update {
                        authToken.toString()
                    }
                    signInWithSuccess(platform, authToken)
                }.onFailure {
                    error.update {
                        it.toString()
                    }
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
                    .createAccount(
                        platform = platform,
                        authToken = authToken,
                    ).onSuccess {
                        error.update {
                            it.toString()
                        }
                        when (accountRepository.isRegisterUser) {
                            true -> _uiEffect.send(UiEffect.NavigateToHome)
                            false -> _uiEffect.send(UiEffect.NavigateToOnboarding)
                        }
                    }.onFailure {
                        error.update {
                            it.toString()
                        }
                        signInWithFailure()
                    }
            }
        }

        private fun signInWithFailure() {
            viewModelScope.launch {
                _uiEffect.send(UiEffect.ShowToast)
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
