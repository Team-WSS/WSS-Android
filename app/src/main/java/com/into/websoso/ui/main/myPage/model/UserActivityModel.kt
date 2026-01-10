package com.into.websoso.ui.main.myPage.model

import com.into.websoso.ui.main.myPage.model.ActivitiesModel.ActivityModel

data class UserActivityModel(
    val activity: ActivityModel,
    val userProfile: UserProfileModel,
)
