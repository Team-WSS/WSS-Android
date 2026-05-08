package com.into.websoso.ui.detailExploreResult.model

import android.os.Parcelable
import com.into.websoso.ui.detailExplore.info.model.Genre
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailExploreFilteredModel(
    val genres: List<Genre>? = null,
    val isCompleted: Boolean? = null,
    val novelRatingStart: Float = RATING_MIN_DEFAULT,
    val novelRatingEnd: Float = RATING_MAX_DEFAULT,
    val keywordIds: List<Int>? = null,
) : Parcelable {
    companion object {
        const val RATING_MIN_DEFAULT = 0.0f
        const val RATING_MAX_DEFAULT = 5.0f
    }
}
