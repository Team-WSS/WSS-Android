package com.into.websoso.ui.mapper

import com.into.websoso.data.model.MyProfileEntity
import com.into.websoso.data.model.OtherUserProfileEntity
import com.into.websoso.ui.main.myPage.model.MyProfileModel
import com.into.websoso.ui.main.myPage.myActivity.model.UserProfileModel
import com.into.websoso.ui.profileEdit.model.Genre.Companion.toGenreFromTag
import com.into.websoso.ui.profileEdit.model.NicknameModel
import com.into.websoso.ui.profileEdit.model.ProfileModel

fun MyProfileEntity.toUi(): MyProfileModel {
    return MyProfileModel(
        nickname = this.nickname,
        avatarImage = this.avatarImage,
    )
}

fun MyProfileModel.toUi(): UserProfileModel {
    return UserProfileModel(
        nickname = this.nickname,
        avatarImage = this.avatarImage,
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

fun MyProfileEntity.toUserProfileModel(): UserProfileModel {
    return UserProfileModel(
        nickname = this.nickname,
        avatarImage = this.avatarImage,
    )
}

fun OtherUserProfileEntity.toUserProfileModel(): UserProfileModel {
    return UserProfileModel(
        nickname = this.nickname,
        avatarImage = this.avatarImage,
    )
}