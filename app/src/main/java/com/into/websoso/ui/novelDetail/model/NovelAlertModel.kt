package com.into.websoso.ui.novelDetail.model

import android.os.Parcelable
import com.into.websoso.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class NovelAlertModel(
    val title: String,
    val message: String = "",
    val acceptButtonText: String,
    val cancelButtonText: String,
    val acceptButtonColor: Int = R.drawable.bg_novel_detail_secondary_100_radius_8dp,
    val onAcceptClick: () -> Unit = {},
    val onCancelClick: () -> Unit = {},
) : Parcelable
