package com.teamwss.websoso.ui.onboarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.teamwss.websoso.domain.usecase.ValidateNicknameUseCase
import com.teamwss.websoso.ui.onboarding.first.model.NicknameInputType
import com.teamwss.websoso.ui.onboarding.first.model.OnboardingFirstUiState
import com.teamwss.websoso.ui.onboarding.model.OnboardingPage
import com.teamwss.websoso.ui.onboarding.model.OnboardingPageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val validateNicknameUseCase: ValidateNicknameUseCase,
) : ViewModel() {
    private val pageManager = OnboardingPageManager()

    val currentPage: LiveData<OnboardingPage> = pageManager.currentPage
    val progressBarPercent: LiveData<Int> = pageManager.progressBarPercent
    val isBackButtonVisible: LiveData<Boolean> = pageManager.isBackButtonVisible
    val isSkipTextVisible: LiveData<Boolean> = pageManager.isSkipTextVisible

    private val _onboardingFirstUiState: MutableLiveData<OnboardingFirstUiState> =
        MutableLiveData(OnboardingFirstUiState())
    val onBoardingFirstUiState: LiveData<OnboardingFirstUiState> = _onboardingFirstUiState

    val currentNicknameInput: MutableLiveData<String> = MutableLiveData("")

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
        pageManager.updateNextPage()
    }

    fun goToPreviousPage() {
        pageManager.updatePreviousPage()
    }
}
