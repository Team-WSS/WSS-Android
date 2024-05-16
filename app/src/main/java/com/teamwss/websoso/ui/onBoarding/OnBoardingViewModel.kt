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
    private val validateNicknameUseCase: ValidateNicknameUseCase
) : ViewModel() {
    private val _currentPage: MutableLiveData<OnBoardingPage> =
        MutableLiveData<OnBoardingPage>(OnBoardingPage.FIRST)
    val currentPage: LiveData<OnBoardingPage> = _currentPage

    private val _progressBarPercent: MutableLiveData<Int> =
        MutableLiveData<Int>(calculateProgressPercent(OnBoardingPage.FIRST))
    val progressBarPercent: LiveData<Int> = _progressBarPercent

    private val _isBackButtonVisible: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isBackButtonVisible: LiveData<Boolean> = _isBackButtonVisible

    private val _isSkipTextVisible: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val isSkipTextVisible: LiveData<Boolean> = _isSkipTextVisible

    private val _onBoardingFirstUiState: MutableLiveData<OnBoardingFirstUiState> =
        MutableLiveData<OnBoardingFirstUiState>(
            OnBoardingFirstUiState(
                nicknameInputType = NicknameInputType.INITIAL,
                nicknameValidationMessage = "",
                isDuplicationCheckButtonEnable = false,
                isNextButtonEnable = false
            )
        )
    val onBoardingFirstUiState: LiveData<OnBoardingFirstUiState> = _onBoardingFirstUiState

    val inputNickname: MutableLiveData<String> = MutableLiveData<String>()

    private fun calculateProgressPercent(page: OnBoardingPage): Int {
        return ((page.ordinal + 1) * 100) / OnBoardingPage.values().size
    }

    fun updateCurrentPage(page: OnBoardingPage) {
        if (_currentPage.value != page) {
            _currentPage.value = page
            _progressBarPercent.value = calculateProgressPercent(page)
            _isBackButtonVisible.value = page != OnBoardingPage.FIRST
            _isSkipTextVisible.value = page == OnBoardingPage.THIRD
        }
    }

    fun validateNickname() {
        val currentInput: String = inputNickname.value.orEmpty()
        if (currentInput.isEmpty()) {
            updateOnBoardingFirstUiState(NicknameInputType.INITIAL, "")
        } else {
            val result: ValidateNicknameUseCase.ValidationResult =
                validateNicknameUseCase(currentInput)
            if (result.isSuccess) {
                updateOnBoardingFirstUiState(NicknameInputType.TYPING, result.message)
            } else {
                updateOnBoardingFirstUiState(NicknameInputType.ERROR, result.message)
            }
        }
    }

    fun checkDuplicationNickname() {
        // TODO: 중복 확인 로직 구현
        updateOnBoardingFirstUiState(NicknameInputType.COMPLETE, "")
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
        inputNickname.value = ""
    }

    fun goToNextPage() {
        _currentPage.value?.let {
            val nextPage = it.ordinal + 1
            if (nextPage < OnBoardingPage.values().size) {
                updateCurrentPage(OnBoardingPage.values()[nextPage])
            }
        }
    }

    fun goToPreviousPage() {
        _currentPage.value?.let {
            val previousPage = it.ordinal - 1
            if (previousPage >= 0) {
                updateCurrentPage(OnBoardingPage.values()[previousPage])
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                OnBoardingViewModel(
                    validateNicknameUseCase = WebsosoApp.getValidateNicknameUseCase()
                )
            }
        }
    }
}
