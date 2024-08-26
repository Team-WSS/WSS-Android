package com.teamwss.websoso.ui.changeUserInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.changeUserInfo.model.Gender
import com.teamwss.websoso.ui.changeUserInfo.model.Gender.FEMALE
import com.teamwss.websoso.ui.changeUserInfo.model.Gender.MALE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeUserInfoViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _gender = MutableLiveData<Gender>()
    private val gender: LiveData<Gender> get() = _gender

    private val _birthYear: MutableLiveData<Int> = MutableLiveData()
    val birthYear: LiveData<Int> get() = _birthYear

    private val _isMaleButtonSelected: MutableLiveData<Boolean> = MutableLiveData()
    val isMaleButtonSelected: LiveData<Boolean> get() = _isMaleButtonSelected

    private val _isFemaleButtonSelected: MutableLiveData<Boolean> = MutableLiveData()
    val isFemaleButtonSelected: LiveData<Boolean> get() = _isFemaleButtonSelected

    init {
        updateUserInfo()
    }

    private fun updateUserInfo() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserInfo()
            }.onSuccess { userInfo ->
                _birthYear.value = userInfo.birthYear
                _gender.value = Gender.from(userInfo.gender)
                updateGenderButtonSelection(_gender.value ?: return@onSuccess)
            }
        }
    }

    private fun updateGenderButtonSelection(gender: Gender) {
        _isMaleButtonSelected.value = gender == MALE
        _isFemaleButtonSelected.value = gender == FEMALE
    }

    fun updateUserGender(isMaleSelected: Boolean) {
        _gender.value = if (isMaleSelected) MALE else FEMALE
        updateGenderButtonSelection(gender.value ?: throw IllegalArgumentException())
    }
}