package com.teamwss.websoso.ui.novelDetail.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NovelAlertModel(
    val title: String,
    val message: String = "",
    val acceptButtonText: String,
    val cancelButtonText: String,
    val onAcceptClick: () -> Unit,
) : Parcelable
