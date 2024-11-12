package com.teamwss.websoso.ui.main.myPage.myActivity.model

import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
data class MyActivityUiState(
    val isLoading: Boolean = false,
    val activities: List<ActivityModel> = emptyList(),
    val likeState: ActivityLikeState? = null,
    val error: Boolean = false,
)