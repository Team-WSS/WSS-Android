package com.teamwss.websoso.ui.accountInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountInfoViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _userEmail: MutableLiveData<String> = MutableLiveData()
    val userEmail: LiveData<String> get() = _userEmail

    init {
        updateUserEmail()
    }

    private fun updateUserEmail() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserInfo()
            }.onSuccess { userInfo ->
                _userEmail.value = userInfo.email
            }.onFailure {
            }
        }
    }
}
