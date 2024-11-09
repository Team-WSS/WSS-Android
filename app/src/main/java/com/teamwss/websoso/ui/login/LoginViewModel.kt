package com.teamwss.websoso.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.R
import com.teamwss.websoso.data.repository.AuthRepository
import com.teamwss.websoso.ui.login.model.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _loginUiState = MutableLiveData<LoginUiState>()
    val loginUiState: LiveData<LoginUiState> = _loginUiState

    private val _loginImages = MutableLiveData<List<Int>>()
    val loginImages: LiveData<List<Int>> = _loginImages

    init {
        _loginImages.value = listOf(
            R.drawable.img_login_1,
            R.drawable.img_login_2,
            R.drawable.img_login_3,
            R.drawable.img_login_4,
        )
    }

    fun autoLogin() {
        viewModelScope.launch {
            val isConfigured = authRepository.isAutoLoginConfigured()
            if (isConfigured) {
                val accessToken = authRepository.fetchAccessToken()
                val refreshToken = authRepository.fetchRefreshToken()
                _loginUiState.value = LoginUiState.Success(
                    isRegistered = true,
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            } else {
                _loginUiState.value = LoginUiState.Idle
            }
        }
    }

    fun loginWithKakao(accessToken: String) {
        viewModelScope.launch {
            runCatching {
                authRepository.loginWithKakao(accessToken)
            }.onSuccess { loginEntity ->
                _loginUiState.value = LoginUiState.Success(
                    isRegistered = loginEntity.isRegister,
                    accessToken = loginEntity.authorization,
                    refreshToken = loginEntity.refreshToken
                )

                if (loginEntity.isRegister && authRepository.isAutoLoginConfigured().not()) {
                    authRepository.saveAccessToken(loginEntity.authorization)
                    authRepository.saveRefreshToken(loginEntity.refreshToken)
                    authRepository.setAutoLoginConfigured(true)
                }
            }.onFailure { error ->
                _loginUiState.value = LoginUiState.Failure(error)
            }
        }
    }
}
