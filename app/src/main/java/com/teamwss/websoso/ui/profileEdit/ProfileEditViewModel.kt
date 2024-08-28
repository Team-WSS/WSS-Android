package com.teamwss.websoso.ui.profileEdit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamwss.websoso.R
import com.teamwss.websoso.data.repository.AvatarRepository
import com.teamwss.websoso.data.repository.UserRepository
import com.teamwss.websoso.ui.mapper.toUi
import com.teamwss.websoso.ui.profileEdit.model.Avatar
import com.teamwss.websoso.ui.profileEdit.model.AvatarChangeUiState
import com.teamwss.websoso.ui.profileEdit.model.AvatarModel
import com.teamwss.websoso.ui.profileEdit.model.Genre
import com.teamwss.websoso.ui.profileEdit.model.NicknameEditResult
import com.teamwss.websoso.ui.profileEdit.model.NicknameEditResult.INVALID_NICKNAME_DUPLICATION
import com.teamwss.websoso.ui.profileEdit.model.NicknameEditResult.INVALID_NICKNAME_LENGTH
import com.teamwss.websoso.ui.profileEdit.model.NicknameEditResult.INVALID_NICKNAME_SPECIAL_CHARACTER
import com.teamwss.websoso.ui.profileEdit.model.NicknameEditResult.NONE
import com.teamwss.websoso.ui.profileEdit.model.NicknameEditResult.VALID_NICKNAME
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
    private val avatarRepository: AvatarRepository,
) : ViewModel() {

    private val _profileEditUiState = MutableLiveData<ProfileEditUiState>(ProfileEditUiState())
    val profileEditUiState: LiveData<ProfileEditUiState> get() = _profileEditUiState

    private val _avatarChangeUiState = MutableLiveData<AvatarChangeUiState>(AvatarChangeUiState())
    val avatarChangeUiState: LiveData<AvatarChangeUiState> get() = _avatarChangeUiState

    private val invalidLengthRegex = Regex("^\\s|\\s$")
    private val specialCharacterRegex = Regex("[^\\w가-힣-_]")
    private val hangulConsonantAndVowelRegex = Regex("[ㄱ-ㅎㅏ-ㅣ]")

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
            nicknameEditResult = if (profileEditUiState.value?.previousProfile?.nicknameModel?.nickname == nickname) VALID_NICKNAME else NONE,
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
        val result = nickname.getIsNicknameValid()
        if (result != VALID_NICKNAME) {
            _profileEditUiState.value = profileEditUiState.value?.copy(
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
                    _profileEditUiState.value = profileEditUiState.value?.copy(
                        nicknameEditResult = VALID_NICKNAME,
                    )
                } else {
                    _profileEditUiState.value = profileEditUiState.value?.copy(
                        nicknameEditResult = INVALID_NICKNAME_DUPLICATION,
                    )
                }
            }
        }
    }

    fun updateProfile() {
        if (profileEditUiState.value?.nicknameEditResult != VALID_NICKNAME) return
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

    private fun String.getIsNicknameValid(): NicknameEditResult {
        return when {
            this.length !in 2..10 -> INVALID_NICKNAME_LENGTH
            this.contains(invalidLengthRegex) -> INVALID_NICKNAME_SPECIAL_CHARACTER
            this.contains(specialCharacterRegex) -> INVALID_NICKNAME_SPECIAL_CHARACTER
            this.contains(hangulConsonantAndVowelRegex) -> INVALID_NICKNAME_SPECIAL_CHARACTER
            else -> VALID_NICKNAME
        }
    }

    fun updateCheckDuplicateNicknameBtnEnabled() {
        val nickname = profileEditUiState.value?.profile?.nicknameModel?.nickname ?: ""
        val isCheckDuplicateNicknameBtnEnabled = nickname.isNotEmpty() && profileEditUiState.value?.nicknameEditResult == NONE
        if (isCheckDuplicateNicknameBtnEnabled == profileEditUiState.value?.isCheckDuplicateNicknameEnabled) return
        _profileEditUiState.value = profileEditUiState.value?.copy(
            isCheckDuplicateNicknameEnabled = isCheckDuplicateNicknameBtnEnabled,
        )
    }

    fun fetchAvatars() {
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

    fun getAvatarAnimation(avatarId: Int): Int {
        Avatar.entries.forEach { avatar ->
            if (avatar.avatarId == avatarId) {
                return getLottieResourceId(avatar.avatarName)
            }
        }
        return R.raw.lottie_sosocat_0
    }

    private fun getLottieResourceId(avatarName: String): Int {
        val randomNumber = (0..1).random()
        val resourceName = "lottie_${avatarName}_${randomNumber}"
        val resourceId = R.raw::class.java.getField(resourceName).getInt(null)
        return if (resourceId != 0) resourceId else R.raw.lottie_sosocat_0
    }

    fun getSelectedAvatar(): AvatarModel {
        return avatarChangeUiState.value?.selectedAvatar ?: AvatarModel()
    }

    companion object {
        private const val MAX_CHARACTER_COLUMN_COUNT = 5
        private const val MIN_CHARACTER_COLUMN_COUNT = 1
    }
}
