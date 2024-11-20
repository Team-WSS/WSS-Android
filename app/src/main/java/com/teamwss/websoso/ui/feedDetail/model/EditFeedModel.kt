package com.teamwss.websoso.ui.feedDetail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditFeedModel(
    val feedId: Long = -1,
    val novelId: Long?,
    val novelTitle: String?,
    val feedContent: String = "",
    val isSpoiler: Boolean = false,
    val feedCategory: List<String> = emptyList(),
) : Parcelable
