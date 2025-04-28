package com.into.websoso.ui.main.myPage.myActivity.model

import com.into.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel

data class MyActivityUiState(
    val isLoading: Boolean = false,
    val activities: List<ActivityModel> = emptyList(),
    val likeState: ActivityLikeState? = null,
    val isError: Boolean = false,
)
