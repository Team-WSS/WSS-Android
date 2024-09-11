package com.teamwss.websoso.ui.mapper

import com.teamwss.websoso.ui.main.myPage.model.MyProfileModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserProfileModel

fun MyProfileModel.toUi(): UserProfileModel =
    UserProfileModel(
        nickname = nickname,
        avatarImage = avatarImage,
        userId = userId,
    )



