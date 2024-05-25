package com.teamwss.websoso.ui.onBoarding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.teamwss.websoso.WebsosoApp
import com.teamwss.websoso.domain.usecase.ValidateNicknameUseCase
import com.teamwss.websoso.ui.onBoarding.first.model.NicknameInputType
import com.teamwss.websoso.ui.onBoarding.first.model.OnBoardingFirstUiState
import com.teamwss.websoso.ui.onBoarding.model.OnBoardingPage

class OnBoardingViewModel(
    private val validateNicknameUseCase: ValidateNicknameUseCase,
) : ViewModel() {
    private val _currentPage: MutableLiveData<OnBoardingPage> = MutableLiveData(OnBoardingPage.FIRST)
    val currentPage: LiveData<OnBoardingPage> = _currentPage

    private val _progressBarPercent: MutableLiveData<Int> = MutableLiveData(OnBoardingPage.FIRST.progressPercent)
    val progressBarPercent: LiveData<Int> = _progressBarPercent

    private val _isBackButtonVisible: MutableLiveData<Boolean> = MutableLiveData(OnBoardingPage.FIRST.isBackButtonVisible)
    val isBackButtonVisible: LiveData<Boolean> = _isBackButtonVisible

    private val _isSkipTextVisible: MutableLiveData<Boolean> = MutableLiveData(OnBoardingPage.FIRST.isSkipTextVisible)
    val isSkipTextVisible: LiveData<Boolean> = _isSkipTextVisible

    private val _onBoardingFirstUiState: MutableLiveData<OnBoardingFirstUiState> = MutableLiveData(
        OnBoardingFirstUiState(
            nicknameInputType = NicknameInputType.INITIAL,
            nicknameValidationMessage = "",
            isDuplicationCheckButtonEnable = false,
            isNextButtonEnable = false
        )
    )
    val onBoardingFirstUiState: LiveData<OnBoardingFirstUiState> = _onBoardingFirstUiState

    val currentNicknameInput: MutableLiveData<String> = MutableLiveData("")

    private fun updateUIBasedOnPage(page: OnBoardingPage) {
        _progressBarPercent.value = page.progressPercent
        _isBackButtonVisible.value = page.isBackButtonVisible
        _isSkipTextVisible.value = page.isSkipTextVisible
    }

    fun validateNickname() {
        val currentInput: String = currentNicknameInput.value.orEmpty()
        if (currentInput.isEmpty()) {
            updateOnBoardingFirstUiState(NicknameInputType.INITIAL, "")
        } else {
            val (isSuccess, message) = validateNicknameUseCase(currentInput)
            if (isSuccess) {
                updateOnBoardingFirstUiState(NicknameInputType.TYPING, message)
            } else {
                updateOnBoardingFirstUiState(NicknameInputType.ERROR, message)
            }
        }
    }

    fun checkDuplicationNickname() {
        updateOnBoardingFirstUiState(NicknameInputType.COMPLETE, "사용 가능한 닉네임 입니다")
    }

    private fun updateOnBoardingFirstUiState(type: NicknameInputType, message: String) {
        _onBoardingFirstUiState.value = _onBoardingFirstUiState.value?.copy(
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
            updateUIBasedOnPage(nextPage)
        }
    }

    fun goToPreviousPage() {
        _currentPage.value?.previousPage()?.let { previousPage ->
            _currentPage.value = previousPage
            updateUIBasedOnPage(previousPage)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                OnBoardingViewModel(
                    validateNicknameUseCase = WebsosoApp.getValidateNicknameUseCase(),
                )
            }
        }
    }
}
