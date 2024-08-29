package com.teamwss.websoso.ui.profileEdit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.AvatarRepository
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.domain.model.NicknameValidationResult
import com.teamwss.websoso.domain.model.NicknameValidationResult.INVALID_NICKNAME_DUPLICATION
import com.teamwss.websoso.domain.model.NicknameValidationResult.NETWORK_ERROR
import com.teamwss.websoso.domain.model.NicknameValidationResult.NONE
import com.teamwss.websoso.domain.model.NicknameValidationResult.UNKNOWN_ERROR
import com.teamwss.websoso.domain.model.NicknameValidationResult.VALID_NICKNAME
import com.teamwss.websoso.domain.model.NicknameValidationResult.VALID_NICKNAME_SPELLING
import com.teamwss.websoso.domain.usecase.CheckNicknameValidityUseCase
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.profileEdit.model.AvatarChangeUiState
import com.teamwss.websoso.ui.profileEdit.model.AvatarModel
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
    private val avatarRepository: AvatarRepository,
) : ViewModel() {

    private val _profileEditUiState = MutableLiveData<ProfileEditUiState>(ProfileEditUiState())
    val profileEditUiState: LiveData<ProfileEditUiState> get() = _profileEditUiState

    private val _avatarChangeUiState = MutableLiveData<AvatarChangeUiState>(AvatarChangeUiState())
    val avatarChangeUiState: LiveData<AvatarChangeUiState> get() = _avatarChangeUiState

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
                    nicknameEditResult = if (result == VALID_NICKNAME_SPELLING) {
                        checkNicknameDuplication(nickname)
                    } else result,
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
                    avatarId = (currentProfile.avatarId to previousProfile.avatarId).compareAndReturnNewOrNullValue(),
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

    fun updateAvatars() {
        viewModelScope.launch {
            runCatching {
                _avatarChangeUiState.value = avatarChangeUiState.value?.copy(
                    loading = true,
                )
                avatarRepository.fetchAvatars()
            }.onSuccess { avatars ->
                val avatarsModel = avatars.map { it.toUi() }
                _avatarChangeUiState.value = avatarChangeUiState.value?.copy(
                    avatars = avatarsModel,
                    loading = false,
                )
            }.onFailure {
                _avatarChangeUiState.value = avatarChangeUiState.value?.copy(
                    error = true,
                    loading = false,
                )
            }
        }
    }

    fun updateSelectedAvatar(avatar: AvatarModel) {
        _avatarChangeUiState.value = avatarChangeUiState.value?.copy(
            avatars = avatarChangeUiState.value?.avatars?.map { previousAvatar ->
                if (previousAvatar.avatarId == avatar.avatarId) {
                    previousAvatar.copy(isRepresentative = true)
                } else {
                    previousAvatar.copy(isRepresentative = false)
                }
            } ?: emptyList(),
            selectedAvatar = avatar,
        )
    }

    fun updateRepresentativeAvatar() {
        val selectedAvatar = avatarChangeUiState.value?.selectedAvatar ?: return
        _profileEditUiState.value = profileEditUiState.value?.copy(
            profile = profileEditUiState.value?.profile?.copy(
                avatarId = selectedAvatar.avatarId,
                avatarThumbnail = selectedAvatar.avatarThumbnail
            ) ?: ProfileModel(),
        )
    }

    fun getRepresentativeAvatar(): AvatarModel {
        return profileEditUiState.value?.profile?.let { profile ->
            avatarChangeUiState.value?.avatars?.find { it.avatarId == profile.avatarId }
        } ?: AvatarModel()
    }

    fun getFormattedSpanCount(): Int {
        return avatarChangeUiState.value?.avatars?.size.let { avatarCount ->
            if (avatarCount == null) return MIN_CHARACTER_COLUMN_COUNT
            if (avatarCount < MAX_CHARACTER_COLUMN_COUNT) avatarCount
            else MAX_CHARACTER_COLUMN_COUNT
        }
    }

    companion object {
        private const val MAX_CHARACTER_COLUMN_COUNT = 5
        private const val MIN_CHARACTER_COLUMN_COUNT = 1
    }
}
