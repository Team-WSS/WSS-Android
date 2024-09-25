package com.teamwss.websoso.ui.profileEdit.model

import android.os.Parcelable
import com.teamwss.websoso.data.model.MyProfileEntity
import com.teamwss.websoso.ui.profileEdit.model.Genre.Companion.toGenreFromTag
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileModel(
    val nicknameModel: NicknameModel = NicknameModel(),
    val introduction: String = "",
    val avatarId: Int = 0,
    val avatarThumbnail: String = "",
    val genrePreferences: List<Genre> = emptyList(),
) : Parcelable {

    companion object {
        fun MyProfileEntity.toProfileEditModel(): ProfileModel {
            return ProfileModel(
                nicknameModel = NicknameModel(nickname = nickname),
                introduction = intro,
                avatarThumbnail = avatarImage,
                genrePreferences = genrePreferences.map { it.toGenreFromTag() },
            )
        }
    }
}

@Parcelize
data class NicknameModel(
    val nickname: String = "",
    val hasFocus: Boolean = false,
) : Parcelable

sealed interface ProfileEditResult {
    data object Loading : ProfileEditResult
    data object Success : ProfileEditResult
    data object Failure : ProfileEditResult
}

sealed interface LoadProfileResult {
    data object Success : LoadProfileResult
    data object Loading : LoadProfileResult
    data object Failure : LoadProfileResult
}
