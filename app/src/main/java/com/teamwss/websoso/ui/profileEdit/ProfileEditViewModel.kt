package com.teamwss.websoso.ui.profileEdit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.profileEdit.model.Genre
import com.teamwss.websoso.ui.profileEdit.model.NicknameEditResult
import com.teamwss.websoso.ui.profileEdit.model.NicknameEditResult.Companion.checkNicknameValidity
import com.teamwss.websoso.ui.profileEdit.model.NicknameModel
import com.teamwss.websoso.ui.profileEdit.model.ProfileEditResult
import com.teamwss.websoso.ui.profileEdit.model.ProfileEditUiState
import com.teamwss.websoso.ui.profileEdit.model.ProfileModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableLiveData<ProfileEditUiState>(ProfileEditUiState())
    val uiState: LiveData<ProfileEditUiState> get() = _uiState

    fun updatePreviousProfile(profile: ProfileModel) {
        _uiState.value = uiState.value?.copy(
            profile = profile,
            previousProfile = profile,
        )
    }

    fun updateSelectedGenres(selectedGenre: Genre) {
        val genrePreferences = uiState.value?.profile?.genrePreferences?.toMutableList() ?: mutableListOf()
        if (genrePreferences.contains(selectedGenre)) genrePreferences.remove(selectedGenre)
        else genrePreferences.add(selectedGenre)
        _uiState.value = uiState.value?.copy(
            profile = uiState.value?.profile?.copy(
                genrePreferences = genrePreferences,
            ) ?: ProfileModel(),
        )
    }

    fun updateNickname(nickname: String) {
        _uiState.value = uiState.value?.copy(
            profile = uiState.value?.profile?.copy(
                nicknameModel = uiState.value?.profile?.nicknameModel?.copy(
                    nickname = nickname,
                ) ?: NicknameModel(),
            ) ?: ProfileModel(),
            nicknameEditResult = if (uiState.value?.previousProfile?.nicknameModel?.nickname == nickname) NicknameEditResult.VALID_NICKNAME else NicknameEditResult.NONE,
        )
    }

    fun updateIntroduction(introduction: String) {
        _uiState.value = uiState.value?.copy(
            profile = uiState.value?.profile?.copy(
                introduction = introduction,
            ) ?: ProfileModel(),
        )
    }

    fun updateNicknameFocus(hasFocus: Boolean) {
        _uiState.value = uiState.value?.copy(
            profile = uiState.value?.profile?.copy(
                nicknameModel = uiState.value?.profile?.nicknameModel?.copy(
                    hasFocus = hasFocus,
                ) ?: NicknameModel(),
            ) ?: ProfileModel(),
        )
    }

    fun clearNickname() {
        _uiState.value = uiState.value?.copy(
            profile = uiState.value?.profile?.copy(
                nicknameModel = uiState.value?.profile?.nicknameModel?.copy(
                    nickname = "",
                ) ?: NicknameModel(),
            ) ?: ProfileModel(),
        )
    }

    fun checkNicknameValidity(nickname: String) {
        val result = nickname.checkNicknameValidity()
        if (result != NicknameEditResult.VALID_NICKNAME) {
            _uiState.value = uiState.value?.copy(
                nicknameEditResult = result,
            )
        } else checkNicknameDuplication(nickname)
    }

    private fun checkNicknameDuplication(nickname: String) {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchNicknameValidity(nickname)
            }.onSuccess { isDuplicated ->
                if (isDuplicated) {
                    _uiState.value = uiState.value?.copy(
                        nicknameEditResult = NicknameEditResult.VALID_NICKNAME,
                    )
                } else {
                    _uiState.value = uiState.value?.copy(
                        nicknameEditResult = NicknameEditResult.INVALID_NICKNAME_DUPLICATION,
                    )
                }
            }
        }
    }

    fun updateProfile() {
        if (uiState.value?.nicknameEditResult != NicknameEditResult.VALID_NICKNAME) return
        val previousProfile = uiState.value?.previousProfile ?: return
        val currentProfile = uiState.value?.profile ?: return
        viewModelScope.launch {
            runCatching {
                userRepository.saveUserProfile(
                    // TODO: 실제 아바타 아이디로 변경
                    avatarId = 1,
                    nickname = (previousProfile.nicknameModel.nickname to currentProfile.nicknameModel.nickname).compareAndReturnNewOrNullValue(),
                    intro = (previousProfile.introduction to currentProfile.introduction).compareAndReturnNewOrNullValue(),
                    genrePreferences = currentProfile.genrePreferences.map { it.tag }
                )
            }.onSuccess {
                _uiState.value = uiState.value?.copy(
                    profileEditResult = ProfileEditResult.Success,
                )
            }.onFailure {
                _uiState.value = uiState.value?.copy(
                    profileEditResult = ProfileEditResult.Loading,
                )
            }
        }
    }

    private fun <T> Pair<T, T>.compareAndReturnNewOrNullValue(): T? {
        val (oldValue, newValue) = this
        return if (oldValue == newValue) null else newValue
    }
}
