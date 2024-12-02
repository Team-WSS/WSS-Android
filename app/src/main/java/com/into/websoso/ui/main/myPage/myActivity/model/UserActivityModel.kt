package com.into.websoso.ui.main.myPage.myActivity.model

import com.into.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel

data class UserActivityModel(
    val activity: ActivityModel,
    val userProfile: UserProfileModel,
)