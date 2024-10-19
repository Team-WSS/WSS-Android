package com.teamwss.websoso.ui.activityDetail.model

import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel

data class ActivityDetailUiState(
    val isLoading: Boolean = false,
    val activities: List<ActivitiesModel.ActivityModel> = emptyList(),
    val lastFeedId: Long = 0L,
    val error: Boolean = false,
)