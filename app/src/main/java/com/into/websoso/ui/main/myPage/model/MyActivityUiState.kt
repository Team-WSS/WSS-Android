package com.into.websoso.ui.main.myPage.model

import com.into.websoso.ui.main.myPage.model.ActivitiesModel.ActivityModel

data class MyActivityUiState(
    val isLoading: Boolean = false,
    val activities: List<ActivityModel> = emptyList(),
    val likeState: ActivityLikeState? = null,
    val isError: Boolean = false,
)
