package com.teamwss.websoso.ui.main.myPage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.MyProfileEntity
import com.teamwss.websoso.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _myProfile = MutableLiveData<MyProfileEntity>()
    val myProfile: LiveData<MyProfileEntity> get() = _myProfile

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchMyProfile()
            }.onSuccess { myProfile ->
                _myProfile.value = myProfile
            }.onFailure { exception ->
                Log.e("MyPageViewModel", "Failed to load user profile", exception)
            }
        }
    }
}
