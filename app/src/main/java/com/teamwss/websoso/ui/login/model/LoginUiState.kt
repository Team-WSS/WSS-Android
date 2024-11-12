package com.teamwss.websoso.ui.login.model

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading: LoginUiState()
    data class Success(val isRegistered: Boolean, val accessToken: String, val refreshToken: String) : LoginUiState()
    data class Failure(val error: Throwable) : LoginUiState()
}