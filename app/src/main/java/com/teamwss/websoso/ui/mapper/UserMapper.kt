package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.data.model.MyProfileEntity
import com.teamwss.websoso.ui.main.myPage.model.MyProfileModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.teamwss.websoso.ui.profileEdit.model.Genre.Companion.toGenreFromTag
import com.teamwss.websoso.ui.profileEdit.model.NicknameModel
import com.teamwss.websoso.ui.profileEdit.model.ProfileModel

fun MyProfileEntity.toUi(): MyProfileModel {
    return MyProfileModel(
        nickname = this.nickname,
        avatarImage = this.avatarImage,
        userId = 2L,
    )
}

fun MyProfileModel.toUi(): UserProfileModel {
    return UserProfileModel(
        nickname = this.nickname,
        avatarImage = this.avatarImage,
        userId = 2L,
    )
}

fun MyProfileEntity.toProfileEdit(): ProfileModel {
    return ProfileModel(
        nicknameModel = NicknameModel(nickname = nickname),
        introduction = intro,
        avatarThumbnail = avatarImage,
        genrePreferences = genrePreferences.map { it.toGenreFromTag() },
    )
}
