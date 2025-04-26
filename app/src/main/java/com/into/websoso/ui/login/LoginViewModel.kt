package com.into.websoso.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.into.websoso.core.resource.R.drawable.img_login_1
import com.into.websoso.core.resource.R.drawable.img_login_2
import com.into.websoso.core.resource.R.drawable.img_login_3
import com.into.websoso.core.resource.R.drawable.img_login_4
import com.into.websoso.data.repository.AuthRepository
import com.into.websoso.ui.login.model.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _loginUiState = MutableLiveData<LoginUiState>()
    val loginUiState: LiveData<LoginUiState> get() = _loginUiState

    private val _loginImages = MutableLiveData<List<Int>>()
    val loginImages: LiveData<List<Int>> = _loginImages

    lateinit var accessToken: String
        private set
    lateinit var refreshToken: String
        private set

    init {
        _loginImages.value = listOf(
            img_login_1,
            img_login_2,
            img_login_3,
            img_login_4,
        )
    }

    fun loginWithKakao(kakaoAccessToken: String) {
        viewModelScope.launch {
            _loginUiState.value = LoginUiState.Loading
            runCatching {
                authRepository.loginWithKakao(kakaoAccessToken)
            }.onSuccess { loginEntity ->
                accessToken = loginEntity.authorization
                refreshToken = loginEntity.refreshToken

                if (loginEntity.isRegister) {
                    authRepository.updateAccessToken(loginEntity.authorization)
                    authRepository.updateRefreshToken(loginEntity.refreshToken)
                    authRepository.updateIsAutoLogin(true)
                }

                _loginUiState.value = LoginUiState.Success(
                    isRegistered = loginEntity.isRegister,
                )
            }.onFailure { error ->
                _loginUiState.value = LoginUiState.Failure(error)
            }
        }
    }
}
