package com.teamwss.websoso.ui.profileEdit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.domain.model.NicknameValidationResult
import com.teamwss.websoso.domain.model.NicknameValidationResult.INVALID_NICKNAME_DUPLICATION
import com.teamwss.websoso.domain.model.NicknameValidationResult.NETWORK_ERROR
import com.teamwss.websoso.domain.model.NicknameValidationResult.NONE
import com.teamwss.websoso.domain.model.NicknameValidationResult.UNKNOWN_ERROR
import com.teamwss.websoso.domain.model.NicknameValidationResult.VALID_NICKNAME
import com.teamwss.websoso.domain.usecase.CheckNicknameValidityUseCase
import com.teamwss.websoso.ui.profileEdit.model.Genre
import com.teamwss.websoso.ui.profileEdit.model.NicknameModel
import com.teamwss.websoso.ui.profileEdit.model.ProfileEditResult
import com.teamwss.websoso.ui.profileEdit.model.ProfileEditUiState
import com.teamwss.websoso.ui.profileEdit.model.ProfileModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val checkNicknameValidityUseCase: CheckNicknameValidityUseCase,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _profileEditUiState = MutableLiveData<ProfileEditUiState>(ProfileEditUiState())
    val profileEditUiState: LiveData<ProfileEditUiState> get() = _profileEditUiState

    fun updatePreviousProfile(profile: ProfileModel) {
        _profileEditUiState.value = profileEditUiState.value?.copy(
            profile = profile,
            previousProfile = profile,
        )
    }

    fun updateSelectedGenres(selectedGenre: Genre) {
        val genrePreferences = profileEditUiState.value?.profile?.genrePreferences?.toMutableList() ?: mutableListOf()
        if (genrePreferences.contains(selectedGenre)) genrePreferences.remove(selectedGenre)
        else genrePreferences.add(selectedGenre)
        _profileEditUiState.value = profileEditUiState.value?.copy(
            profile = profileEditUiState.value?.profile?.copy(
                genrePreferences = genrePreferences,
            ) ?: ProfileModel(),
        )
    }

    fun updateNickname(nickname: String) {
        _profileEditUiState.value = profileEditUiState.value?.copy(
            profile = profileEditUiState.value?.profile?.copy(
                nicknameModel = profileEditUiState.value?.profile?.nicknameModel?.copy(
                    nickname = nickname,
                ) ?: NicknameModel(),
            ) ?: ProfileModel(),
            nicknameEditResult = NONE,
        )
    }

    fun updateIntroduction(introduction: String) {
        _profileEditUiState.value = profileEditUiState.value?.copy(
            profile = profileEditUiState.value?.profile?.copy(
                introduction = introduction,
            ) ?: ProfileModel(),
        )
    }

    fun updateNicknameFocus(hasFocus: Boolean) {
        _profileEditUiState.value = profileEditUiState.value?.copy(
            profile = profileEditUiState.value?.profile?.copy(
                nicknameModel = profileEditUiState.value?.profile?.nicknameModel?.copy(
                    hasFocus = hasFocus,
                ) ?: NicknameModel(),
            ) ?: ProfileModel(),
        )
    }

    fun clearNickname() {
        _profileEditUiState.value = profileEditUiState.value?.copy(
            profile = profileEditUiState.value?.profile?.copy(
                nicknameModel = profileEditUiState.value?.profile?.nicknameModel?.copy(
                    nickname = "",
                ) ?: NicknameModel(),
            ) ?: ProfileModel(),
        )
    }

    fun checkNicknameValidity(nickname: String) {
        viewModelScope.launch {
            runCatching {
                checkNicknameValidityUseCase(nickname)
            }.onSuccess { result ->
                _profileEditUiState.value = profileEditUiState.value?.copy(
                    nicknameEditResult = if (result == VALID_NICKNAME) checkNicknameDuplication(nickname) else result,
                )
            }.onFailure {
                _profileEditUiState.value = profileEditUiState.value?.copy(
                    nicknameEditResult = UNKNOWN_ERROR,
                )
            }
        }
    }

    private suspend fun checkNicknameDuplication(nickname: String): NicknameValidationResult {
        runCatching {
            userRepository.fetchNicknameValidity(nickname)
        }.onSuccess { isNicknameValid ->
            return if (isNicknameValid) VALID_NICKNAME
            else INVALID_NICKNAME_DUPLICATION
        }.onFailure {
            return NETWORK_ERROR
        }
        return UNKNOWN_ERROR
    }

    fun updateProfile() {
        val isInvalidNickname = profileEditUiState.value?.nicknameEditResult != VALID_NICKNAME
        val isNicknameChanged =
            profileEditUiState.value?.profile?.nicknameModel?.nickname != profileEditUiState.value?.previousProfile?.nicknameModel?.nickname
        if (isInvalidNickname && isNicknameChanged) return

        val previousProfile = profileEditUiState.value?.previousProfile ?: return
        val currentProfile = profileEditUiState.value?.profile ?: return

        viewModelScope.launch {
            runCatching {
                userRepository.saveUserProfile(
                    // TODO: 실제 아바타 아이디로 변경
                    avatarId = 1,
                    nickname = (previousProfile.nicknameModel.nickname to currentProfile.nicknameModel.nickname).compareAndReturnNewOrNullValue(),
                    intro = (previousProfile.introduction to currentProfile.introduction).compareAndReturnNewOrNullValue(),
                    genrePreferences = currentProfile.genrePreferences.map { it.tag },
                )
            }.onSuccess {
                _profileEditUiState.value = profileEditUiState.value?.copy(
                    profileEditResult = ProfileEditResult.Success,
                )
            }.onFailure {
                _profileEditUiState.value = profileEditUiState.value?.copy(
                    profileEditResult = ProfileEditResult.Loading,
                )
            }
        }
    }

    private fun <T> Pair<T, T>.compareAndReturnNewOrNullValue(): T? {
        val (oldValue, newValue) = this
        return if (oldValue == newValue) null else newValue
    }

    fun updateCheckDuplicateNicknameButtonEnabled() {
        val isEnable =
            profileEditUiState.value?.profile?.nicknameModel?.nickname?.isNotEmpty() == true && profileEditUiState.value?.nicknameEditResult == NONE
        if (isEnable == profileEditUiState.value?.isCheckDuplicateNicknameEnabled) return
        _profileEditUiState.value = profileEditUiState.value?.copy(
            isCheckDuplicateNicknameEnabled = isEnable,
        )
    }
}
