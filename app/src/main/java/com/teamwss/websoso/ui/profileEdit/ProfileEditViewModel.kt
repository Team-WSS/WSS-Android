package com.teamwss.websoso.ui.profileEdit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.data.repository.AvatarRepository
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.domain.model.NicknameValidationResult.INVALID_NICKNAME_DUPLICATION
import com.teamwss.websoso.domain.model.NicknameValidationResult.NETWORK_ERROR
import com.teamwss.websoso.domain.model.NicknameValidationResult.NONE
import com.teamwss.websoso.domain.model.NicknameValidationResult.UNKNOWN_ERROR
import com.teamwss.websoso.domain.model.NicknameValidationResult.VALID_NICKNAME
import com.teamwss.websoso.domain.model.NicknameValidationResult.VALID_NICKNAME_SPELLING
import com.teamwss.websoso.domain.usecase.CheckNicknameValidityUseCase
import com.teamwss.websoso.ui.mapper.toProfileEdit
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.profileEdit.model.AvatarChangeUiState
import com.teamwss.websoso.ui.profileEdit.model.AvatarModel
import com.teamwss.websoso.ui.profileEdit.model.Genre
import com.teamwss.websoso.ui.profileEdit.model.LoadProfileResult
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

    private fun updatePreviousProfile(profile: ProfileModel) {
        _profileEditUiState.value = profileEditUiState.value?.copy(
            profile = profile,
            previousProfile = profile,
        )
    }

    fun updateSelectedGenres(selectedGenre: Genre) {
        val genrePreferences =
            profileEditUiState.value?.profile?.genrePreferences?.toMutableList() ?: mutableListOf()
        if (genrePreferences.contains(selectedGenre)) genrePreferences.remove(selectedGenre)
        else genrePreferences.add(selectedGenre)
        _profileEditUiState.value = profileEditUiState.value?.let { uiState ->
            uiState.copy(
                profile = uiState.profile.copy(
                    genrePreferences = genrePreferences,
                )
            )
        }
    }

    fun updateNickname(nickname: String) {
        _profileEditUiState.value = profileEditUiState.value?.let { uiState ->
            uiState.copy(
                profile = uiState.profile.copy(
                    nicknameModel = uiState.profile.nicknameModel.copy(
                        nickname = nickname,
                    ),
                ),
                nicknameEditResult = NONE,
            )
        }
    }

    fun updateIntroduction(introduction: String) {
        _profileEditUiState.value = profileEditUiState.value?.let { uiState ->
            uiState.copy(
                profile = uiState.profile.copy(
                    introduction = introduction,
                ),
            )
        }
    }

    fun updateNicknameFocus(hasFocus: Boolean) {
        _profileEditUiState.value = profileEditUiState.value?.let { uiState ->
            uiState.copy(
                profile = uiState.profile.copy(
                    nicknameModel = uiState.profile.nicknameModel.copy(
                        hasFocus = hasFocus,
                    ),
                ),
            )
        }
    }

    fun clearNickname() {
        _profileEditUiState.value = profileEditUiState.value?.let { uiState ->
            uiState.copy(
                profile = uiState.profile.copy(
                    nicknameModel = uiState.profile.nicknameModel.copy(
                        nickname = "",
                    ),
                ),
            )
        }
    }

    fun checkNicknameValidity(nickname: String) {
        if (isNicknameChanged(nickname)) return

        viewModelScope.launch {
            runCatching {
                checkNicknameValidityUseCase(nickname)
            }.onSuccess { result ->
                when (result) {
                    VALID_NICKNAME_SPELLING -> checkNicknameDuplication(nickname)
                    else -> _profileEditUiState.value = profileEditUiState.value?.copy(
                        nicknameEditResult = result,
                    )
                }
            }.onFailure {
                _profileEditUiState.value = profileEditUiState.value?.copy(
                    nicknameEditResult = UNKNOWN_ERROR,
                )
            }
        }
    }

    private fun isNicknameChanged(nickname: String): Boolean {
        return when (nickname == profileEditUiState.value.let {
            it?.previousProfile?.nicknameModel?.nickname
        }) {
            true -> {
                _profileEditUiState.value = profileEditUiState.value?.copy(
                    nicknameEditResult = VALID_NICKNAME,
                )
                true
            }

            false -> false
        }
    }

    private fun checkNicknameDuplication(nickname: String) {
        viewModelScope.launch {
            runCatching {
                userRepository.fetchNicknameValidity(nickname)
            }.onSuccess { isNicknameValid ->
                _profileEditUiState.value = profileEditUiState.value.let { uiState ->
                    uiState?.copy(
                        nicknameEditResult = when (isNicknameValid) {
                            true -> VALID_NICKNAME
                            false -> INVALID_NICKNAME_DUPLICATION
                        },
                    )
                }
            }.onFailure {
                _profileEditUiState.value = profileEditUiState.value.let { uiState ->
                    uiState?.copy(
                        nicknameEditResult = NETWORK_ERROR,
                    )
                }
            }
        }
    }

    fun updateProfile() {
        val previousProfile = profileEditUiState.value?.previousProfile ?: return
        val currentProfile = profileEditUiState.value?.profile ?: return

        val isInvalidNickname = profileEditUiState.value?.nicknameEditResult != VALID_NICKNAME
        val isNicknameChanged =
            currentProfile.nicknameModel.nickname != previousProfile.nicknameModel.nickname
        if (isInvalidNickname && isNicknameChanged) return

        viewModelScope.launch {
            runCatching {
                userRepository.saveUserProfile(
                    avatarId = (previousProfile.avatarId to currentProfile.avatarId).compareAndReturnNewOrNullValue(),
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

    private fun updateAvatars() {
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
                val representativeAvatar = avatarsModel.find { it.isRepresentative }
                updatePreviousProfile(
                    profileEditUiState.value?.profile?.copy(
                        avatarId = representativeAvatar?.avatarId ?: 0,
                        avatarThumbnail = representativeAvatar?.avatarThumbnail ?: "",
                    ) ?: ProfileModel()
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
        _avatarChangeUiState.value = avatarChangeUiState.value?.let { uiState ->
            uiState.copy(
                avatars = uiState.avatars.map { previousAvatar ->
                    if (previousAvatar.avatarId == avatar.avatarId) {
                        previousAvatar.copy(isRepresentative = true)
                    } else {
                        previousAvatar.copy(isRepresentative = false)
                    }
                },
                selectedAvatar = avatar,
            )
        }
    }

    fun updateRepresentativeAvatar() {
        val selectedAvatar = avatarChangeUiState.value?.selectedAvatar ?: return
        _profileEditUiState.value = profileEditUiState.value?.let { uiState ->
            uiState.copy(
                profile = uiState.profile.copy(
                    avatarId = selectedAvatar.avatarId,
                    avatarThumbnail = selectedAvatar.avatarThumbnail,
                ),
            )
        }
    }

    fun getRepresentativeAvatar(): AvatarModel {
        return profileEditUiState.value?.profile?.let { profile ->
            avatarChangeUiState.value?.avatars?.find { it.avatarId == profile.avatarId }
        } ?: AvatarModel()
    }

    fun getFormattedSpanCount(): Int {
        return avatarChangeUiState.value?.avatars?.size?.let { avatarCount ->
            when {
                avatarCount < MIN_CHARACTER_COLUMN_COUNT -> MIN_CHARACTER_COLUMN_COUNT
                avatarCount < MAX_CHARACTER_COLUMN_COUNT -> avatarCount
                else -> MAX_CHARACTER_COLUMN_COUNT
            }
        } ?: MIN_CHARACTER_COLUMN_COUNT
    }

    fun updateUserProfile() {
        viewModelScope.launch {
            runCatching {
                _profileEditUiState.value = profileEditUiState.value?.copy(
                    loadProfileResult = LoadProfileResult.Loading,
                )
                userRepository.fetchMyProfile()
            }.onSuccess { profile ->
                _profileEditUiState.value = profileEditUiState.value?.copy(
                    loadProfileResult = LoadProfileResult.Success,
                )
                updatePreviousProfile(profile.toProfileEdit())
                updateAvatars()
            }.onFailure {
                _profileEditUiState.value = profileEditUiState.value?.copy(
                    loadProfileResult = LoadProfileResult.Failure,
                )
            }
        }
    }

    fun updateAvatarThumbnail(avatarThumbnail: String) {
        _profileEditUiState.value = profileEditUiState.value?.let { uiState ->
            uiState.copy(
                profile = uiState.profile.copy(
                    avatarThumbnail = avatarThumbnail,
                ),
            )
        }
    }

    companion object {
        private const val MAX_CHARACTER_COLUMN_COUNT = 5
        private const val MIN_CHARACTER_COLUMN_COUNT = 1
    }
}
