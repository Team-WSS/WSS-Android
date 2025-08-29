package com.into.websoso.ui.detailExploreResult.model

import android.os.Parcelable
import com.into.websoso.ui.detailExplore.info.model.Genre
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailExploreFilteredModel(
    val genres: List<Genre>? = null,
    val isCompleted: Boolean? = null,
    val novelRating: Float? = null,
    val keywordIds: List<Int>? = null,
) : Parcelable
