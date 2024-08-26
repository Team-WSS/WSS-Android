package com.teamwss.websoso.ui.changeUserInfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.changeUserInfo.model.ChangeUserInfoUiState
import com.teamwss.websoso.ui.changeUserInfo.model.Gender
import com.teamwss.websoso.ui.changeUserInfo.model.Gender.Companion.getOppositeGender
import com.teamwss.websoso.ui.changeUserInfo.model.Gender.FEMALE
import com.teamwss.websoso.ui.changeUserInfo.model.Gender.MALE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class ChangeUserInfoViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _uiState: MutableLiveData<ChangeUserInfoUiState> =
        MutableLiveData(ChangeUserInfoUiState())
    val uiState: LiveData<ChangeUserInfoUiState> get() = _uiState

    private lateinit var isInitializeOfGender: Gender
    private var isInitializeOfBirthYear by Delegates.notNull<Int>()

    init {
        updateUserInfo()
    }

    private fun updateUserInfo() {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchUserInfo()
            }.onSuccess { userInfo ->
                val gender = Gender.from(userInfo.gender)
                isInitializeOfGender = gender
                isInitializeOfBirthYear = userInfo.birthYear

                _uiState.value = uiState.value?.copy(
                    loading = false,
                    gender = Gender.from(userInfo.gender),
                    birthYear = userInfo.birthYear,
                    isMaleButtonSelected = gender == MALE,
                    isFemaleButtonSelected = gender == FEMALE,
                )
            }
        }
    }

    fun updateUserGender(isMaleSelected: Boolean) {
        val updatedGender = if (isMaleSelected) MALE else FEMALE
        _uiState.value = _uiState.value?.copy(
            gender = updatedGender,
            isMaleButtonSelected = updatedGender == MALE,
            isFemaleButtonSelected = updatedGender == FEMALE,
        )
        updateIsCompleteButtonEnabled()
    }

    fun updateBirthYear(birthYear: Int) {
        _uiState.value = uiState.value?.copy(birthYear = birthYear)
        updateIsCompleteButtonEnabled()
    }

    private fun updateIsCompleteButtonEnabled() {
        val genderSelected = _uiState.value?.gender != isInitializeOfGender
        val birthYearSelected = _uiState.value?.birthYear != isInitializeOfBirthYear

        _uiState.value = _uiState.value?.copy(
            isCompleteButtonEnabled = genderSelected || birthYearSelected
        )
    }

    fun saveUserInfo() {
        viewModelScope.launch {
            _uiState.value = uiState.value?.copy(loading = true)
            runCatching {
                userRepository.saveUserInfo(
                    gender = uiState.value?.gender?.genderCode
                        ?: isInitializeOfGender.getOppositeGender().genderCode,
                    birthYear = uiState.value?.birthYear ?: isInitializeOfBirthYear,
                )
            }.onSuccess {
                _uiState.value = uiState.value?.copy(
                    loading = false,
                    isSaveStatusComplete = true,
                )
            }
        }
    }
}