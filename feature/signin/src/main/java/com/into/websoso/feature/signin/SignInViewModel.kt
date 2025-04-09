package com.into.websoso.feature.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel
    @Inject
    constructor() : ViewModel() {
        private val _uiEvent = Channel<UiEffect>(Channel.CONFLATED)
        val uiEvent = _uiEvent.receiveAsFlow()

        init {
            startAutoScroll()
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
}
