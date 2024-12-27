package com.into.websoso.ui.activityDetail.model

import com.into.websoso.ui.main.myPage.myActivity.model.ActivitiesModel

data class ActivityDetailUiState(
    val isLoading: Boolean = false,
    val activities: List<ActivitiesModel.ActivityModel> = emptyList(),
    val lastFeedId: Long = 0L,
    val isLoadable: Boolean = true,
    val isError: Boolean = false,
)