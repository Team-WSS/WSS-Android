package com.into.websoso.ui.login.model

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(
        val isRegistered: Boolean,
    ) : LoginUiState()

    data class Failure(val error: Throwable) : LoginUiState()
}