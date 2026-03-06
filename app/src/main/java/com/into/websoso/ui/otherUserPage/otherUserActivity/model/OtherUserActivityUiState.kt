package com.into.websoso.ui.otherUserPage.otherUserActivity.model

import com.into.websoso.ui.main.myPage.model.ActivitiesModel.ActivityModel
import com.into.websoso.ui.main.myPage.model.ActivityLikeState

data class OtherUserActivityUiState(
    val isLoading: Boolean = false,
    val activities: List<ActivityModel> = emptyList(),
    val likeState: ActivityLikeState? = null,
    val error: Boolean = false,
)
