package com.teamwss.websoso.ui.feedDetail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditFeedModel(
    val novelId: Long?,
    val novelTitle: String?,
    val feedContent: String = "",
    val feedCategory: List<String> = emptyList(),
) : Parcelable
