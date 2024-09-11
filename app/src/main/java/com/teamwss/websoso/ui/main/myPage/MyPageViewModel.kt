package com.teamwss.websoso.ui.main.myPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.model.MyProfileEntity
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.main.myPage.model.MyProfileModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _myProfile = MutableLiveData<MyProfileEntity>()
    val myProfile: LiveData<MyProfileEntity> get() = _myProfile

    private val _myProfileModel = MutableLiveData<MyProfileModel>()
    val myProfileModel: LiveData<MyProfileModel> get() = _myProfileModel

    init {
        updateUserProfile()
    }

    private fun updateUserProfile() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchMyProfile()
            }.onSuccess { myProfileEntity ->
                _myProfile.value = myProfileEntity
                _myProfileModel.value = myProfileEntity.toModel()
            }.onFailure { exception ->

            }
        }
    }

    private fun MyProfileEntity.toModel(): MyProfileModel {
        return MyProfileModel(
            nickname = this.nickname,
            avatarImage = this.avatarImage,
            userId = getUserId()
        )
    }

    private fun getUserId(): Long {
        return 2L
    }
}
