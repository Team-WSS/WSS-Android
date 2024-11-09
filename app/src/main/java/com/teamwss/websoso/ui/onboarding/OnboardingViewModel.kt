package com.teamwss.websoso.ui.onboarding

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.AuthRepository
import com.teamwss.websoso.domain.model.NicknameValidationResult
import com.teamwss.websoso.domain.usecase.CheckNicknameValidityUseCase
import com.teamwss.websoso.domain.usecase.ValidateNicknameUseCase
import com.teamwss.websoso.ui.onboarding.first.model.NicknameInputType
import com.teamwss.websoso.ui.onboarding.first.model.OnboardingFirstUiState
import com.teamwss.websoso.ui.onboarding.model.OnboardingPage
import com.teamwss.websoso.ui.onboarding.model.UserModel
import com.teamwss.websoso.ui.onboarding.second.model.OnboardingSecondUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val validateNicknameUseCase: ValidateNicknameUseCase,
    private val checkNicknameValidityUseCase: CheckNicknameValidityUseCase,
) : ViewModel() {
    private val _currentPage = MutableLiveData(OnboardingPage.FIRST)
    val currentPage: LiveData<OnboardingPage> get() = _currentPage

    private val _progressBarPercent = MutableLiveData(OnboardingPage.FIRST.progressPercent)
    val progressBarPercent: LiveData<Int> get() = _progressBarPercent

    private val _isBackButtonVisible = MutableLiveData(OnboardingPage.FIRST.isBackButtonVisible)
    val isBackButtonVisible: LiveData<Boolean> get() = _isBackButtonVisible

    private val _isSkipTextVisible = MutableLiveData(OnboardingPage.FIRST.isSkipTextVisible)
    val isSkipTextVisible: LiveData<Boolean> get() = _isSkipTextVisible

    private val _onboardingFirstUiState: MutableLiveData<OnboardingFirstUiState> =
        MutableLiveData(OnboardingFirstUiState())
    val onboardingFirstUiState: LiveData<OnboardingFirstUiState> get() = _onboardingFirstUiState

    private val _onboardingSecondUiState: MutableLiveData<OnboardingSecondUiState> =
        MutableLiveData(OnboardingSecondUiState())
    val onboardingSecondUiState: LiveData<OnboardingSecondUiState> get() = _onboardingSecondUiState

    val currentNicknameInput: MutableLiveData<String> = MutableLiveData("")

    private val _userProfile: MutableLiveData<UserModel> = MutableLiveData(UserModel())
    val userProfile: LiveData<UserModel> get() = _userProfile

    private val _selectedGenres = MutableLiveData<Set<String>>(setOf())
    val selectedGenres: LiveData<Set<String>> get() = _selectedGenres

    private val _isUserProfileSubmit = MutableLiveData<Boolean>(false)
    val isUserProfileSubmit: LiveData<Boolean> get() = _isUserProfileSubmit

    private var accessToken: String = ""
    private var refreshToken: String = ""

    fun setTokens(accessToken: String, refreshToken: String) {
        Log.d("asdf", accessToken + "\n" + refreshToken)
        this.accessToken = accessToken
        this.refreshToken = refreshToken
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

    fun dispatchNicknameValidity() {
        val nickname = currentNicknameInput.value ?: ""
        viewModelScope.launch {
            runCatching {
                checkNicknameValidityUseCase(nickname)
            }.onSuccess { result ->
                when (result) {
                    NicknameValidationResult.VALID_NICKNAME_SPELLING -> dispatchNicknameDuplication(
                        nickname
                    )

                    else -> updateOnBoardingFirstUiState(
                        NicknameInputType.ERROR,
                        result.profileEditMessage
                    )
                }
            }.onFailure {
                updateOnBoardingFirstUiState(
                    NicknameInputType.ERROR,
                    NicknameValidationResult.UNKNOWN_ERROR.profileEditMessage
                )
            }
        }
    }

    private fun dispatchNicknameDuplication(nickname: String) {
        viewModelScope.launch {
            runCatching {
                authRepository.fetchNicknameValidity(accessToken, nickname)
            }.onSuccess { isNicknameValid ->
                when (isNicknameValid) {
                    true -> {
                        updateOnBoardingFirstUiState(
                            NicknameInputType.COMPLETE,
                            NicknameValidationResult.VALID_NICKNAME.profileEditMessage
                        )
                    }

                    false -> {
                        updateOnBoardingFirstUiState(
                            NicknameInputType.ERROR,
                            NicknameValidationResult.INVALID_NICKNAME_DUPLICATION.profileEditMessage
                        )
                    }
                }
            }.onFailure {
                updateOnBoardingFirstUiState(
                    NicknameInputType.ERROR,
                    NicknameValidationResult.NETWORK_ERROR.profileEditMessage
                )
            }
        }
    }

    private fun updateOnBoardingFirstUiState(type: NicknameInputType, message: String) {
        _onboardingFirstUiState.value = onboardingFirstUiState.value?.copy(
            nicknameInputType = type,
            nicknameValidationMessage = message,
            isDuplicationCheckButtonEnable = type == NicknameInputType.TYPING,
            isNextButtonEnable = type == NicknameInputType.COMPLETE
        )
    }

    fun updateNicknameInputType(nicknameInputType: NicknameInputType) {
        updateOnBoardingFirstUiState(nicknameInputType, "")
    }

    fun clearInputNickname() {
        if (onboardingFirstUiState.value?.nicknameInputType != NicknameInputType.COMPLETE) {
            currentNicknameInput.value = ""
        }
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
        _userProfile.value = userProfile.value?.copy(birthYear = birthYear)
        updateSecondNextButtonUiState()
    }

    fun updateUserGenderUiState(isManSelected: Boolean) {
        _onboardingSecondUiState.value = onboardingSecondUiState.value?.copy(
            isManSelected = isManSelected,
            isWomanSelected = !isManSelected,
        )
        updateUserGender(isManSelected)
    }

    private fun updateUserGender(isManSelected: Boolean) {
        _userProfile.value =
            userProfile.value?.copy(gender = if (isManSelected) GENDER_MALE else GENDER_FEMALE)
        updateSecondNextButtonUiState()
    }

    private fun updateSecondNextButtonUiState() {
        _onboardingSecondUiState.value = onboardingSecondUiState.value?.copy(
            isNextButtonEnable = !userProfile.value?.gender.isNullOrEmpty() && userProfile.value?.birthYear != UNSELECTED_BIRTH_YEAR
        )
    }

    fun updateGenreSelection(genreTag: String) {
        val currentSelected = _selectedGenres.value?.toMutableSet() ?: mutableSetOf()
        when (currentSelected.contains(genreTag)) {
            true -> currentSelected.remove(genreTag)
            false -> currentSelected.add(genreTag)
        }
        _selectedGenres.value = currentSelected

        _userProfile.value = _userProfile.value?.copy(
            genrePreferences = currentSelected.toList()
        )
    }

    fun isGenreSelected(genreTag: String): Boolean {
        return _selectedGenres.value?.contains(genreTag) ?: false
    }

    fun submitUserProfile() {
        viewModelScope.launch {
            runCatching {
                _userProfile.value = userProfile.value?.copy(
                    nickname = currentNicknameInput.value.orEmpty(),
                )
                userProfile.value?.let { profile ->
                    authRepository.signUp(
                        authorization = accessToken,
                        nickname = profile.nickname,
                        gender = profile.gender,
                        birth = profile.birthYear,
                        genrePreferences = profile.genrePreferences,
                    )
                }
            }.onSuccess {
                authRepository.saveAccessToken(accessToken)
                authRepository.saveRefreshToken(refreshToken)
                authRepository.setAutoLoginConfigured(true)
                _isUserProfileSubmit.value = true
            }.onFailure { exception ->
                exception.printStackTrace()
            }
        }
    }

    fun clearGenreSelection() {
        _selectedGenres.value = setOf()
    }

    companion object {
        private const val GENDER_MALE = "M"
        private const val GENDER_FEMALE = "F"
        private const val UNSELECTED_BIRTH_YEAR = 0
    }
}
