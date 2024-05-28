package com.teamwss.websoso.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.domain.usecase.ValidateNicknameUseCase
import com.teamwss.websoso.ui.onboarding.first.model.NicknameInputType
import com.teamwss.websoso.ui.onboarding.first.model.OnboardingFirstUiState
import com.teamwss.websoso.ui.onboarding.model.OnboardingPage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val validateNicknameUseCase: ValidateNicknameUseCase,
) : ViewModel() {
    private val _currentPage: MutableLiveData<OnboardingPage> =
        MutableLiveData(OnboardingPage.FIRST)
    val currentPage: LiveData<OnboardingPage> = _currentPage

    private val _progressBarPercent: MutableLiveData<Int> =
        MutableLiveData(OnboardingPage.FIRST.progressPercent)
    val progressBarPercent: LiveData<Int> = _progressBarPercent

    private val _isBackButtonVisible: MutableLiveData<Boolean> =
        MutableLiveData(OnboardingPage.FIRST.isBackButtonVisible)
    val isBackButtonVisible: LiveData<Boolean> = _isBackButtonVisible

    private val _isSkipTextVisible: MutableLiveData<Boolean> =
        MutableLiveData(OnboardingPage.FIRST.isSkipTextVisible)
    val isSkipTextVisible: LiveData<Boolean> = _isSkipTextVisible

    private val _onboardingFirstUiState: MutableLiveData<OnboardingFirstUiState> =
        MutableLiveData(OnboardingFirstUiState())
    val onBoardingFirstUiState: LiveData<OnboardingFirstUiState> = _onboardingFirstUiState

    val currentNicknameInput: MutableLiveData<String> = MutableLiveData("")

    private fun updateUIByPage(page: OnboardingPage) {
        _progressBarPercent.value = page.progressPercent
        _isBackButtonVisible.value = page.isBackButtonVisible
        _isSkipTextVisible.value = page.isSkipTextVisible
    }

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
        _onboardingFirstUiState.value = _onboardingFirstUiState.value?.copy(
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
        _currentPage.value?.nextPage()?.let { nextPage ->
            _currentPage.value = nextPage
            updateUIByPage(nextPage)
        }
    }

    fun goToPreviousPage() {
        _currentPage.value?.previousPage()?.let { previousPage ->
            _currentPage.value = previousPage
            updateUIByPage(previousPage)
        }
    }
}
