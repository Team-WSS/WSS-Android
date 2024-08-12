package com.teamwss.websoso.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.domain.usecase.ValidateNicknameUseCase
import com.teamwss.websoso.ui.onboarding.first.model.NicknameInputType
import com.teamwss.websoso.ui.onboarding.first.model.OnboardingFirstUiState
import com.teamwss.websoso.ui.onboarding.model.OnboardingPage
import com.teamwss.websoso.ui.onboarding.model.UserModel
import com.teamwss.websoso.ui.onboarding.second.model.OnboardingSecondUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val validateNicknameUseCase: ValidateNicknameUseCase,
) : ViewModel() {
    private val _currentPage = MutableLiveData(OnboardingPage.FIRST)
    val currentPage: LiveData<OnboardingPage> = _currentPage

    private val _progressBarPercent = MutableLiveData(OnboardingPage.FIRST.progressPercent)
    val progressBarPercent: LiveData<Int> = _progressBarPercent

    private val _isBackButtonVisible = MutableLiveData(OnboardingPage.FIRST.isBackButtonVisible)
    val isBackButtonVisible: LiveData<Boolean> = _isBackButtonVisible

    private val _isSkipTextVisible = MutableLiveData(OnboardingPage.FIRST.isSkipTextVisible)
    val isSkipTextVisible: LiveData<Boolean> = _isSkipTextVisible

    private val _onboardingFirstUiState: MutableLiveData<OnboardingFirstUiState> =
        MutableLiveData(OnboardingFirstUiState())
    val onboardingFirstUiState: LiveData<OnboardingFirstUiState> = _onboardingFirstUiState

    private val _onboardingSecondUiState: MutableLiveData<OnboardingSecondUiState> =
        MutableLiveData(OnboardingSecondUiState())
    val onboardingSecondUiState: LiveData<OnboardingSecondUiState> = _onboardingSecondUiState

    val currentNicknameInput: MutableLiveData<String> = MutableLiveData("")

    private val _userInfo: MutableLiveData<UserModel> = MutableLiveData(UserModel())
    val userInfo: LiveData<UserModel> = _userInfo

    fun validateNickname() {
        val currentInput: String = currentNicknameInput.value.orEmpty()
        if (currentInput.isEmpty()) {
            updateOnBoardingFirstUiState(NicknameInputType.INITIAL, "")
            return
        }
        val (isSuccess, message) = validateNicknameUseCase(currentInput)
        when (isSuccess) {
            true -> updateOnBoardingFirstUiState(NicknameInputType.TYPING, message)
            false -> updateOnBoardingFirstUiState(NicknameInputType.ERROR, message)
        }
    }

    fun checkDuplicationNickname() {
        updateOnBoardingFirstUiState(NicknameInputType.COMPLETE, "사용 가능한 닉네임 입니다")
    }

    private fun updateOnBoardingFirstUiState(type: NicknameInputType, message: String) {
        _onboardingFirstUiState.value = onboardingFirstUiState.value?.copy(
            nicknameInputType = type,
            nicknameValidationMessage = message,
            isDuplicationCheckButtonEnable = type == NicknameInputType.TYPING,
            isNextButtonEnable = type == NicknameInputType.COMPLETE
        )
    }

    fun clearInputNickname() {
        currentNicknameInput.value = ""
    }

    fun goToNextPage() {
        _currentPage.value?.let { currentPage ->
            val nextPage = currentPage.nextPage()
            if (nextPage != currentPage) {
                _currentPage.value = nextPage
                updateUIByPage(nextPage)
            }
        }
    }

    fun goToPreviousPage() {
        _currentPage.value?.let { currentPage ->
            val previousPage = currentPage.previousPage()
            if (previousPage != currentPage) {
                _currentPage.value = previousPage
                updateUIByPage(previousPage)
            }
        }
    }

    private fun updateUIByPage(page: OnboardingPage) {
        with(page) {
            _progressBarPercent.value = progressPercent
            _isBackButtonVisible.value = isBackButtonVisible
            _isSkipTextVisible.value = isSkipTextVisible
        }
    }

    fun updateUserBirthYear(birthYear: Int) {
        _userInfo.value = userInfo.value?.copy(birthYear = birthYear)
        updateSecondNextButtonUiState()
    }

    fun updateUserGenderUiState(isManSelected: Boolean) {
        _onboardingSecondUiState.value = onboardingSecondUiState.value?.copy(
            isManButtonSelected = isManSelected,
            isWomanButtonSelected = !isManSelected,
        )
        updateUserGender(isManSelected)
    }

    private fun updateUserGender(isManSelected: Boolean) {
        _userInfo.value = userInfo.value?.copy(gender = if (isManSelected) "Man" else "Woman")
        updateSecondNextButtonUiState()
    }

    private fun updateSecondNextButtonUiState() {
        _onboardingSecondUiState.value = onboardingSecondUiState.value?.copy(
            isNextButtonEnable = !userInfo.value?.gender.isNullOrEmpty() && userInfo.value?.birthYear != 0
        )
    }
}
