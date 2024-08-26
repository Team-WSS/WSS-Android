package com.teamwss.websoso.ui.changeUserInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeUserInfoViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _gender: MutableLiveData<String> = MutableLiveData()
    val gender: LiveData<String> get() = _gender

    private val _birthYear: MutableLiveData<Int> = MutableLiveData()
    val birthYear: LiveData<Int> get() = _birthYear

    init {
        updateUserInfo()
    }

    private fun updateUserInfo() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserInfo()
            }.onSuccess { userInfo ->
                _birthYear.value = userInfo.birthYear
                _gender.value = userInfo.gender
            }
        }
    }
}