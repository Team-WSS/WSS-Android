package com.teamwss.websoso.ui.profileEdit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.domain.model.NicknameValidationResult
import com.teamwss.websoso.domain.model.NicknameValidationResult.NONE
import com.teamwss.websoso.domain.model.NicknameValidationResult.VALID_NICKNAME
import com.teamwss.websoso.domain.usecase.CheckNicknameValidityUseCase
import com.teamwss.websoso.domain.usecase.SaveChangedProfileUseCase
import com.teamwss.websoso.ui.mapper.toDomain
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
    private val saveChangedProfileUseCase: SaveChangedProfileUseCase,
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
                    nicknameEditResult = result,
                )
            }.onFailure {
                _profileEditUiState.value = profileEditUiState.value?.copy(
                    nicknameEditResult = NicknameValidationResult.UNKNOWN_ERROR,
                )
            }
        }
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
                saveChangedProfileUseCase(previousProfile.toDomain(), currentProfile.toDomain())
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

    fun updateCheckDuplicateNicknameButtonEnabled() {
        val isEnable =
            profileEditUiState.value?.profile?.nicknameModel?.nickname?.isNotEmpty() == true && profileEditUiState.value?.nicknameEditResult == NONE
        if (isEnable == profileEditUiState.value?.isCheckDuplicateNicknameEnabled) return
        _profileEditUiState.value = profileEditUiState.value?.copy(
            isCheckDuplicateNicknameEnabled = isEnable,
        )
    }
}
