package com.teamwss.websoso.ui.otherUserPage.otherUserActivity.model

import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivitiesModel.ActivityModel
import com.teamwss.websoso.ui.main.myPage.myActivity.model.ActivityLikeState

data class OtherUserActivityUiState(
    val isLoading: Boolean = false,
    val activities: List<ActivityModel> = emptyList(),
    val likeState: ActivityLikeState? = null,
    val error: Boolean = false,
)