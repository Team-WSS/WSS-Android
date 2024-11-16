package com.teamwss.websoso.ui.changeUserInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.changeUserInfo.model.ChangeUserInfoUiState
import com.teamwss.websoso.common.ui.model.Gender
import com.teamwss.websoso.common.ui.model.Gender.Companion.getOppositeGender
import com.teamwss.websoso.common.ui.model.Gender.FEMALE
import com.teamwss.websoso.common.ui.model.Gender.MALE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeUserInfoViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState: MutableLiveData<ChangeUserInfoUiState> =
        MutableLiveData(ChangeUserInfoUiState())
    val uiState: LiveData<ChangeUserInfoUiState> get() = _uiState

    private var isInitializeOfGender: Gender = FEMALE
    private var isInitializeOfBirthYear: Int = 2000

    private val _isCompleteButtonEnabled = MediatorLiveData<Boolean>()
    val isCompleteButtonEnabled: LiveData<Boolean> get() = _isCompleteButtonEnabled

    init {
        _isCompleteButtonEnabled.addSource(_uiState) {
            _isCompleteButtonEnabled.value = updateCompleteButtonEnabled()
        }

        updateUserInfoDetail()
    }

    private fun updateUserInfoDetail() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserInfoDetail()
            }.onSuccess { userInfo ->
                isInitializeOfGender = Gender.from(userInfo.gender)
                isInitializeOfBirthYear = userInfo.birthYear

                _uiState.value = uiState.value?.copy(
                    loading = false,
                    gender = isInitializeOfGender,
                    birthYear = userInfo.birthYear,
                    isMaleButtonSelected = isInitializeOfGender == MALE,
                    isFemaleButtonSelected = isInitializeOfGender == FEMALE,
                )
            }
        }
    }

    private fun updateCompleteButtonEnabled(): Boolean {
        val genderSelected = uiState.value?.gender != isInitializeOfGender
        val birthYearSelected = uiState.value?.birthYear != isInitializeOfBirthYear
        return genderSelected || birthYearSelected
    }

    fun updateUserGender(isMaleSelected: Boolean) {
        val updatedGender = if (isMaleSelected) MALE else FEMALE
        _uiState.value = _uiState.value?.copy(
            gender = updatedGender,
            isMaleButtonSelected = updatedGender == MALE,
            isFemaleButtonSelected = updatedGender == FEMALE,
        )
    }

    fun updateBirthYear(birthYear: Int) {
        _uiState.value = uiState.value?.copy(birthYear = birthYear)
    }

    fun saveUserInfo() {
        viewModelScope.launch {
            _uiState.value = uiState.value?.copy(loading = true)
            runCatching {
                userRepository.saveUserInfoDetail(
                    gender = uiState.value?.gender?.genderCode
                        ?: isInitializeOfGender.getOppositeGender().genderCode,
                    birthYear = uiState.value?.birthYear ?: isInitializeOfBirthYear,
                )
            }.onSuccess {
                userRepository.saveGender(
                    uiState.value?.gender?.genderCode
                        ?: isInitializeOfGender.getOppositeGender().genderCode,
                )
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    isSaveStatusComplete = true,
                )
            }
        }
    }
}