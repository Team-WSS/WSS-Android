package com.teamwss.websoso.ui.activityDetail.model

import com.teamwss.websoso.ui.main.myPage.myActivity.model.UserActivityModel

sealed interface ActivityType {

    data class Activity(
        val activity: UserActivityModel,
    ) : ActivityType

    data object Loading : ActivityType

    data object NoMore : ActivityType

    enum class ItemType {
        ACTIVITY, LOADING, NO_MORE;

        companion object {

            fun valueOf(ordinal: Int): ItemType =
                entries.find { it.ordinal == ordinal } ?: throw IllegalArgumentException()
        }
    }
}