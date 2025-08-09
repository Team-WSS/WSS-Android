package com.into.websoso.ui.feedDetail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditFeedModel(
    val feedId: Long = -1,
    val novelId: Long?,
    val novelTitle: String?,
    val feedContent: String = "",
    val isSpoiler: Boolean = false,
    val isPublic: Boolean = true,
    val feedCategory: List<String> = emptyList(),
    val imageUrls: List<String> = emptyList(),
) : Parcelable
